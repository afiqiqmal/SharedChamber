package com.zeroone.concealexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.crypto.CryptoConfig;
import com.zeroone.conceal.ConcealPrefRepository;
import com.zeroone.conceal.OnDataChangeListener;

/**
 * @author : hafiq on 27/03/2017.
 */

public class BaseActivity extends AppCompatActivity implements OnDataChangeListener {

    ConcealPrefRepository concealPrefRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        concealPrefRepository = new ConcealPrefRepository.PreferencesBuilder(this)
                .useDefaultPrefStorage()
                .sharedPrefsBackedKeyChain(CryptoConfig.KEY_256)
                .enableCrypto(false,true)
                .createPassword("Android")
                .setFolderName("testing")
                .setPrefListener(this)
                .create();

    }

    @Override
    public void onDataChange(String key,String value) {

    }
}
