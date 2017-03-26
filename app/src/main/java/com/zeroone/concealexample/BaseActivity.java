package com.zeroone.concealexample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.facebook.crypto.CryptoConfig;
import com.zeroone.conceal.ConcealPrefRepository;

/**
 * @author : hafiq on 27/03/2017.
 */

public class BaseActivity extends AppCompatActivity {

    ConcealPrefRepository concealPrefRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        concealPrefRepository = new ConcealPrefRepository.PreferencesBuilder(this)
                .useDefaultPrefStorage()
                .sharedPrefsBackedKeyChain(CryptoConfig.KEY_256)
                .enableCrypto(true,true)
                .createPassword("Android")
                .setFolderName("testing")
                .create();
    }
}
