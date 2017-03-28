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

    public static File makeFile(File dir,String filename){
        File file = new File(dir,filename);
        try {
            if (file.exists())
                file.delete();

            if (file.createNewFile()){
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public static String getExtension(File file){
        return file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
    }

    public static String getExtension(String file){
        return file.substring(file.lastIndexOf("."));
    }


    public static File moveFile(File file, File dir){
        if (!dir.exists())
            dir.mkdirs();

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

    public static byte[] readContentIntoByteArray(File file)
    {
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try
        {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
            for (int i = 0; i < bFile.length; i++)
            {
                System.out.print((char) bFile[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bFile;
    }


    public static boolean saveBitmap(File imageFile, Bitmap bitmap) {

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

    public static byte[] convertBitmapToArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap convertBytesToBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
    }

    public static boolean isFileForImage(File file) {
        final String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg"};

        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

}
