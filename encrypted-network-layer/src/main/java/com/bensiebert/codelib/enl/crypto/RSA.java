package com.bensiebert.codelib.enl.crypto;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class RSA {

    public static String encrypt(PublicKey pubKey, String data) {
        String result = "";
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] messageBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = cipher.doFinal(messageBytes);
            result = Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.err.println("Error encrypting data: " + e.getMessage());
        }
        return result;
    }

    public static String decrypt(PrivateKey privKey, String data) {
        String result = "";
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(data);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            result = new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Error decrypting data: " + e.getMessage());
        }
        return result;
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(4096);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error generating key pair: " + e.getMessage());
            return null;
        }
    }

}