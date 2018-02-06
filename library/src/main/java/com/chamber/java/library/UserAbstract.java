package com.chamber.java.library;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * Created by hafiq on 28/01/2018.
 */

abstract class UserAbstract<T extends UserAbstract<T>> extends BaseBuilderAbstract {

    UserAbstract(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }

    UserAbstract(@Nullable String keyPrefix, SharedPreferences sharedPreferences) {
        super(keyPrefix, sharedPreferences);
    }

    UserAbstract(@Nullable String keyPrefix, @Nullable String defaultEmptyValue, SharedPreferences sharedPreferences) {
        super(keyPrefix, defaultEmptyValue, sharedPreferences);
    }

    protected abstract T setDefault(@Nullable String defaultEmptyValue);
    protected abstract T setUserDetail(String user_detail);
    protected abstract void applyUserDetail(String user_detail);
    protected abstract T setUserId(String user_id);
    protected abstract void applyUserId(String user_id);
    protected abstract T setUserName(String name);
    protected abstract T setFullName(String fullName);
    protected abstract void applyUserName(String name);
    protected abstract void applyFullName(String fullName);
    protected abstract T setFirstName(String firstName);
    protected abstract T setLastName(String lastName);
    protected abstract T setAge(int age);
    protected abstract T setGender(String gender);
    protected abstract T setBirthDate(String birthDate);
    protected abstract T setAddress(String address);
    protected abstract T setEmail(String email);
    protected abstract T setPushToken(String pushToken);
    protected abstract T setPhoneNumber(String phoneNumber);
    protected abstract T setMobileNumber(String mobileNumber);
    protected abstract T setLogin(boolean login);
    protected abstract T setPassword(String password);
    protected abstract T setFirstTimeUser(boolean firstTimeUser);
    protected abstract T setUserDetail(Object object);

    protected abstract void applyFirstName(String firstName);
    protected abstract void applyLastName(String lastName);
    protected abstract void applyAge(int age);
    protected abstract void applyGender(String gender);
    protected abstract void applyBirthDate(String birthDate);
    protected abstract void applyAddress(String address);
    protected abstract void applyEmail(String email);
    protected abstract void applyPushToken(String pushToken);
    protected abstract void applyPhoneNumber(String phoneNumber);
    protected abstract void applyMobileNumber(String mobileNumber);
    protected abstract void applyLogin(boolean login);
    protected abstract void applyPassword(String password);
    protected abstract void applyFirstTimeUser(boolean firstTimeUser);
    protected abstract void applyUserDetail(Object object);

    protected abstract String getUserId();
    protected abstract String getUserDetail();
    protected abstract Object getUserDetail(Type typeOfT);
    protected abstract Object getUserDetail(Class<Object> classOfT);
    protected abstract String getUserName();
    protected abstract String getFullName();
    protected abstract String getFirstName();
    protected abstract String getLastName();
    protected abstract Integer getAge();
    protected abstract String getGender();
    protected abstract String getBirthDate();
    protected abstract String getAddress();
    protected abstract String getEmail();
    protected abstract String getPushToken();
    protected abstract String getPhoneNumber();
    protected abstract String getMobileNumber();
    protected abstract Boolean hasLogin();
    protected abstract String getPassword();
    protected abstract Boolean isFirstTimeUser();

}
