package com.zeroone.conceal;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * @author : hafiq on 29/03/2017.
 */

class CipherUtils {

    static String obscureEncodeSixFourString(byte[] plaintext){
        return Base64.encodeToString(plaintext, Base64.DEFAULT);
    }

    static byte[] obscureEncodeSixFourBytes(byte[] plaintext){
        return Base64.encode(plaintext, Base64.DEFAULT);
    }

    static byte[] deObscureSixFour(String cipher){
        return Base64.decode(cipher, Base64.DEFAULT);
    }

    static byte[] deObscureSixFour(byte[] cipher){
        return Base64.decode(cipher, Base64.DEFAULT);
    }

    static String encode64WithIteration(String plaintext,int iteration){
        try {
            byte[] dataDec = plaintext.getBytes("UTF-8");
            for (int x=0;x<iteration;x++){
                dataDec = obscureEncodeSixFourBytes(dataDec);
            }

            return new String(dataDec);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return plaintext;
    }

    static String decode64WithIteration(String plaintext,int iteration){
        try {
            byte[] dataDec = plaintext.getBytes("UTF-8");
            for (int x=0;x<iteration;x++){
                dataDec = deObscureSixFour(dataDec);
            }

            return new String(dataDec);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
