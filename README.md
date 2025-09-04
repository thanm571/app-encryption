# app-encryption without Passphrase

Load config source file , destination file and recipient key from database

## table structure
<img width="240" height="95" alt="image" src="https://github.com/user-attachments/assets/a930d6ba-10b5-4240-ac12-401a0f277ab0" />

## key location
src/main/resources/keys <br><br>
<img width="221" height="56" alt="image" src="https://github.com/user-attachments/assets/48f6b56e-a9db-4716-802d-6566b7a1430b" />
<br>

##### * load all file pattern *.asc

## initial data
Recipient : gpg_key1 <br>
Source File Path : xxx/PA/gpg/test.txt <br>
Destination File Path : xxx/PA/gpg/test.gpg <br>

## process encrypt
curl -X POST http://localhost:8080/api/gpg/encrypt/{configId}
