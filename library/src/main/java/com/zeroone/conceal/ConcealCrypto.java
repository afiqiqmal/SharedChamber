package com.zeroone.conceal;

import android.content.Context;
import android.util.Base64;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.keychain.KeyChain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author : hafiq on 23/03/2017.
 */

public class ConcealCrypto {

    private Crypto crypto;
    private KeyChain keyChain;
    private Entity mEntityPassword = Entity.create(BuildConfig.APPLICATION_ID);
    private boolean enableCrypto=true;
    private boolean enableKeyCrypt=true;

    public ConcealCrypto(CryptoBuilder builder){
        crypto = builder.crypto;
        mEntityPassword = builder.mEntityPassword;
        enableCrypto = builder.mEnabledCrypto;
        enableKeyCrypt = builder.mEnableCryptKey;
    }

    public ConcealCrypto(Context context,CryptoConfig config){
        keyChain = new SharedPrefsBackedKeyChain(context,config==null?CryptoConfig.KEY_256:config);
        crypto = AndroidConceal.get().createDefaultCrypto(keyChain);
    }

    public void setEntityPassword(Entity mEntityPassword) {
        if (mEntityPassword!=null)this.mEntityPassword = mEntityPassword;
    }
    public void setEntityPassword(String password) {
        if (password!=null)this.mEntityPassword = Entity.create(Base64.encodeToString(password.getBytes(),Base64.DEFAULT));
    }

    public void setEnableCrypto(boolean enableCrypto) {
        this.enableCrypto = enableCrypto;
    }

    public void setEnableKeyCrypto(boolean enableKeyCrypt) {
        this.enableKeyCrypt = enableKeyCrypt;
    }

    public void clearCrypto(){
        if (crypto.isAvailable()){
            keyChain.destroyKeys();
        }
    }


    //Encrypt
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

    //Decrypt
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

    //SHA-256 hash key Message Digest
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

    public static class CryptoBuilder{
        Context context;
        private KeyChain makeKeyChain;
        private Crypto crypto;
        private CryptoConfig mKeyChain = CryptoConfig.KEY_256;
        private boolean mEnabledCrypto = false;
        private boolean mEnableCryptKey = false;
        private Entity mEntityPassword = null;
        private String mEntityPasswordRaw = BuildConfig.APPLICATION_ID;

        public CryptoBuilder(Context context) {
            this.context = context;
        }

        public CryptoBuilder setKeyChain(CryptoConfig config){
            this.mKeyChain = config;
            return this;
        }

        public CryptoBuilder setEnableCrypto(boolean enableCrypto) {
            this.mEnabledCrypto = enableCrypto;
            return this;
        }

        public CryptoBuilder setEnableKeyCrypto(boolean enableKeyCrypt) {
            this.mEnableCryptKey = enableKeyCrypt;
            return this;
        }

        public CryptoBuilder createPassword(String password){
            if(password!=null)mEntityPasswordRaw = password;
            return this;
        }

        public ConcealCrypto create(){
            mEntityPassword = Entity.create(Base64.encodeToString(mEntityPasswordRaw.getBytes(),Base64.DEFAULT));
            makeKeyChain = new SharedPrefsBackedKeyChain(context,(mKeyChain==null)?CryptoConfig.KEY_256:mKeyChain);

            if (mKeyChain == null) {
                crypto = AndroidConceal.get().createDefaultCrypto(makeKeyChain);
            } else if (mKeyChain == CryptoConfig.KEY_128) {
                crypto = AndroidConceal.get().createCrypto128Bits(makeKeyChain);
            } else {
                crypto = AndroidConceal.get().createCrypto256Bits(makeKeyChain);
            }

            return new ConcealCrypto(this);
        }
    }

}
