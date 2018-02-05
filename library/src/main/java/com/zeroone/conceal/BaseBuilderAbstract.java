package com.zeroone.conceal;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * Created by hafiq on 28/01/2018.
 */

@SuppressLint("CommitPrefEdits")
abstract class BaseBuilderAbstract {

    private String defaultPrefix = "";
    private String DEFAULT_VALUE = null;
    private String SEPARATOR = "_";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SecretChamber secretChamber;

    BaseBuilderAbstract(SharedPreferences sharedPreferences) {
        this.defaultPrefix = null;

        if (sharedPreferences == null){
            throw new IllegalArgumentException("Need to initialize SharedChamber.ChamberBuilder first");
        }

        this.sharedPreferences = sharedPreferences;
        this.editor = this.sharedPreferences.edit();
    }

    BaseBuilderAbstract(@Nullable String keyPrefix, SharedPreferences sharedPreferences) {
        if (keyPrefix != null)
            this.defaultPrefix = keyPrefix+this.SEPARATOR;

        if (sharedPreferences == null){
            throw new IllegalArgumentException("Need to initialize SharedChamber.ChamberBuilder first");
        }

        this.sharedPreferences = sharedPreferences;
        this.editor = this.sharedPreferences.edit();

    }

    BaseBuilderAbstract(@Nullable String keyPrefix, @Nullable String defaultEmptyValue, SharedPreferences sharedPreferences) {
        if (defaultEmptyValue != null) {
            this.DEFAULT_VALUE = defaultEmptyValue;
        }

        if (keyPrefix != null)
            this.defaultPrefix = keyPrefix+this.SEPARATOR;

        if (sharedPreferences == null){
            throw new IllegalArgumentException("Need to initialize SharedChamber.ChamberBuilder first");
        }

        this.sharedPreferences = sharedPreferences;
        this.editor = this.sharedPreferences.edit();

    }

    void setSecretChamber(SecretChamber secretChamber) {
        this.secretChamber = secretChamber;
    }

    SecretChamber getSecretChamber() {
        return this.secretChamber;
    }

    void setDefaultPrefix(String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
    }

    void setDefaultValue(String defaultValue) {
        if (defaultValue != null) {
            this.DEFAULT_VALUE = defaultValue;
        }
    }

    SharedPreferences.Editor getEditor() {
        if (this.editor == null){
            throw new IllegalArgumentException("Need to initialize SharedChamber.ChamberBuilder first");
        }

        return this.editor;
    }

    public void setEditor(SharedPreferences.Editor editor) {
        this.editor = editor;
    }

    String returnValue(String KEY){
        String value = this.secretChamber.openVault(this.sharedPreferences.getString(setHashKey(KEY), null));
        if (value == null)
            return this.DEFAULT_VALUE;

        return value;
    }

    String setHashKey(String key) {
        return this.secretChamber.hashVault(defaultPrefix +key);
    }

    String hideValue(String value) {
        return this.secretChamber.lockVault(value);
    }

    public void apply() {
        getEditor().apply();
    }

    public void commit() {
        getEditor().commit();
    }
}
