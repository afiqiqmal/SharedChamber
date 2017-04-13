package com.zeroone.conceal;

import android.Manifest;
import android.content.Context;
import android.support.annotation.RequiresPermission;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.KeyChain;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.zeroone.conceal.model.Constant.DEFAULT_DIRECTORY;
import static com.zeroone.conceal.model.Constant.DEFAULT_FILES_FOLDER;
import static com.zeroone.conceal.model.Constant.DEFAULT_IMAGE_FOLDER;
import static com.zeroone.conceal.model.Constant.DEFAULT_MAIN_FOLDER;
import static com.zeroone.conceal.model.Constant.DEFAULT_PREFIX_FILENAME;

/**
 * @author : hafiq on 23/03/2017.
 */

public class ConcealCrypto {

    private Crypto crypto;
    private KeyChain keyChain;
    private Entity mEntityPassword = Entity.create(BuildConfig.APPLICATION_ID);
    private boolean enableCrypto=true;
    private boolean enableHashKey =true;
    private String MAIN_DIRECTORY;

    private ConcealCrypto(CryptoBuilder builder){
        crypto = builder.crypto;
        mEntityPassword = builder.mEntityPassword;
        enableCrypto = builder.mEnabledCrypto;
        enableHashKey = builder.mEnableHashKey;
        MAIN_DIRECTORY = builder.mFolderName;

        if (MAIN_DIRECTORY == null) MAIN_DIRECTORY = DEFAULT_MAIN_FOLDER;
    }

    public ConcealCrypto(Context context,CryptoConfig config){
        keyChain = new SharedPrefsBackedKeyChain(context,config==null?CryptoConfig.KEY_256:config);
        crypto = AndroidConceal.get().createDefaultCrypto(keyChain);
    }

    public void setEntityPassword(Entity mEntityPassword) {
        if (mEntityPassword!=null)this.mEntityPassword = mEntityPassword;
    }
    public void setEntityPassword(String password) {
        if (password!=null)this.mEntityPassword = Entity.create(CipherUtils.obscureEncodeSixFourString(password.getBytes()));
    }

    public void setEnableCrypto(boolean enableCrypto) {
        this.enableCrypto = enableCrypto;
    }

    public void setEnableKeyCrypto(boolean enableKeyCrypt) {
        this.enableHashKey = enableKeyCrypt;
    }

    public Crypto getCrypto(){
        return crypto;
    }

    private String makeDirectory(){
        if (MAIN_DIRECTORY == null) MAIN_DIRECTORY = DEFAULT_MAIN_FOLDER;

        return DEFAULT_DIRECTORY+ MAIN_DIRECTORY +"/";
    }

    private String makeFileDirectory(){
        return makeDirectory()+DEFAULT_FILES_FOLDER;
    }

    private String makeImagesDirectory(){
        return makeDirectory()+DEFAULT_IMAGE_FOLDER;
    }

    public void clearCrypto(){
        if (crypto.isAvailable()){
            keyChain.destroyKeys();
        }
    }


