package com.example.caglarkc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
/**
 * CheckBodyMeasurementActivity: This activity allows users to review and visualize their body measurement data
 * by selecting a specific date. Users can choose a year, month, and day from spinners to display measurements
 * recorded on that date. Measurements are shown as values positioned over specific body parts on an image,
 * with each label corresponding to the relevant body part (e.g., arm, chest). Users can tap on these labels
 * to receive further information.
 *
 *
 * Key Components:
 * - Firebase database integration to retrieve user data.
 * - Dynamic UI elements displaying measurements over a body image.
 * - Spinner controls for selecting date.
 */

public class CheckBodyMeasurementActivity extends AppCompatActivity {
    SharedPreferences sharedUser;
    DatabaseReference mReferenceUser, mReferenceMeasurementData;

    ImageView imageViewBody;
    FrameLayout frameLayout;
    LinearLayout dateContainer;
    Button buttonCheckDates;
    Spinner yearSpinner, monthSpinner, daySpinner;
    ConstraintLayout constraintLayoutParent, main;
    TextView textViewData;

    String sharedUserUid;
    boolean isYearSpinnerInitial = true, isMonthSpinnerInitial = true, isDaySpinnerInitial = true;
    int twentyDp = 20, twentyPixel;

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
        frameLayout = findViewById(R.id.frameLayout);
        imageViewBody = findViewById(R.id.imageViewBody);
        yearSpinner = findViewById(R.id.yearSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        daySpinner = findViewById(R.id.daySpinner);
        dateContainer = findViewById(R.id.dateContainer);
        buttonCheckDates = findViewById(R.id.buttonCheckDates);
        textViewData = findViewById(R.id.textViewData);
        main = findViewById(R.id.main);

        textViewData.setVisibility(View.GONE);


        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");

        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);
        mReferenceMeasurementData = mReferenceUser.child("user_dailyMeasurementData");

        twentyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                twentyDp,
                getResources().getDisplayMetrics()
        );


        dateList = MainMethods.getBodyMeasurementDates();
        createSpinners();

        buttonCheckDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            checkListOfDates();            }
        });



    }

    private void checkListOfDates() {
        clearLayout();
        imageViewBody.setVisibility(View.GONE);
        dateContainer.setVisibility(View.VISIBLE);
        LinearLayout linearLayout = new LinearLayout(CheckBodyMeasurementActivity.this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);


        for (String date : dateList) {
            LinearLayout miniLayout = new LinearLayout(CheckBodyMeasurementActivity.this);
            miniLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            miniLayout.setOrientation(LinearLayout.HORIZONTAL);

            ImageView imageView = new ImageView(CheckBodyMeasurementActivity.this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(twentyPixel,twentyPixel));
            imageView.setImageResource(R.drawable.calendar_icon);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    twentyPixel
            );
            layoutParams.bottomMargin = 5;
            layoutParams.leftMargin = 20;
            TextView textView = new TextView(CheckBodyMeasurementActivity.this);
            textView.setLayoutParams(layoutParams);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(8,2,8,2);
            textView.setText(date);

            miniLayout.addView(imageView);
            miniLayout.addView(textView);
            linearLayout.addView(miniLayout);
        }

        dateContainer.addView(linearLayout);

    }


    private void clearLayout() {
        textViewData.setVisibility(View.GONE);
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
                                System.out.println(value);
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
        String length = "";
        String weight = "";
        String neck = "";
        String shoulder = "";
        String chest = "";
        String arm = "";
        String forearm = "";
        String abdomen = "";
        String hip = "";
        String quad = "";
        String calf = "";

        for (Map.Entry<String , String> entry : hashMapData.entrySet()) {
            String bodyPart = entry.getKey();
            String value = entry.getValue();

            if (bodyPart.equals("length")) {
                createTextView(750,20,value,"length");
                length = value;
            }else if (bodyPart.equals("weight")) {
                createTextView(950,20,value,"weight");
                weight = value;
            }else if (bodyPart.equals("neck")) {
                createTextView(638,400,value,"neck");
                neck = value;
            }else if (bodyPart.equals("shoulder")) {
                createTextView(800,487,value,"shoulder");
                shoulder = value;
            }else if (bodyPart.equals("chest")) {
                createTextView(617,531,value,"chest");
                chest = value;
            }else if (bodyPart.equals("arm")) {
                createTextView(832,682,value,"arm");
                arm = value;
            }else if (bodyPart.equals("forearm")) {
                createTextView(930,899,value,"forearm");
                forearm = value;
            }else if (bodyPart.equals("abdomen")) {
                createTextView(600,845,value,"abdomen");
                abdomen = value;
            }else if (bodyPart.equals("hip")) {
                createTextView(755,1023,value,"hip");
                hip = value;
            }else if (bodyPart.equals("quad")) {
                createTextView(650,1208,value,"quad");
                quad = value;
            }else if (bodyPart.equals("calf")) {
                createTextView(795,1708,value,"calf");
                calf = value;
            }
        }

        // Tüm verileri birleştirerek TextView içerisine yerleştiriyoruz
        String displayText = "Length: " + length + "\n" +
                "Weight: " + weight + "\n" +
                "Neck: " + neck + "\n" +
                "Shoulder: " + shoulder + "\n" +
                "Chest: " + chest + "\n" +
                "Arm: " + arm + "\n" +
                "Forearm: " + forearm + "\n" +
                "Abdomen: " + abdomen + "\n" +
                "Hip: " + hip + "\n" +
                "Quad: " + quad + "\n" +
                "Calf: " + calf;

        textViewData.setText(displayText);
        textViewData.setVisibility(View.VISIBLE);
        textViewData.bringToFront(); // En üstte olması için
        textViewData.invalidate();    // Yeniden çizim talebi
        textViewData.requestLayout();
        Toast.makeText(CheckBodyMeasurementActivity.this,"For remove textview, click somewhere...",Toast.LENGTH_SHORT).show();

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewData.setVisibility(View.GONE);
            }
        });

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
                dateContainer.setVisibility(View.GONE);
                imageViewBody.setVisibility(View.VISIBLE);
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
                dateContainer.setVisibility(View.GONE);
                imageViewBody.setVisibility(View.VISIBLE);
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
                dateContainer.setVisibility(View.GONE);
                imageViewBody.setVisibility(View.VISIBLE);
                showSelectedDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}