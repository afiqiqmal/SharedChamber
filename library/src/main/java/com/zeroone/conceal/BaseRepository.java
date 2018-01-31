package com.zeroone.conceal;

import android.content.Context;
import android.content.SharedPreferences;

import com.zeroone.conceal.listener.OnDataChangeListener;

/**
 * Created by hafiq on 28/01/2018.
 */

class BaseRepository {

    Context mContext;
    static String mFolderName;
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    static ConcealCrypto concealCrypto;
    static OnDataChangeListener onDataChangeListener;
    static String defaultPrefix = null;

    void throwRunTimeException(String message, Throwable throwable){
        new RuntimeException(message,throwable).printStackTrace();
    }

    String hashKey(String key){
        return concealCrypto.hashKey(defaultPrefix+key);
    }

    String hideValue(String value){
        return concealCrypto.obscure(value);
    }
}
