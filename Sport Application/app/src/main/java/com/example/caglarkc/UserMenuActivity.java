package com.example.caglarkc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * UserMenuActivity: The main menu for the user to access different app features.
 * - Buttons navigate to various activities: fitness programs, exercise list, daily check, diet plan, and food list.
 * - Initializes data for exercises and foods from Firebase, populating static lists for easy access throughout the app.
 * - Logs out the user on back press and returns to the login screen.
 */

public class UserMenuActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser mUser;


    Button buttonFitnessProgram, buttonExerciseList, buttonDailyCheck, buttonDietPlan, buttonFoodList, buttonBfiCalculator;



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
        buttonBfiCalculator = findViewById(R.id.buttonBfiCalculator);



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
                Intent intent = new Intent(UserMenuActivity.this,DietPlanActivity.class);
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

        buttonBfiCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMenuActivity.this,BfiCalculatorActivity.class);
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