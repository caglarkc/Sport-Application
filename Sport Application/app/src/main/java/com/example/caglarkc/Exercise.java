package com.example.caglarkc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Exercise implements Serializable {
    private String name, region;
    private int image;
    static List<Exercise> exerciseList = new ArrayList<>();

    public Exercise(String name, String region){
        this.name = name;
        this.region = region;
        if (region.equals("Chest")){
            this.image = R.drawable.chest_icon;
        }else if (region.equals("Back")){
            this.image = R.drawable.back_muscle_icon;
        }else if (region.equals("Shoulder")){
            this.image = R.drawable.shoulder_icon;
        }else if (region.equals("Leg")){
            this.image = R.drawable.leg_icon;
        }else if (region.equals("Abs")){
            this.image = R.drawable.abs_icon;
        }else if (region.equals("Biceps")){
            this.image = R.drawable.biceps_icon;
        }else if (region.equals("Triceps")){
            this.image = R.drawable.triceps_icon;
        }else if (region.equals("Null")){
            this.image = R.drawable.exercise_icon;
        }
    }

    public static List<Exercise> getExerciseList(){
        return exerciseList;
    }
    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
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
