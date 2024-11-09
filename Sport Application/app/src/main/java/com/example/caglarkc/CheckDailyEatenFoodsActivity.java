package com.example.caglarkc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * CheckDailyEatenFoodsActivity: This activity allows users to view their food intake records by selecting a specific date.
 * Users can choose a year, month, and day from spinners to display a list of foods consumed on that date, grouped by meal times.
 * The activity retrieves and displays the food name and gram amount for each entry from the Firebase database.
 */
public class CheckDailyEatenFoodsActivity extends AppCompatActivity {
    SharedPreferences sharedUser;
    DatabaseReference mReferenceUser, mReferenceFoods;

    LinearLayout container;
    ConstraintLayout constraintLayoutParent;
    Spinner yearSpinner, monthSpinner, daySpinner;
    ProgressBar progressBar;

    boolean isYearSpinnerInitial = true, isMonthSpinnerInitial = true, isDaySpinnerInitial = true;
    String sharedUserUid;
    int thirtyDp = 30, thirtyPixel, oneHundredTwentyDp = 120, oneHundredTwentyPixel;
    ArrayList<String> years = new ArrayList<>();
    ArrayList<String> months = new ArrayList<>();
    ArrayList<String> days = new ArrayList<>();
    List<String> dateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_daily_eaten_foods);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");

        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);

        progressBar = findViewById(R.id.progressBar);
        constraintLayoutParent = findViewById(R.id.constraintLayoutParent);
        container = findViewById(R.id.container);
        yearSpinner = findViewById(R.id.yearSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        daySpinner = findViewById(R.id.daySpinner);

        thirtyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                thirtyDp,
                getResources().getDisplayMetrics()
        );
        oneHundredTwentyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                oneHundredTwentyDp,
                getResources().getDisplayMetrics()
        );

        constraintLayoutParent.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        dateList = MainMethods.getDailyEatenFoodDates();



        createSpinners();
    }

    private void createSpinners() {

        // Yıllar için veri seti (2000 - 2024 arası)
        for (int i = 2000; i <= 2024; i++) {
            years.add(i + "");
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, years);
        yearAdapter.setDropDownViewResource(R.layout.spinner_item);
        yearSpinner.setAdapter(yearAdapter);

        // Aylar için veri seti
        for (int i = 1; i <= 12; i++) {
            // Ay numarasını 01, 02 gibi formatlamak için String.format kullanıyoruz
            months.add(String.format("%02d", i));
        }

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, months);
        monthAdapter.setDropDownViewResource(R.layout.spinner_item);
        monthSpinner.setAdapter(monthAdapter);

        // Günler için veri seti (1 - 31 arası)
        for (int i = 1; i <= 31; i++) {
            days.add(String.format("%02d", i));
        }
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, days);
        dayAdapter.setDropDownViewResource(R.layout.spinner_item);
        daySpinner.setAdapter(dayAdapter);

        // Seçim işlemlerini dinleyelim
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // İlk seçimi atla
                if (isYearSpinnerInitial) {
                    isYearSpinnerInitial = false;  // İlk seçimi geçtik, artık flag'i güncelliyoruz
                    return;
                }
                // Seçilen yılı al
                clearLayout();
                showSelectedDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // İlk seçimi atla
                if (isMonthSpinnerInitial) {
                    isMonthSpinnerInitial = false;  // İlk seçimi geçtik, artık flag'i güncelliyoruz
                    return;
                }
                clearLayout();
                showSelectedDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // İlk seçimi atla
                if (isDaySpinnerInitial) {
                    isDaySpinnerInitial = false;  // İlk seçimi geçtik, artık flag'i güncelliyoruz
                    return;
                }
                clearLayout();
                showSelectedDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CheckDailyEatenFoodsActivity.this,DailyCheckActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void clearLayout() {
        container.removeAllViews();
    }

    private void showSelectedDate() {
        String selectedYear = (String) yearSpinner.getSelectedItem();
        String selectedMonth = (String) monthSpinner.getSelectedItem();
        String selectedDay = (String) daySpinner.getSelectedItem();

        String selectedDate = selectedDay + "-" + selectedMonth + "-" + selectedYear;

        if (dateList.contains(selectedDate)) {
            progressBar.setVisibility(View.VISIBLE);
            constraintLayoutParent.setVisibility(View.GONE);

            mReferenceUser.child("user_dailyEatenFoodsData").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot mealTimeSnapshot : snapshot.getChildren()) {
                            String mealTime = mealTimeSnapshot.getKey();
                            if (mealTime != null) {
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                layoutParams.leftMargin = 100;
                                LinearLayout tempContainer = new LinearLayout(CheckDailyEatenFoodsActivity.this);
                                tempContainer.setLayoutParams(layoutParams);
                                tempContainer.setOrientation(LinearLayout.VERTICAL);
                                tempContainer.setGravity(Gravity.START);
                                tempContainer.setBackgroundColor(Color.BLACK);
                                tempContainer.setBackgroundResource(R.drawable.exercise_background_border);

                                LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                textViewParams.topMargin = 25;
                                TextView textView = new TextView(CheckDailyEatenFoodsActivity.this);
                                textView.setLayoutParams(textViewParams);
                                textView.setTextSize(18);
                                textView.setTextColor(Color.WHITE);
                                textView.setTypeface(null, Typeface.BOLD_ITALIC);
                                textView.setText("  " + mealTime + "     ");

                                for (DataSnapshot foodSnapshot : mealTimeSnapshot.getChildren()) {
                                    String foodName = foodSnapshot.getKey();
                                    String gram = foodSnapshot.getValue(String.class);
                                    if (foodName != null && gram != null) {
                                        gram = gram.replace("gr","");
                                        LinearLayout linearLayout = createFoodContainer(foodName,gram);
                                        linearLayout.setBackgroundResource(R.drawable.border);
                                        tempContainer.addView(linearLayout);
                                    }
                                }
                                container.addView(textView);
                                container.addView(tempContainer);
                            }
                        }
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            constraintLayoutParent.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    },500);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("ERROR",error.getMessage());
                }
            });
        }
    }

    private LinearLayout createFoodContainer(String foodName, String gram) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                thirtyPixel
        );
        layoutParams.topMargin = 4;

        LinearLayout linearLayout = new LinearLayout(CheckDailyEatenFoodsActivity.this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(10,0,0,0);

        Button buttonName = new Button(CheckDailyEatenFoodsActivity.this);
        buttonName.setText("   " + foodName);
        buttonName.setLayoutParams(new ViewGroup.LayoutParams(oneHundredTwentyPixel,thirtyPixel));
        buttonName.setTextSize(16);
        buttonName.setTypeface(null, Typeface.ITALIC);
        buttonName.setTextColor(Color.WHITE);
        buttonName.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        buttonName.setPadding(0,0,0,0);
        buttonName.setBackgroundColor(Color.TRANSPARENT);

        buttonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckDailyEatenFoodsActivity.this,FoodDetailActivity.class);
                intent.putExtra("back_activity","CheckDailyEatenFoodsActivity");
                intent.putExtra("food_name",foodName);
                startActivity(intent);
                finish();
            }
        });



        TextView textViewGram = new TextView(CheckDailyEatenFoodsActivity.this);
        textViewGram.setText(gram + "gr");
        textViewGram.setLayoutParams(new ViewGroup.LayoutParams(thirtyPixel*2, thirtyPixel));
        textViewGram.setTextSize(16);
        textViewGram.setTypeface(null, Typeface.ITALIC);
        textViewGram.setTextColor(Color.WHITE);
        textViewGram.setGravity(Gravity.CENTER);

        linearLayout.addView(buttonName);
        linearLayout.addView(textViewGram);


        return linearLayout;
    }
}