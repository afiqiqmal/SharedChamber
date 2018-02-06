package com.chamber.java.sample;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.chamber.java.library.SecretBuilder;
import com.chamber.java.library.SharedChamber;
import com.chamber.java.library.SecretChamber;
import com.chamber.java.library.model.ChamberType;
import com.zeroone.concealexample.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    String NAME_KEY = "user_name";
    String AGE_KEY = "user_age";
    String EMAIL_KEY = "user_email";
    String USER_DETAIL = "user_detail";
    String TASK_DETAIL = "task_detail";
    String IMAGE_KEY = "user_image";
    String FILE_KEY = "user_file";
    String PREFIX = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedChamber.initChamber(getApplication());

        chamber.clearChamber();

        List<Float> floats = new ArrayList<>();
        floats.add(10f);
        floats.add(10f);
        floats.add(10f);

        //FIRST TEST
        chamber.put(NAME_KEY, "HAFIQ IQMAL");
        chamber.put(AGE_KEY, floats);
        chamber.putModel(USER_DETAIL, Data.getUser(this));
        chamber.putModel(TASK_DETAIL, Data.getTaskData(this));

        Log.d("FIRST TEST", chamber.getString(NAME_KEY));
        Log.d("FIRST TEST", chamber.getListFloat(AGE_KEY).toString());
        Log.d("FIRST TEST", chamber.getModel(USER_DETAIL, User.class).toString());
        Log.d("FIRST TEST", chamber.getModel(TASK_DETAIL, new TypeToken<ArrayList<Task>>(){}.getType()).toString());
        Log.d("FIRST TEST SIZE", ""+ chamber.getChamberSize());

        chamber.clearChamber();

        //SECOND TEST
        new SharedChamber.Editor()
                .put(NAME_KEY, "Hafiq Iqmal")
                .put(AGE_KEY, 24)
                .put(EMAIL_KEY, "hafiqiqmal93@gmail.com")
                .apply();

        Log.d("SECOND TEST", chamber.getString(NAME_KEY));
        Log.d("SECOND TEST", chamber.getString(AGE_KEY));
        Log.d("SECOND TEST SIZE", ""+ chamber.getChamberSize());

        getList();


        //add user details preferences
        new SharedChamber.UserChamber(PREFIX).setFirstName("Firstname").setLastName("Lasname").setEmail("hello@gmail.com").apply();

        //get user details
        Log.d("THIRD_TEST", new SharedChamber.UserChamber(PREFIX).getFirstName());
        Log.d("THIRD_TEST", new SharedChamber.UserChamber().setDefault("No Data").getLastName());
        Log.d("THIRD_TEST", new SharedChamber.UserChamber(PREFIX).setDefault("No Data").getEmail());
        Log.d("THIRD_TEST TEST SIZE", ""+ chamber.getChamberSize());

        getList();


        SharedChamber.UserChamber userPref = new SharedChamber.UserChamber(PREFIX, "No Data");
        userPref.setUserName("afiqiqmal");
        userPref.setEmail("afiqiqmal@example.com");
        userPref.apply();

        //get user details
        Log.d("FOURTH_TEST",userPref.getUserName());
        Log.d("FOURTH_TEST",userPref.getEmail());

        getList();


        SharedChamber.DeviceChamber devicePref = new SharedChamber.DeviceChamber(PREFIX, "No Data");
        devicePref.setDeviceId("ABC123123123");
        devicePref.setDeviceOS("android");
        devicePref.apply();

        //get user details
        Log.d("FIFTH_TEST",devicePref.getDeviceId());
        Log.d("FIFTH_TEST",devicePref.getDeviceOs());


        getList();

        SecretChamber secretChamber = new SecretBuilder(this)
                .setEnableValueEncryption(true) //default true
                .setEnableKeyEncryption(true) // default true
                .setChamberType(ChamberType.KEY_256) // ChamberType.KEY_256 or ChamberType.KEY_128
                .setPassword("Mac OSX")
                .buildSecret();

        String test = "Hello World";
        String cipher =  secretChamber.lockVault(test); //encrypt
        Log.d("CYRPTO TEST E", cipher);
        String dec = secretChamber.openVault(cipher); //decrypt
        Log.d("CYRPTO TEST D", dec);

        test = "Hello World Iteration";
        cipher =  secretChamber.lockVaultBase(test,4); //encrypt with 4 times
        Log.d("CYRPTO TEST E", cipher);
        dec = secretChamber.openVaultBase(cipher,4); //decrypt with 4 times
        Log.d("CYRPTO TEST D", dec);



        cipher =  secretChamber.lockVaultAes("Hello World is World Hello Aes Cryption");
        Log.d("AES E", cipher);
        dec = secretChamber.openVaultAes(cipher);
        Log.d("AES D", dec);
    }


    private void getList() {
        List<String> mapList = chamber.getEverythingInChamberInList();
        for(String s: mapList){
            try {
                Log.d("VIEW_LIST", s);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        Log.d("VIEW ALL SIZE", ""+ chamber.getChamberSize());
    }
}
