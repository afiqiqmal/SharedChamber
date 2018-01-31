package com.zeroone.conceal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;

import com.facebook.soloader.SoLoader;
import com.google.gson.Gson;
import com.zeroone.conceal.listener.OnDataChangeListener;
import com.zeroone.conceal.model.Constant;
import com.zeroone.conceal.model.CryptoFile;
import com.zeroone.conceal.model.CryptoType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.zeroone.conceal.FileUtils.getDirectory;
import static com.zeroone.conceal.FileUtils.getImageDirectory;
import static com.zeroone.conceal.FileUtils.getListFiles;
import static com.zeroone.conceal.model.Constant.*;

/**
 * @author : hafiq on 23/03/2017.
 */
@SuppressWarnings({"unused", "unchecked"})
public class ConcealPrefRepository<T> extends BaseRepository {

    @SuppressLint("CommitPrefEdits")
    private ConcealPrefRepository(@NonNull PreferencesBuilder builder){
        mContext = builder.getContext();
        mFolderName = builder.getFolderName();
        sharedPreferences = builder.getSharedPreferences();
        onDataChangeListener = builder.getOnDataChangeListener();
        defaultPrefix = (builder.getDefaultPrefix() == null ? "" : builder.getDefaultPrefix());

        CryptoType mKeyChain = builder.getKeyChain();
        boolean mEnabledCrypto = builder.isEnabledCrypto();
        boolean mEnableCryptKey = builder.isEnableCryptKey();
        String mEntityPasswordRaw = builder.getEntityPasswordRaw();

        //init editor
        editor = sharedPreferences.edit();

        //init crypto
        concealCrypto = new ConcealCrypto.CryptoBuilder(mContext)
                .createPassword(mEntityPasswordRaw)
                .setKeyChain(mKeyChain)
                .setEnableValueEncryption(mEnabledCrypto)
                .setEnableKeyEncryption(mEnableCryptKey)
                .setStoredFolder(mFolderName)
                .create();

        //init listener if set
        if (onDataChangeListener!=null) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    onDataChangeListener.onDataChange(key, sharedPreferences.getString(key,""));
                }
            });
        }
    }

    /***
     * Since Conceal facebook v2.0.+ (2017-06-27) you will need to initialize the native library loader.
     * This step is needed because the library loader uses the context.
     * The highly suggested way to do it is in the application class onCreate method like this:
     * @param application - Application Context ex: this
     */

    public static void applicationInit(Application application){
        SoLoader.init(application, false);
    }

    /**********************
     * DESTROY FILES
     **********************/
    public void destroyCrypto(){
        concealCrypto.clearCrypto();
    }

    public void clearPrefs(){
        try {
            editor.clear().apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*******************************
     * GET SHAREDPREFERENCES TOTAL
     *******************************/
    public int getPrefsSize(){
        return getPreferences().getAll().size();
    }

    /*******************************
     * GET Current ConcealCrypto
     *******************************/
    public ConcealCrypto getCrypto(){
        return concealCrypto;
    }


    /*******************************
     * REMOVING KEYS
     *******************************/
    /* Remove by Key */
    public void remove(@NonNull String... keys){
        for (String key:keys){
            editor.remove(hashKey(key));
        }
        editor.apply();
    }

    public void remove(@NonNull String key) {
        editor.remove(hashKey(key)).apply();
    }

    /**
     * special cases for file to remove by key
     * @param key preferences key
     * @return boolean
     */
    public boolean removeFile(@NonNull String key){
        String path = getString(key);
        if (path != null) {
            File imagePath = new File(path);
            if (imagePath.exists()) {
                if (!imagePath.delete()) {
                    return false;
                }
                remove(key);
            }
            return true;
        }
        return false;
    }


    /**
     * get all encrypted file in created folder
     * @return @CryptoFile
     */
    public List<CryptoFile> getAllConcealEncryptedFiles(){
        return getListFiles(getDirectory(mFolderName));
    }

    /**
     * get list of key and values inside sharedPreferences
     * @return Map
     */
    public Map<String,String> getAllSharedPrefData(){
        Map<String,?> keys = getPreferences().getAll();
        Map<String,String> data = new HashMap<>();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            try {
                data.put(entry.getKey(), entry.getValue().toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return data;
    }

    public List<String> getAllSharedPrefDataToString(){
        Map<String,?> keys = getPreferences().getAll();
        List<String> data = new ArrayList<>();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            try {
                data.add("["+entry.getKey()+"] : "+ entry.getValue().toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return data;
    }


    /**
     * get SharedPreferences
     * @return SharedPreferences
     */
    public SharedPreferences getPreferences(){
        return sharedPreferences;
    }


    /**
     * check whether value is existed or not
     * @param key - key string
     * @return - value
     */
    public boolean contains(@NonNull String key){
        return sharedPreferences.contains(hashKey(key));
    }

    /* Save Data */

    public void put(@NonNull String key, String value) {
        editor.putString(hashKey(key), hideValue(value)).apply();
    }

    public void put(@NonNull String key, int value) {
        put(key, Integer.toString(value));
    }

    public void put(@NonNull String key, long value) {
        put(key, Long.toString(value));
    }

    public void put(@NonNull String key, double value) {
        put(key, Double.toString(value));
    }

    public void put(@NonNull String key, float value) {
        put(key, Float.toString(value));
    }

    public void put(@NonNull String key, boolean value) {
        put(key, Boolean.toString(value));
    }

    public void put(@NonNull String key, @NonNull List<?> value){
        put(key, value.toString());
    }

    public void put(@NonNull String key, Map<String,String> values){
        put(key, ConverterListUtils.convertMapToString(values));
    }

    public void put(@NonNull String key,byte[] bytes){
        put(key,new String(bytes));
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public String put(@NonNull String key, Bitmap bitmap){
        File imageFile = new File(getImageDirectory(mFolderName),"images_"+System.currentTimeMillis()+".png");
        if(FileUtils.saveBitmap(imageFile, bitmap)){
            concealCrypto.obscureFile(imageFile,true);
            editor.putString(key,imageFile.getAbsolutePath()).apply();
            return imageFile.getAbsolutePath();
        }
        return null;
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public String put(@NonNull String key, @Nullable File file){
        if (FileUtils.isFileForImage(file)) {
            File imageFile = FileUtils.moveFile(file,getImageDirectory(mFolderName));
            if (imageFile!=null && imageFile.exists()) {
                concealCrypto.obscureFile(imageFile,true);
                put(key, imageFile.getAbsolutePath());
                return imageFile.getAbsolutePath();
            }
        }
        return null;
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public File put(@NonNull String key, @Nullable File file, boolean deleteOldFile){

        if (file == null)
            return null;

        try {
            if (file.exists() && !FileUtils.isFileForImage(file)) {
                File enc = concealCrypto.obscureFile(file,deleteOldFile);
                put(key, enc.getAbsolutePath());
                return enc;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public String putDrawable(@NonNull String key, @DrawableRes int resId){
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
        if (bitmap!=null) {
            File imageFile = new File(getImageDirectory(mFolderName), "images_" + System.currentTimeMillis() + ".png");
            if (FileUtils.saveBitmap(imageFile, bitmap)) {
                concealCrypto.obscureFile(imageFile, true);
                put(key, imageFile.getAbsolutePath());
                return imageFile.getAbsolutePath();
            }
        }
        else{
            throw new IllegalArgumentException(resId+" : Drawable not found!");
        }

        return null;
    }

    // use for objects with GSON
    public void putModel(@NonNull String key, Object object) {
        put(key, new Gson().toJson(object));
    }


    /************************************
     * FETCHING DATA FROM SHAREDPREFS
     ************************************/
    @CheckResult
    public String getString(@NonNull String key){
        return concealCrypto.deObscure(sharedPreferences.getString(hashKey(key),null));
    }

    @CheckResult
    public String getString(@NonNull String key,String defaultValue){
        return concealCrypto.deObscure(sharedPreferences.getString(hashKey(key),defaultValue));
    }

    @CheckResult
    public Object getModel(@NonNull String key, Type typeOfT) {
        String value = getString(key);
        return new Gson().fromJson(value, typeOfT);
    }

    @CheckResult
    public Object getModel(@NonNull String key, Class<Object> classOfT) {
        String value = getString(key);
        return new Gson().fromJson(value, classOfT);
    }

    @CheckResult
    public Integer getInt(@NonNull String key){
        try {
            String value = getString(key);
            if (value == null)
                return -99;

            return Integer.parseInt(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Integer data type",e);
            return -99;
        }
    }

    @CheckResult
    public Integer getInt(@NonNull String key,int defaultValue){
        try {
            String value = getString(key);

            if (value == null)
                return defaultValue;

            return Integer.parseInt(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Integer data type",e);
            return -99;
        }
    }

    @CheckResult
    public Float getFloat(@NonNull String key){
        try {
            String value = getString(key);
            if (value == null)
                return 0f;

            return Float.parseFloat(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Float data type",e);
            return 0f;
        }
    }

    @CheckResult
    public Float getFloat(@NonNull String key,float defaultValue){
        try {
            String value = getString(key);

            if (value == null)
                return defaultValue;

            return Float.parseFloat(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Float data type",e);
            return defaultValue;
        }
    }

    @CheckResult
    public Double getDouble(@NonNull String key){
        try {
            String value = getString(key);
            if (value == null)
                return 0D;

            return Double.parseDouble(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Double data type",e);
            return 0D;
        }
    }

    @CheckResult
    public Double getDouble(@NonNull String key,double defaultValue){
        try {
            String value = getString(key);

            if (value == null)
                return defaultValue;

            return Double.parseDouble(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Double data type",e);
            return defaultValue;
        }
    }

    @CheckResult
    public Long getLong(@NonNull String key){
        try {
            String value = getString(key);
            if (value == null)
                return 0L;

            return Long.parseLong(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Long data type",e);
            return 0L;
        }
    }

    @CheckResult
    public Long getLong(@NonNull String key,long defaultValue){
        try {
            String value = getString(key);

            if (value == null)
                return defaultValue;

            return Long.parseLong(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Long data type",e);
            return defaultValue;
        }
    }

    @CheckResult
    public Boolean getBoolean(@NonNull String key){
        try {
            String value = getString(key);
            return value != null && Boolean.parseBoolean(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Boolean data type",e);
            return false;
        }
    }

    @CheckResult
    public Boolean getBoolean(@NonNull String key,boolean defaultValue){
        try {
            String value = getString(key);
            if (value == null)
                return defaultValue;

            return Boolean.parseBoolean(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Boolean data type",e);
            return false;
        }
    }

    @CheckResult
    public List<String> getListString(@NonNull String key){
        return ConverterListUtils.toStringArray(getString(key));
    }

    @CheckResult
    public List<Float> getListFloat(@NonNull String key){
        return ConverterListUtils.toFloatArray(getString(key));
    }

    @CheckResult
    public List<Double> getListDouble(@NonNull String key){
        return ConverterListUtils.toDoubleArray(getString(key));
    }

    @CheckResult
    public List<Boolean> getListBoolean(@NonNull String key){
        return ConverterListUtils.toBooleanArray(getString(key));
    }

    @CheckResult
    public List<Integer> getListInteger(@NonNull String key){
        return ConverterListUtils.toIntArray(getString(key));
    }

    @CheckResult
    public List<Long> getListLong(@NonNull String key){
        return ConverterListUtils.toLongArray(getString(key));
    }

    @CheckResult
    public LinkedHashMap<String,String> getMap(@NonNull String key){
        return ConverterListUtils.convertStringToMap(getString(key));
    }

    @CheckResult
    public byte[] getArrayBytes(@NonNull String key){
        return getString(key).getBytes();
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    @CheckResult
    public Bitmap getImage(@NonNull String key){
        String path = getString(key);
        if (path !=null) {
            try {
                File file = new File(path);
                return BitmapFactory.decodeFile(concealCrypto.deObscureFile(file,true).getAbsolutePath());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    @CheckResult
    public File getFile(@NonNull String key,boolean deleteOldFile){
        try {
            String path = getString(key);
            if (path ==null) return null;

            File getFile = new File(path);
            if (getFile.exists()) {
                File dec = concealCrypto.deObscureFile(getFile,deleteOldFile);
                if (dec == null)
                    throw new Exception("File can't decrypt");

                return dec;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static class DevicePref extends DeviceAbstract<DevicePref>{

        public DevicePref() {
            super(sharedPreferences);
            setConcealCrypto(concealCrypto);
        }

        public DevicePref(@Nullable String keyPrefix) {
            super(keyPrefix, sharedPreferences);
            if (keyPrefix == null) {
                setDefaultPrefix(defaultPrefix);
            }
            setConcealCrypto(concealCrypto);
        }

        public DevicePref(@Nullable String keyPrefix, @Nullable String defaultEmptyValue) {
            super(keyPrefix, defaultEmptyValue, sharedPreferences);
            if (keyPrefix == null) {
                setDefaultPrefix(defaultPrefix);
            }
            setConcealCrypto(concealCrypto);
        }

        @Override
        public DevicePref setDefault(@Nullable String defaultEmptyValue){
            setDefaultValue(defaultEmptyValue);
            return this;
        }

        @Override
        public DevicePref setDeviceId(String deviceId){
            getEditor().putString(setHashKey(DEVICE_ID),hideValue(deviceId));
            return this;
        }
        @Override
        public void applyDeviceId(String deviceId){
            getEditor().putString(setHashKey(DEVICE_ID),hideValue(deviceId)).apply();
        }
        @Override
        public DevicePref setDeviceVersion(String version){
            getEditor().putString(setHashKey(DEVICE_VERSION), hideValue(version));
            return this;
        }
        @Override
        public void applyDeviceVersion(String version){
            getEditor().putString(setHashKey(DEVICE_VERSION), hideValue(version)).apply();
        }
        @Override
        public DevicePref setDeviceIsUpdated(boolean updated){
            getEditor().putString(setHashKey(DEVICE_IS_UPDATE), hideValue(String.valueOf(updated)));
            return this;
        }
        @Override
        public void applyDeviceIsUpdated(boolean updated){
            getEditor().putString(setHashKey(DEVICE_IS_UPDATE), hideValue(String.valueOf(updated))).apply();
        }
        @Override
        public DevicePref setDeviceOS(String os){
            getEditor().putString(setHashKey(DEVICE_OS), hideValue(String.valueOf(os)));
            return this;
        }
        @Override
        public void applyDeviceOS(String os){
            getEditor().putString(setHashKey(DEVICE_OS), hideValue(String.valueOf(os))).apply();
        }
        @Override
        public DevicePref setDeviceDetail(Object object) {
            getEditor().putString(setHashKey(DEVICE_DETAIL), hideValue(new Gson().toJson(object)));
            return this;
        }
        @Override
        public void applyDeviceDetail(Object object) {
            getEditor().putString(setHashKey(DEVICE_DETAIL), hideValue(new Gson().toJson(object))).apply();
        }

        @Override
        public Boolean isDeviceUpdate(){
            try {
                return Boolean.parseBoolean(returnValue(DEVICE_IS_UPDATE));
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        public String getDeviceId(){
            return returnValue(DEVICE_ID);
        }
        @Override
        public String getDeviceVersion(){
            return returnValue(DEVICE_VERSION);
        }
        @Override
        public String getDeviceOs(){
            return returnValue(DEVICE_OS);
        }
        @Override
        public Object getDeviceDetail(Type typeOfT) {
            String value = returnValue(DEVICE_DETAIL);
            return new Gson().fromJson(value, typeOfT);
        }
        @Override
        public Object getDeviceDetail(Class<Object> classOfT) {
            String value = returnValue(DEVICE_DETAIL);
            return new Gson().fromJson(value, classOfT);
        }
    }

    public static class UserPref extends UserAbstract<UserPref> {

        public UserPref() {
            super(sharedPreferences);
            setConcealCrypto(concealCrypto);
        }

        public UserPref(@Nullable String keyPrefix) {
            super(keyPrefix, sharedPreferences);
            if (keyPrefix == null) {
                setDefaultPrefix(defaultPrefix);
            }
            setConcealCrypto(concealCrypto);
        }

        public UserPref(@Nullable String keyPrefix, @Nullable String defaultEmptyValue) {
            super(keyPrefix, defaultEmptyValue, sharedPreferences);
            if (keyPrefix == null) {
                setDefaultPrefix(defaultPrefix);
            }
            setConcealCrypto(concealCrypto);
        }

        @Override
        public UserPref setDefault(@Nullable String defaultEmptyValue){
            setDefaultValue(defaultEmptyValue);
            return this;
        }

        @Override
        public UserPref setUserDetail(String user_detail){
            getEditor().putString(setHashKey(USER_JSON),hideValue(user_detail));
            return this;
        }

        @Override
        public void applyUserDetail(String user_detail){
            getEditor().putString(setHashKey(USER_JSON),hideValue(user_detail)).apply();
        }

        @Override
        public UserPref setUserId(String user_id){
            getEditor().putString(setHashKey(USER_ID),hideValue(user_id));
            return this;
        }

        @Override
        public void applyUserId(String user_id){
            getEditor().putString(setHashKey(USER_ID),hideValue(user_id)).apply();
        }

        @Override
        public UserPref setUserName(String name){
            getEditor().putString(setHashKey(NAME),hideValue(name));
            return this;
        }

        @Override
        public void applyUserName(String name){
            getEditor().putString(setHashKey(NAME),hideValue(name)).apply();
        }

        @Override
        public UserPref setFullName(String fullName){
            getEditor().putString(setHashKey(FULLNAME),hideValue(fullName));
            return this;
        }

        @Override
        public void applyFullName(String fullName){
            getEditor().putString(setHashKey(FULLNAME),hideValue(fullName)).apply();
        }

        @Override
        public UserPref setFirstName(String firstName){
            getEditor().putString(setHashKey(FIRST_NAME),hideValue(firstName));
            return this;
        }

        @Override
        public void applyFirstName(String firstName){
            getEditor().putString(setHashKey(FIRST_NAME),hideValue(firstName)).apply();
        }

        @Override
        public UserPref setLastName(String lastName){
            getEditor().putString(setHashKey(LAST_NAME),hideValue(lastName));
            return this;
        }

        @Override
        public void applyLastName(String lastName){
            getEditor().putString(setHashKey(LAST_NAME),hideValue(lastName)).apply();
        }

        @Override
        public UserPref setAge(int age){
            getEditor().putString(setHashKey(AGE),hideValue(String.valueOf(age)));
            return this;
        }

        @Override
        public void applyAge(int age){
            getEditor().putString(setHashKey(AGE),hideValue(String.valueOf(age))).apply();
        }

        @Override
        public UserPref setGender(String gender){
            getEditor().putString(setHashKey(GENDER),hideValue(gender));
            return this;
        }

        @Override
        public void applyGender(String gender){
            getEditor().putString(setHashKey(GENDER),hideValue(gender)).apply();
        }

        @Override
        public UserPref setBirthDate(String birthDate){
            getEditor().putString(setHashKey(BIRTH_DATE),hideValue(birthDate));
            return this;
        }

        @Override
        public void applyBirthDate(String birthDate){
            getEditor().putString(setHashKey(BIRTH_DATE),hideValue(birthDate)).apply();
        }

        @Override
        public UserPref setAddress(String address){
            getEditor().putString(setHashKey(ADDRESS),hideValue(address));
            return this;
        }

        @Override
        public void applyAddress(String address){
            getEditor().putString(setHashKey(ADDRESS),hideValue(address)).apply();
        }

        @Override
        public UserPref setEmail(String email){
            getEditor().putString(setHashKey(EMAIL),hideValue(email));
            return this;
        }

        @Override
        public void applyEmail(String email){
            getEditor().putString(setHashKey(EMAIL),hideValue(email)).apply();
        }

        @Override
        public UserPref setPushToken(String token){
            getEditor().putString(setHashKey(PUSH_TOKEN),hideValue(token));
            return this;
        }

        @Override
        public void applyPushToken(String token){
            getEditor().putString(setHashKey(PUSH_TOKEN),hideValue(token)).apply();
        }

        @Override
        public UserPref setPhoneNumber(String phoneNumber){
            getEditor().putString(setHashKey(PHONE_NO),hideValue(phoneNumber));
            return this;
        }

        @Override
        public void applyPhoneNumber(String phoneNumber){
            getEditor().putString(setHashKey(PHONE_NO),hideValue(phoneNumber)).apply();
        }

        @Override
        public UserPref setMobileNumber(String mobileNumber){
            getEditor().putString(setHashKey(MOBILE_NO),hideValue(mobileNumber));
            return this;
        }

        @Override
        public void applyMobileNumber(String mobileNumber){
            getEditor().putString(setHashKey(MOBILE_NO),hideValue(mobileNumber)).apply();
        }

        @Override
        public UserPref setLogin(boolean login){
            getEditor().putString(setHashKey(HAS_LOGIN),hideValue(String.valueOf(login)));
            return this;
        }

        @Override
        public void applyLogin(boolean login){
            getEditor().putString(setHashKey(HAS_LOGIN),hideValue(String.valueOf(login))).apply();
        }

        @Override
        public UserPref setPassword(String password){
            getEditor().putString(setHashKey(PASSWORD),hideValue(password));
            return this;
        }

        @Override
        public void applyPassword(String password){
            getEditor().putString(setHashKey(PASSWORD),hideValue(password)).apply();
        }

        @Override
        public UserPref setFirstTimeUser(boolean firstTime){
            getEditor().putString(setHashKey(FIRST_TIME_USER),hideValue(String.valueOf(firstTime)));
            return this;
        }

        @Override
        public void applyFirstTimeUser(boolean firstTime){
            getEditor().putString(setHashKey(FIRST_TIME_USER),hideValue(String.valueOf(firstTime))).apply();
        }

        @Override
        public UserPref setUserDetail(Object object) {
            getEditor().putString(setHashKey(USER_JSON), hideValue(new Gson().toJson(object)));
            return this;
        }

        @Override
        public void applyUserDetail(Object object) {
            getEditor().putString(setHashKey(USER_JSON), hideValue(new Gson().toJson(object))).apply();
        }

        @CheckResult
        @Override
        public String getUserId(){
            return returnValue(USER_ID);
        }

        @CheckResult
        @Override
        public String getUserDetail(){
            return returnValue(USER_JSON);
        }

        @CheckResult
        @Override
        public Object getUserDetail(Type typeOfT) {
            String value = returnValue(USER_JSON);
            return new Gson().fromJson(value, typeOfT);
        }

        @CheckResult
        @Override
        public Object getUserDetail(Class<Object> classOfT) {
            String value = returnValue(USER_JSON);
            return new Gson().fromJson(value, classOfT);
        }

        @CheckResult
        @Override
        public String getUserName(){
            return returnValue(NAME);
        }

        @CheckResult
        @Override
        public String getFullName(){
            return returnValue(FULLNAME);
        }

        @CheckResult
        @Override
        public String getFirstName(){
            return returnValue(FIRST_NAME);
        }

        @CheckResult
        @Override
        public String getLastName(){
            return returnValue(LAST_NAME);
        }

        @CheckResult
        @Override
        public Integer getAge(){
            try {
                return Integer.parseInt(returnValue(AGE));
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @CheckResult
        @Override
        public String getGender(){
            return returnValue(GENDER);
        }

        @CheckResult
        @Override
        public String getBirthDate(){
            return returnValue(BIRTH_DATE);
        }

        @CheckResult
        @Override
        public String getAddress(){
            return returnValue(ADDRESS);
        }

        @CheckResult
        @Override
        public String getEmail(){
            return returnValue(EMAIL);
        }

        @CheckResult
        @Override
        public String getPushToken(){
            return returnValue(PUSH_TOKEN);
        }

        @CheckResult
        @Override
        public String getPhoneNumber(){
            return returnValue(PHONE_NO);
        }

        @CheckResult
        @Override
        public String getMobileNumber(){
            return returnValue(MOBILE_NO);
        }

        @CheckResult
        @Override
        public Boolean hasLogin(){
            try {
                return Boolean.parseBoolean(returnValue(HAS_LOGIN));
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @CheckResult
        @Override
        public String getPassword(){
            return returnValue(PASSWORD);
        }

        @CheckResult
        @Override
        public Boolean isFirstTimeUser(){
            try {
                return Boolean.parseBoolean(returnValue(FIRST_TIME_USER));
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }


    /******************************************
     * SharedPreferences Editor Builder
     ******************************************/
    public static final class Editor extends BaseEditorAbstract<Editor> {

        public Editor() {
            super(sharedPreferences);
            setDefaultPrefix(defaultPrefix);
            setConcealCrypto(concealCrypto);
        }

        public Editor(@Nullable String keyPrefix) {
            super(keyPrefix, sharedPreferences);
            if (keyPrefix == null) {
                setDefaultPrefix(defaultPrefix);
            }

            setConcealCrypto(concealCrypto);
        }

        @Override
        public Editor put(@NonNull String key, String value) {
            getEditor().putString(setHashKey(key), hideValue(value));
            return this;
        }

        @Override
        public void apply(@NonNull String key, String value) {
            getEditor().putString(setHashKey(key), hideValue(value)).apply();
        }

        @Override
        public Editor put(@NonNull String key, int value) {
            getEditor().putString(setHashKey(key), hideValue(Integer.toString(value)));
            return this;
        }

        @Override
        public void apply(@NonNull String key, int value) {
            getEditor().putString(setHashKey(key), hideValue(Integer.toString(value))).apply();
        }

        @Override
        public Editor put(@NonNull String key, long value) {
            getEditor().putString(setHashKey(key), hideValue(Long.toString(value)));
            return this;
        }

        @Override
        public void apply(@NonNull String key, long value) {
            getEditor().putString(setHashKey(key), hideValue(Long.toString(value))).apply();
        }

        @Override
        public Editor put(@NonNull String key, double value) {
            getEditor().putString(setHashKey(key), hideValue(Double.toString(value)));
            return this;
        }

        @Override
        public void apply(@NonNull String key, double value) {
            getEditor().putString(setHashKey(key), hideValue(Double.toString(value))).apply();
        }

        @Override
        public Editor put(@NonNull String key, float value) {
            getEditor().putString(setHashKey(key), hideValue(Float.toString(value)));
            return this;
        }

        @Override
        public void apply(@NonNull String key, float value) {
            getEditor().putString(setHashKey(key), hideValue(Float.toString(value))).apply();
        }

        @Override
        public Editor put(@NonNull String key, boolean value) {
            getEditor().putString(setHashKey(key), hideValue(Boolean.toString(value)));
            return this;
        }

        @Override
        public void apply(@NonNull String key, boolean value) {
            getEditor().putString(setHashKey(key), hideValue(Boolean.toString(value))).apply();
        }

        @Override
        public Editor put(@NonNull String key, List<?> value){
            getEditor().putString(setHashKey(key),hideValue(value.toString()));
            return this;
        }

        @Override
        public void apply(@NonNull String key, List<?> value){
            getEditor().putString(setHashKey(key),hideValue(value.toString())).apply();
        }

        @Override
        public Editor put(@NonNull String key, byte[] bytes){
            getEditor().putString(setHashKey(key),hideValue(new String(bytes)));
            return this;
        }

        @Override
        public void apply(@NonNull String key, byte[] bytes){
            getEditor().putString(setHashKey(key),hideValue(new String(bytes))).apply();
        }

        @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        @Override
        public Editor putDrawable(@NonNull String key, @DrawableRes int resId, Context context){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            if (bitmap!=null) {
                File imageFile = new File(getImageDirectory(getFolderPath()), "images_" + System.currentTimeMillis() + ".png");
                if (FileUtils.saveBitmap(imageFile, bitmap)) {
                    getEditor().putString(setHashKey(key), hideValue(getConcealCrypto().obscureFile(imageFile, true).getAbsolutePath()));
                }
            }
            else{
                throw new RuntimeException(resId+" : Drawable not found!");
            }
            return this;
        }

        @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        @Override
        public void applyDrawable(@NonNull String key, @DrawableRes int resId, Context context){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            if (bitmap!=null) {
                File imageFile = new File(getImageDirectory(getFolderPath()), "images_" + System.currentTimeMillis() + ".png");
                if (FileUtils.saveBitmap(imageFile, bitmap)) {
                    getEditor().putString(setHashKey(key), hideValue(getConcealCrypto().obscureFile(imageFile, true).getAbsolutePath())).apply();
                }
            }
            else{
                throw new RuntimeException(resId+" : Drawable not found!");
            }
        }

        @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        @Override
        public Editor put(@NonNull String key, Bitmap bitmap){
            File imageFile = new File(getImageDirectory(getFolderPath()),"images_"+System.currentTimeMillis()+".png");
            if(FileUtils.saveBitmap(imageFile, bitmap)){
                getEditor().putString(setHashKey(key), hideValue(getConcealCrypto().obscureFile(imageFile,true).getAbsolutePath()));
            }
            return this;
        }

        @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        @Override
        public void apply(@NonNull String key, Bitmap bitmap){
            File imageFile = new File(getImageDirectory(getFolderPath()),"images_"+System.currentTimeMillis()+".png");
            if(FileUtils.saveBitmap(imageFile, bitmap)){
                getEditor().putString(setHashKey(key), hideValue(getConcealCrypto().obscureFile(imageFile,true).getAbsolutePath())).apply();
            }
        }

        @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        @Override
        public Editor put(@NonNull String key, File file){
            if (FileUtils.isFileForImage(file)) {
                File imageFile = FileUtils.moveFile(file,getImageDirectory(getFolderPath()));
                if (imageFile!=null && imageFile.exists()) {
                    getConcealCrypto().obscureFile(imageFile,true);
                    getEditor().putString(setHashKey(key), hideValue(imageFile.getAbsolutePath()));
                }
            }
            return this;
        }

        @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        @Override
        public void apply(@NonNull String key, File file){
            if (FileUtils.isFileForImage(file)) {
                File imageFile = FileUtils.moveFile(file,getImageDirectory(getFolderPath()));
                if (imageFile!=null && imageFile.exists()) {
                    getConcealCrypto().obscureFile(imageFile,true);
                    getEditor().putString(setHashKey(key), hideValue(imageFile.getAbsolutePath())).apply();
                }
            }
        }

        @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        @Override
        public Editor put(@NonNull String key, File file, boolean deleteOldFile){
            try {
                if (file.exists() && !FileUtils.isFileForImage(file)) {
                    File enc = getConcealCrypto().obscureFile(file,deleteOldFile);
                    getEditor().putString(setHashKey(key), hideValue(enc.getAbsolutePath()));
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return this;
        }

        @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        @Override
        public void apply(@NonNull String key, File file, boolean deleteOldFile){
            try {
                if (file.exists() && !FileUtils.isFileForImage(file)) {
                    File enc = getConcealCrypto().obscureFile(file,deleteOldFile);
                    getEditor().putString(setHashKey(key), hideValue(enc.getAbsolutePath())).apply();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public Editor put(@NonNull String key,Map<String,String> values){
            getEditor().putString(setHashKey(key),hideValue(ConverterListUtils.convertMapToString(values)));
            return this;
        }

        @Override
        public void apply(@NonNull String key,Map<String,String> values){
            getEditor().putString(setHashKey(key),hideValue(ConverterListUtils.convertMapToString(values))).apply();
        }

        @Override
        public Editor putModel(@NonNull String key, Object object) {
            put(key, new Gson().toJson(object));
            return this;
        }

        @Override
        public void applyModel(@NonNull String key, Object object) {
            put(key, new Gson().toJson(object)).apply();
        }


        @Override
        public Editor remove(@NonNull String key) {
            getEditor().remove(setHashKey(key));
            return this;
        }

        @Override
        public Editor clear() {
            getEditor().clear();
            return this;
        }
    }


    /************************v***************************************************************
     * Preferences builder,  ConcealPrefRepository.PreferencesBuilder
     ****************************************************************************************/
    public static class PreferencesBuilder extends BasePreferencesBuilder<PreferencesBuilder> {

        public PreferencesBuilder(Context context) {
            super(context);
        }

        @Override
        public PreferencesBuilder useThisPrefStorage(String prefName){
            setPrefName(prefName);
            return this;
        }

        /**
         * Enable encryption for keys-values
         * @param encryptKey true/false to enable encryption for key
         * @param encryptValue true/false to enable encryption for values
         * @return PreferencesBuilder
         */
        @Override
        public PreferencesBuilder enableCrypto(boolean encryptKey,boolean encryptValue){
            setEnabledCrypto(encryptValue);
            setEnableCryptKey(encryptKey);
            return this;
        }

        @Override
        public PreferencesBuilder enableKeyPrefix(boolean enable, @Nullable String defaultPrefix) {
            if (enable) {
                if (defaultPrefix == null) {
                    setDefaultPrefix(Constant.PREFIX);
                } else {
                    setDefaultPrefix(defaultPrefix);
                }
            } else {
                setDefaultPrefix("");
            }
            return this;
        }

        /**
         * Use Conceal keychain
         * @param keyChain Cryptography type
         * @return PreferencesBuilder
         */
        @Override
        public PreferencesBuilder sharedPrefsBackedKeyChain(CryptoType keyChain){
            setKeyChain(keyChain);
            return this;
        }

        /**
         * Setup password / paraphrase for encryption
         * @param password string password
         * @return PreferencesBuilder
         */
        @Override
        public PreferencesBuilder createPassword(String password){
            setEntityPasswordRaw(password);
            return this;
        }

        /**
         * Set folder name to store files and images
         * @param folderName folder path
         * @return PreferencesBuilder
         */
        @Override
        public PreferencesBuilder setFolderName(String folderName){
            setmFolderName(folderName);
            return this;
        }

        /**
         * Listen to data changes
         * @param listener OnDataChangeListener listener
         * @return PreferencesBuilder
         */
        @Override
        public PreferencesBuilder setPrefListener(OnDataChangeListener listener){
            setOnDataChangeListener(listener);
            return this;
        }

        /**
         * Create Preferences
         * @return ConcealPrefRepository
         */

        public ConcealPrefRepository create(){

            if (getContext() == null){
                throw new RuntimeException("Context cannot be null");
            }

            if(getFolderName() !=null){
                File file = new File(getFolderName());
                try {
                    file.getCanonicalPath();
                    String newFolder = (getFolderName().startsWith("."))? getFolderName().substring(1): getFolderName();
                    setmFolderName(newFolder);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Folder Name is not Valid",e);
                }
            }
            else{
                setmFolderName(DEFAULT_MAIN_FOLDER);
            }

            if (getPrefName()!=null){
                setSharedPreferences(getContext().getSharedPreferences(CipherUtils.obscureEncodeSixFourString(getPrefName().getBytes()), MODE_PRIVATE));
            }
            else {
                setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(getContext()));
            }

            return new ConcealPrefRepository(this);
        }
    }
}
