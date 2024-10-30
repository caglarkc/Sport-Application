package com.example.caglarkc;

import java.util.ArrayList;
import java.util.List;

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
