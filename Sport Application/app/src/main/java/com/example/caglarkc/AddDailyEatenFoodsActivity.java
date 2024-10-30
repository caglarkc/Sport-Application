package com.example.caglarkc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddDailyEatenFoodsActivity extends AppCompatActivity {
    DatabaseReference mReferenceFoods, mReferenceUser;
    SharedPreferences sharedUser;


    ArrayAdapter<String> mealTimeAdapter, foodAdapter;
    Spinner foodSpinner, mealTimeSpinner;

    Button buttonAddNewFood, buttonDeleteLastFood, buttonSaveFoods;
    ProgressBar progressBar;
    ConstraintLayout constraintLayoutParent;
    LinearLayout container;

    String sharedUserUid, date;
    int seventyDp = 70, seventyPixel, thirtyDp = 30, thirtyPixel, hintColor;
    List<String> mealTimesList = new ArrayList<>();
    List<String> foodNameList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_daily_eaten_foods);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");

        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);
        mReferenceFoods = FirebaseDatabase.getInstance().getReference("Foods");

        constraintLayoutParent = findViewById(R.id.constraintLayoutParent);
        progressBar = findViewById(R.id.progressBar);
        buttonAddNewFood = findViewById(R.id.buttonAddNewFood);
        container = findViewById(R.id.container);
        buttonDeleteLastFood = findViewById(R.id.buttonDeleteLastFood);
        buttonSaveFoods = findViewById(R.id.buttonSaveFoods);


        seventyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                seventyDp,
                getResources().getDisplayMetrics()
        );
        thirtyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                thirtyDp,
                getResources().getDisplayMetrics()
        );
        hintColor = getColor(R.color.hint_color);
        date = getCurrentDateTime();

        getFoods();

        // meal_times dizisini çekin
        String[] mealTimesArray = getResources().getStringArray(R.array.mealtimes);
        mealTimesList = Arrays.asList(mealTimesArray);

        //Spinnera foodlara resimleri ekle başına spinnner dogru calısıyor item_food a ekle
        buttonAddNewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // RelativeLayout oluştur
                RelativeLayout relativeLayout = new RelativeLayout(AddDailyEatenFoodsActivity.this);

                // LayoutParams oluştur ve top margin ekle
                RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, // Tüm genişliği kaplayacak
                        ViewGroup.LayoutParams.WRAP_CONTENT // Yükseklik içeriğe göre ayarlanacak
                );
                relativeLayoutParams.setMargins(0, 10, 0, 0); // Sadece üstten 10dp margin ekledik

                // RelativeLayout'a layout parametrelerini uygula
                relativeLayout.setLayoutParams(relativeLayoutParams);


                // foodSpinner oluştur ve sola hizala
                foodAdapter = new ArrayAdapter<>(AddDailyEatenFoodsActivity.this, R.layout.spinner_item, foodNameList);
                foodAdapter.setDropDownViewResource(R.layout.spinner_item);
                foodSpinner = new Spinner(AddDailyEatenFoodsActivity.this);
                RelativeLayout.LayoutParams foodSpinnerParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        thirtyPixel
                );
                foodSpinnerParams.addRule(RelativeLayout.ALIGN_PARENT_START); // Sola hizala
                foodSpinner.setLayoutParams(foodSpinnerParams);
                foodSpinner.setBackgroundTintList(null);
                foodSpinner.setBackgroundResource(R.drawable.border);
                foodSpinner.setPadding(8,0,8,0);
                foodSpinner.setAdapter(foodAdapter);

                // EditText oluştur ve ortada hizala
                EditText editText = new EditText(AddDailyEatenFoodsActivity.this);
                editText.setId(View.generateViewId());
                RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(
                        seventyPixel, // Genişlik sabit olarak belirlenen 70 pixel
                        thirtyPixel
                );
                editTextParams.addRule(RelativeLayout.CENTER_IN_PARENT); // Ortaya hizala
                editText.setLayoutParams(editTextParams);
                editText.setGravity(Gravity.CENTER);
                editText.setHintTextColor(hintColor);
                editText.setHint("GRAM");
                editText.setTextColor(Color.WHITE);
                editText.setPadding(0, 0, 0, 0);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setBackgroundResource(R.drawable.under_line_border);

                // mealTimeSpinner oluştur ve sağa hizala
                mealTimeAdapter = new ArrayAdapter<>(AddDailyEatenFoodsActivity.this, R.layout.spinner_item, mealTimesList);
                mealTimeAdapter.setDropDownViewResource(R.layout.spinner_item);
                mealTimeSpinner = new Spinner(AddDailyEatenFoodsActivity.this);
                RelativeLayout.LayoutParams mealTimeSpinnerParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        thirtyPixel
                );
                mealTimeSpinnerParams.addRule(RelativeLayout.ALIGN_PARENT_END); // Sağa hizala
                mealTimeSpinner.setLayoutParams(mealTimeSpinnerParams);
                mealTimeSpinner.setBackgroundTintList(null);
                mealTimeSpinner.setBackgroundResource(R.drawable.border);
                mealTimeSpinner.setPadding(8,0,8,0);
                mealTimeSpinner.setAdapter(mealTimeAdapter);

                // RelativeLayout'a view'leri ekle
                relativeLayout.addView(foodSpinner);
                relativeLayout.addView(editText);
                relativeLayout.addView(mealTimeSpinner);

                // Container'a RelativeLayout'u ekle
                container.addView(relativeLayout);
            }
        });

        buttonDeleteLastFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Eğer container içinde en az bir öğe varsa
                if (container.getChildCount() > 0) {
                    // Son eklenen view'i kaldır
                    container.removeViewAt(container.getChildCount() - 1);
                } else {
                    Toast.makeText(AddDailyEatenFoodsActivity.this, "No items to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSaveFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (container.getChildCount() != 0) {
                    // Tüm RelativeLayout'ları döngüyle gez
                    for (int i = 0; i < container.getChildCount(); i++) {
                        View child = container.getChildAt(i);

                        if (child instanceof RelativeLayout) {
                            // RelativeLayout içindeki bileşenleri bul
                            RelativeLayout relativeLayout = (RelativeLayout) child;

                            Spinner foodSpinner = (Spinner) relativeLayout.getChildAt(0);
                            EditText gramEditText = (EditText) relativeLayout.getChildAt(1);
                            Spinner timeSpinner = (Spinner) relativeLayout.getChildAt(2);

                            // Spinner'lardan seçilen değerleri al
                            String selectedFood = foodSpinner.getSelectedItem().toString();
                            String selectedTime = timeSpinner.getSelectedItem().toString();
                            String gramValue = gramEditText.getText().toString();
                            gramValue = gramValue + "gr";

                            // Eğer Spinner'larda "choose" seçili değilse ve EditText boş değilse kaydet
                            if (!selectedFood.equals("Choose Food") && !selectedTime.equals("Choose Time") && !TextUtils.isEmpty(gramValue)) {
                                mReferenceUser.child("user_dailyEatenFoodsData").child(date).child(selectedTime).child(selectedFood).setValue(gramValue);

                                // Ya da veritabanına kaydedebilirsin
                            } else {
                                // Eğer validasyon başarısızsa hata mesajı göster
                                Toast.makeText(AddDailyEatenFoodsActivity.this, "Please make sure all fields are filled correctly.", Toast.LENGTH_SHORT).show();
                                return; // Validasyon başarısızsa işlemi sonlandır
                            }
                        }
                    }

                    // Tüm relativeLayout'lar başarıyla kontrol edildi, işlemler tamamlandı
                    container.removeAllViews();
                    Toast.makeText(AddDailyEatenFoodsActivity.this, "All data saved successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void getFoods() {
        constraintLayoutParent.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        foodNameList.add("Choose Food");
        for (int i = 0 ; i < Food.getFoodList().size() ; i++) {
            String name = Food.getFoodList().get(i).getName();
            foodNameList.add(name);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                constraintLayoutParent.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        },100);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddDailyEatenFoodsActivity.this,DailyCheckActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        Date currentDateTime = calendar.getTime();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateTimeFormat.format(currentDateTime);
    }
}