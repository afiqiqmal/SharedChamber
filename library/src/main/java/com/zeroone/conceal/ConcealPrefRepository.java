package com.zeroone.conceal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.crypto.CryptoConfig;
import com.zeroone.conceal.helper.ConverterListUtils;
import com.zeroone.conceal.helper.CryptoFile;
import com.zeroone.conceal.helper.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.zeroone.conceal.helper.Constant.DEFAULT_DIRECTORY;
import static com.zeroone.conceal.helper.Constant.DEFAULT_IMAGE_FOLDER;
import static com.zeroone.conceal.helper.Constant.DEFAULT_MAIN_FOLDER;
import static com.zeroone.conceal.helper.Constant.DEFAULT_PREFIX_FILENAME;

/**
 * @author : hafiq on 23/03/2017.
 */

public class ConcealPrefRepository {

    private Context mContext;
    private CryptoConfig mKeyChain = CryptoConfig.KEY_256;
    private boolean mEnabledCrypto = false;
    private boolean mEnableCryptKey = false;
    private String mEntityPasswordRaw = null;
    private static String mFolderName;
    private static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static ConcealCrypto concealCrypto;

    @SuppressLint("CommitPrefEdits")
    ConcealPrefRepository(PreferencesBuilder builder){
        mContext = builder.mContext;
        mKeyChain = builder.mKeyChain;
        mEnabledCrypto = builder.mEnabledCrypto;
        mEnableCryptKey = builder.mEnableCryptKey;
        sharedPreferences = builder.sharedPreferences;
        mEntityPasswordRaw = builder.mEntityPasswordRaw;
        mFolderName = builder.mFolderName;

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
    }

    /* Destroy Files*/
    public void destroyCrypto(){
        concealCrypto.clearCrypto();
    }

    public void destroySharedPreferences(){
        editor.clear().apply();
        destroyCrypto();
    }


    /* Get Preferences Size */
    public int getPrefsSize(){
        return sharedPreferences.getAll().size();
    }


    /* Remove by Key */
    public void remove(String... keys){
        for (String key:keys){
            editor.remove(concealCrypto.hashKey(key));
        }
        editor.apply();
    }

    //special cases for file to remove by key
    public boolean removeFile(String key){
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

    /* get all encrypted file in created folder */
    public List<CryptoFile> getAllConcealEncryptedFiles(){
        return getListFiles(getDirectory());
    }

    /* get list of key and values inside sharedpreferences */
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


    /* Get Preferences */
    public SharedPreferences getPreferences(){
        return sharedPreferences;
    }


    /* Contains */
    public boolean contains(String key){
        return sharedPreferences.contains(concealCrypto.hashKey(key));
    }


    /* Save Data */
    public void putString(String key, String value) {
        editor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(value)).apply();
    }

