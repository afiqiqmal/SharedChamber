package com.chamber.java.library;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.chamber.java.library.model.Constant.UTF8;

/**
 * @author : hafiq on 29/03/2017.
 */

class CipherUtils {

    static String obscureEncodeSixFourString(byte[] plaintext){
        return Base64.encodeToString(plaintext, Base64.DEFAULT);
    }

    static byte[] obscureEncodeSixFourBytes(byte[] plaintext){
        return Base64.encode(plaintext, Base64.DEFAULT);
    }

    static byte[] deObscureSixFour(String cipher){
        return Base64.decode(cipher, Base64.DEFAULT);
    }

    static byte[] deObscureSixFour(byte[] cipher){
        return Base64.decode(cipher, Base64.DEFAULT);
    }

    static String encode64WithIteration(String plaintext,int iteration){
        try {
            byte[] dataDec = plaintext.getBytes(UTF8);
            for (int x=0;x<iteration;x++){
                dataDec = obscureEncodeSixFourBytes(dataDec);
            }

            return new String(dataDec);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return plaintext;
    }

    static String decode64WithIteration(String plaintext,int iteration){
        try {
            byte[] dataDec = plaintext.getBytes(UTF8);
            for (int x=0;x<iteration;x++){
                dataDec = deObscureSixFour(dataDec);
            }

            return new String(dataDec);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    static String getRandomString(int length) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }

    static String AesCrypt(String key, String iv, String data) {
        try {
            int CIPHER_KEY_LEN = 16;
            if (key.length() < CIPHER_KEY_LEN) {
                int numPad = CIPHER_KEY_LEN - key.length();

                StringBuilder keyBuilder = new StringBuilder(key);
                for(int i = 0; i < numPad; i++){
                    keyBuilder.append("0");
                }
                key = keyBuilder.toString();

            } else if (key.length() > CIPHER_KEY_LEN) {
                key = key.substring(0, CIPHER_KEY_LEN); //truncate to 16 bytes
            }


            IvParameterSpec initVector = new IvParameterSpec(iv.getBytes(UTF8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(UTF8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, initVector);

            byte[] encryptedData = cipher.doFinal((data.getBytes()));

            String base64_EncryptedData = Base64.encodeToString(encryptedData, Base64.DEFAULT);
            String base64_IV = Base64.encodeToString(iv.getBytes(UTF8), Base64.DEFAULT);

            return Base64.encodeToString((base64_EncryptedData + ":" + base64_IV).getBytes(UTF8), Base64.DEFAULT);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    static String AesDecrypt(String key, String data) {
        try {

            int CIPHER_KEY_LEN = 16;
            if (key.length() < CIPHER_KEY_LEN) {
                int numPad = CIPHER_KEY_LEN - key.length();

                StringBuilder keyBuilder = new StringBuilder(key);
                for(int i = 0; i < numPad; i++){
                    keyBuilder.append("0");
                }
                key = keyBuilder.toString();

            } else if (key.length() > CIPHER_KEY_LEN) {
                key = key.substring(0, CIPHER_KEY_LEN); //truncate to 16 bytes
            }

            String[] parts = new String(Base64.decode(data, Base64.DEFAULT)).split(":");

            IvParameterSpec iv = new IvParameterSpec(Base64.decode(parts[1], Base64.DEFAULT));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(UTF8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] decodedEncryptedData = Base64.decode(parts[0], Base64.DEFAULT);

            byte[] original = cipher.doFinal(decodedEncryptedData);

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
