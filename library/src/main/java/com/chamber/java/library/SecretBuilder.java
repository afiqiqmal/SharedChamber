package com.chamber.java.library;

import android.content.Context;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.keychain.KeyChain;
import com.zeroone.conceal.BuildConfig;
import com.chamber.java.library.model.ChamberType;

import java.lang.ref.WeakReference;

/**
 * Created by hafiq on 05/02/2018.
 */

public class SecretBuilder {
    private WeakReference<Context> context;
    private KeyChain makeKeyChain;
    private Crypto crypto;
    private ChamberType defaultKeyChain = ChamberType.KEY_256;
    private boolean enableCrypto = true;
    private boolean enableHashKey = true;
    private Entity entityPassword = null;
    private String entityPasswordRaw = BuildConfig.APPLICATION_ID;
    private String folderName;

    public KeyChain getMakeKeyChain() {
        return makeKeyChain;
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public ChamberType getDefaultKeyChain() {
        return defaultKeyChain;
    }

    public boolean isEnableCrypto() {
        return enableCrypto;
    }

    public boolean isEnableHashKey() {
        return enableHashKey;
    }

    public Entity getEntityPassword() {
        return entityPassword;
    }

    public String getEntityPasswordRaw() {
        return entityPasswordRaw;
    }

    public String getFolderName() {
        return folderName;
    }

    public SecretBuilder(Context context) {
        this.context = new WeakReference<>(context.getApplicationContext());
    }

    public SecretBuilder setChamberType(ChamberType config){
        this.defaultKeyChain = config;
        return this;
    }

    public SecretBuilder setEnableValueEncryption(boolean enableCrypto) {
        this.enableCrypto = enableCrypto;
        return this;
    }

    public SecretBuilder setEnableKeyEncryption(boolean enableKeyCrypt) {
        this.enableHashKey = enableKeyCrypt;
        return this;
    }

    public SecretBuilder setPassword(String password){
        if(password!=null) entityPasswordRaw = password;
        return this;
    }

    /***
     * @param folderName - Main Folder to be stored
     * @return - SecretBuilder
     */
    public SecretBuilder setStoredFolder(String folderName){
        this.folderName = folderName;
        return this;
    }

    public SecretChamber buildSecret(){

        if (this.context == null){
            throw new RuntimeException("Context cannot be null");
        }

        entityPassword = Entity.create(CipherUtils.obscureEncodeSixFourString(entityPasswordRaw.getBytes()));
        makeKeyChain = new SharedPrefsBackedKeyChain(this.context.get(), (defaultKeyChain ==null)? ChamberType.KEY_256.getConfig() : defaultKeyChain.getConfig());

        if (defaultKeyChain == null) {
            crypto = AndroidConceal.get().createDefaultCrypto(makeKeyChain);
        } else if (defaultKeyChain == ChamberType.KEY_128) {
            crypto = AndroidConceal.get().createCrypto128Bits(makeKeyChain);
        } else {
            crypto = AndroidConceal.get().createCrypto256Bits(makeKeyChain);
        }

        return new SecretChamber(this);
    }
}
