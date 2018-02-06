package com.chamber.java.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by hafiq on 28/01/2018.
 */

public abstract class BaseEditorAbstract<T extends BaseEditorAbstract<T>> extends BaseBuilderAbstract {

    private String folderPath = null;

    BaseEditorAbstract(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }

    BaseEditorAbstract(@Nullable String keyPrefix, SharedPreferences sharedPreferences) {
        super(keyPrefix, sharedPreferences);
    }

    BaseEditorAbstract(@Nullable String keyPrefix, @Nullable String defaultEmptyValue, SharedPreferences sharedPreferences) {
        super(keyPrefix, defaultEmptyValue, sharedPreferences);
    }

    abstract T put(@NonNull String key, String value);
    abstract void apply(@NonNull String key, String value);
    abstract T put(@NonNull String key, int value);
    abstract void apply(@NonNull String key, int value);
    abstract T put(@NonNull String key, long value);
    abstract void apply(@NonNull String key, long value);
    abstract T put(@NonNull String key, double value);
    abstract void apply(@NonNull String key, double value);
    abstract T put(@NonNull String key, float value);
    abstract void apply(@NonNull String key, float value);
    abstract T put(@NonNull String key, boolean value);
    abstract void apply(@NonNull String key, boolean value);
    abstract T put(@NonNull String key, List<?> value);
    abstract void apply(@NonNull String key, List<?> value);
    abstract T put(@NonNull String key, byte[] bytes);
    abstract void apply(@NonNull String key, byte[] bytes);
    abstract T putModel(@NonNull String key, Object object);
    abstract void applyModel(@NonNull String key, Object object);

    abstract T putDrawable(@NonNull String key, @DrawableRes int resId, Context context);
    abstract void applyDrawable(@NonNull String key, @DrawableRes int resId, Context context);
    abstract T put(@NonNull String key, Bitmap bitmap);
    abstract void apply(@NonNull String key, Bitmap bitmap);
    abstract T put(@NonNull String key, File file);
    abstract void apply(@NonNull String key, File file);
    abstract T put(@NonNull String key,File file,boolean deleteOldFile);
    abstract void apply(@NonNull String key,File file,boolean deleteOldFile);

    abstract T put(@NonNull String key,Map<String,String> values);
    abstract void apply(@NonNull String key,Map<String,String> values);

    abstract T remove(@NonNull String key);
    abstract T clear();


    void setFolderName(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFolderPath() {
        return folderPath;
    }
}
