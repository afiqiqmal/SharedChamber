package com.zeroone.conceal.helper;

import java.util.Arrays;
import java.util.List;

/**
 * @author : hafiq on 23/03/2017.
 */

public class ArrayUtils {

    public static List<Integer> toIntArray(String string) {
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

    public static List<Boolean> toBooleanArray(String string) {
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

    public static List<Long> toLongArray(String string) {
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

    public static List<Double> toDoubleArray(String string) {
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

    public static List<Float> toFloatArray(String string) {
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

    public static List<String> toStringArray(String string) {
        return Arrays.asList(string.replace("[", "").replace("]", "").split(", "));
    }
}
