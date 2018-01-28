package com.zeroone.conceal;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * Created by hafiq on 28/01/2018.
 */

@SuppressLint("CommitPrefEdits")
abstract class BaseBuilderAbstract {

    private String PREFIX = "conceal";
    private String DEFAULT_VALUE = null;
    private String SEPARATOR = "_";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ConcealCrypto concealCrypto;

    BaseBuilderAbstract(SharedPreferences sharedPreferences) {
        this.PREFIX = null;

        if (sharedPreferences == null){
            throw new IllegalArgumentException("Need to initialize ConcealPrefRepository.PreferencesBuilder first");
        }

        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    BaseBuilderAbstract(@Nullable String keyPrefix, SharedPreferences sharedPreferences) {
        if (keyPrefix != null)
            this.PREFIX = keyPrefix+this.SEPARATOR;

        if (sharedPreferences == null){
            throw new IllegalArgumentException("Need to initialize ConcealPrefRepository.PreferencesBuilder first");
        }

        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();

    }

    BaseBuilderAbstract(@Nullable String keyPrefix, @Nullable String defaultEmptyValue, SharedPreferences sharedPreferences) {
        if (defaultEmptyValue != null) {
            this.DEFAULT_VALUE = defaultEmptyValue;
        }

        if (keyPrefix != null)
            this.PREFIX = keyPrefix+this.SEPARATOR;

        if (sharedPreferences == null){
            throw new IllegalArgumentException("Need to initialize ConcealPrefRepository.PreferencesBuilder first");
        }

        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();

    }

    void setConcealCrypto(ConcealCrypto concealCrypto) {
        this.concealCrypto = concealCrypto;
    }

    void setDefaultValue(String defaultValue) {
        if (defaultValue != null) {
            this.DEFAULT_VALUE = defaultValue;
        }
    }

    SharedPreferences.Editor getEditor() {
        if (editor == null){
            throw new IllegalArgumentException("Need to initialize ConcealPrefRepository.PreferencesBuilder first");
        }

        return editor;
    }


    String returnValue(String KEY){
        String value = concealCrypto.deObscure(sharedPreferences.getString(setHashKey(KEY), null));
        if (value == null)
            return this.DEFAULT_VALUE;

        return value;
    }

    String setHashKey(String key) {
        return concealCrypto.hashKey(PREFIX+key);
    }

    String hideValue(String value) {
        return concealCrypto.obscure(value);
    }

    protected abstract void apply();

    protected abstract void commit();

}
