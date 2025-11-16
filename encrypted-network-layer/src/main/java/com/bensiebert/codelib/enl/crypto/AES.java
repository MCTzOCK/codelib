package com.bensiebert.codelib.enl.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {

    public static SecretKey getNewAESKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);
            return kg.generateKey();
        } catch (Exception e) {
            System.err.println("Error generating AES key: " + e.getMessage());
        }
        return null;
    }

    public static String encrypt(String message, SecretKey key) {
        try {
            Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

            aesCipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            byte[] encrypted = aesCipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
            buffer.put(iv);
            buffer.put(encrypted);
            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception e) {
            System.err.println("Error creating AES cipher: " + e.getMessage());
        }
        return "";
    }

    public static String decrypt(String message, SecretKey key) {
        try {
            Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] decoded = Base64.getDecoder().decode(message);

            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[12];
            buffer.get(iv);
            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);

            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

            aesCipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            byte[] decrypted = aesCipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Error creating AES cipher: " + e.getMessage());
        }
        return "";
    }

    public static String encodeSecretKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static SecretKey decodeSecretKey(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, "AES");
    }
}
