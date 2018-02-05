package com.zeroone.concealexample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zeroone.conceal.SharedChamber;
import com.zeroone.conceal.listener.OnDataChamberChangeListener;
import com.zeroone.conceal.model.ChamberType;

/**
 * @author : hafiq on 27/03/2017.
 */

public class BaseActivity extends AppCompatActivity implements OnDataChamberChangeListener {

    SharedChamber chamber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chamber = new SharedChamber.ChamberBuilder(this)
                .setChamberType(ChamberType.KEY_256)
                .enableCrypto(true,true)
                .enableKeyPrefix(true,"walaoweh")
                .setPassword("Password@123")
                .setPrefListener(this)
                .buildChamber();
    }

    @Override
    public void onDataChange(String key,String value) {
        Log.d("DATACHANGE",key+" :: "+value);
    }
}
