package com.zeroone.concealexample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zeroone.conceal.ConcealPrefRepository;
import com.zeroone.conceal.OnDataChangeListener;
import com.zeroone.conceal.model.CryptoType;

/**
 * @author : hafiq on 27/03/2017.
 */

public class BaseActivity extends AppCompatActivity implements OnDataChangeListener {

    ConcealPrefRepository concealPrefRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        concealPrefRepository = new ConcealPrefRepository.PreferencesBuilder(this)
                .sharedPrefsBackedKeyChain(CryptoType.KEY_256)
                .enableCrypto(true,true)
                .enableKeyPrefix(true,"walaoweh")
                .createPassword("Password@123")
                .setPrefListener(this)
                .create();
    }

    @Override
    public void onDataChange(String key,String value) {
        Log.d("DATACHANGE",key+" :: "+value);
    }
}
