package com.zeroone.conceal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringRes;

import com.facebook.crypto.CryptoConfig;
import com.facebook.soloader.SoLoader;
import com.zeroone.conceal.model.CryptoFile;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.zeroone.conceal.FileUtils.getDirectory;
import static com.zeroone.conceal.FileUtils.getImageDirectory;
import static com.zeroone.conceal.FileUtils.getListFiles;
import static com.zeroone.conceal.model.Constant.DEFAULT_MAIN_FOLDER;

/**
 * @author : hafiq on 23/03/2017.
 */
@SuppressWarnings("unused")
public class ConcealPrefRepository {

    private Context mContext;
    private static String mFolderName;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static ConcealCrypto concealCrypto;
    private static OnDataChangeListener onDataChangeListener;

    @SuppressLint("CommitPrefEdits")
    private ConcealPrefRepository(@NonNull PreferencesBuilder builder){
        mContext = builder.mContext.get();
        mFolderName = builder.mFolderName;
        sharedPreferences = builder.sharedPreferences;
        onDataChangeListener = builder.onDataChangeListener;

        CryptoConfig mKeyChain = builder.mKeyChain;
        boolean mEnabledCrypto = builder.mEnabledCrypto;
        boolean mEnableCryptKey = builder.mEnableCryptKey;
        String mEntityPasswordRaw = builder.mEntityPasswordRaw;

        //init editor
        editor = sharedPreferences.edit();

        //init crypto
        concealCrypto = new ConcealCrypto.CryptoBuilder(mContext)
                .createPassword(mEntityPasswordRaw)
                .setKeyChain(mKeyChain)
                .setEnableCrypto(mEnabledCrypto)
                .setEnableKeyCrypto(mEnableCryptKey)
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

    public void putString(@NonNull String key, String value) {
        editor.putString(hashKey(key), hideValue(value)).apply();
    }

    public void putString(@NonNull String key, @StringRes int value) {
        editor.putString(hashKey(key), hideValue(mContext.getResources().getString(value))).apply();
    }

    public void putInt(@NonNull String key, int value) {
        editor.putString(hashKey(key), hideValue(Integer.toString(value))).apply();
    }

    public void putLong(@NonNull String key, long value) {
        editor.putString(hashKey(key), hideValue(Long.toString(value))).apply();
    }

    public void putDouble(@NonNull String key, double value) {
        editor.putString(hashKey(key), hideValue(Double.toString(value))).apply();
    }

    public void putFloat(@NonNull String key, float value) {
        editor.putString(hashKey(key), hideValue(Float.toString(value))).apply();
    }

    public void putBoolean(@NonNull String key, boolean value) {
        editor.putString(hashKey(key), hideValue(Boolean.toString(value))).apply();
    }

    public void putListString(@NonNull String key, List<String> value){
        editor.putString(hashKey(key), hideValue(value.toString())).apply();
    }

    public void putListFloat(@NonNull String key, List<Float> value){
        editor.putString(hashKey(key),hideValue(value.toString())).apply();
    }

    public void putListInteger(@NonNull String key, List<Integer> value){
        editor.putString(hashKey(key),hideValue(value.toString())).apply();
    }

    public void putListDouble(@NonNull String key, List<Double> value){
        editor.putString(hashKey(key),hideValue(value.toString())).apply();
    }

    public void putListLong(@NonNull String key, List<Long> value){
        editor.putString(hashKey(key),hideValue(value.toString())).apply();
    }

    public void putListBoolean(@NonNull String key, List<Boolean> value){
        editor.putString(hashKey(key),hideValue(value.toString())).apply();
    }

    public void putMap(@NonNull String key,Map<String,String> values){
        editor.putString(hashKey(key),hideValue(ConverterListUtils.convertMapToString(values))).apply();
    }

    public void putByte(@NonNull String key,byte[] bytes){
        editor.putString(hashKey(key),hideValue(new String(bytes))).apply();
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public String putImage(@NonNull String key, Bitmap bitmap){
        File imageFile = new File(getImageDirectory(mFolderName),"images_"+System.currentTimeMillis()+".png");
        if(FileUtils.saveBitmap(imageFile, bitmap)){
            concealCrypto.obscureFile(imageFile,true);
            editor.putString(hashKey(key),hideValue(imageFile.getAbsolutePath())).apply();
            return imageFile.getAbsolutePath();
        }
        return null;
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public String putImage(@NonNull String key, @DrawableRes int resId){
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
        if (bitmap!=null) {
            File imageFile = new File(getImageDirectory(mFolderName), "images_" + System.currentTimeMillis() + ".png");
            if (FileUtils.saveBitmap(imageFile, bitmap)) {
                concealCrypto.obscureFile(imageFile, true);
                editor.putString(hashKey(key), hideValue(imageFile.getAbsolutePath())).apply();
                return imageFile.getAbsolutePath();
            }
        }
        else{
            throw new IllegalArgumentException(resId+" : Drawable not found!");
        }

        return null;
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public String putImage(@NonNull String key, @Nullable File file){
        if (FileUtils.isFileForImage(file)) {
            File imageFile = FileUtils.moveFile(file,getImageDirectory(mFolderName));
            if (imageFile!=null && imageFile.exists()) {
                concealCrypto.obscureFile(imageFile,true);
                editor.putString(hashKey(key), hideValue(imageFile.getAbsolutePath())).apply();
                return imageFile.getAbsolutePath();
            }
        }
        return null;
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public File putFile(@NonNull String key,@Nullable File file,boolean deleteOldFile){

        if (file == null)
            return null;

        try {
            if (file.exists() && !FileUtils.isFileForImage(file)) {
                File enc = concealCrypto.obscureFile(file,deleteOldFile);
                editor.putString(hashKey(key), hideValue(enc.getAbsolutePath())).apply();
                return enc;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    /************************************
     * FETCHING DATA FROM SHAREDPREFS
     ************************************/
    public String getString(@NonNull String key){
        return concealCrypto.deObscure(sharedPreferences.getString(hashKey(key),null));
    }

    public String getString(@NonNull String key,String defaultValue){
        return concealCrypto.deObscure(sharedPreferences.getString(hashKey(key),defaultValue));
    }

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

    public List<String> getListString(@NonNull String key){
        return ConverterListUtils.toStringArray(getString(key));
    }

    public List<Float> getListFloat(@NonNull String key){
        return ConverterListUtils.toFloatArray(getString(key));
    }

    public List<Double> getListDouble(@NonNull String key){
        return ConverterListUtils.toDoubleArray(getString(key));
    }

    public List<Boolean> getListBoolean(@NonNull String key){
        return ConverterListUtils.toBooleanArray(getString(key));
    }

    public List<Integer> getListInteger(@NonNull String key){
        return ConverterListUtils.toIntArray(getString(key));
    }

    public List<Long> getListLong(@NonNull String key){
        return ConverterListUtils.toLongArray(getString(key));
    }

    public LinkedHashMap<String,String> getMaps(@NonNull String key){
        return ConverterListUtils.convertStringToMap(getString(key));
    }

    public byte[] getArrayBytes(@NonNull String key){
        return getString(key).getBytes();
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
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

    public static class DevicePref{
        private static final String DEVICE_ID = "conceal.device.id";
        private static final String DEVICE_IS_UPDATE = "conceal.device.is_update";
        private static final String DEVICE_VERSION = "conceal.device.version";
        private static final String DEVICE_OS = "conceal.device.os";

        private String DEFAULT_VALUE = null;
        private String PREFIX = "conceal";
        private String SEPARATOR = "_";

        public DevicePref() {
            this.PREFIX = null;
            if (editor == null){
                throw new IllegalArgumentException("Need to initialize ConcealPrefRepository.PreferencesBuilder first");
            }
        }

        public DevicePref(@Nullable String keyPrefix) {
            if (keyPrefix == null)
                this.PREFIX = null;
            else
                this.PREFIX = keyPrefix+this.SEPARATOR;
            if (editor == null){
                throw new IllegalArgumentException("Need to initialize ConcealPrefRepository.PreferencesBuilder first");
            }
        }

        public DevicePref(@Nullable String keyPrefix, @Nullable String defaultEmptyValue) {
            if (defaultEmptyValue != null) {
                this.DEFAULT_VALUE = defaultEmptyValue;
            }

            if (keyPrefix == null)
                this.PREFIX = null;
            else
                this.PREFIX = keyPrefix+this.SEPARATOR;

            if (editor == null){
                throw new IllegalArgumentException("Need to initialize ConcealPrefRepository.PreferencesBuilder first");
            }
        }

        public DevicePref setDefault(@Nullable String defaultEmptyValue){
            if (defaultEmptyValue != null) {
                this.DEFAULT_VALUE = defaultEmptyValue;
            }
            return this;
        }

        /**
         * SET DATA
         */

        public DevicePref setDeviceId(String deviceId){
            editor.putString(setHashKey(DEVICE_ID),hideValue(deviceId));
            return this;
        }

        public void applyDeviceId(String deviceId){
            editor.putString(setHashKey(DEVICE_ID),hideValue(deviceId)).apply();
        }

        public DevicePref setDeviceVersion(String version){
            editor.putString(setHashKey(DEVICE_VERSION), hideValue(version));
            return this;
        }

        public void applyDeviceVersion(String version){
            editor.putString(setHashKey(DEVICE_VERSION), hideValue(version)).apply();
        }

        public DevicePref setDeviceIsUpdated(boolean updated){
            editor.putString(setHashKey(DEVICE_IS_UPDATE), hideValue(String.valueOf(updated)));
            return this;
        }

        public void applyDeviceIsUpdated(boolean updated){
            editor.putString(setHashKey(DEVICE_IS_UPDATE), hideValue(String.valueOf(updated))).apply();
        }

        public DevicePref setDeviceOS(String os){
            editor.putString(setHashKey(DEVICE_OS), hideValue(String.valueOf(os)));
            return this;
        }

        public void applyDeviceOS(String os){
            editor.putString(setHashKey(DEVICE_OS), hideValue(String.valueOf(os))).apply();
        }

        /**
         * GET DATA
         */

        public Boolean isDeviceUpdate(){
            try {
                return Boolean.parseBoolean(returnValue(DEVICE_IS_UPDATE));
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        public String getDeviceId(){
            return returnValue(DEVICE_ID);
        }

        public String getDeviceVersion(){
            return returnValue(DEVICE_VERSION);
        }

        public String getDeviceOs(){
            return returnValue(DEVICE_OS);
        }


        /**
         * UTIL DATA
         */

        private String returnValue(String KEY){
            String value = concealCrypto.deObscure(sharedPreferences.getString(setHashKey(KEY), null));
            if (value == null)
                return this.DEFAULT_VALUE;

            return value;
        }

        private String setHashKey(String key) {
            if (PREFIX == null)
                return hashKey(key);
            else
                return hashKey(PREFIX+key);
        }

        public void apply() {
            editor.apply();
        }
        public void commit() {
            editor.commit();
        }

    }

    public static class UserPref{
        private static final String NAME = "conceal.user.username";
        private static final String FULLNAME = "conceal.user.fullname";
        private static final String FIRST_NAME = "conceal.user.first_name";
        private static final String LAST_NAME = "conceal.user.last_name";
        private static final String AGE = "conceal.user.age";
        private static final String GENDER = "conceal.user.gender";
        private static final String BIRTH_DATE = "conceal.user.dob";
        private static final String ADDRESS = "conceal.user.address";
        private static final String EMAIL = "conceal.user.email";
        private static final String PUSH_TOKEN = "conceal.user.push.token";
        private static final String PHONE_NO = "conceal.user.phone_number";
        private static final String MOBILE_NO = "conceal.conceal.user.mobile_number";
        private static final String HAS_LOGIN = "conceal.user.has_login";
        private static final String PASSWORD = "conceal.user.password";
        private static final String FIRST_TIME_USER = "conceal.user.first_time";
        private static final String USER_ID = "conceal.user.user_id";
        private static final String USER_JSON = "conceal.user.json";

        private String PREFIX = "conceal";
        private String DEFAULT_VALUE = null;
        private String SEPARATOR = "_";

        public UserPref() {
            this.PREFIX = null;
            if (editor == null){
                throw new IllegalArgumentException("Need to initialize ConcealPrefRepository.PreferencesBuilder first");
            }
        }

        public UserPref(@Nullable String keyPrefix) {
            if (keyPrefix == null)
                this.PREFIX = null;
            else
                this.PREFIX = keyPrefix+this.SEPARATOR;

            if (editor == null){
                throw new IllegalArgumentException("Need to initialize ConcealPrefRepository.PreferencesBuilder first");
            }
        }

        public UserPref(@Nullable String keyPrefix, @Nullable String defaultEmptyValue) {
            if (defaultEmptyValue != null) {
                this.DEFAULT_VALUE = defaultEmptyValue;
            }

            if (keyPrefix == null)
                this.PREFIX = null;
            else
                this.PREFIX = keyPrefix+this.SEPARATOR;

            if (editor == null){
                throw new IllegalArgumentException("Need to initialize ConcealPrefRepository.PreferencesBuilder first");
            }
        }

        public UserPref setDefault(@Nullable String defaultEmptyValue){
            if (defaultEmptyValue != null) {
                this.DEFAULT_VALUE = defaultEmptyValue;
            }
            return this;
        }

        public UserPref setUserDetail(String user_detail){
            editor.putString(setHashKey(USER_JSON),hideValue(user_detail));
            return this;
        }

        public void applyUserDetail(String user_detail){
            editor.putString(setHashKey(USER_JSON),hideValue(user_detail)).apply();
        }

        public UserPref setUserId(String user_id){
            editor.putString(setHashKey(USER_ID),hideValue(user_id));
            return this;
        }

        public void applyUserId(String user_id){
            editor.putString(setHashKey(USER_ID),hideValue(user_id)).apply();
        }

        public UserPref setUserName(String name){
            editor.putString(setHashKey(NAME),hideValue(name));
            return this;
        }

        public void applyUserName(String name){
            editor.putString(setHashKey(NAME),hideValue(name)).apply();
        }

        public UserPref setFullName(String fullName){
            editor.putString(setHashKey(FULLNAME),hideValue(fullName));
            return this;
        }

        public void applyFullName(String fullName){
            editor.putString(setHashKey(FULLNAME),hideValue(fullName)).apply();
        }

        public UserPref setFirstName(String firstName){
            editor.putString(setHashKey(FIRST_NAME),hideValue(firstName));
            return this;
        }

        public void applyFirstName(String firstName){
            editor.putString(setHashKey(FIRST_NAME),hideValue(firstName)).apply();
        }

        public UserPref setLastName(String lastName){
            editor.putString(setHashKey(LAST_NAME),hideValue(lastName));
            return this;
        }

        public void applyLastName(String lastName){
            editor.putString(setHashKey(LAST_NAME),hideValue(lastName)).apply();
        }

        public UserPref setAge(int age){
            editor.putString(setHashKey(AGE),hideValue(String.valueOf(age)));
            return this;
        }

        public void applyAge(int age){
            editor.putString(setHashKey(AGE),hideValue(String.valueOf(age))).apply();
        }

        public UserPref setGender(String gender){
            editor.putString(setHashKey(GENDER),hideValue(gender));
            return this;
        }

        public void applyGender(String gender){
            editor.putString(setHashKey(GENDER),hideValue(gender)).apply();
        }

        public UserPref setBirthDate(String birthDate){
            editor.putString(setHashKey(BIRTH_DATE),hideValue(birthDate));
            return this;
        }

        public void applyBirthDate(String birthDate){
            editor.putString(setHashKey(BIRTH_DATE),hideValue(birthDate)).apply();
        }

        public UserPref setAddress(String address){
            editor.putString(setHashKey(ADDRESS),hideValue(address));
            return this;
        }

        public void applyAddress(String address){
            editor.putString(setHashKey(ADDRESS),hideValue(address)).apply();
        }

        public UserPref setEmail(String email){
            editor.putString(setHashKey(EMAIL),hideValue(email));
            return this;
        }

        public void applyEmail(String email){
            editor.putString(setHashKey(EMAIL),hideValue(email)).apply();
        }

        public UserPref setPushToken(String token){
            editor.putString(setHashKey(PUSH_TOKEN),hideValue(token));
            return this;
        }

        public void applyPushToken(String token){
            editor.putString(setHashKey(PUSH_TOKEN),hideValue(token)).apply();
        }

        public UserPref setPhoneNumber(String phoneNumber){
            editor.putString(setHashKey(PHONE_NO),hideValue(phoneNumber));
            return this;
        }

        public void applyPhoneNumber(String phoneNumber){
            editor.putString(setHashKey(PHONE_NO),hideValue(phoneNumber)).apply();
        }

        public UserPref setMobileNumber(String mobileNumber){
            editor.putString(setHashKey(MOBILE_NO),hideValue(mobileNumber));
            return this;
        }

        public void applyMobileNumber(String mobileNumber){
            editor.putString(setHashKey(MOBILE_NO),hideValue(mobileNumber)).apply();
        }

        public UserPref setLogin(boolean login){
            editor.putString(setHashKey(HAS_LOGIN),hideValue(String.valueOf(login)));
            return this;
        }

        public void applyLogin(boolean login){
            editor.putString(setHashKey(HAS_LOGIN),hideValue(String.valueOf(login))).apply();
        }

        public UserPref setPassword(String password){
            editor.putString(setHashKey(PASSWORD),hideValue(password));
            return this;
        }

        public void applyPassword(String password){
            editor.putString(setHashKey(PASSWORD),hideValue(password)).apply();
        }

        public UserPref setFirstTimeUser(boolean firstTime){
            editor.putString(setHashKey(FIRST_TIME_USER),hideValue(String.valueOf(firstTime)));
            return this;
        }

        public void applyFirstTimeUser(boolean firstTime){
            editor.putString(setHashKey(FIRST_TIME_USER),hideValue(String.valueOf(firstTime))).apply();
        }

        public String getUserId(){
            return returnValue(USER_ID);
        }
        public String getUserDetail(){
            return returnValue(USER_JSON);
        }
        public String getUserName(){
            return returnValue(NAME);
        }
        public String getFullName(){
            return returnValue(FULLNAME);
        }
        public String getFirstName(){
            return returnValue(FIRST_NAME);
        }
        public String getLastName(){
            return returnValue(LAST_NAME);
        }
        public Integer getAge(){
            try {
                return Integer.parseInt(returnValue(AGE));
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        public String getGender(){
            return returnValue(GENDER);
        }
        public String getBirthDate(){
            return returnValue(BIRTH_DATE);
        }
        public String getAddress(){
            return returnValue(ADDRESS);
        }
        public String getEmail(){
            return returnValue(EMAIL);
        }
        public String getPushToken(){
            return returnValue(PUSH_TOKEN);
        }
        public String getPhoneNumber(){
            return returnValue(PHONE_NO);
        }
        public String getMobileNumber(){
            return returnValue(MOBILE_NO);
        }
        public Boolean hasLogin(){
            try {
                return Boolean.parseBoolean(returnValue(HAS_LOGIN));
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        public String getPassword(){
            return returnValue(PASSWORD);
        }

        public Boolean isFirstTimeUser(){
            try {
                return Boolean.parseBoolean(returnValue(FIRST_TIME_USER));
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        private String returnValue(String KEY){
            String value = concealCrypto.deObscure(sharedPreferences.getString(setHashKey(KEY), null));
            if (value == null)
                return this.DEFAULT_VALUE;

            return value;
        }

        private String setHashKey(String key) {
            if (PREFIX == null)
                return hashKey(key);
            else
                return hashKey(PREFIX+key);
        }

        public void apply() {
            editor.apply();
        }
        public void commit() {
            editor.commit();
        }
    }


    /******************************************
     * SharedPreferences Editor Builder
     ******************************************/
    public static final class Editor {
        private String PREFIX = "conceal";
        private String SEPARATOR = "_";
        public Editor() {
            this.PREFIX = null;
            if (editor ==null){
                throw new IllegalArgumentException("Need to initialize ConcealPrefRepository.PreferencesBuilder first");
            }
        }

        public Editor(@Nullable String keyPrefix) {
            if (keyPrefix == null)
                this.PREFIX = null;
            else
                this.PREFIX = keyPrefix+this.SEPARATOR;

            if (editor ==null){
                throw new IllegalArgumentException("Need to initialize ConcealPrefRepository.PreferencesBuilder first");
            }
        }

        public Editor putString(@NonNull String key, String value) {
            editor.putString(setHashKey(key), hideValue(value));
            return this;
        }

        public void applyString(@NonNull String key, String value) {
            editor.putString(setHashKey(key), hideValue(value)).apply();
        }

        public Editor putInt(@NonNull String key, int value) {
            editor.putString(setHashKey(key), hideValue(Integer.toString(value)));
            return this;
        }

        public void applyInt(@NonNull String key, int value) {
            editor.putString(setHashKey(key), hideValue(Integer.toString(value))).apply();
        }

        public Editor putLong(@NonNull String key, long value) {
            editor.putString(setHashKey(key), hideValue(Long.toString(value)));
            return this;
        }

        public void applyLong(@NonNull String key, long value) {
            editor.putString(setHashKey(key), hideValue(Long.toString(value))).apply();
        }

        public Editor putDouble(@NonNull String key, double value) {
            editor.putString(setHashKey(key), hideValue(Double.toString(value)));
            return this;
        }

        public void applyDouble(@NonNull String key, double value) {
            editor.putString(setHashKey(key), hideValue(Double.toString(value))).apply();
        }

        public Editor putFloat(@NonNull String key, float value) {
            editor.putString(setHashKey(key), hideValue(Float.toString(value)));
            return this;
        }

        public void applyFloat(@NonNull String key, float value) {
            editor.putString(setHashKey(key), hideValue(Float.toString(value))).apply();
        }

        public Editor putBoolean(@NonNull String key, boolean value) {
            editor.putString(setHashKey(key), hideValue(Boolean.toString(value)));
            return this;
        }

        public void applyBoolean(@NonNull String key, boolean value) {
            editor.putString(setHashKey(key), hideValue(Boolean.toString(value))).apply();
        }

        public Editor putListString(@NonNull String key, List<String> value){
            editor.putString(setHashKey(key),hideValue(value.toString()));
            return this;
        }

        public void applyListString(@NonNull String key, List<String> value){
            editor.putString(setHashKey(key),hideValue(value.toString())).apply();
        }

        public Editor putListFloat(@NonNull String key, List<Float> value){
            editor.putString(setHashKey(key),hideValue(value.toString()));
            return this;
        }

        public void applyListFloat(@NonNull String key, List<Float> value){
            editor.putString(setHashKey(key),hideValue(value.toString())).apply();
        }

        public Editor putListInteger(@NonNull String key, List<Integer> value){
            editor.putString(setHashKey(key),hideValue(value.toString()));
            return this;
        }

        public void applyListInteger(@NonNull String key, List<Integer> value){
            editor.putString(setHashKey(key),hideValue(value.toString())).apply();
        }

        public Editor putListDouble(@NonNull String key, List<Double> value){
            editor.putString(setHashKey(key),hideValue(value.toString()));
            return this;
        }

        public void applyListDouble(@NonNull String key, List<Double> value){
            editor.putString(setHashKey(key),hideValue(value.toString())).apply();
        }

        public Editor putListLong(@NonNull String key, List<Long> value){
            editor.putString(setHashKey(key),hideValue(value.toString()));
            return this;
        }

        public void applyListLong(@NonNull String key, List<Long> value){
            editor.putString(setHashKey(key),hideValue(value.toString())).apply();
        }

        public Editor putListBoolean(@NonNull String key, List<Boolean> value){
            editor.putString(setHashKey(key),hideValue(value.toString()));
            return this;
        }

        public void applyListBoolean(@NonNull String key, List<Boolean> value){
            editor.putString(setHashKey(key),hideValue(value.toString())).apply();
        }

        public Editor putByte(@NonNull String key,byte[] bytes){
            editor.putString(setHashKey(key),hideValue(new String(bytes)));
            return this;
        }

        public void applyByte(@NonNull String key,byte[] bytes){
            editor.putString(setHashKey(key),hideValue(new String(bytes))).apply();
        }

        @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        public Editor putImage(@NonNull String key, @DrawableRes int resId, Context context){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            if (bitmap!=null) {
                File imageFile = new File(getImageDirectory(mFolderName), "images_" + System.currentTimeMillis() + ".png");
                if (FileUtils.saveBitmap(imageFile, bitmap)) {
                    editor.putString(setHashKey(key), hideValue(concealCrypto.obscureFile(imageFile, true).getAbsolutePath()));
                }
            }
            else{
                throw new RuntimeException(resId+" : Drawable not found!");
            }
            return this;
        }

        @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        public Editor putImage(@NonNull String key, Bitmap bitmap){
            File imageFile = new File(getImageDirectory(mFolderName),"images_"+System.currentTimeMillis()+".png");
            if(FileUtils.saveBitmap(imageFile, bitmap)){
                editor.putString(setHashKey(key),hideValue(concealCrypto.obscureFile(imageFile,true).getAbsolutePath()));
            }
            return this;
        }

        @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        public Editor putImage(@NonNull String key, File file){
            if (FileUtils.isFileForImage(file)) {
                File imageFile = FileUtils.moveFile(file,getImageDirectory(mFolderName));
                if (imageFile!=null && imageFile.exists()) {
                    concealCrypto.obscureFile(imageFile,true);
                    editor.putString(setHashKey(key), hideValue(imageFile.getAbsolutePath()));
                }
            }
            return this;
        }

        @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        public Editor putFile(@NonNull String key,File file,boolean deleteOldFile){
            try {
                if (file.exists() && !FileUtils.isFileForImage(file)) {
                    File enc = concealCrypto.obscureFile(file,deleteOldFile);
                    editor.putString(setHashKey(key), hideValue(enc.getAbsolutePath()));
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return this;
        }
        public Editor remove(@NonNull String key) {
            editor.remove(setHashKey(key));
            return this;
        }

        public Editor putMap(@NonNull String key,Map<String,String> values){
            editor.putString(setHashKey(key),hideValue(ConverterListUtils.convertMapToString(values)));
            return this;
        }

        public Editor clear() {
            editor.clear();
            return this;
        }

        private String setHashKey(String key) {
            if (PREFIX == null)
                return hashKey(key);
            else
                return hashKey(PREFIX+key);
        }

        public boolean commit() {
            return editor.commit();
        }
        public void apply() {
            editor.apply();
        }
    }


    /************************v***************************************************************
     * Preferences builder,  ConcealPrefRepository.PreferencesBuilder
     ****************************************************************************************/
    public static class PreferencesBuilder{

        private WeakReference<Context> mContext;
        private CryptoConfig mKeyChain = CryptoConfig.KEY_256;
        private String mPrefname = null;
        private String mFolderName = null;
        private boolean mEnabledCrypto = false;
        private boolean mEnableCryptKey = false;
        private String mEntityPasswordRaw = null;
        private SharedPreferences sharedPreferences;
        private OnDataChangeListener onDataChangeListener;

        public PreferencesBuilder(Context context) {
            mContext = new WeakReference<>(context.getApplicationContext());
        }

        public PreferencesBuilder useDefaultPrefStorage(){
            return this;
        }

        public PreferencesBuilder useThisPrefStorage(String prefName){
            mPrefname = prefName;
            return this;
        }

        /**
         * Enable encryption for keys-values
         * @param encryptKey true/false to enable encryption for key
         * @param encryptValue true/false to enable encryption for values
         * @return PreferencesBuilder
         */
        public PreferencesBuilder enableCrypto(boolean encryptKey,boolean encryptValue){
            mEnabledCrypto = encryptValue;
            mEnableCryptKey = encryptKey;
            return this;
        }

        /**
         * Use Conceal keychain
         * @param keyChain Cryptography type
         * @return PreferencesBuilder
         */
        public PreferencesBuilder sharedPrefsBackedKeyChain(CryptoConfig keyChain){
            mKeyChain = keyChain;
            return this;
        }

        /**
         * Setup password / paraphrase for encryption
         * @param password string password
         * @return PreferencesBuilder
         */
        public PreferencesBuilder createPassword(String password){
            mEntityPasswordRaw = password;
            return this;
        }

        /**
         * Set folder name to store files and images
         * @param folderName folder path
         * @return PreferencesBuilder
         */
        public PreferencesBuilder setFolderName(String folderName){
            mFolderName = folderName;
            return this;
        }

        /**
         * Listen to data changes
         * @param listener OnDataChangeListener listener
         * @return PreferencesBuilder
         */
        public PreferencesBuilder setPrefListener(OnDataChangeListener listener){
            onDataChangeListener = listener;
            return this;
        }

        /**
         * Create Preferences
         * @return ConcealPrefRepository
         */
        public ConcealPrefRepository create(){

            if (this.mContext == null){
                throw new RuntimeException("Context cannot be null");
            }

            if(mFolderName !=null){
                File file = new File(mFolderName);
                try {
                    file.getCanonicalPath();
                    mFolderName = (mFolderName.startsWith("."))? mFolderName.substring(1):mFolderName;
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Folder Name is not Valid",e);
                }
            }
            else{
                mFolderName = DEFAULT_MAIN_FOLDER;
            }

            if (mPrefname!=null){
                sharedPreferences = this.mContext.get().getSharedPreferences(CipherUtils.obscureEncodeSixFourString(mPrefname.getBytes()), MODE_PRIVATE);
            }
            else {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext.get());
            }

            return new ConcealPrefRepository(this);

        }
    }

    // ============================================================================================================

    private static void throwRunTimeException(String message, Throwable throwable){
        new RuntimeException(message,throwable).printStackTrace();
    }

    private static String hashKey(String key){
        if (key == null || key.isEmpty())
            throw new NullPointerException("Key cannot be null or empty");

        return concealCrypto.hashKey(key);
    }

    private static String hideValue(String value){
        return concealCrypto.obscure(value);
    }

    // ============================================================================================================

}
