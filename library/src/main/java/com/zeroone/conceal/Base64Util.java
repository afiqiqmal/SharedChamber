package com.zeroone.conceal;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * @author : hafiq on 29/03/2017.
 */

class Base64Util {

    static String Base64String(byte[] plaintext){
        return Base64.encodeToString(plaintext, Base64.DEFAULT);
    }

    static byte[] Base64bytes(byte[] plaintext){
        return Base64.encode(plaintext, Base64.DEFAULT);
    }

    static byte[] decodeBase64(String cipher){
        return Base64.decode(cipher, Base64.DEFAULT);
    }

    static byte[] decodeBase64(byte[] cipher){
        return Base64.decode(cipher, Base64.DEFAULT);
    }

    static String encode64WithIteration(String plaintext,int iteration){
        try {
            byte[] dataDec = plaintext.getBytes("UTF-8");
            for (int x=0;x<iteration;x++){
                dataDec = Base64bytes(dataDec);
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
                dataDec = decodeBase64(dataDec);
            }

            return new String(dataDec);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
