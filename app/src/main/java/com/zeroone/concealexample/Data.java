package com.zeroone.concealexample;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hafiq on 15/12/2017.
 */

public class Data {

    public static List<Task> getTaskData(Context context) {
        return new Gson().fromJson(loadJSONFromAsset(context, "task.json"), new TypeToken<ArrayList<Task>>(){}.getType());
    }

    public static User getUser(Context context) {
        return new Gson().fromJson(loadJSONFromAsset(context, "users.json"), User.class);
    }

    public static String loadJSONFromAsset(Context context, String filename) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
