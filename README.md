# app-encryption without Passphrase

Load config source file , destination file and recipient key from database

## table structure
<img width="240" height="95" alt="image" src="https://github.com/user-attachments/assets/a930d6ba-10b5-4240-ac12-401a0f277ab0" />

## key location
src/main/resources/keys

## initial data
config.setRecipientKeyId("gpg_key1"); 
config.setSourceFilePath("xxx/PA/gpg/test.txt");
config.setDestinationFilePath("xxx/PA/gpg/test.gpg");

## process encrypt
curl -X POST http://localhost:8080/api/gpg/encrypt/{configId}
