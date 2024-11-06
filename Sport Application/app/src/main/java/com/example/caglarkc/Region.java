package com.example.caglarkc;

import java.util.ArrayList;
import java.util.List;
/**
 * Region: Represents a specific body region with a name and associated icon.
 * - Based on the region name, a specific image resource is assigned.
 * - Holds a static list of all Region instances created.
 * - Includes methods to get and set the name and image of the region, as well as retrieve the full list of regions.
 */

public class Region {
    private String name;
    private int image;
    static List<Region> regionList = new ArrayList<>();

    public Region(String name){
        this.name = name;
        if (name.equals("Chest")){
            this.image = R.drawable.chest_icon;
        }else if (name.equals("Back")){
            this.image = R.drawable.back_muscle_icon;
        }else if (name.equals("Shoulder")){
            this.image = R.drawable.shoulder_icon;
        }else if (name.equals("Leg")){
            this.image = R.drawable.leg_icon;
        }else if (name.equals("Abs")){
            this.image = R.drawable.abs_icon;
        }else if (name.equals("Biceps")){
            this.image = R.drawable.biceps_icon;
        }else if (name.equals("Triceps")){
            this.image = R.drawable.triceps_icon;
        }else if (name.equals("Choose Region")){
            this.image = 0;
        }
    }

    public static List<Region> getRegionList(){
        return regionList;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
