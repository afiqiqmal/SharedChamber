package com.zeroone.conceal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author : hafiq on 24/03/2017.
 */

class FileUtils {

    static File moveFile(File file, File dir){
        if (dir == null || file == null)
            return null;

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                File newFile = new File(dir, file.getName());
                FileChannel outputChannel;
                FileChannel inputChannel;
                try {
                    outputChannel = new FileOutputStream(newFile).getChannel();
                    inputChannel = new FileInputStream(file).getChannel();
                    inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                    inputChannel.close();

                    inputChannel.close();
                    outputChannel.close();

                    file.delete();

                    Log.d("Files","File have been moved to : "+newFile.getAbsolutePath());
                }
                catch (Exception e){
                    return null;
                }

                return newFile;
            }
        }

        return file;
    }

    static boolean saveBitmap(File imageFile, Bitmap bitmap) {

        boolean fileCreated = false;
        boolean bitmapCompressed = false;
        boolean streamClosed = false;

        if (imageFile.exists())
            if (!imageFile.delete())
                return false;

        try {
            fileCreated = imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
            bitmapCompressed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (Exception e) {
            e.printStackTrace();
            bitmapCompressed = false;

        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                    streamClosed = true;

                } catch (IOException e) {
                    e.printStackTrace();
                    streamClosed = false;
                }
            }
        }

        return (fileCreated && bitmapCompressed && streamClosed);
    }

    static boolean isFileForImage(File file) {
        if (file == null)
            return false;

        final String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg"};

        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

}
