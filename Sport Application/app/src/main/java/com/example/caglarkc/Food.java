package com.example.caglarkc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Food: Represents a food item with nutritional values including carbohydrates, fat, protein, and calories,
 * along with an image URL and name. Each food item is stored in a static list, `foodList`, for easy access
 * and retrieval across the application. This class provides getter methods for each attribute and manages
 * the static list of all food instances.
 */

public class Food {
    String name, imageUrl;
    int carbVal, fatVal, proVal, calVal;
    static List<Food> foodList = new ArrayList<>();
    static HashMap<String , Food> foodHashMap = new HashMap<>();

    public Food(String name, int carbVal, int fatVal, int proVal, int calVal, String imageUrl) {
        this.name = name;
        this.carbVal = carbVal;
        this.fatVal = fatVal;
        this.proVal = proVal;
        this.calVal = calVal;
        this.imageUrl = imageUrl;

        foodList.add(this);
        foodHashMap.put(name,this);
    }


    public String getName() {
        return name;
    }

    public static int getCarbVal(String name) {
        Food food = foodHashMap.get(name);
        if (food != null) {
            return food.carbVal;
        }
        return -1;
    }

    public static int getFatVal(String name) {
        Food food = foodHashMap.get(name);
        if (food != null) {
            return food.fatVal;
        }
        return -1;
    }

    public static int getProVal(String name) {
        Food food = foodHashMap.get(name);
        if (food != null) {
            return food.proVal;
        }
        return -1;
    }

    public static int getCalVal(String name) {
        Food food = foodHashMap.get(name);
        if (food != null) {
            return food.calVal;
        }
        return -1;
    }

    public static String getImageUrl(String name) {
        Food food = foodHashMap.get(name);
        if (food != null) {
            return food.imageUrl;
        }
        return "";
    }

    public static List<Food> getFoodList() {
        return foodList;
    }
}
