package com.example.caglarkc;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AddNewDietPlanActivity allows users to create a weekly diet plan with specific food items or macros for each day.
 * The activity provides options to:
 * - Choose between adding specific foods or daily macros for each day.
 * - Add diet plans for each day of the week, where each day can have its unique set of meals.
 * - Save the completed weekly diet plan to Firebase Realtime Database under a unique plan ID.
 * - Navigate to specific activities for adding food items to each day.
 * If all days are completed, the weekly diet plan is saved, and the user is redirected.
 */

public class AddNewDietPlanActivity extends AppCompatActivity {
    SharedPreferences sharedUser;
    DatabaseReference mReferencePath, mReferenceUser;

    ConstraintLayout dayContainer, main;
    Button buttonMonday, buttonTuesday, buttonWednesday, buttonThursday, buttonFriday, buttonSaturday, buttonSunday;

    boolean choose;
    String sharedUserUid;
    boolean isMonday, isTuesday, isWednesday, isThursday, isFriday, isSaturday, isSunday ;
    HashMap<String , String> hashMapDayData = new HashMap<>();
    List<String> daysOfWeek = new ArrayList<>();
    List<String> dietPlanNamesList;
    HashMap<String , Integer> hashMapFoodCalories = new HashMap<>();
    int totalCalories;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_diet_plan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        daysOfWeek.add("Monday");
        daysOfWeek.add("Tuesday");
        daysOfWeek.add("Wednesday");
        daysOfWeek.add("Thursday");
        daysOfWeek.add("Friday");
        daysOfWeek.add("Saturday");
        daysOfWeek.add("Sunday");


