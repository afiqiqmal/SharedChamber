package com.chamber.java.library;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * Created by hafiq on 28/01/2018.
 */

abstract class DeviceAbstract<T extends DeviceAbstract<T>> extends BaseBuilderAbstract {

    DeviceAbstract(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }

    DeviceAbstract(@Nullable String keyPrefix, SharedPreferences sharedPreferences) {
        super(keyPrefix, sharedPreferences);
    }

    DeviceAbstract(@Nullable String keyPrefix, @Nullable String defaultEmptyValue, SharedPreferences sharedPreferences) {
        super(keyPrefix, defaultEmptyValue, sharedPreferences);
    }

    protected abstract T setDefault(@Nullable String defaultEmptyValue);
    protected abstract T setDeviceId( String deviceId);
    protected abstract void applyDeviceId(String deviceId);
    protected abstract T setDeviceVersion(String version);
    protected abstract void applyDeviceVersion(String version);
    protected abstract T setDeviceIsUpdated(boolean updated);
    protected abstract void applyDeviceIsUpdated(boolean updated);
    protected abstract T setDeviceOS(String os);
    protected abstract void applyDeviceOS(String os);
    protected abstract T setDeviceDetail(Object object);
    protected abstract void applyDeviceDetail(Object object);
    protected abstract Boolean isDeviceUpdate();
    protected abstract String getDeviceId();
    protected abstract String getDeviceVersion();
    protected abstract String getDeviceOs();
    protected abstract Object getDeviceDetail(Type typeOfT);
    protected abstract Object getDeviceDetail(Class<Object> classOfT);

}
