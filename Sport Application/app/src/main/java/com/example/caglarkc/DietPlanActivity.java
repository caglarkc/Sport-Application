package com.example.caglarkc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DietPlanActivity extends AppCompatActivity {
    Button buttonDietPlanDetails, buttonAddNewDietPlan, buttonCheckDietPlans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diet_plan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonDietPlanDetails = findViewById(R.id.buttonDietPlanDetails);
        buttonAddNewDietPlan = findViewById(R.id.buttonAddNewDietPlan);
        buttonCheckDietPlans = findViewById(R.id.buttonCheckDietPlans);


        buttonDietPlanDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DietPlanActivity.this,DietPlanDetailsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonAddNewDietPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DietPlanActivity.this,AddNewDietPlanActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonCheckDietPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DietPlanActivity.this,CheckDietPlansActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DietPlanActivity.this,UserMenuActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}