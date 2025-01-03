package com.example.caglarkc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
/**
 * DailyCheckActivity: This activity provides users with daily tracking options to manage and monitor their fitness and health goals.
 * The available actions include:
 * 1. Adding and viewing daily fitness programs.
 * 2. Adding and viewing body measurements.
 * 3. Adding and viewing daily progress.
 * 4. Adding and viewing daily eaten foods.
 * Each button directs the user to the respective activity for tracking their daily routines and measurements.
 */
public class DailyCheckActivity extends AppCompatActivity {
    DatabaseReference mReferenceUser;
    SharedPreferences sharedUser;

    Button buttonAddDailyProgram, buttonCheckDailyProgram, buttonAddBodyMeasurement, buttonCheckBodyMeasurement,
            buttonCheckYourProgress, buttonAddDailyEatenFoods, buttonAddDailyProgress, buttonCheckDailyEatenFoods;

    String sharedUserUid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_check);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonAddDailyProgram = findViewById(R.id.buttonAddDailyProgram);
        buttonCheckDailyProgram = findViewById(R.id.buttonCheckDailyProgram);
        buttonAddBodyMeasurement = findViewById(R.id.buttonAddBodyMeasurement);
        buttonCheckBodyMeasurement = findViewById(R.id.buttonCheckBodyMeasurement);
        buttonAddDailyProgress = findViewById(R.id.buttonAddDailyProgress);
        buttonCheckYourProgress = findViewById(R.id.buttonCheckYourProgress);
        buttonAddDailyEatenFoods = findViewById(R.id.buttonAddDailyEatenFoods);
        buttonCheckDailyEatenFoods = findViewById(R.id.buttonCheckDailyEatenFoods);

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");
        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);

        buttonAddDailyProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DailyCheckActivity.this,AddDailyProgramActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonCheckDailyProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DailyCheckActivity.this,CheckDailyProgramActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonAddBodyMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DailyCheckActivity.this,AddDailyBodyMeasurementActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonCheckBodyMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DailyCheckActivity.this,CheckDailyBodyMeasurementsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonAddDailyProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DailyCheckActivity.this,AddDailyProgressActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonCheckYourProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DailyCheckActivity.this,CheckDailyProgressActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonAddDailyEatenFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DailyCheckActivity.this,AddDailyEatenFoodsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonCheckDailyEatenFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DailyCheckActivity.this,CheckDailyEatenFoodsActivity.class);
                startActivity(intent);
                finish();
            }
        });







    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DailyCheckActivity.this,UserMenuActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}