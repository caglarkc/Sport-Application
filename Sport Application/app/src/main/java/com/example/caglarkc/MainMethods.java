package com.example.caglarkc;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * MainMethods: A utility class providing core methods for password security, data handling, and user preferences.
 * - `isPasswordStrongEnough`: Verifies if a password meets security requirements (minimum length, contains uppercase, lowercase, and numeric characters).
 * - `hashPassword`: Hashes a given password using SHA-256 to ensure secure storage.
 * - `setDayDataHashMap` & `returnDayDataHashMap`: Stores and retrieves daily data as key-value pairs.
 * - `getDietPlanChoose` & `setDietPlanChoose`: Manages a boolean flag indicating whether a diet plan has been selected.
 */

public class MainMethods {
    static HashMap<String , String> dayDataHashMap = new HashMap<>();
    static Boolean dietPlanChoose;
    static List<String> dietPlanNamesList = new ArrayList<>();
    static HashMap<String , Integer> hashMapFoodCalories = new HashMap<>();

    public static boolean isPasswordStrongEnough(String password){
        if (password.length()<8){
            return false;
        }
        else if (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*[0-9].*")) {
            return false;
        }
        else {
            return true;
        }
    }

    public static String hashPassword(String password) {
        try {
            // SHA-256 hashing algoritmasını kullanarak bir MessageDigest örneği oluşturun
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Kullanıcı şifresini byte dizisine dönüştürün ve hash'leyin
            byte[] hashedBytes = digest.digest(password.getBytes());

            // Byte dizisini hexadecimal formatına dönüştürün
            StringBuilder hexString = new StringBuilder();
            for (byte hashedByte : hashedBytes) {
                String hex = Integer.toHexString(0xff & hashedByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // Hexadecimal string'i döndürün (hash değeri)
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Hashing algoritması bulunamazsa null döndürün (hata durumu)
            return null;
        }
    }

    public static void setDayDataHashMap(HashMap<String , String> hashMap) {
        if (dayDataHashMap != null) {
            dayDataHashMap.putAll(hashMap);
        }else {
            dayDataHashMap = hashMap;
        }

    }

    public static HashMap<String , String> returnDayDataHashMap() {

        return dayDataHashMap;
    }

    public static Boolean getDietPlanChoose() {
        return dietPlanChoose;
    }

    public static void setDietPlanChoose(Boolean choose) {
        dietPlanChoose = choose;
    }

    public static List<String> getDietPlanNamesList() {
        return dietPlanNamesList;
    }

    public static void setDietPlanNamesList(List<String> list) {
        dietPlanNamesList = list;
    }

    public static HashMap<String, Integer> getHashMapFoodCalories() {
        return hashMapFoodCalories;
    }

    public static void setHashMapFoodCalories(HashMap<String, Integer> hashMap) {
        hashMapFoodCalories = hashMap;
    }
}