    public void putInt(String key, int value) {
        editor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(Integer.toString(value))).apply();
    }

    public void putLong(String key, long value) {
        editor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(Long.toString(value))).apply();
    }

    public void putDouble(String key, double value) {
        editor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(Double.toString(value))).apply();
    }

    public void putFloat(String key, float value) {
        editor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(Float.toString(value))).apply();
    }

    public void putBoolean(String key, boolean value) {
        editor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(Boolean.toString(value))).apply();
    }

    public void putListString(String key, List<String> value){
        editor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(value.toString())).apply();
    }

    public void putListFloat(String key, List<Float> value){
        editor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(value.toString())).apply();
    }

    public void putListInteger(String key, List<Integer> value){
        editor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(value.toString())).apply();
    }

    public void putListDouble(String key, List<Double> value){
        editor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(value.toString())).apply();
    }

    public void putListLong(String key, List<Long> value){
        editor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(value.toString())).apply();
    }

    public void putListBoolean(String key, List<Boolean> value){
        editor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(value.toString())).apply();
    }

    public void putMap(String key,Map<String,String> values){
        editor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(ConverterListUtils.convertMapToString(values))).apply();
    }

    public void putByte(String key,byte[] bytes){
        editor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(new String(bytes))).apply();
    }

    public String putImage(String key, Bitmap bitmap){
        File imageFile = new File(getImageDirectory(mFolderName),"images_"+System.currentTimeMillis()+".png");
        if(FileUtils.saveBitmap(imageFile, bitmap)){
            concealCrypto.obscureFile(imageFile,true);
            editor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(imageFile.getAbsolutePath())).apply();
            return imageFile.getAbsolutePath();
        }
        return null;
    }

    public String putImage(String key, File file){
        if (FileUtils.isFileForImage(file)) {
            File imageFile = FileUtils.moveFile(file,getImageDirectory(mFolderName));
            if (imageFile!=null && imageFile.exists()) {
                concealCrypto.obscureFile(imageFile,true);
                editor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(imageFile.getAbsolutePath())).apply();
                return imageFile.getAbsolutePath();
            }
        }
        return null;
    }

    public File putFile(String key,File file,boolean deleteOldFile){
        try {
            if (file.exists() && !FileUtils.isFileForImage(file)) {
                File enc = concealCrypto.obscureFile(file,deleteOldFile);
                editor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(enc.getAbsolutePath())).apply();
                return enc;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    /* Fetch Data */
    public String getString(String key){
        return concealCrypto.deObscure(sharedPreferences.getString(concealCrypto.hashKey(key),null));
    }

    public String getString(String key,String def){
        return concealCrypto.deObscure(sharedPreferences.getString(concealCrypto.hashKey(key),def));
    }

    public Integer getInt(String key){
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

    public Integer getInt(String key,int def){
        try {
            String value = getString(key);

            if (value == null)
                return def;

            return Integer.parseInt(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Integer data type",e);
            return -99;
        }
    }

    public Float getFloat(String key){
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

    public Float getFloat(String key,float def){
        try {
            String value = getString(key);

            if (value == null)
                return def;

            return Float.parseFloat(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Float data type",e);
            return def;
        }
    }

    public Double getDouble(String key){
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

    public Double getDouble(String key,double def){
        try {
            String value = getString(key);

            if (value == null)
                return def;

            return Double.parseDouble(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Double data type",e);
            return def;
        }
    }


    public Long getLong(String key){
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

    public Long getLong(String key,long def){
        try {
            String value = getString(key);

            if (value == null)
                return def;

            return Long.parseLong(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Long data type",e);
            return def;
        }
    }


    public Boolean getBoolean(String key){
        try {
            String value = getString(key);
            return value != null && Boolean.parseBoolean(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Boolean data type",e);
            return false;
        }
    }

    public Boolean getBoolean(String key,boolean def){
        try {
            String value = getString(key);
            if (value == null)
                return def;

            return Boolean.parseBoolean(value);
        }
        catch (Exception e){
            throwRunTimeException("Unable to convert to Boolean data type",e);
            return false;
        }
    }

    public List<String> getListString(String key){
        return ConverterListUtils.toStringArray(getString(key));
    }

    public List<Float> getListFloat(String key){
        return ConverterListUtils.toFloatArray(getString(key));
    }

    public List<Double> getListDouble(String key){
        return ConverterListUtils.toDoubleArray(getString(key));
    }

    public List<Boolean> getListBoolean(String key){
        return ConverterListUtils.toBooleanArray(getString(key));
    }

    public List<Integer> getListInteger(String key){
        return ConverterListUtils.toIntArray(getString(key));
    }

    public List<Long> getListLong(String key){
        return ConverterListUtils.toLongArray(getString(key));
    }

    public LinkedHashMap<String,String> getMaps(String key){
        return ConverterListUtils.convertStringToMap(getString(key));
    }

    public byte[] getArrayBytes(String key){
        return getString(key).getBytes();
    }

    public Bitmap getImage(String key){
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

    public File getFile(String key,boolean deleteOldFile){
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


    public static final class Editor {

        private SharedPreferences.Editor mEditor;

        @SuppressLint("CommitPrefEdits")
        public Editor() {
            mEditor = sharedPreferences.edit();
        }

        public Editor putString(String key, String value) {
            mEditor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(value));
            return this;
        }

        public Editor putInt(String key, int value) {
            mEditor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(Integer.toString(value)));
            return this;
        }

        public Editor putLong(String key, long value) {
            mEditor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(Long.toString(value)));
            return this;
        }

        public Editor putDouble(String key, double value) {
            mEditor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(Double.toString(value)));
            return this;
        }

        public Editor putFloat(String key, float value) {
            mEditor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(Float.toString(value)));
            return this;
        }

        public Editor putBoolean(String key, boolean value) {
            mEditor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(Boolean.toString(value)));
            return this;
        }

        public Editor putListString(String key, List<String> value){
            mEditor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(value.toString()));
            return this;
        }

        public Editor putListFloat(String key, List<Float> value){
            mEditor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(value.toString()));
            return this;
        }

        public Editor putListInteger(String key, List<Integer> value){
            mEditor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(value.toString()));
            return this;
        }

        public Editor putListDouble(String key, List<Double> value){
            mEditor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(value.toString()));
            return this;
        }

        public Editor putListLong(String key, List<Long> value){
            mEditor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(value.toString()));
            return this;
        }

        public Editor putListBoolean(String key, List<Boolean> value){
            mEditor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(value.toString()));
            return this;
        }

        public Editor putByte(String key,byte[] bytes){
            mEditor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(new String(bytes)));
            return this;
        }

        public Editor putImage(String key, Bitmap bitmap){
            File imageFile = new File(getImageDirectory(mFolderName),"images_"+System.currentTimeMillis()+".png");
            if(FileUtils.saveBitmap(imageFile, bitmap)){
                mEditor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(concealCrypto.obscureFile(imageFile,true).getAbsolutePath()));
            }
            return this;
        }

        public Editor putImage(String key, File file){
            if (FileUtils.isFileForImage(file)) {
                File imageFile = FileUtils.moveFile(file,getImageDirectory(mFolderName));
                if (imageFile!=null && imageFile.exists()) {
                    concealCrypto.obscureFile(imageFile,true);
                    mEditor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(imageFile.getAbsolutePath()));
                }
            }
            return this;
        }

        public Editor putFile(String key,File file,boolean deleteOldFile){
            try {
                if (file.exists() && !FileUtils.isFileForImage(file)) {
                    File enc = concealCrypto.obscureFile(file,deleteOldFile);
                    mEditor.putString(concealCrypto.hashKey(key), concealCrypto.obscure(enc.getAbsolutePath()));
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return this;
        }
        public Editor remove(String key) {
            mEditor.remove(concealCrypto.hashKey(key));
            return this;
        }

        public Editor putMap(String key,Map<String,String> values){
            mEditor.putString(concealCrypto.hashKey(key),concealCrypto.obscure(ConverterListUtils.convertMapToString(values)));
            return this;
        }

        public Editor clear() {
            mEditor.clear();
            return this;
        }

        public boolean commit() {
            return mEditor.commit();
        }


        public void apply() {
            mEditor.apply();
        }
    }

    public static class PreferencesBuilder{

        private Context mContext;
        private CryptoConfig mKeyChain = CryptoConfig.KEY_256;
        private String mPrefname = null;
        private String mFolderName = null;
        private boolean mEnabledCrypto = false;
        private boolean mEnableCryptKey = false;
        private String mEntityPasswordRaw = null;
        private SharedPreferences sharedPreferences;

        public PreferencesBuilder(Context context) {
            mContext = context;
        }

        public PreferencesBuilder useDefaultPrefStorage(){
            return this;
        }

        public PreferencesBuilder useThisPrefStorage(String prefName){
            mPrefname = prefName;
            return this;
        }

        public PreferencesBuilder enableCrypto(boolean enabled,boolean cryptKey){
            mEnabledCrypto = enabled;
            mEnableCryptKey = cryptKey;
            return this;
        }

        public PreferencesBuilder sharedPrefsBackedKeyChain(CryptoConfig keyChain){
            mKeyChain = keyChain;
            return this;
        }

        public PreferencesBuilder createPassword(String password){
            mEntityPasswordRaw = password;
            return this;
        }

        public PreferencesBuilder setFolderName(String folderName){
            mFolderName = folderName;
            return this;
        }

        public ConcealPrefRepository create(){

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
                sharedPreferences = mContext.getSharedPreferences(mPrefname, MODE_PRIVATE);
            }
            else{
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            }

            return new ConcealPrefRepository(this);

        }
    }

    private void throwRunTimeException(String message, Throwable throwable){
        new RuntimeException(message,throwable).printStackTrace();
    }


    private List<CryptoFile> getListFiles(File parentDir) {
        List<CryptoFile> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                if(file.getName().startsWith(DEFAULT_PREFIX_FILENAME)){
                    CryptoFile cryptoFile = new CryptoFile();
                    cryptoFile.setFileName(file.getName());
                    cryptoFile.setPath(file.getAbsolutePath());
                    cryptoFile.setType(file.getParent());
                    inFiles.add(cryptoFile);
                }
            }
        }
        return inFiles;
    }

    @Nullable
    private static File getDirectory(){
        File file = new File(DEFAULT_DIRECTORY+mFolderName+"/"+DEFAULT_IMAGE_FOLDER);
        if (file.exists())
            return file;

        return null;
    }

    @Nullable
    private static File getImageDirectory(String mFolderName){
        File file = new File(DEFAULT_DIRECTORY+mFolderName+"/"+DEFAULT_IMAGE_FOLDER);
        Log.d("Conceal",file.getAbsolutePath());
        if (file.mkdirs())
            return file;
        if (file.exists())
            return file;

        return null;
    }

}
