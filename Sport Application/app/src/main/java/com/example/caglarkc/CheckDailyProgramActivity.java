package com.example.caglarkc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckDailyProgramActivity extends AppCompatActivity {
    SharedPreferences sharedUser;
    DatabaseReference mReferenceDailyProgram;

    Spinner yearSpinner, monthSpinner, daySpinner;
    Button buttonCheckDates;
    LinearLayout programDetailContainer;
    ConstraintLayout constraintLayoutParent;
    ProgressBar progressBar;

    boolean isYearSpinnerInitial = true, isMonthSpinnerInitial = true, isDaySpinnerInitial = true;
    String sharedUserUid;
    int setWidthDp = 140, spaceWidth = 40, exerciseWidthDp = 140, layoutWidthDp = 240, thirtyDp = 30;
    int setPixel, spacePixel, exercisePixel, layoutWidthPixel, thirtyPixel;

    ArrayList<String> years = new ArrayList<>();
    ArrayList<String> months = new ArrayList<>();
    ArrayList<String> days = new ArrayList<>();
    List<String> dateList = new ArrayList<>();

    //En aşagıya activitynin butun tarhileri gormek için ekrana bir view getiren button ekle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_daily_program);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        constraintLayoutParent = findViewById(R.id.constraintLayoutParent);
        programDetailContainer = findViewById(R.id.programDetailContainer);
        progressBar = findViewById(R.id.progressBar);
        yearSpinner = findViewById(R.id.yearSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        daySpinner = findViewById(R.id.daySpinner);
        buttonCheckDates = findViewById(R.id.buttonCheckDates);

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");

        mReferenceDailyProgram = FirebaseDatabase.getInstance().getReference("Users")
                .child(sharedUserUid).child("user_dailyFitnessProgramData");
        progressBar.setVisibility(View.VISIBLE);
        constraintLayoutParent.setVisibility(View.GONE);
        mReferenceDailyProgram.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    if (dateSnapshot.exists()) {
                        String date = dateSnapshot.getKey();
                        if (date != null) {
                            dateList.add(date);
                        }
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            constraintLayoutParent.setVisibility(View.VISIBLE);
                        }
                    },500);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        createSpinners();

        setPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                setWidthDp,
                getResources().getDisplayMetrics()
        );

        spacePixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                spaceWidth,
                getResources().getDisplayMetrics()
        );
        exercisePixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                exerciseWidthDp,
                getResources().getDisplayMetrics()
        );

        layoutWidthPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                layoutWidthDp,
                getResources().getDisplayMetrics()
        );

        thirtyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                thirtyDp,
                getResources().getDisplayMetrics()
        );


    }



    private void clearLayout() {
        programDetailContainer.removeAllViews();
    }

    private void showSelectedDate() {
        String selectedYear = (String) yearSpinner.getSelectedItem();
        String selectedMonth = (String) monthSpinner.getSelectedItem();
        String selectedDay = (String) daySpinner.getSelectedItem();

        String selectedDate = selectedDay + "-" + selectedMonth + "-" + selectedYear;

        if (dateList.contains(selectedDate)) {
            progressBar.setVisibility(View.VISIBLE);
            constraintLayoutParent.setVisibility(View.GONE);
            mReferenceDailyProgram.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot exerciseSnapshot : snapshot.getChildren()) {
                        String exerciseName = exerciseSnapshot.getKey();
                        if (exerciseName != null) {
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            layoutParams.topMargin = 3;
                            LinearLayout tempContainer = new LinearLayout(CheckDailyProgramActivity.this);
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
                            TextView textView = new TextView(CheckDailyProgramActivity.this);
                            textView.setLayoutParams(textViewParams);
                            textView.setTextSize(18);
                            textView.setTextColor(Color.WHITE);
                            textView.setTypeface(null, Typeface.BOLD_ITALIC);
                            textView.setText("  " + exerciseName + "     ");


                            LinearLayout capital = createFirstSetContainer("Set Number","Weight","Repeat");
                            tempContainer.addView(capital);

                            for (DataSnapshot setSnapshot : exerciseSnapshot.getChildren()) {
                                String setNumber = setSnapshot.getKey();
                                String data = setSnapshot.getValue(String.class);
                                if (setNumber != null && data != null) {
                                    setNumber = setNumber.replace("Set","");
                                    String[] array = data.split("_");
                                    String w = array[0];
                                    String r = array[1];
                                    w = w.replace("Kg","");
                                    r = r.replace("Repeat","");

                                    LinearLayout setContainer = createSetContainer(setNumber,w,r);
                                    tempContainer.addView(setContainer);
                                }
                            }
                            programDetailContainer.addView(textView);
                            programDetailContainer.addView(tempContainer);

                        }
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            constraintLayoutParent.setVisibility(View.VISIBLE);
                        }
                    },100);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private LinearLayout createSetContainer(String set, String w, String r) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                thirtyPixel
        );
        layoutParams.topMargin = 4;

        LinearLayout linearLayout = new LinearLayout(CheckDailyProgramActivity.this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(10,0,0,0);

        // Soldaki TextView (2 birim genişlik)
        TextView textViewSet = new TextView(CheckDailyProgramActivity.this);
        textViewSet.setText("     " + set + ".Set");
        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(
                0,  // Genişliği 0 yapıyoruz, çünkü ağırlığa göre genişlik ayarlanacak
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // 1 birim ağırlık (oran olarak)
        );
        leftParams.leftMargin = 20;
        textViewSet.setLayoutParams(leftParams);
        textViewSet.setTextSize(16);
        textViewSet.setTypeface(null, Typeface.ITALIC);
        textViewSet.setTextColor(Color.WHITE);
        textViewSet.setGravity(Gravity.START);

        //Sağda olacak olan RelativeLayout
        LinearLayout linearLayoutRight = new LinearLayout(CheckDailyProgramActivity.this);
        linearLayoutRight.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(
                0,  // Genişliği 0 yapıyoruz, çünkü ağırlığa göre genişlik ayarlanacak
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // 1 birim ağırlık (oran olarak)
        );
        rightParams.rightMargin = 20;
        linearLayoutRight.setLayoutParams(rightParams);

        TextView textViewW = new TextView(CheckDailyProgramActivity.this);
        textViewW.setText(w + " Kg");
        LinearLayout.LayoutParams miniLeftParams = new LinearLayout.LayoutParams(
                0,  // Genişliği 0 yapıyoruz, çünkü ağırlığa göre genişlik ayarlanacak
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // 1 birim ağırlık (oran olarak)
        );
        textViewW.setLayoutParams(miniLeftParams);
        textViewW.setTextSize(16);
        textViewW.setTypeface(null, Typeface.ITALIC);
        textViewW.setTextColor(Color.WHITE);
        textViewW.setGravity(Gravity.CENTER);

        TextView textViewR = new TextView(CheckDailyProgramActivity.this);
        textViewR.setText(r);
        LinearLayout.LayoutParams miniRightParams = new LinearLayout.LayoutParams(
                0,  // Genişliği 0 yapıyoruz, çünkü ağırlığa göre genişlik ayarlanacak
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // 1 birim ağırlık (oran olarak)
        );
        textViewR.setLayoutParams(miniRightParams);
        textViewR.setTextSize(16);
        textViewR.setTypeface(null, Typeface.ITALIC);
        textViewR.setTextColor(Color.WHITE);
        textViewR.setGravity(Gravity.CENTER);

        linearLayoutRight.addView(textViewW);
        linearLayoutRight.addView(textViewR);

        linearLayout.addView(textViewSet);
        linearLayout.addView(linearLayoutRight);


        return linearLayout;
    }

    private LinearLayout createFirstSetContainer(String set, String w, String r) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                thirtyPixel
        );
        layoutParams.topMargin = 4;

        LinearLayout linearLayout = new LinearLayout(CheckDailyProgramActivity.this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(10,0,10,0);

        // Soldaki TextView (2 birim genişlik)
        TextView textViewSet = new TextView(CheckDailyProgramActivity.this);
        textViewSet.setText(set);
        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(
                0,  // Genişliği 0 yapıyoruz, çünkü ağırlığa göre genişlik ayarlanacak
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // 1 birim ağırlık (oran olarak)
        );
        leftParams.leftMargin = 20;
        textViewSet.setLayoutParams(leftParams);
        textViewSet.setTextSize(16);
        textViewSet.setTypeface(null, Typeface.ITALIC);
        textViewSet.setTextColor(Color.WHITE);
        textViewSet.setGravity(Gravity.START);

        //Sağda olacak olan RelativeLayout
        LinearLayout linearLayoutRight = new LinearLayout(CheckDailyProgramActivity.this);
        linearLayoutRight.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(
                0,  // Genişliği 0 yapıyoruz, çünkü ağırlığa göre genişlik ayarlanacak
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // 1 birim ağırlık (oran olarak)
        );
        rightParams.rightMargin = 20;
        linearLayoutRight.setLayoutParams(rightParams);

        TextView textViewW = new TextView(CheckDailyProgramActivity.this);
        textViewW.setText(w);
        LinearLayout.LayoutParams miniLeftParams = new LinearLayout.LayoutParams(
                0,  // Genişliği 0 yapıyoruz, çünkü ağırlığa göre genişlik ayarlanacak
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // 1 birim ağırlık (oran olarak)
        );
        textViewW.setLayoutParams(miniLeftParams);
        textViewW.setTextSize(16);
        textViewW.setTypeface(null, Typeface.ITALIC);
        textViewW.setTextColor(Color.WHITE);
        textViewW.setGravity(Gravity.CENTER);

        TextView textViewR = new TextView(CheckDailyProgramActivity.this);
        textViewR.setText(r);
        LinearLayout.LayoutParams miniRightParams = new LinearLayout.LayoutParams(
                0,  // Genişliği 0 yapıyoruz, çünkü ağırlığa göre genişlik ayarlanacak
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f // 1 birim ağırlık (oran olarak)
        );
        textViewR.setLayoutParams(miniRightParams);
        textViewR.setTextSize(16);
        textViewR.setTypeface(null, Typeface.ITALIC);
        textViewR.setTextColor(Color.WHITE);
        textViewR.setGravity(Gravity.CENTER);

        linearLayoutRight.addView(textViewW);
        linearLayoutRight.addView(textViewR);

        linearLayout.addView(textViewSet);
        linearLayout.addView(linearLayoutRight);


        return linearLayout;
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
        Intent intent = new Intent(CheckDailyProgramActivity.this,DailyCheckActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}