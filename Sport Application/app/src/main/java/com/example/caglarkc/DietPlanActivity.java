package com.example.caglarkc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.List;

/**
 * DietPlanActivity: This activity serves as the main menu for managing diet plans.
 * It provides three options for users:
 * 1. View diet plan details.
 * 2. Add a new diet plan.
 * 3. Check saved diet plans.
 * Each button navigates the user to the corresponding activity for diet plan management.
 */
public class DietPlanActivity extends AppCompatActivity {
    DatabaseReference mReferenceUser, mReferenceDietPlansName;
    SharedPreferences sharedUser;

    Button buttonDietPlanDetails, buttonAddNewDietPlan, buttonCheckDietPlans;
    LinearLayout container;
    ProgressBar progressBar;

    String sharedUserUid, dietPlanName;
    int dietPlanCounter = 0;
    List<String> dietPlanNamesList = new ArrayList<>();

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
        container = findViewById(R.id.container);
        progressBar = findViewById(R.id.progressBar);

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");

        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);
        mReferenceDietPlansName = FirebaseDatabase.getInstance().getReference("DietPlans");

        buttonDietPlanDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DietPlanActivity.this,"Please first choose a program from 'Check Diet Plans' ...",Toast.LENGTH_SHORT).show();
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


        setViewVisibilities(false);
        mReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot detailSnapshot : snapshot.getChildren()){
                    String detail = detailSnapshot.getKey();
                    if (detail != null && detail.equals("user_currentDietPlan")){
                        dietPlanName = detailSnapshot.getValue(String.class);

                        buttonDietPlanDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(DietPlanActivity.this,DietPlanDetailsActivity.class);
                                intent.putExtra("dietPlan_name",dietPlanName);
                                intent.putExtra("back_activity","DietPlanActivity");
                                startActivity(intent);
                                finish();
                            }
                        });


                    }else if (detail != null && detail.equals("user_dietPlans")){
                        for (DataSnapshot programNamesSnapshot: detailSnapshot.getChildren()){
                            if (programNamesSnapshot.exists()){
                                dietPlanCounter++;
                            }
                        }
                        if (dietPlanCounter == 5){

                            buttonAddNewDietPlan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(DietPlanActivity.this,"You reach the adding program limit...",Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    }
                }

                mReferenceDietPlansName.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dietPlanNameSnapshot : snapshot.getChildren()) {
                            String name = dietPlanNameSnapshot.getKey();
                            if (name != null) {
                                dietPlanNamesList.add(name);
                            }
                        }

                        MainMethods.setDietPlanNamesList(dietPlanNamesList);
                        setViewVisibilities(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DietPlanActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
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

    private void setViewVisibilities(Boolean b) {
        if (b) {
            container.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }else {
            container.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}