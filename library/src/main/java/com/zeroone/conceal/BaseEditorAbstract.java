package com.zeroone.conceal;

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

    abstract T putString(@NonNull String key, String value);
    abstract void applyString(@NonNull String key, String value);
    abstract T putInt(@NonNull String key, int value);
    abstract void applyInt(@NonNull String key, int value);
    abstract T putLong(@NonNull String key, long value);
    abstract void applyLong(@NonNull String key, long value);
    abstract T putDouble(@NonNull String key, double value);
    abstract void applyDouble(@NonNull String key, double value);
    abstract T putFloat(@NonNull String key, float value);
    abstract void applyFloat(@NonNull String key, float value);
    abstract T putBoolean(@NonNull String key, boolean value);
    abstract void applyBoolean(@NonNull String key, boolean value);
    abstract T putListString(@NonNull String key, List<String> value);
    abstract void applyListString(@NonNull String key, List<String> value);
    abstract T putListFloat(@NonNull String key, List<Float> value);
    abstract void applyListFloat(@NonNull String key, List<Float> value);
    abstract T putListInteger(@NonNull String key, List<Integer> value);
    abstract void applyListInteger(@NonNull String key, List<Integer> value);
    abstract T putListDouble(@NonNull String key, List<Double> value);
    abstract void applyListDouble(@NonNull String key, List<Double> value);
    abstract T putListLong(@NonNull String key, List<Long> value);
    abstract void applyListLong(@NonNull String key, List<Long> value);
    abstract T putListBoolean(@NonNull String key, List<Boolean> value);
    abstract void applyListBoolean(@NonNull String key, List<Boolean> value);
    abstract T putByte(@NonNull String key, byte[] bytes);
    abstract void applyByte(@NonNull String key, byte[] bytes);

    abstract T putImage(@NonNull String key, @DrawableRes int resId, Context context);
    abstract T putImage(@NonNull String key, Bitmap bitmap);
    abstract T putImage(@NonNull String key, File file);
    abstract T putFile(@NonNull String key,File file,boolean deleteOldFile);

    abstract T putMap(@NonNull String key,Map<String,String> values);

    abstract T remove(@NonNull String key);
    abstract T clear();


    void setFolderName(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFolderPath() {
        return folderPath;
    }
}
