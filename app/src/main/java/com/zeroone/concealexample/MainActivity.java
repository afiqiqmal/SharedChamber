package com.zeroone.concealexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;

import com.facebook.crypto.CryptoConfig;
import com.zeroone.conceal.ConcealCrypto;
import com.zeroone.conceal.ConcealPrefRepository;
import com.zeroone.conceal.model.CryptoFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    String NAME_KEY = "user_name";
    String AGE_KEY = "user_age";
    String EMAIL_KEY = "user_email";
    String IMAGE_KEY = "user_image";
    String FILE_KEY = "user_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConcealPrefRepository.applicationInit(getApplication(), false);

        new ConcealPrefRepository.Editor()
                .putString(NAME_KEY, "Hafiq Iqmal")
                .putInt(AGE_KEY, 24)
                .putString(EMAIL_KEY, "hafiqiqmal93@gmail.com")
                .apply();


        //add user details preferences
        new ConcealPrefRepository.UserPref("PREFIX").setFirstName("Firstname").setLastName("Lasname").setEmail("hello@gmail.com").apply();

        //get user details
        Log.d("TAG",new ConcealPrefRepository.UserPref("PREFIX").getFirstName());
        Log.d("TAG",new ConcealPrefRepository.UserPref().setDefault("No Data").getLastName());
        Log.d("TAG",new ConcealPrefRepository.UserPref().setDefault("No Data").getEmail());


//        Map<String,String> map =concealPrefRepository.getAllSharedPrefData();
//        for(Map.Entry<String,?> entry : map.entrySet()){
//            try {
//                Log.d("TAG",entry.getKey()+" :: "+entry.getValue().toString());
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//
//        List<CryptoFile> cryptoFileList = concealPrefRepository.getAllConcealEncryptedFiles();
//        for (CryptoFile cryptoFile :cryptoFileList){
//            Log.d("TAG",cryptoFile.getFileName());
//        }
//
//        Log.d("TAG",""+concealPrefRepository.getPrefsSize());
//
//        ConcealCrypto concealCrypto = new ConcealCrypto.CryptoBuilder(this)
//                .setEnableCrypto(true) //default true
//                .setKeyChain(CryptoConfig.KEY_256) // CryptoConfig.KEY_256 or CryptoConfig.KEY_128
//                .createPassword("Mac OSX")
//                .create();
//
//        String test = "Hello World";
//        String cipher =  concealCrypto.obscure(test); //encrypt
//        Log.d("Display", cipher);
//        String dec = concealCrypto.deObscure(cipher); //decrypt
//        Log.d("Display", cipher+" ===== "+dec);
//
//
//        cipher =  concealCrypto.obscureWithIteration(test,4); //encrypt with 4 times
//        Log.d("Display", cipher);
//        dec = concealCrypto.deObscureWithIteration(cipher,3); //decrypt with 4 times
//        Log.d("Display", cipher+" ===== "+dec);
    }
}
