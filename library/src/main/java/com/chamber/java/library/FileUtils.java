package com.chamber.java.library;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.chamber.java.library.model.CryptoFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static com.chamber.java.library.model.Constant.DEFAULT_DIRECTORY;
import static com.chamber.java.library.model.Constant.DEFAULT_IMAGE_FOLDER;
import static com.chamber.java.library.model.Constant.DEFAULT_PREFIX_FILENAME;

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
        boolean bitmapCompressed;
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

    /***
     * get default directory
     * @return File
     */
    @Nullable
    static File getDirectory(String mFolderName){
        File file = new File(DEFAULT_DIRECTORY+mFolderName+"/"+DEFAULT_IMAGE_FOLDER);
        if (file.exists())
            return file;

        return null;
    }

    /***
     * get default folder
     * @return File
     */
    @Nullable
    static File getImageDirectory(String mFolderName){
        File file = new File(DEFAULT_DIRECTORY+mFolderName+"/"+DEFAULT_IMAGE_FOLDER);
        if (file.mkdirs())
            return file;
        if (file.exists())
            return file;

        return null;
    }

    /***
     * get List of encrypted file
     * @param parentDir - root directory
     * @return File
     */
    static List<CryptoFile> getListFiles(@Nullable File parentDir) {
        List<CryptoFile> inFiles = new ArrayList<>();
        try {
            if (parentDir!=null) {
                File[] files = parentDir.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        inFiles.addAll(getListFiles(file));
                    } else {
                        if (file.getName().startsWith(DEFAULT_PREFIX_FILENAME)) {
                            CryptoFile cryptoFile = new CryptoFile();
                            cryptoFile.setFileName(file.getName());
                            cryptoFile.setPath(file.getAbsolutePath());
                            cryptoFile.setType(file.getParent());
                            inFiles.add(cryptoFile);
                        }
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return inFiles;
    }

}
