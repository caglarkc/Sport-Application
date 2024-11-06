package com.example.caglarkc;

import java.util.ArrayList;
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

    public Food(String name, int carbVal, int fatVal, int proVal, int calVal, String imageUrl) {
        this.name = name;
        this.carbVal = carbVal;
        this.fatVal = fatVal;
        this.proVal = proVal;
        this.calVal = calVal;
        this.imageUrl = imageUrl;

        foodList.add(this);
    }


    public String getName() {
        return name;
    }

    public int getCarbVal() {
        return carbVal;
    }

    public int getFatVal() {
        return fatVal;
    }

    public int getProVal() {
        return proVal;
    }

    public int getCalVal() {
        return calVal;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public static List<Food> getFoodList() {
        return foodList;
    }
}
