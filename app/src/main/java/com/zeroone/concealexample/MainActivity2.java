package com.zeroone.concealexample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.keychain.KeyChain;

/**
 * @author : hafiq on 23/03/2017.
 */

public class MainActivity2 extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KeyChain keyChain = new SharedPrefsBackedKeyChain(this, CryptoConfig.KEY_256);
        Crypto crypto = AndroidConceal.get().createCrypto256Bits(keyChain);

        try {
            byte[] b = crypto.decrypt(getIntent().getByteArrayExtra("value"),Entity.create("woi"));
            Log.d("Hellob",new String(b));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
