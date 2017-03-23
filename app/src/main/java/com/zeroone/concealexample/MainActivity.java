package com.zeroone.concealexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.KeyChain;
import com.zeroone.conceal.ConcealPrefRepository;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    byte[] a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ConcealPrefRepository concealPrefRepository = new ConcealPrefRepository.PreferencesBuilder(this)
                .useDefaultPrefStorage()
                .createCryptoBits(CryptoConfig.KEY_256)
                .SharedPrefsBackedKeyChain(CryptoConfig.KEY_256)
                .enableCrypto(true,true)
                .createPassword("Android")
                .create();


        new ConcealPrefRepository.Editor()
                .putString("Bellow","Hello")
                .putInt("Number",1000000)
                .apply();


        Log.d("Display",concealPrefRepository.getString("Bellow"));
        Log.d("Display", String.valueOf(concealPrefRepository.getInt("Number")));

    }
}
