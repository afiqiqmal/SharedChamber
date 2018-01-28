package com.zeroone.conceal;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.zeroone.conceal.model.CryptoType;

/**
 * Created by hafiq on 28/01/2018.
 */

abstract class BasePreferencesBuilder<T extends BasePreferencesBuilder<T> > {

    private Context mContext;
    private CryptoType mKeyChain = CryptoType.KEY_256;
    private String mPrefname = null;
    private String mFolderName = null;
    private String defaultPrefix = "";
    private boolean mEnabledCrypto = false;
    private boolean mEnableCryptKey = false;
    private String mEntityPasswordRaw = null;
    private SharedPreferences sharedPreferences;
    private OnDataChangeListener onDataChangeListener;

    void setContext(Context mContext) {
        this.mContext = mContext;
    }

    Context getContext() {
        return mContext;
    }

    CryptoType getKeyChain() {
        return mKeyChain;
    }

    String getPrefName() {
        return mPrefname;
    }

    String getFolderName() {
        return mFolderName;
    }

    String getDefaultPrefix() {
        return defaultPrefix;
    }

    void setDefaultPrefix(String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
    }

    boolean isEnabledCrypto() {
        return mEnabledCrypto;
    }

    boolean isEnableCryptKey() {
        return mEnableCryptKey;
    }

    String getEntityPasswordRaw() {
        return mEntityPasswordRaw;
    }

    SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    OnDataChangeListener getOnDataChangeListener() {
        return onDataChangeListener;
    }

    void setKeyChain(CryptoType mKeyChain) {
        this.mKeyChain = mKeyChain;
    }

    void setPrefName(String mPrefname) {
        this.mPrefname = mPrefname;
    }

    void setmFolderName(String mFolderName) {
        this.mFolderName = mFolderName;
    }

    void setEnabledCrypto(boolean mEnabledCrypto) {
        this.mEnabledCrypto = mEnabledCrypto;
    }

    void setEnableCryptKey(boolean mEnableCryptKey) {
        this.mEnableCryptKey = mEnableCryptKey;
    }

    void setEntityPasswordRaw(String mEntityPasswordRaw) {
        this.mEntityPasswordRaw = mEntityPasswordRaw;
    }

    void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        this.onDataChangeListener = onDataChangeListener;
    }

    BasePreferencesBuilder(Context mContext) {
        this.mContext = mContext;
    }

    protected abstract T useThisPrefStorage(String mPrefname);
    protected abstract T enableCrypto(boolean encryptKey,boolean encryptValue);
    protected abstract T enableKeyPrefix(boolean enable, @Nullable String defaultPrefix);
    protected abstract T setPrefListener(OnDataChangeListener listener);
    protected abstract T setFolderName(String folderName);
    protected abstract T createPassword(String password);
    protected abstract T sharedPrefsBackedKeyChain(CryptoType keyChain);

}
