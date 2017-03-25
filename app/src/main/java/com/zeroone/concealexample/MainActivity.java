package com.zeroone.concealexample;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.crypto.CryptoConfig;
import com.zeroone.conceal.ConcealPrefRepository;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ConcealPrefRepository concealPrefRepository = new ConcealPrefRepository.PreferencesBuilder(this)
                .useDefaultPrefStorage()
                .sharedPrefsBackedKeyChain(CryptoConfig.KEY_256)
                .enableCrypto(true,true)
                .createPassword("Android")
                .setFolderName("testing")
                .create();

////        File getFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/.files/here.pdf");
//
//        new ConcealPrefRepository.Editor()
//                .putString("Bellow","Hello")
//                .putInt("Number",1000000)
//                .putBoolean("enable",true)
//                .putImage("images", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
////                .putFile("filing",getFile,true)
//                .apply();
//
//        Log.d("DisplayPref",concealPrefRepository.getString("Bellow"));
//        Log.d("DisplayPref",String.valueOf(concealPrefRepository.getInt("Number")));
//        Log.d("DisplayPref",concealPrefRepository.getString("images"));
//        Log.d("DisplayPref",concealPrefRepository.getString("filing"));

//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/.testing/files/conceal_enc_here.pdf");
        File file = concealPrefRepository.getFile("filing",true);
        Log.d("TAG",file.getAbsolutePath());

//        String test = "My Name Hafiq";
//        String test2 = "Live in Semenyih";
//
//        ConcealCrypto concealCrypto = new ConcealCrypto(this,CryptoConfig.KEY_256);
//        concealCrypto.setEnableCrypto(true);
//        concealCrypto.setEntityPassword("Android");
//        concealCrypto.setEntityPassword(Entity.create("Android"));
//
//        String cipher =  concealCrypto.obscure(test);
//        String cipher2 =  concealCrypto.obscure(test2);
//
//        Log.d("DisplayCC", "Name: "+concealCrypto.obscure(test)+" ===== "+concealCrypto.deObscure(cipher));
//        Log.d("DisplayCC", "Live: "+concealCrypto.obscure(test2)+" ===== "+concealCrypto.deObscure(cipher2));
//
//
//        ConcealCrypto concealCrypto1 = new ConcealCrypto.CryptoBuilder(this)
//                .setEnableCrypto(true)
//                .setKeyChain(CryptoConfig.KEY_256)
//                .createPassword("Mac OSX")
//                .create();
//
//        cipher =  concealCrypto1.obscure(test);
//        cipher2 =  concealCrypto1.obscure(test2);
//
//        Log.d("DisplayCC2", "Name: "+concealCrypto1.obscure(test)+" ===== "+concealCrypto1.deObscure(cipher));
//        Log.d("DisplayCC2", "Live: "+concealCrypto1.obscure(test2)+" ===== "+concealCrypto1.deObscure(cipher2));
    }
}
