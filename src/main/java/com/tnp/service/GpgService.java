package com.tnp.service;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.tnp.model.EncryptionConfig;
import com.tnp.repository.EncryptionConfigRepository;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class GpgService {

    private final EncryptionConfigRepository configRepository;
    private PGPPublicKeyRingCollection pgpPub;

    @Value("classpath:keys/*.asc")
    private Resource[] publicKeyResources;

    public GpgService(EncryptionConfigRepository configRepository) {
        this.configRepository = configRepository;
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @PostConstruct
    public void init() throws IOException, PGPException {
        this.pgpPub = loadPublicKeysFromDirectory();
    }

    public void encryptFileFromDatabase(Long configId) throws Exception {
        Optional<EncryptionConfig> configOpt = configRepository.findById(configId);
        if (configOpt.isPresent()) {
            EncryptionConfig config = configOpt.get();
            // Assuming recipientKeyId now stores the recipient's name
            encryptFile(config.getSourceFilePath(), config.getDestinationFilePath(), config.getRecipientKeyId());
        } else {
            throw new IllegalArgumentException("Configuration not found with ID: " + configId);
        }
    }

    private void encryptFile(String sourceFilePath, String destinationFilePath, String recipientName) throws Exception {
        PGPPublicKey recipientKey = getRecipientPublicKeyByName(recipientName);
        if (recipientKey == null) {
            throw new IllegalArgumentException("Recipient key not found for name: " + recipientName);
        }
        
        try (InputStream fileIn = new FileInputStream(sourceFilePath);
             OutputStream fileOut = new FileOutputStream(destinationFilePath);
             ArmoredOutputStream armoredOut = new ArmoredOutputStream(fileOut)) {
            
            JcePGPDataEncryptorBuilder dataEncryptorBuilder = new JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256)
                    .setWithIntegrityPacket(true)
                    .setSecureRandom(new SecureRandom())
                    .setProvider(BouncyCastleProvider.PROVIDER_NAME);

            PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(dataEncryptorBuilder);
            encryptedDataGenerator.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(recipientKey).setProvider(BouncyCastleProvider.PROVIDER_NAME));

            try (OutputStream encryptedOut = encryptedDataGenerator.open(armoredOut, new byte[1 << 16])) {
                PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
                try (OutputStream literalOut = literalDataGenerator.open(encryptedOut, PGPLiteralData.TEXT, new File(sourceFilePath))) {
                    byte[] buffer = new byte[1 << 16];
                    int bytesRead;
                    while ((bytesRead = fileIn.read(buffer)) > 0) {
                        literalOut.write(buffer, 0, bytesRead);
                    }
                }
            }
        }
    }

    private PGPPublicKey getRecipientPublicKeyByName(String recipientName) throws PGPException {
        Iterator<PGPPublicKeyRing> keyRingIterator = pgpPub.getKeyRings();
        while (keyRingIterator.hasNext()) {
            PGPPublicKeyRing keyRing = keyRingIterator.next();
            Iterator<PGPPublicKey> keyIterator = keyRing.getPublicKeys();
            while (keyIterator.hasNext()) {
                PGPPublicKey key = keyIterator.next();
                if (key.isEncryptionKey()) {
                    Iterator<String> userIdIterator = key.getUserIDs();
                    while (userIdIterator.hasNext()) {
                        String userId = userIdIterator.next();
                        if (userId.contains(recipientName)) {
                            return key;
                        }
                    }
                }
            }
        }
        return null;
    }

    private PGPPublicKeyRingCollection loadPublicKeysFromDirectory() throws IOException, PGPException {
        List<PGPPublicKeyRing> allKeyRings = new ArrayList<>();
        
        for (Resource resource : publicKeyResources) {
            try (InputStream in = resource.getInputStream()) {
                PGPPublicKeyRingCollection collection = new PGPPublicKeyRingCollection(
                    PGPUtil.getDecoderStream(in), new JcaKeyFingerprintCalculator());
                
                Iterator<PGPPublicKeyRing> keyRingIterator = collection.getKeyRings();
                while (keyRingIterator.hasNext()) {
                    allKeyRings.add(keyRingIterator.next());
                }
            }
        }

        return new PGPPublicKeyRingCollection(allKeyRings);
    }
}