package com.example.caglarkc;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * ProgramDetailsActivity: Displays detailed information about a selected fitness program.
 * - Retrieves program details, including exercises for each day and the creator’s information.
 * - Displays program structure by day, listing exercises with their sets or repetitions.
 * - Allows navigation back to the previous screen specified by the intent, either FitnessProgramActivity or CheckProgramsActivity.
 * - The UI dynamically generates exercise information based on the program structure.
 */

public class ProgramDetailsActivity extends AppCompatActivity {
    DatabaseReference mReferenceProgram, mReferenceUser;

    TextView programNameContainer, textViewWorkDay, textViewExerciseName, textViewExerciseValue, userDetailContainer;
    Space space;
    LinearLayout programContainer, temporaryContainer, dayContainer, exerciseContainer;

    String programName, workDay, exerciseName, exerciseValue, backActivity, userUid, userUsername, userName;
    int widthDp = 220, spaceWidthDp = 20, widthPixel, spaceWidthPixel;
    List<String> workDays = new ArrayList<>(), currentExerciseList , exercises;

    HashMap<String, List<String>> hashMapProgram = new HashMap<>();
    HashMap<String, Integer> daysMap = new HashMap<>();
    List<Integer> values = new ArrayList<>();
    Intent intentSuccessfully;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_program_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Intent getIntent = getIntent();
        programName = getIntent.getStringExtra("program_name");
        backActivity = getIntent.getStringExtra("back_activity");


        mReferenceProgram = FirebaseDatabase.getInstance().getReference("Programs").child("Fitness").child(programName);
        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users");

        programNameContainer = findViewById(R.id.programNameContainer);
        programContainer = findViewById(R.id.programContainer);
        userDetailContainer = findViewById(R.id.userDetailContainer);

        widthPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                widthDp,
                getResources().getDisplayMetrics()
        );

        spaceWidthPixel = (int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                spaceWidthDp,
                getResources().getDisplayMetrics()
        );

        if (backActivity.equals("FitnessProgramActivity")) {
            intentSuccessfully = new Intent(ProgramDetailsActivity.this,FitnessProgramActivity.class);
        } else if (backActivity.equals("CheckProgramsActivity")) {
            intentSuccessfully = new Intent(ProgramDetailsActivity.this,CheckProgramsActivity.class);
        }

        takeProgram(mReferenceProgram);
    }

    @Override
    public void onBackPressed() {
        startActivity(intentSuccessfully);
        finish();
        super.onBackPressed();
    }

    private void takeProgram(DatabaseReference databaseReference){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot daySnapshots : snapshot.getChildren()){
                        workDay = daySnapshots.getKey();
                        if (workDay.equals("Monday")) {
                            daysMap.put("Monday",1);
                        }else if (workDay.equals("Tuesday")){
                            daysMap.put("Tuesday",2);
                        }else if (workDay.equals("Wednesday")){
                            daysMap.put("Wednesday",3);
                        }else if (workDay.equals("Thursday")){
                            daysMap.put("Thursday",4);
                        }else if (workDay.equals("Friday")){
                            daysMap.put("Friday",5);
                        }else if (workDay.equals("Saturday")){
                            daysMap.put("Saturday",6);
                        }else if (workDay.equals("Sunday")){
                            daysMap.put("Sunday",7);
                        }else if (workDay.equals("Creator")){
                            userUid = daySnapshots.getValue(String.class);
                        }
                        workDays.add(workDay);
                        exercises = new ArrayList<>();
                        for (DataSnapshot exerciseSnapshot : daySnapshots.getChildren()){
                            exerciseName = exerciseSnapshot.getKey();
                            exerciseValue = exerciseSnapshot.getValue(String.class);
                            exercises.add(exerciseName + "_" + exerciseValue);
                        }
                        hashMapProgram.put(workDay,exercises);

                    }
                    values.addAll(daysMap.values());
                    Collections.sort(values);
                    showProgramDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProgramDetailsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        mReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot uidSnapshot : snapshot.getChildren()){
                    String databaseUid = uidSnapshot.getKey();
                    if (databaseUid != null && databaseUid.equals(userUid)){
                        for (DataSnapshot detailSnapshot : uidSnapshot.getChildren()){
                            String detail = detailSnapshot.getKey();
                            if (detail != null && detail.equals("user_name")){
                                userName = detailSnapshot.getValue(String.class);
                            }else if (detail != null && detail.equals("user_username")){
                                userUsername = detailSnapshot.getValue(String.class);
                            }
                        }
                    }
                }
                if (userName!= null){
                    userDetailContainer.setText("Creator Name: "+ userName);
                }else {
                    userDetailContainer.setText("Creator Username: "+ userUsername);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showProgramDetails(){
        programNameContainer.setText(programName);

        LinearLayout.LayoutParams layoutParamsWithMarginTop = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        layoutParamsWithMarginTop.topMargin=40;


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        temporaryContainer = new LinearLayout(ProgramDetailsActivity.this);
        temporaryContainer.setLayoutParams(layoutParams);
        temporaryContainer.setOrientation(LinearLayout.VERTICAL);



        for (Integer val: values){
            for (Map.Entry<String , Integer> entry : daysMap.entrySet()){
                if (entry.getValue().equals(val)){
                    String workday = entry.getKey();
                    dayContainer = new LinearLayout(ProgramDetailsActivity.this);
                    dayContainer.setLayoutParams(layoutParamsWithMarginTop);
                    dayContainer.setOrientation(LinearLayout.VERTICAL);


                    textViewWorkDay = new TextView(ProgramDetailsActivity.this);
                    textViewWorkDay.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    textViewWorkDay.setTypeface(null, Typeface.BOLD_ITALIC);
                    textViewWorkDay.setTextSize(25);
                    textViewWorkDay.setTextColor(Color.WHITE);

                    textViewWorkDay.setText(workday);
                    dayContainer.addView(textViewWorkDay);

                    currentExerciseList = new ArrayList<>();
                    currentExerciseList = hashMapProgram.get(workday);

                    for (String exercise: currentExerciseList){
                        //Exerciselerin adını tutacak textView
                        textViewExerciseName = new TextView(ProgramDetailsActivity.this);
                        textViewExerciseName.setLayoutParams(new ViewGroup.LayoutParams(
                                widthPixel,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        textViewExerciseName.setTextSize(18);
                        textViewExerciseName.setGravity(Gravity.START);
                        textViewExerciseName.setTextColor(Color.WHITE);
                        textViewExerciseName.setTypeface(null,Typeface.ITALIC);


                        //Set sayılarını tutan textView
                        textViewExerciseValue = new TextView(ProgramDetailsActivity.this);
                        textViewExerciseValue.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                        );
                        textViewExerciseValue.setTextSize(18);
                        textViewExerciseValue.setTypeface(null,Typeface.ITALIC);
                        textViewExerciseValue.setTextColor(Color.WHITE);


                        exerciseContainer = new LinearLayout(ProgramDetailsActivity.this);
                        exerciseContainer.setLayoutParams(layoutParams);
                        exerciseContainer.setOrientation(LinearLayout.HORIZONTAL);

                        String[] exerciseArray = exercise.split("_");
                        exerciseName = exerciseArray[0];
                        exerciseValue = exerciseArray[1];


                        space = new Space(ProgramDetailsActivity.this);
                        space.setLayoutParams(new ViewGroup.LayoutParams(
                                spaceWidthPixel,
                                ViewGroup.LayoutParams.WRAP_CONTENT));


                        textViewExerciseName.setText(exerciseName);
                        textViewExerciseValue.setText(exerciseValue);
                        exerciseContainer.addView(space);
                        exerciseContainer.addView(textViewExerciseName);
                        exerciseContainer.addView(textViewExerciseValue);
                        dayContainer.addView(exerciseContainer);
                    }
                    temporaryContainer.addView(dayContainer);

                }
            }
        }

        programContainer.addView(temporaryContainer);

    }
}