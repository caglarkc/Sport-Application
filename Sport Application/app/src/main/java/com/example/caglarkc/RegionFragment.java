package com.example.caglarkc;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
/**
 * RegionFragment: Displays exercises for a selected muscle region.
 * - Retrieves and displays exercises based on the selected region passed via arguments.
 * - Each exercise is shown as a clickable button, which allows navigation back to the exercise list with details of the selected exercise.
 * - Layout adjustments ensure proper alignment and spacing for different screen sizes.
 */


public class RegionFragment extends Fragment {
    public RegionFragment () {}

    DatabaseReference mReferenceRegionExercises;

    LinearLayout exerciseContainer;

    int thirtyPixel, thirtyDp = 30, threeHundredPixel, threeHundredDp = 300;
    String regionName;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_region, container, false);

        Bundle args = getArguments();
        if (args != null) {
            regionName = args.getString("region_name");
        }

        mReferenceRegionExercises = FirebaseDatabase.getInstance().getReference("Exercises").child(regionName);
        exerciseContainer = view.findViewById(R.id.exerciseContainer);



        thirtyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                thirtyDp,
                getResources().getDisplayMetrics()
        );
        threeHundredPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                threeHundredDp,
                getResources().getDisplayMetrics()
        );

        mReferenceRegionExercises.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot exerciseSnapshot : snapshot.getChildren()) {
                    String exerciseName = exerciseSnapshot.getKey();
                    RelativeLayout relativeLayout = new RelativeLayout(getContext());
                    relativeLayout = createRegionLayout(exerciseName,regionName);

                    exerciseContainer.addView(relativeLayout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }


    private RelativeLayout createRegionLayout(String name, String region){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.bottomMargin = 15 ;

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams nameButtonParams = new RelativeLayout.LayoutParams(threeHundredPixel,thirtyPixel);
        nameButtonParams.addRule(RelativeLayout.ALIGN_PARENT_START);

        Button buttonExerciseName = new Button(getContext());
        buttonExerciseName.setLayoutParams(nameButtonParams);
        buttonExerciseName.setPadding(0,0,0,0);
        buttonExerciseName.getBackground().setTintList(null);
        buttonExerciseName.setBackgroundResource(R.drawable.border);
        buttonExerciseName.setTextColor(Color.WHITE);
        buttonExerciseName.setAllCaps(false);
        buttonExerciseName.setText(name);

        buttonExerciseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ExerciseListActivity) requireActivity()).getBackFragment();
                ((ExerciseListActivity) requireActivity()).goExerciseDetail(name, region);

            }
        });


        relativeLayout.addView(buttonExerciseName);

        return relativeLayout;
    }

}
