package com.example.caglarkc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewDietPlanActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    ConstraintLayout dayContainer, main;
    Button buttonMonday, buttonTuesday, buttonWednesday, buttonThursday, buttonFriday, buttonSaturday, buttonSunday;

    Boolean choose, isMonday = false, isTuesday = false, isWednesday = false, isThursday = false
            , isFriday = false, isSaturday = false, isSunday = false, isFinished = false;

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

        sharedPreferences = getSharedPreferences("day_data",MODE_PRIVATE);

        dayContainer = findViewById(R.id.dayContainer);
        main = findViewById(R.id.main);
        buttonMonday = findViewById(R.id.buttonMonday);
        buttonTuesday = findViewById(R.id.buttonTuesday);
        buttonWednesday = findViewById(R.id.buttonWednesday);
        buttonThursday = findViewById(R.id.buttonThursday);
        buttonFriday = findViewById(R.id.buttonFriday);
        buttonSaturday = findViewById(R.id.buttonSaturday);
        buttonSunday = findViewById(R.id.buttonSunday);


        dayContainer.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewDietPlanActivity.this);
        builder.setTitle("Choose");
        builder.setMessage("Do you want add daily specific foods or add daily macros");
        builder.setNegativeButton("Specific Foods", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                choose = true;
                dayContainer.setVisibility(View.VISIBLE);
            }
        });
        builder.setPositiveButton("Macros", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                choose = false;
                dayContainer.setVisibility(View.VISIBLE);
            }
        });
        builder.show();

        checkDays();

        if (isFinished) {
            saveDataToFirebase();
        }

        buttonMonday.setOnClickListener(view -> {
            if (choose) {
                openAddDailyFoodsFragment("Monday");
            } else {

            }
        });

        buttonTuesday.setOnClickListener(view -> {
            if (choose) {
                openAddDailyFoodsFragment("Tuesday");
            } else {

            }
        });

        buttonWednesday.setOnClickListener(view -> {
            if (choose) {
                openAddDailyFoodsFragment("Wednesday");
            } else {

            }
        });

        buttonThursday.setOnClickListener(view -> {
            if (choose) {
                openAddDailyFoodsFragment("Thursday");
            } else {

            }
        });

        buttonFriday.setOnClickListener(view -> {
            if (choose) {
                openAddDailyFoodsFragment("Friday");
            } else {

            }
        });

        buttonSaturday.setOnClickListener(view -> {
            if (choose) {
                openAddDailyFoodsFragment("Saturday");
            } else {

            }
        });

        buttonSunday.setOnClickListener(view -> {
            if (choose) {
                openAddDailyFoodsFragment("Sunday");
            } else {

            }
        });


    }

    private void openAddDailyFoodsFragment(String day) {
        dayContainer.setVisibility(View.GONE);
        AddDailyFoodsDietFragment fragment = AddDailyFoodsDietFragment.newInstance(day);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // Eğer geri yığındaysa, Fragment'i yığından çıkar
            getSupportFragmentManager().popBackStack();
            dayContainer.setVisibility(View.VISIBLE);
        } else {
            Intent intent = new Intent(AddNewDietPlanActivity.this,DietPlanActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }

    public void successfullyAdded(String day) {
        getSupportFragmentManager().popBackStack();
        dayContainer.setVisibility(View.VISIBLE);
        Toast.makeText(AddNewDietPlanActivity.this,"Day is saved, continue add other days...",Toast.LENGTH_SHORT).show();
        if (day.equals("Monday")) {
            buttonMonday.setVisibility(View.GONE);
            isMonday = true;
        } else if (day.equals("Tuesday")) {
            buttonTuesday.setVisibility(View.GONE);
            isTuesday = true;
        } else if (day.equals("Wednesday")) {
            buttonWednesday.setVisibility(View.GONE);
            isWednesday = true;
        } else if (day.equals("Thursday")) {
            buttonThursday.setVisibility(View.GONE);
            isThursday = true;
        } else if (day.equals("Friday")) {
            buttonFriday.setVisibility(View.GONE);
            isFriday = true;
        } else if (day.equals("Saturday")) {
            buttonSaturday.setVisibility(View.GONE);
            isSaturday = true;
        } else if (day.equals("Sunday")) {
            buttonSunday.setVisibility(View.GONE);
            isSunday = true;
        }
    }

    private void checkDays() {
        if (isMonday && isTuesday && isWednesday && isThursday && isFriday && isSaturday && isSunday) {
            isFinished = true;
        }
    }

    private void saveDataToFirebase() {
        
    }

}


