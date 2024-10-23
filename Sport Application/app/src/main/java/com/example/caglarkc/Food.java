package com.example.caglarkc;

import java.util.ArrayList;
import java.util.List;

public class Food {
    String name;
    int carbVal, fatVal, proVal, calVal;
    int image;
    List<Food> foodList = new ArrayList<>();

    public Food(String name, int carbVal, int fatVal, int proVal, int calVal, int image) {
        this.name = name;
        this.carbVal = carbVal;
        this.fatVal = fatVal;
        this.proVal = proVal;
        this.calVal = calVal;
        this.image = image;

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

    public int getImage() {
        return image;
    }

    public List<Food> getFoodList() {
        return foodList;
    }
}
