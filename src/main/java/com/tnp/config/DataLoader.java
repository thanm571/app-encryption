package com.tnp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tnp.model.EncryptionConfig;
import com.tnp.repository.EncryptionConfigRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(EncryptionConfigRepository repository) {
        return args -> {
            // Check if a configuration already exists to avoid duplicates
            if (repository.count() == 0) {
                EncryptionConfig config = new EncryptionConfig();

                config.setRecipientKeyId("gpg_key1"); 
                config.setSourceFilePath("xxx/PA/gpg/test.txt");
                config.setDestinationFilePath("xxx/PA/gpg/test.gpg");

                repository.save(config);
                System.out.println("Sample encryption configuration saved to database.");
            }
        };
    }
}
