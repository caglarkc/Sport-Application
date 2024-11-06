package com.example.caglarkc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
/**
 * ExerciseListActivity: This activity displays a list of exercises grouped by muscle regions and allows users to view details of each exercise.
 * Users can add new exercises or filter exercises by specific muscle regions (e.g., Chest, Back, Legs).
 * The activity dynamically generates buttons for each exercise and provides options to view exercise details or explore exercises by region.
 * By selecting a region, users can load and view exercises specific to that body part, which is handled using fragments and animations for a smooth transition.
 */

public class ExerciseListActivity extends AppCompatActivity {
    DatabaseReference mReferenceExercises;

    Button buttonAddExercise, buttonCheckRegion;
    LinearLayout container, buttonContainer, firstPartContainer, secondPartContainer;
    RelativeLayout capitalContainer, activityContainer;
    TextView textViewCapital;
    ProgressBar progressBar;

    Intent intentSuccessfully;

    List<String> listChest = new ArrayList<>(), listBack = new ArrayList<>(), listLeg = new ArrayList<>(),
            listShoulder = new ArrayList<>(), listTriceps = new ArrayList<>(), listBiceps = new ArrayList<>(),
            listAbdomen = new ArrayList<>();
    int twoHundredTwentyDp = 220, twoHundredTwentyPixel, threeHundredDp = 300, threeHundredPixel, thirtyDp = 30, thirtyPixel, ninetyDp = 90, ninetyPixel, tenDp = 10, tenPixel;
    String exerciseName, exerciseRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mReferenceExercises = FirebaseDatabase.getInstance().getReference("Exercises");
        intentSuccessfully = new Intent(ExerciseListActivity.this,UserMenuActivity.class);

