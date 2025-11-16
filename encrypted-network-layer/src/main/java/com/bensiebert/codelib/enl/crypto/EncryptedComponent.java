package com.bensiebert.codelib.enl.crypto;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class EncryptedComponent {

    public PrivateKey privateKey;
    public PublicKey publicKey;
    public KeyPair keyPair;

    public EncryptedComponent() {
        this.keyPair = RSA.generateKeyPair();
        assert this.keyPair != null;
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public EncryptedComponent(KeyPair keyPair) {
        this.keyPair = keyPair;
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }


    public Payload encryptMessage(String message, PublicKey pubKey) {
        SecretKey aesKey = AES.getNewAESKey();
        String encryptedMessage = AES.encrypt(message, aesKey);
        assert aesKey != null;
        String encryptedKey = RSA.encrypt(pubKey, AES.encodeSecretKey(aesKey));
        return new Payload(encryptedKey, encryptedMessage);
    }

    public String decryptMessage(String rawData) {
        String[] parts = rawData.split("\u0000");
        String encryptedKey = parts[0];
        String encryptedMessage = parts[1];
        SecretKey aesKey = AES.decodeSecretKey(RSA.decrypt(this.privateKey, encryptedKey));
        return AES.decrypt(encryptedMessage, aesKey);
    }

}