package com.example.caglarkc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewDietPlanActivity extends AppCompatActivity {
    ConstraintLayout dayContainer, main;
    Button buttonMonday, buttonTuesday, buttonWednesday, buttonThursday, buttonFriday, buttonSaturday, buttonSunday;

    Boolean choose;
    boolean isMonday, isTuesday, isWednesday, isThursday, isFriday, isSaturday, isSunday ;
    HashMap<String , String> hashMapDayData = new HashMap<>();
    List<String> daysOfWeek = new ArrayList<>();

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
                Toast.makeText(AddNewDietPlanActivity.this,"Her Gün tamam",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddNewDietPlanActivity.this,DietPlanActivity.class);
                startActivity(intent);
                finish();
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
//En son intent ile tek tek gunlerın datasını almayı yapamadım daha dogrusu dataları aldık diger activtiyde duruyor
// sadece ordan gun gun hepsini tek tek sırayla alıp kaydetmesini sitemem lazım, olmadı butun hepsini tek activiye geçir
//En son butun activitylere tek tek açıklama eklet gptye ve commitle


