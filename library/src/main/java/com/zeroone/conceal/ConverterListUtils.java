package com.zeroone.conceal;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : hafiq on 23/03/2017.
 */

class ConverterListUtils {

    static List<Integer> toIntArray(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        Integer result[] = new Integer[strings.length];
        try {
            for (int i = 0; i < result.length; i++) {
                result[i] = Integer.parseInt(strings[i]);
            }
            return Arrays.asList(result);
        }
        catch (Exception e){
            return null;
        }
    }

    static List<Boolean> toBooleanArray(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        Boolean result[] = new Boolean[strings.length];
        try {
            for (int i = 0; i < result.length; i++) {
                result[i] = Boolean.parseBoolean(strings[i]);
            }
            return Arrays.asList(result);
        }
        catch (Exception e){
            return null;
        }
    }

    static List<Long> toLongArray(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        Long result[] = new Long[strings.length];
        try {
            for (int i = 0; i < result.length; i++) {
                result[i] = Long.parseLong(strings[i]);
            }
            return Arrays.asList(result);
        }
        catch (Exception e){
            return null;
        }

    }

    static List<Double> toDoubleArray(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        Double result[] = new Double[strings.length];
        try {
            for (int i = 0; i < result.length; i++) {
                result[i] = Double.parseDouble(strings[i]);
            }

            return Arrays.asList(result);
        }
        catch (Exception e){
            return null;
        }
    }

    static List<Float> toFloatArray(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        Float result[] = new Float[strings.length];
        try {
            for (int i = 0; i < result.length; i++) {
                result[i] = Float.parseFloat(strings[i]);
            }
            return Arrays.asList(result);
        }
        catch (Exception e){
            return null;
        }
    }

    static List<String> toStringArray(String string) {
        return Arrays.asList(string.replace("[", "").replace("]", "").split(", "));
    }


    static String convertMapToString(Map<String,String> maps){
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : maps.keySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            String value = maps.get(key);
            try {
                stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
                stringBuilder.append("=");
                stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires UTF-8 encoding support", e);
            }
        }

        return stringBuilder.toString();
    }

    static LinkedHashMap<String,String> convertStringToMap(String input){
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        String[] nameValuePairs = input.split("&");
        for (String nameValuePair : nameValuePairs) {
            String[] nameValue = nameValuePair.split("=");
            try {
                map.put(URLDecoder.decode(nameValue[0], "UTF-8"), nameValue.length > 1 ? URLDecoder.decode(nameValue[1], "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires UTF-8 encoding support", e);
            }
        }

        return map;
    }
}