        dayContainer = findViewById(R.id.dayContainer);
        main = findViewById(R.id.main);
        buttonMonday = findViewById(R.id.buttonMonday);
        buttonTuesday = findViewById(R.id.buttonTuesday);
        buttonWednesday = findViewById(R.id.buttonWednesday);
        buttonThursday = findViewById(R.id.buttonThursday);
        buttonFriday = findViewById(R.id.buttonFriday);
        buttonSaturday = findViewById(R.id.buttonSaturday);
        buttonSunday = findViewById(R.id.buttonSunday);

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");
        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);

        dietPlanNamesList = MainMethods.getDietPlanNamesList();
        hashMapFoodCalories = MainMethods.getHashMapFoodCalories();

        hashMapDayData = MainMethods.returnDayDataHashMap();
        if (hashMapDayData.isEmpty()) {
            isMonday = false;
            isTuesday = false;
            isWednesday = false;
            isThursday = false;
            isFriday = false;
            isSaturday = false;
            isSunday = false;


            dayContainer.setVisibility(View.GONE);

            AlertDialog.Builder builder = new AlertDialog.Builder(AddNewDietPlanActivity.this);
            builder.setTitle("Choose");
            builder.setMessage("Do you want add daily specific foods or add daily macros");
            builder.setNegativeButton("Specific Foods", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    choose = true;
                    MainMethods.setDietPlanChoose(choose);
                    dayContainer.setVisibility(View.VISIBLE);
                }
            });
            builder.setPositiveButton("Macros", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    choose = false;
                    MainMethods.setDietPlanChoose(choose);
                    dayContainer.setVisibility(View.VISIBLE);
                }
            });
            builder.show();



        }else {
            choose = MainMethods.getDietPlanChoose();

            for (Map.Entry<String, String> entry : hashMapDayData.entrySet()) {
                String day = entry.getKey();
                if (day.equals("Monday")) {
                    isMonday = true;
                    buttonMonday.setVisibility(View.GONE);
                } else if (day.equals("Tuesday")) {
                    isTuesday = true;
                    buttonTuesday.setVisibility(View.GONE);
                } else if (day.equals("Wednesday")) {
                    isWednesday = true;
                    buttonWednesday.setVisibility(View.GONE);
                } else if (day.equals("Thursday")) {
                    isThursday = true;
                    buttonThursday.setVisibility(View.GONE);
                } else if (day.equals("Friday")) {
                    isFriday = true;
                    buttonFriday.setVisibility(View.GONE);
                } else if (day.equals("Saturday")) {
                    isSaturday = true;
                    buttonSaturday.setVisibility(View.GONE);
                } else if (day.equals("Sunday")) {
                    isSunday = true;
                    buttonSunday.setVisibility(View.GONE);
                }
            }


            if (isMonday && isTuesday && isWednesday && isThursday && isFriday && isSaturday && isSunday) {
                // Custom layout'u inflate ediyoruz
                View dialogView = getLayoutInflater().inflate(R.layout.diet_plan_name_dialog, null);

                // EditText'i custom layout'tan alıyoruz
                EditText editText = dialogView.findViewById(R.id.editTextDietPlanName);

                AlertDialog.Builder builder = new AlertDialog.Builder(AddNewDietPlanActivity.this);
                builder.setTitle("NAME");
                builder.setMessage("Please enter a name for your diet plan...");
                builder.setView(dialogView); // Custom layout'u dialog'a ekliyoruz

                // Positive Button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dietPlanName = editText.getText().toString();
                        if (!dietPlanNamesList.contains(dietPlanName)) {
                            mReferencePath = FirebaseDatabase.getInstance().getReference("DietPlans").child(dietPlanName);

                            for (String day : daysOfWeek) {
                                if (hashMapDayData.containsKey(day)) {
                                    // Günün veri girişini alıyoruz
                                    String[] x1 = (hashMapDayData.get(day)).split("=");
                                    int dayCalorie = 0;

                                    for (int i = 0; i < x1.length; i++) {
                                        String[] x2 = x1[i].split(";");
                                        String mealName = x2[0];
                                        String[] x3 = x2[1].split("_");

                                        for (int k = 0; k < x3.length; k++) {
                                            String foodName = x3[k].split(",")[0];
                                            String foodGram = x3[k].split(",")[1];
                                            int gr = Integer.parseInt(foodGram);
                                            gr = gr / 100;
                                            int cal = hashMapFoodCalories.get(foodName);
                                            cal = cal * gr;
                                            dayCalorie += cal;

                                            // Firebase'e veri ekliyoruz
                                            mReferencePath.child(day).child(mealName).child(foodName).setValue(foodGram + "Gr")
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("ERROR", e.getMessage());
                                                            Toast.makeText(AddNewDietPlanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                            mReferenceUser.child("user_dietPlans").child(dietPlanName).child(day).child(mealName).child(foodName)
                                                    .setValue(foodGram + "Gr")
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("ERROR", e.getMessage());
                                                            Toast.makeText(AddNewDietPlanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }

                                    // Toplam kaloriyi gün kalorisine ekliyoruz
                                    totalCalories += dayCalorie;
                                }
                            }
                            /*
                                                        for (Map.Entry<String , String> entry : hashMapDayData.entrySet()) {
                                String day = entry.getKey();
                                String[] x1 = (entry.getValue()).split("=");
                                int dayCalorie = 0;
                                for (int i = 0 ; i < x1.length ; i++) {
                                    String[] x2 = x1[i].split(";");
                                    String mealName = x2[0];
                                    String[] x3 = x2[1].split("_");
                                    for (int k = 0 ; k < x3.length ; k++) {
                                        String foodName = x3[k].split(",")[0];
                                        String foodGram = x3[k].split(",")[1];
                                        int gr = Integer.parseInt(foodGram);
                                        gr = gr / 100;
                                        int cal = hashMapFoodCalories.get(foodName);
                                        cal = cal * gr;
                                        dayCalorie += cal;

                                        mReferencePath.child(day).child(mealName).child(foodName).setValue(foodGram + "Gr").addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("ERROR",e.getMessage());
                                                Toast.makeText(AddNewDietPlanActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        mReferenceUser.child("user_dietPlans").child(dietPlanName).child(day).child(mealName).child(foodName).setValue(foodGram + "Gr").addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("ERROR",e.getMessage());
                                                Toast.makeText(AddNewDietPlanActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                                totalCalories += dayCalorie;
                            }

                             */
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    int k = (int) Math.round((double) totalCalories / 7);
                                    mReferencePath.child("dietPlan_averageCalorie").setValue(k);
                                    mReferenceUser.child("user_dietPlans").child(dietPlanName).child("dietPlan_averageCalorie").setValue(k);
                                    Toast.makeText(AddNewDietPlanActivity.this,"Diet plan saved with successfully...",Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(AddNewDietPlanActivity.this,DietPlanActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },500);
                        }else {
                            Toast.makeText(AddNewDietPlanActivity.this,"Diet plan name is exist...",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                // Negative Button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Dialog'u göster
                builder.show();


            }


        }




        buttonMonday.setOnClickListener(view -> {
            if (choose) {
                openAddDayDietPlanToProgramActivity("Monday");
            } else {

            }
        });

        buttonTuesday.setOnClickListener(view -> {
            if (choose) {
                openAddDayDietPlanToProgramActivity("Tuesday");
            } else {

            }
        });

        buttonWednesday.setOnClickListener(view -> {
            if (choose) {
                openAddDayDietPlanToProgramActivity("Wednesday");
            } else {

            }
        });

        buttonThursday.setOnClickListener(view -> {
            if (choose) {
                openAddDayDietPlanToProgramActivity("Thursday");
            } else {

            }
        });

        buttonFriday.setOnClickListener(view -> {
            if (choose) {
                openAddDayDietPlanToProgramActivity("Friday");
            } else {

            }
        });

        buttonSaturday.setOnClickListener(view -> {
            if (choose) {
                openAddDayDietPlanToProgramActivity("Saturday");
            } else {

            }
        });

        buttonSunday.setOnClickListener(view -> {
            if (choose) {
                openAddDayDietPlanToProgramActivity("Sunday");
            } else {

            }
        });


    }

    private void openAddDayDietPlanToProgramActivity(String day) {
        Intent intent = new Intent(AddNewDietPlanActivity.this,AddDayDietPlanToProgramActivity.class);
        intent.putExtra("day",day);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddNewDietPlanActivity.this,DietPlanActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }




}
