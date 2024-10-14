package com.example.caglarkc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserMenuActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mReferenceExercises;

    Button buttonFitnessProgram, buttonExerciseList, buttonDailyCheck, buttonDietPlan, buttonFoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        buttonFitnessProgram = findViewById(R.id.buttonFitnessProgram);
        buttonExerciseList = findViewById(R.id.buttonExerciseList);
        buttonDailyCheck = findViewById(R.id.buttonDailyCheck);
        buttonDietPlan = findViewById(R.id.buttonDietPlan);
        buttonFoodList = findViewById(R.id.buttonFoodList);

        Exercise.exerciseList.clear();
        Exercise exercise = new Exercise("Choose Exercise","Null");
        Exercise.exerciseList.add(0,exercise);
        mReferenceExercises = FirebaseDatabase.getInstance().getReference("Exercises");
        mReferenceExercises.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot regionSnapshot : snapshot.getChildren()){
                    String region = regionSnapshot.getKey();
                    if (region != null){
                        for (DataSnapshot exerciseSnapshot : regionSnapshot.getChildren()){
                            String exerciseName = exerciseSnapshot.getKey();
                            Exercise exercise = new Exercise(exerciseName,region);
                            Exercise.exerciseList.add(exercise);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonFitnessProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMenuActivity.this,FitnessProgramActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonExerciseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMenuActivity.this,ExerciseListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonDailyCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMenuActivity.this,DailyCheckActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonDietPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMenuActivity.this,UserMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonFoodList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMenuActivity.this,FoodListActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

    @Override
    public void onBackPressed() {
        mUser = null;
        mAuth.signOut();
        Toast.makeText(UserMenuActivity.this,"Exiting...",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(UserMenuActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}