        buttonAddExercise = findViewById(R.id.buttonAddExercise);
        firstPartContainer = findViewById(R.id.firstPartContainer);
        secondPartContainer = findViewById(R.id.secondPartContainer);
        buttonCheckRegion = findViewById(R.id.buttonCheckRegion);
        container = findViewById(R.id.container);
        buttonContainer = findViewById(R.id.buttonContainer);
        textViewCapital = findViewById(R.id.textViewCapital);
        capitalContainer = findViewById(R.id.capitalContainer);
        activityContainer = findViewById(R.id.activityContainer);
        progressBar = findViewById(R.id.progressBar);

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
        tenPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                tenDp,
                getResources().getDisplayMetrics()
        );
        twoHundredTwentyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                twoHundredTwentyDp,
                getResources().getDisplayMetrics()
        );
        ninetyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                ninetyDp,
                getResources().getDisplayMetrics()
        );

        takeExercises();

        buttonCheckRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capitalContainer.setVisibility(View.GONE);
                container.removeAllViews();
                buttonContainer.setVisibility(View.GONE);
                textViewCapital.setText("Choose Region");
                clickCheckRegion();
            }
        });

        buttonAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExerciseListActivity.this,AddExerciseActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private RelativeLayout createEntryLayout(String name, String region){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.bottomMargin = 8;

        RelativeLayout relativeLayout = new RelativeLayout(ExerciseListActivity.this);
        relativeLayout.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams nameButtonParams = new RelativeLayout.LayoutParams(twoHundredTwentyPixel,thirtyPixel);
        nameButtonParams.addRule(RelativeLayout.ALIGN_PARENT_START);

        RelativeLayout.LayoutParams regionParams = new RelativeLayout.LayoutParams(ninetyPixel,thirtyPixel);
        regionParams.addRule(RelativeLayout.ALIGN_PARENT_END);


        Button buttonExerciseName = new Button(ExerciseListActivity.this);
        buttonExerciseName.setLayoutParams(nameButtonParams);
        buttonExerciseName.setPadding(0,0,0,0);
        buttonExerciseName.getBackground().setTintList(null);
        buttonExerciseName.setBackgroundResource(R.drawable.border);
        buttonExerciseName.setTextColor(Color.WHITE);
        buttonExerciseName.setAllCaps(false);
        buttonExerciseName.setText(name);

        Button buttonRegion = new Button(ExerciseListActivity.this);
        buttonRegion.setLayoutParams(regionParams);
        buttonRegion.setPadding(0,0,0,0);
        buttonRegion.getBackground().setTintList(null);
        buttonRegion.setBackgroundResource(R.drawable.border);
        buttonRegion.setTextColor(Color.WHITE);
        buttonRegion.setAllCaps(false);
        buttonRegion.setText(region);

        buttonRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExerciseListActivity.this,ExerciseDetailsActivity.class);
                intent.putExtra("back_activity","ExerciseListActivity");
                intent.putExtra("exercise_name",name);
                intent.putExtra("exercise_region",region);
                startActivity(intent);
                finish();
            }
        });

        buttonExerciseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExerciseListActivity.this,ExerciseDetailsActivity.class);
                intent.putExtra("back_activity","ExerciseListActivity");
                intent.putExtra("exercise_name",name);
                intent.putExtra("exercise_region",region);
                startActivity(intent);
                finish();
            }
        });

        relativeLayout.addView(buttonExerciseName);
        relativeLayout.addView(buttonRegion);

        return relativeLayout;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ExerciseListActivity.this,UserMenuActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    public void getBackFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    public void goExerciseDetail(String name, String region) {
        Intent intent = new Intent(ExerciseListActivity.this, ExerciseDetailsActivity.class);
        intent.putExtra("exercise_name",name);
        intent.putExtra("exercise_region",region);
        intent.putExtra("back_activity","ExerciseListActivity");
        startActivity(intent);
        finish();
    }

    private void takeExercises(){
        progressBar.setVisibility(View.VISIBLE);
        activityContainer.setVisibility(View.GONE);
        mReferenceExercises.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot regionSnapshot: snapshot.getChildren()){
                    exerciseRegion = regionSnapshot.getKey();
                    if (exerciseRegion != null){
                        for (DataSnapshot exerciseSnapshot: regionSnapshot.getChildren()){
                            exerciseName = exerciseSnapshot.getKey();
                            if (exerciseName != null){
                                if (exerciseRegion.equals("Chest")){
                                    listChest.add(exerciseName);
                                }else if (exerciseRegion.equals("Back")){
                                    listBack.add(exerciseName);
                                }else if (exerciseRegion.equals("Leg")){
                                    listLeg.add(exerciseName);
                                }else if (exerciseRegion.equals("Shoulder")){
                                    listShoulder.add(exerciseName);
                                }else if (exerciseRegion.equals("Triceps")){
                                    listTriceps.add(exerciseName);
                                }else if (exerciseRegion.equals("Biceps")){
                                    listBiceps.add(exerciseName);
                                }else if (exerciseRegion.equals("Abdomen")){
                                    listAbdomen.add(exerciseName);
                                }
                            }
                            RelativeLayout relativeLayout = new RelativeLayout(ExerciseListActivity.this);
                            relativeLayout = createEntryLayout(exerciseName,exerciseRegion);
                            container.addView(relativeLayout);
                        }
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        activityContainer.setVisibility(View.VISIBLE);
                    }
                },200);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clickCheckRegion(){
        container.removeAllViews();
        buttonAddExercise.setVisibility(View.INVISIBLE);
        buttonCheckRegion.setVisibility(View.INVISIBLE);
        textViewCapital.setText("Choose Region");

        LinearLayout tempContainer = new LinearLayout(ExerciseListActivity.this);
        tempContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tempContainer.setOrientation(LinearLayout.VERTICAL);
        tempContainer.setGravity(Gravity.CENTER);

        Space spaceHigh1 = new Space(ExerciseListActivity.this);
        spaceHigh1.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                tenPixel*3
        ));

        Space spaceHigh2 = new Space(ExerciseListActivity.this);
        spaceHigh2.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                tenPixel*3
        ));

        Space spaceHigh3 = new Space(ExerciseListActivity.this);
        spaceHigh3.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                tenPixel*3
        ));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout firstLayout = new LinearLayout(ExerciseListActivity.this);
        firstLayout.setLayoutParams(layoutParams);
        firstLayout.setOrientation(LinearLayout.HORIZONTAL);

        ViewGroup.LayoutParams viewLayoutParams = new ViewGroup.LayoutParams(
                thirtyPixel*5,
                thirtyPixel*2);

        Button buttonChest = new Button(ExerciseListActivity.this);
        buttonChest.setLayoutParams(viewLayoutParams);
        buttonChest.setText("Chest");
        buttonChest.setBackgroundResource(R.drawable.border);
        buttonChest.setTextColor(Color.WHITE);
        buttonChest.setTypeface(null, Typeface.BOLD_ITALIC);
        buttonChest.setTextSize(20);

        Space space1 = new Space(ExerciseListActivity.this);
        space1.setLayoutParams(new ViewGroup.LayoutParams(
                tenPixel*2,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        Button buttonBack = new Button(ExerciseListActivity.this);
        buttonBack.setLayoutParams(viewLayoutParams);
        buttonBack.setText("Back");
        buttonBack.setBackgroundResource(R.drawable.border);
        buttonBack.setTextColor(Color.WHITE);
        buttonBack.setTypeface(null, Typeface.BOLD_ITALIC);
        buttonBack.setTextSize(20);

        firstLayout.addView(buttonChest);
        firstLayout.addView(space1);
        firstLayout.addView(buttonBack);
        tempContainer.addView(firstLayout);
        tempContainer.addView(spaceHigh1);


        LinearLayout secondLayout = new LinearLayout(ExerciseListActivity.this);
        secondLayout.setLayoutParams(layoutParams);
        secondLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button buttonShoulder = new Button(ExerciseListActivity.this);
        buttonShoulder.setLayoutParams(viewLayoutParams);
        buttonShoulder.setText("Shoulder");
        buttonShoulder.setBackgroundResource(R.drawable.border);
        buttonShoulder.setTextColor(Color.WHITE);
        buttonShoulder.setTypeface(null, Typeface.BOLD_ITALIC);
        buttonShoulder.setTextSize(20);

        Space space2 = new Space(ExerciseListActivity.this);
        space2.setLayoutParams(new ViewGroup.LayoutParams(
                tenPixel*2,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        Button buttonLeg = new Button(ExerciseListActivity.this);
        buttonLeg.setLayoutParams(viewLayoutParams);
        buttonLeg.setText("Leg");
        buttonLeg.setBackgroundResource(R.drawable.border);
        buttonLeg.setTextColor(Color.WHITE);
        buttonLeg.setTypeface(null, Typeface.BOLD_ITALIC);
        buttonLeg.setTextSize(20);

        secondLayout.addView(buttonShoulder);
        secondLayout.addView(space2);
        secondLayout.addView(buttonLeg);
        tempContainer.addView(secondLayout);
        tempContainer.addView(spaceHigh2);


        LinearLayout thirdLayout = new LinearLayout(ExerciseListActivity.this);
        thirdLayout.setLayoutParams(layoutParams);
        thirdLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button buttonTriceps = new Button(ExerciseListActivity.this);
        buttonTriceps.setLayoutParams(viewLayoutParams);
        buttonTriceps.setText("Triceps");
        buttonTriceps.setBackgroundResource(R.drawable.border);
        buttonTriceps.setTextColor(Color.WHITE);
        buttonTriceps.setTypeface(null, Typeface.BOLD_ITALIC);
        buttonTriceps.setTextSize(20);

        Space space3 = new Space(ExerciseListActivity.this);
        space3.setLayoutParams(new ViewGroup.LayoutParams(
                tenPixel*2,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        Button buttonBiceps = new Button(ExerciseListActivity.this);
        buttonBiceps.setLayoutParams(viewLayoutParams);
        buttonBiceps.setText("Biceps");
        buttonBiceps.setBackgroundResource(R.drawable.border);
        buttonBiceps.setTextColor(Color.WHITE);
        buttonBiceps.setTypeface(null, Typeface.BOLD_ITALIC);
        buttonBiceps.setTextSize(20);

        thirdLayout.addView(buttonTriceps);
        thirdLayout.addView(space3);
        thirdLayout.addView(buttonBiceps);
        tempContainer.addView(thirdLayout);
        tempContainer.addView(spaceHigh3);


        LinearLayout fourthLayout = new LinearLayout(ExerciseListActivity.this);
        fourthLayout.setLayoutParams(layoutParams);
        fourthLayout.setOrientation(LinearLayout.HORIZONTAL);
        fourthLayout.setGravity(Gravity.CENTER);

        Button buttonAbdomen = new Button(ExerciseListActivity.this);
        buttonAbdomen.setLayoutParams(viewLayoutParams);
        buttonAbdomen.setText("Abdomen");
        buttonAbdomen.setBackgroundResource(R.drawable.border);
        buttonAbdomen.setTextColor(Color.WHITE);
        buttonAbdomen.setTypeface(null, Typeface.BOLD_ITALIC);
        buttonAbdomen.setTextSize(20);

        fourthLayout.addView(buttonAbdomen);
        tempContainer.addView(fourthLayout);


        Space forHighSpace = new Space(ExerciseListActivity.this);
        forHighSpace.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                tenPixel*6
        ));
        container.addView(forHighSpace);
        container.addView(tempContainer);


        buttonChest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstPartContainer.removeAllViews();
                secondPartContainer.removeAllViews();

                Bundle args = new Bundle();
                args.putString("region_name","Chest");

                RegionFragment regionFragment = new RegionFragment();
                regionFragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );
                transaction.replace(R.id.regionFragmentContainer, regionFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstPartContainer.removeAllViews();
                secondPartContainer.removeAllViews();

                Bundle args = new Bundle();
                args.putString("region_name","Back");

                RegionFragment regionFragment = new RegionFragment();
                regionFragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );
                transaction.replace(R.id.regionFragmentContainer, regionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonShoulder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstPartContainer.removeAllViews();
                secondPartContainer.removeAllViews();

                Bundle args = new Bundle();
                args.putString("region_name","Shoulder");

                RegionFragment regionFragment = new RegionFragment();
                regionFragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );
                transaction.replace(R.id.regionFragmentContainer, regionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonLeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstPartContainer.removeAllViews();
                secondPartContainer.removeAllViews();

                Bundle args = new Bundle();
                args.putString("region_name","Leg");

                RegionFragment regionFragment = new RegionFragment();
                regionFragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );
                transaction.replace(R.id.regionFragmentContainer, regionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonTriceps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstPartContainer.removeAllViews();
                secondPartContainer.removeAllViews();

                Bundle args = new Bundle();
                args.putString("region_name","Triceps");

                RegionFragment regionFragment = new RegionFragment();
                regionFragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );
                transaction.replace(R.id.regionFragmentContainer, regionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonBiceps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstPartContainer.removeAllViews();
                secondPartContainer.removeAllViews();

                Bundle args = new Bundle();
                args.putString("region_name","Biceps");

                RegionFragment regionFragment = new RegionFragment();
                regionFragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );
                transaction.replace(R.id.regionFragmentContainer, regionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonAbdomen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstPartContainer.removeAllViews();
                secondPartContainer.removeAllViews();

                Bundle args = new Bundle();
                args.putString("region_name","Abdomen");

                RegionFragment regionFragment = new RegionFragment();
                regionFragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );
                transaction.replace(R.id.regionFragmentContainer, regionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


    }




}