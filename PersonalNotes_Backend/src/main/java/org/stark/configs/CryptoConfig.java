package org.stark.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.stark.exceptions.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Configuration
public class CryptoConfig {

    @Bean
    public AesUtil aesUtil(AesConfig aesConfig) {
        return new AesUtil(aesConfig.getKey());
    }

    public static class AesUtil {
        private final SecretKeySpec secretKey;
        private final SecureRandom secureRandom = new SecureRandom();

        public AesUtil(String key) {
            if (key.length() != 32) {  // AES-256 requires 32 bytes
                throw new IllegalArgumentException("AES key must be 32 characters for AES-256");
            }
            this.secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        }

        // Encrypt with random IV
        public String encrypt(String data) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

                byte[] iv = new byte[16];
                secureRandom.nextBytes(iv); // random IV
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
                byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

                // Prepend IV to ciphertext
                byte[] combined = new byte[iv.length + encrypted.length];
                System.arraycopy(iv, 0, combined, 0, iv.length);
                System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

                return Base64.getEncoder().encodeToString(combined);
            } catch (Exception e) {
                throw new CryptoException("Failed to encrypt data", e);
            }
        }

        // Decrypt
        public String decrypt(String encryptedData) {
            try {
                byte[] combined = Base64.getDecoder().decode(encryptedData);

                byte[] iv = new byte[16];
                byte[] ciphertext = new byte[combined.length - 16];

                System.arraycopy(combined, 0, iv, 0, 16);
                System.arraycopy(combined, 16, ciphertext, 0, ciphertext.length);

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

                byte[] decrypted = cipher.doFinal(ciphertext);
                return new String(decrypted, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new CryptoException("Failed to decrypt data", e);
            }
        }
    }
}
