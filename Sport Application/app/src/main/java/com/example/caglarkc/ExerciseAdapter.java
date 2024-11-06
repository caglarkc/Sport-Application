package com.example.caglarkc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
/**
 * ExerciseAdapter: A custom adapter for displaying a list of exercises in a ListView or GridView.
 * It provides the exercise name and image for each item in the list.
 * When the exercise name is "Choose Exercise", the image visibility is set to invisible to highlight it as a placeholder.
 * This adapter inflates the `item_exercise` layout for each item in the list.
 */

public class ExerciseAdapter extends BaseAdapter {
    private Context context;
    private List<Exercise> exerciseList;

    public ExerciseAdapter(Context context , List<Exercise> exerciseList){
        this.context = context;
        this.exerciseList = exerciseList;
    }

    @Override
    public int getCount() {
        return exerciseList != null ? exerciseList.size() : 0 ;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_exercise,viewGroup, false);

        TextView textName = rootView.findViewById(R.id.name);
        ImageView image = rootView.findViewById(R.id.image);

        textName.setText(exerciseList.get(i).getName());
        image.setImageResource(exerciseList.get(i).getImage());

        if (exerciseList.get(i).getName().equals("Choose Exercise")){
            image.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }
}
