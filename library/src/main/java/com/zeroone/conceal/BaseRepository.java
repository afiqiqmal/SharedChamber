package com.zeroone.conceal;

import android.content.Context;
import android.content.SharedPreferences;

import com.zeroone.conceal.listener.OnDataChamberChangeListener;

/**
 * Created by hafiq on 28/01/2018.
 */

class BaseRepository {

    Context mContext;
    static String chamberFolderName;
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    static SecretChamber secretChamber;
    static OnDataChamberChangeListener onDataChangeListener;
    static String defaultPrefix = null;

    void throwRunTimeException(String message, Throwable throwable){
        new RuntimeException(message,throwable).printStackTrace();
    }

    String hashKey(String key){
        return secretChamber.hashVault(defaultPrefix+key);
    }

    String hideValue(String value){
        return secretChamber.lockVault(value);
    }

    public Context getmContext() {
        return mContext;
    }

    public static String getChamberFolderName() {
        return chamberFolderName;
    }

    public SharedPreferences getChamber() {
        return sharedPreferences;
    }

    public SharedPreferences.Editor getChamberEditor() {
        return editor;
    }

    public SecretChamber getSecretChamber() {
        return secretChamber;
    }

    public OnDataChamberChangeListener getOnDataChangeListener() {
        return onDataChangeListener;
    }

    public String getDefaultPrefix() {
        return defaultPrefix;
    }
}
