package com.example.caglarkc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CheckYourProgressActivity extends AppCompatActivity {
    SharedPreferences sharedUser;
    DatabaseReference mReferenceUser, mReferenceImages;

    ProgressBar progressBar;
    ConstraintLayout constraintLayoutParent;
    LinearLayout dateContainer, imageButtonContainer;
    Spinner yearSpinner, monthSpinner, daySpinner;

    String sharedUserUid, selectedDate;
    boolean isYearSpinnerInitial = true, isMonthSpinnerInitial = true, isDaySpinnerInitial = true;
    int threeHundredDp = 300, threeHundredPixel, fourHundredDp = 400, fourHundredPixel;

    List<String> dateList = new ArrayList<>();
    ArrayList<String> years = new ArrayList<>();
    ArrayList<String> months = new ArrayList<>();
    ArrayList<String> days = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_your_progress);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        yearSpinner = findViewById(R.id.yearSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        daySpinner = findViewById(R.id.daySpinner);
        dateContainer = findViewById(R.id.dateContainer);
        constraintLayoutParent = findViewById(R.id.constraintLayoutParent);
        progressBar = findViewById(R.id.progressBar);
        imageButtonContainer = findViewById(R.id.imageButtonContainer);

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");
        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);
        mReferenceImages = mReferenceUser.child("user_dailyProgressImages");

        threeHundredPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                threeHundredDp,
                getResources().getDisplayMetrics()
        );
        fourHundredPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                fourHundredDp,
                getResources().getDisplayMetrics()
        );

        progressBar.setVisibility(View.VISIBLE);
        constraintLayoutParent.setVisibility(View.GONE);
        getDatesFromDatabase();
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
        Intent intent = new Intent(CheckYourProgressActivity.this,DailyCheckActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void getDatesFromDatabase() {
        mReferenceImages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        if (date != null) {
                            dateList.add(date);
                        }
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
    }

    private void showSelectedDate() {
        String selectedYear = (String) yearSpinner.getSelectedItem();
        String selectedMonth = (String) monthSpinner.getSelectedItem();
        String selectedDay = (String) daySpinner.getSelectedItem();

        selectedDate = selectedDay + "-" + selectedMonth + "-" + selectedYear;

        if (dateList.contains(selectedDate)) {
            mReferenceImages.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot imageSnapshot : snapshot.getChildren()) {
                            String imageName = imageSnapshot.getKey();
                            String url = imageSnapshot.getValue(String.class);
                            if (url != null && imageName != null) {
                                createEntryLayout(url , imageName);
                            }
                        }
                        Space space = new Space(CheckYourProgressActivity.this);
                        space.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT
                                ,fourHundredPixel/4));
                        imageButtonContainer.addView(space);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void createEntryLayout(String url , String imageName) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(threeHundredPixel,fourHundredPixel);
        layoutParams.bottomMargin = 30;

        ImageButton imageButton = new ImageButton(CheckYourProgressActivity.this);
        imageButton.setLayoutParams(layoutParams);
        imageButton.setPadding(4,4,4,4);
        imageButton.setBackgroundResource(R.drawable.border);
        imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(CheckYourProgressActivity.this)
                .load(url)
                .into(imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckYourProgressActivity.this);
                builder.setTitle("DELETE");
                builder.setMessage("Do you want delete this image ? ");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing ...
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference reference = storage.getReferenceFromUrl(url);

                        // Dosya silme işlemi (Firebase Storage)
                        Task<Void> deleteStorageTask = reference.delete();

                        // Realtime Database'den veriyi silme işlemi
                        Task<Void> deleteDatabaseTask = mReferenceImages.child(selectedDate).child(imageName).removeValue();

                        // Her iki işlemi de paralel olarak çalıştır ve sonuçlarını kontrol et
                        Tasks.whenAll(deleteStorageTask, deleteDatabaseTask).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Her iki işlem de başarıyla tamamlandı
                                    Toast.makeText(getApplicationContext(), "Image deleted with successfully...", Toast.LENGTH_SHORT).show();
                                    clearLayout();
                                    showSelectedDate();
                                } else {
                                    // Bir veya daha fazla işlem başarısız oldu
                                    Toast.makeText(getApplicationContext(), "Delete process is failed...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Her iki işlemden biri başarısız olduğunda buraya girer
                                Log.d("ERROR",e.getMessage());
                            }
                        });
                    }
                });
                builder.show();
            }
        });
        imageButtonContainer.addView(imageButton);

    }

    private void clearLayout() {
        for (int i = 0; i < imageButtonContainer.getChildCount(); i++) {
            View child = imageButtonContainer.getChildAt(i);

            // Hem ImageButton hem de Space instance'larını kaldır
            if (child instanceof ImageButton || child instanceof Space) {
                imageButtonContainer.removeView(child);

                // View kaldırıldıktan sonra index ayarlamak için i'yi azaltıyoruz
                i--;
            }
        }
    }


}