package com.example.caglarkc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckBodyMeasurementActivity extends AppCompatActivity {
    SharedPreferences sharedUser;
    DatabaseReference mReferenceUser, mReferenceMeasurementData;

    ImageView imageViewBody;
    FrameLayout frameLayout;
    Spinner yearSpinner, monthSpinner, daySpinner;
    ConstraintLayout constraintLayoutParent;
    ProgressBar progressBar;

    String sharedUserUid;
    boolean isYearSpinnerInitial = true, isMonthSpinnerInitial = true, isDaySpinnerInitial = true;

    List<String> dateList = new ArrayList<>();
    ArrayList<String> years = new ArrayList<>();
    ArrayList<String> months = new ArrayList<>();
    ArrayList<String> days = new ArrayList<>();
    HashMap<String , String> hashMapData = new HashMap<>();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_body_measurement);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        constraintLayoutParent = findViewById(R.id.constraintLayoutParent);
        progressBar = findViewById(R.id.progressBar);
        frameLayout = findViewById(R.id.frameLayout);
        imageViewBody = findViewById(R.id.imageViewBody);
        yearSpinner = findViewById(R.id.yearSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        daySpinner = findViewById(R.id.daySpinner);

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");


        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);
        mReferenceMeasurementData = mReferenceUser.child("user_dailyMeasurementData");

        progressBar.setVisibility(View.VISIBLE);
        constraintLayoutParent.setVisibility(View.GONE);
        mReferenceMeasurementData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        createSpinners();





    }

    private void clearLayout() {
    // FrameLayout içindeki tüm alt öğeleri al
        for (int i = 0; i < frameLayout.getChildCount(); i++) {
            View child = frameLayout.getChildAt(i);

            // Eğer alt öğe bir TextView ise
            if (child instanceof TextView) {
                // TextView'i FrameLayout'tan kaldır
                frameLayout.removeView(child);

                // removeView işlemi child sayısını değiştireceği için döngüyü yeniden başlatmak gerekir
                i--;  // Bu sayede döngü tekrar doğru child üzerinde devam eder
            }
        }

    }

    private void showSelectedDate() {
        String selectedYear = (String) yearSpinner.getSelectedItem();
        String selectedMonth = (String) monthSpinner.getSelectedItem();
        String selectedDay = (String) daySpinner.getSelectedItem();

        String selectedDate = selectedDay + "-" + selectedMonth + "-" + selectedYear;

        if (dateList.contains(selectedDate)) {
            mReferenceMeasurementData.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot bodyPartSnapshot : snapshot.getChildren()) {
                            String bodyPart = bodyPartSnapshot.getKey();
                            if (bodyPart != null) {
                                String value = bodyPartSnapshot.getValue(String.class);
                                hashMapData.put(bodyPart,value);
                            }
                        }
                        createEntryLayouts();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }


    private void createEntryLayouts() {
        for (Map.Entry<String , String> entry : hashMapData.entrySet()) {
            String bodyPart = entry.getKey();
            String value = entry.getValue();

            if (bodyPart.equals("length")) {
                createTextView(750,20,value,"length");
            }else if (bodyPart.equals("weight")) {
                createTextView(950,20,value,"weight");
            }else if (bodyPart.equals("neck")) {
                createTextView(638,400,value,"neck");
            }else if (bodyPart.equals("shoulder")) {
                createTextView(800,487,value,"shoulder");
            }else if (bodyPart.equals("chest")) {
                createTextView(617,531,value,"chest");
            }else if (bodyPart.equals("arm")) {
                createTextView(832,682,value,"arm");
            }else if (bodyPart.equals("forearm")) {
                createTextView(930,899,value,"forearm");
            }else if (bodyPart.equals("abdomen")) {
                createTextView(600,845,value,"abdomen");
            }else if (bodyPart.equals("hip")) {
                createTextView(755,1023,value,"hip");
            }else if (bodyPart.equals("quad")) {
                createTextView(650,1208,value,"quad");
            }else if (bodyPart.equals("calf")) {
                createTextView(795,1708,value,"calf");
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private TextView createTextView(float x, float y, String size,String region) {
        TextView textView = new TextView(CheckBodyMeasurementActivity.this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setX(x);
        textView.setY(y);
        textView.setPadding(10,2,12,2);
        textView.setText(size);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.drawable.text_changed_border);

        // OnTouchListener ekle
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Dokunma başladığında yapılacak işlem
                        return true; // Olayın işlendiğini belirtir

                    case MotionEvent.ACTION_UP:
                        // Dokunma bittiğinde yapılacak işlem
                        String x = region.substring(0,1).toUpperCase() + region.substring(1);
                        Toast.makeText(CheckBodyMeasurementActivity.this,x,Toast.LENGTH_SHORT).show();
                        Log.d("TouchEvent", "Dokunma bırakıldı");
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Dokunma hareket ettirildiğinde yapılacak işlem
                        Log.d("TouchEvent", "Parmak hareket ettirildi");
                        return true;
                }
                return false; // Olayın işlenmediğini belirtir, başka olaylar da işlenebilir
            }
        });
        frameLayout.addView(textView);

        return textView;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CheckBodyMeasurementActivity.this,DailyCheckActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
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
}