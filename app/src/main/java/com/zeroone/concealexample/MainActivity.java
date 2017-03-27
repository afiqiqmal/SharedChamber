package com.zeroone.concealexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;

import com.zeroone.conceal.ConcealPrefRepository;
import com.zeroone.conceal.helper.CryptoFile;

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

        File getFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.files/here.pdf");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        new ConcealPrefRepository.Editor()
                .putString(NAME_KEY, "Hafiq Iqmal")
                .putInt(AGE_KEY, 24)
                .putString(EMAIL_KEY, "hafiqiqmal93@gmail.com")
                .putImage(IMAGE_KEY, BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .putFile(FILE_KEY, getFile, true)
                .apply();


        Map<String,String> map =concealPrefRepository.getAllSharedPrefData();
        for(Map.Entry<String,?> entry : map.entrySet()){
            try {
                Log.d("TAG",entry.getKey()+" :: "+entry.getValue().toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        List<CryptoFile> cryptoFileList = concealPrefRepository.getAllConcealEncryptedFiles();
        for (CryptoFile cryptoFile :cryptoFileList){
            Log.d("TAG",cryptoFile.getFileName());
        }

        Log.d("TAG",""+concealPrefRepository.getPrefsSize());
    }
}