    /***
     * Encryption of plaintext
     * @param plain - plaintext of string to be encrypt
     * @return String
     */
    public String obscure(String plain){
        if (plain == null)
            return null;

        if (enableCrypto) {
            try {
                byte[] a = crypto.encrypt(plain.getBytes(), mEntityPassword);
                return CipherUtils.obscureEncodeSixFourString(a);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            return plain;
        }
    }

    /***
     * Encryption of bytes
     * @param bytes - array bytes to be encrypt
     * @return bytes
     */
    public byte[] obscure(byte[] bytes){
        if (bytes == null)
            return null;

        if (enableCrypto) {
            try {
                byte[] a = crypto.encrypt(bytes, mEntityPassword);
                return CipherUtils.obscureEncodeSixFourBytes(a);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            return bytes;
        }
    }

    //encrypt files
    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public File obscureFile(File file,boolean deleteOldFile){
        if (enableCrypto) {
            try {
                boolean isImage = FileUtils.isFileForImage(file);

                File mEncryptedFile = new File(makeDirectory()+DEFAULT_PREFIX_FILENAME+file.getName());
                OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(mEncryptedFile));
                OutputStream outputStream = crypto.getCipherOutputStream(fileStream, mEntityPassword);

                int read;
                byte[] buffer = new byte[1024];
                BufferedInputStream  bis = new BufferedInputStream(new FileInputStream(file));
                while ((read = bis.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.close();
                bis.close();

                if (deleteOldFile)
                    file.delete();

                File pathDir = new File(isImage?makeImagesDirectory():makeFileDirectory());
                return FileUtils.moveFile(mEncryptedFile,pathDir);

            } catch (KeyChainException | CryptoInitializationException | IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            return file;
        }
    }

    /**
     * Decryption string
     * @param cipher cipher string
     * @return String plaintext
     */
    public String deObscure(String cipher){
        if (cipher == null)
            return null;

        if (enableCrypto) {
            try {
                return new String(crypto.decrypt(CipherUtils.deObscureSixFour(cipher), mEntityPassword));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else{
            return cipher;
        }
    }

    /**
     * Decryption bytes
     * @param cipher cipher bytes[]
     * @return String plaintext
     */
    public byte[] deObscure(byte[] cipher){
        if (cipher == null)
            return null;

        if (enableCrypto) {
            try {
                return crypto.decrypt(CipherUtils.deObscureSixFour(cipher), mEntityPassword);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else{
            return cipher;
        }
    }

    //decrypt file
    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public File deObscureFile(File file,boolean deleteOldFile){
        if (enableCrypto) {
            try {
                if (file.getName().contains(DEFAULT_PREFIX_FILENAME)) {

                    boolean isImage = FileUtils.isFileForImage(file);

                    File mDecryptedFile = new File(makeDirectory() + file.getName().replace(DEFAULT_PREFIX_FILENAME,""));

                    InputStream inputStream = crypto.getCipherInputStream(new FileInputStream(file), mEntityPassword);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    OutputStream outputStream = new FileOutputStream(mDecryptedFile);
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    int mRead;
                    byte[] mBuffer = new byte[1024];
                    while ((mRead = bis.read(mBuffer)) != -1) {
                        outputStream.write(mBuffer, 0, mRead);
                    }
                    bis.close();
                    out.writeTo(outputStream);
                    inputStream.close();
                    outputStream.close();
                    out.close();

                    if (deleteOldFile)
                        file.delete();

                    File pathDir = new File(isImage?makeImagesDirectory():makeFileDirectory());
                    return FileUtils.moveFile(mDecryptedFile, pathDir);
                }

                return null;

            } catch (KeyChainException | CryptoInitializationException | IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return file;
    }

    /***
     * hashing sharedpref key
     * @param key - key
     * @return - hash key
     */
    public String hashKey(String key) {
        if (key == null || key.equals(""))
            throw new NullPointerException("Key cannot be null or empty");

        if (enableHashKey) {
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


    /***
     * encrypt base64 with iteration Conceal
     */

    public String obscureWithIteration(String plainText,int iteration){
        String cipher = plainText;
        for (int x=0;x<iteration;x++){
            cipher = obscure(cipher);
        }

        return cipher;
    }

    public String deObscureWithIteration(String cipher,int iteration){
        String plainText = cipher;
        for (int x=0;x<iteration;x++){
            plainText = deObscure(plainText);
        }

        return plainText;
    }

    public static class CryptoBuilder{
        Context context;
        private KeyChain makeKeyChain;
        private Crypto crypto;
        private CryptoConfig mKeyChain = CryptoConfig.KEY_256;
        private boolean mEnabledCrypto = false;
        private boolean mEnableHashKey = false;
        private Entity mEntityPassword = null;
        private String mEntityPasswordRaw = BuildConfig.APPLICATION_ID;
        private String mFolderName;

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
            this.mEnableHashKey = enableKeyCrypt;
            return this;
        }

        public CryptoBuilder createPassword(String password){
            if(password!=null)mEntityPasswordRaw = password;
            return this;
        }

        /***
         * @param folderName - Main Folder to be stored
         * @return - CryptoBuilder
         */
        public CryptoBuilder setStoredFolder(String folderName){
            mFolderName = (folderName!=null)?folderName:null;
            return this;
        }

        public ConcealCrypto create(){

            mEntityPassword = Entity.create(CipherUtils.obscureEncodeSixFourString(mEntityPasswordRaw.getBytes()));
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
