package com.zeroone.concealexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.zeroone.conceal.ConcealCrypto;
import com.zeroone.conceal.ConcealPrefRepository;

public class MainActivity extends AppCompatActivity {
    byte[] a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ConcealPrefRepository concealPrefRepository = new ConcealPrefRepository.PreferencesBuilder(this)
                .useDefaultPrefStorage()
                .sharedPrefsBackedKeyChain(CryptoConfig.KEY_256)
                .enableCrypto(true,true)
                .createPassword("Android")
                .create();



        new ConcealPrefRepository.Editor()
                .putString("Bellow","Hello")
                .putInt("Number",1000000)
                .putBoolean("enable",true)
                .apply();


        Log.d("DisplayPref",concealPrefRepository.getString("Bellow"));
        Log.d("DisplayPref", String.valueOf(concealPrefRepository.getInt("Number")));


        String test = "My Name Hafiq";
        String test2 = "Live in Semenyih";

        ConcealCrypto concealCrypto = new ConcealCrypto(this,CryptoConfig.KEY_256);
        concealCrypto.setEnableCrypto(true);
        concealCrypto.setEntityPassword("Android");
        concealCrypto.setEntityPassword(Entity.create("Android"));

        String cipher =  concealCrypto.obscure(test);
        String cipher2 =  concealCrypto.obscure(test2);

        Log.d("DisplayCC", "Name: "+concealCrypto.obscure(test)+" ===== "+concealCrypto.deObscure(cipher));
        Log.d("DisplayCC", "Live: "+concealCrypto.obscure(test2)+" ===== "+concealCrypto.deObscure(cipher2));


        ConcealCrypto concealCrypto1 = new ConcealCrypto.CryptoBuilder(this)
                .setEnableCrypto(true)
                .setKeyChain(CryptoConfig.KEY_256)
                .createPassword("Mac OSX")
                .create();

        cipher =  concealCrypto1.obscure(test);
        cipher2 =  concealCrypto1.obscure(test2);

        Log.d("DisplayCC2", "Name: "+concealCrypto1.obscure(test)+" ===== "+concealCrypto1.deObscure(cipher));
        Log.d("DisplayCC2", "Live: "+concealCrypto1.obscure(test2)+" ===== "+concealCrypto1.deObscure(cipher2));

    }
}
