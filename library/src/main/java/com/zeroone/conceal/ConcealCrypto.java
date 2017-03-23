package com.zeroone.conceal;

import android.util.Base64;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author : hafiq on 23/03/2017.
 */

public class ConcealCrypto {

    private Crypto crypto;
    private Entity mEntityPassword;
    private boolean enableCrypto=false;
    private boolean enableKeyCrypt = false;

    public ConcealCrypto(Crypto crypto,Entity password) {
        this.crypto = crypto;
        this.mEntityPassword = password;
    }

    public void setEnableCrypto(boolean enableCrypto) {
        this.enableCrypto = enableCrypto;
    }

    public void setEnableKeyCrypt(boolean enableKeyCrypt) {
        this.enableKeyCrypt = enableKeyCrypt;
    }

    public String obscure(String plain){
        if (enableCrypto) {
            try {
                byte[] a = crypto.encrypt(plain.getBytes(), mEntityPassword);
                return Base64.encodeToString(a, Base64.DEFAULT);
            } catch (Exception e) {
                return null;
            }
        }
        else {
            return plain;
        }
    }

    public String deObscure(String cipher){
        if (enableCrypto) {
            try {
                return new String(crypto.decrypt(Base64.decode(cipher, Base64.DEFAULT), mEntityPassword));
            } catch (Exception e) {
                return null;
            }
        }
        else{
            return cipher;
        }
    }

    public String hashKey(String key) {
        if (enableKeyCrypt) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] array = md.digest(key.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte anArray : array) {
                    sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        return key;
    }

}
