package com.example.caglarkc;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

/**
 * LoginActivity: Provides user authentication functionality, including login, registration redirection, and password visibility toggle.
 * - Firebase Authentication is used to validate user credentials.
 * - UI adjustments are made based on input, including button style changes and password visibility toggle.
 * - Contains a pre-filled login option for easy testing.
 * - Utilizes SharedPreferences to store user data locally upon successful login.
 */


//iyecekleri favoriye ekleme de yapacagım

    //Menu imagelerini düzenle


/*
Günlük random motivasyon sözleri çıkan bir widget ekle
Günlük programı gösteren bir widget ekle.
ilk tıkladıgında mail ve şifreni isteyip onları kayedeidp bir sonraki tıklamalrında direk giriş yapıp menuyu acan widgetı ekle


Günlük yapılan egzersiz sonucu ykaılan calori hesaplanıp yediig kalori ile hesaplanıp bir hedef belirleyebilir ve ordan takibi ypaılabilcek bi sayfa

 Sonrası için en son optimizasyon kısmında bpi hespalamayı kaldır onun yerine direk daily e measuremnt eklediginde kaydedilsin be ordan dondurulsun
 aynı şekilde total cal ekleme ve gosterme kısmını sıl tammen gunluk yemek eklenınce guncellenip kaydedilcek ve gostermede databaseden alınacak sekılde ayarla
 */





public class LoginActivity extends AppCompatActivity {
    DatabaseReference mReferenceExercises, mReferenceFoods, mReferenceUser;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mReference;

    EditText editTextEmail, editTextPassword;
    Button buttonRegister, buttonLogin, buttonLoginUser;
    ImageButton imageButtonShowPassword;

    SharedPreferences sharedUser;
    SharedPreferences.Editor editor;

    String stringEmail, stringPassword;
    String foodName, foodUrl;
    String abdomenData = "empty", neckData = "empty", wData = "empty", lData = "empty";
    int foodCarb, foodFat, foodProtein, foodCal;
    HashMap<String , Integer> hashMapFoodCalories = new HashMap<>();
    List<String> progressDatesList = new ArrayList<>();
    List<String> bodyMeasurementDatesList = new ArrayList<>();
    List<String> dailyEatenFoodsDatesList = new ArrayList<>();
    List<String> dailyProgramDatesList = new ArrayList<>();
    HashMap<String, String> hashMapBfi = new HashMap<>();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonRegister = findViewById(R.id.buttonRegister);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        imageButtonShowPassword = findViewById(R.id.imageButtonShowPassword);
        buttonLoginUser = findViewById(R.id.buttonLoginUser);

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        editor = sharedUser.edit();

        Region.regionList.clear();
        Region region = new Region("Choose Region");
        Region chest = new Region("Chest");
        Region back = new Region("Back");
        Region leg = new Region("Leg");
        Region abs = new Region("Abs");
        Region shoulder = new Region("Shoulder");
        Region triceps = new Region("Triceps");
        Region biceps = new Region("Biceps");

        Region.regionList.add(0,region);
        Region.regionList.add(1,chest);
        Region.regionList.add(2,back);
        Region.regionList.add(3,leg);
        Region.regionList.add(4,abs);
        Region.regionList.add(5,shoulder);
        Region.regionList.add(6,triceps);
        Region.regionList.add(7,biceps);


        buttonLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextEmail.setText("alicaglarkocer@gmail.com");
                editTextPassword.setText("Erebus13032003_");
                onClickLogin(view);
            }
        });

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    buttonLogin.setBackgroundResource(R.drawable.full_button_border);
                    buttonLogin.setTextColor(Color.WHITE);
                }else {
                    buttonLogin.setBackgroundResource(R.drawable.empty_button_border);
                    buttonLogin.setTextColor(getResources().getColor(R.color.half_light_text_color));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    buttonLogin.setBackgroundResource(R.drawable.full_button_border);
                    buttonLogin.setTextColor(Color.WHITE);
                }else {
                    buttonLogin.setBackgroundResource(R.drawable.empty_button_border);
                    buttonLogin.setTextColor(getResources().getColor(R.color.half_light_text_color));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        imageButtonShowPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageButtonShowPassword.setBackgroundResource(R.drawable.eye_icon);
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        return true;
                    case MotionEvent.ACTION_UP:
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        imageButtonShowPassword.setBackgroundResource(R.drawable.eye_visible_icon);
                        return true;
                }
                return false;
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogin(view);
            }
        });
    }

    private void onClickLogin(View view) {
        if (editTextEmail == null || editTextPassword == null) {
            Toast.makeText(LoginActivity.this, "Enter all data...", Toast.LENGTH_SHORT).show();
            return;
        }

        stringEmail = editTextEmail.getText().toString().trim();
        stringPassword = editTextPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(stringEmail) && !TextUtils.isEmpty(stringPassword)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(stringEmail).matches()) {
                Toast.makeText(LoginActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show progress indicator
            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(stringEmail, stringPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    mUser = mAuth.getCurrentUser();
                    Toast.makeText(LoginActivity.this,"You login with successfully...",Toast.LENGTH_SHORT).show();
                    editor.putString("user_uid", mUser.getUid());
                    editor.apply();
                    mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
                    getALlDataFromFirebase();
                    getAllUserDataFromFirebase(mReferenceUser);
                    getUserBfiDataFromFirebase(mReferenceUser);
                    Intent intent = new Intent(LoginActivity.this,UserMenuActivity.class);
                    startActivity(intent);
                    finish();

                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_LONG).show();
                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(LoginActivity.this, "No account with this email", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    System.out.println(e.getMessage());
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "Enter all data...", Toast.LENGTH_SHORT).show();
        }
    }

    private void getALlDataFromFirebase() {
        Exercise.exerciseList.clear();
        Exercise exercise = new Exercise("Choose Exercise","Null");
        Exercise.exerciseList.add(0,exercise);
        mReferenceExercises = FirebaseDatabase.getInstance().getReference("Exercises");
        mReferenceExercises.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot regionSnapshot : snapshot.getChildren()){
                    String region = regionSnapshot.getKey();
                    if (region != null){
                        for (DataSnapshot exerciseSnapshot : regionSnapshot.getChildren()){
                            String exerciseName = exerciseSnapshot.getKey();
                            Exercise exercise = new Exercise(exerciseName,region);
                            Exercise.exerciseList.add(exercise);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Food.getFoodList().clear();
        mReferenceFoods = FirebaseDatabase.getInstance().getReference("Foods");
        mReferenceFoods.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot detailSnapshot : foodSnapshot.getChildren()) {
                        String detail = detailSnapshot.getKey();
                        String x = detailSnapshot.getValue(String.class);
                        if (detail != null && x != null ) {
                            if (detail.equals("food_name")) {
                                foodName = x;
                            }else if (detail.equals("food_carb")) {
                                x = x.replace("/100gr","");
                                foodCarb = Integer.parseInt(x);
                            }else if (detail.equals("food_fat")) {
                                x = x.replace("/100gr","");
                                foodFat = Integer.parseInt(x);
                            }else if (detail.equals("food_protein")) {
                                x = x.replace("/100gr","");
                                foodProtein = Integer.parseInt(x);
                            }else if (detail.equals("food_cal")) {
                                x = x.replace("/100gr","");
                                foodCal = Integer.parseInt(x);
                            }else if (detail.equals("food_imageUrl")) {
                                foodUrl = x;
                            }
                        }
                    }
                    Food food = new Food(foodName, foodCarb, foodFat, foodProtein, foodCal, foodUrl);
                    hashMapFoodCalories.put(foodName, foodCal);
                }
                MainMethods.setHashMapFoodCalories(hashMapFoodCalories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAllUserDataFromFirebase(DatabaseReference referenceUser) {

        DatabaseReference mReferenceImages = referenceUser.child("user_dailyProgressImages");
        mReferenceImages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        if (date != null) {
                            progressDatesList.add(date);
                        }
                    }
                    MainMethods.setProgressDates(progressDatesList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR",error.getMessage());
            }
        });

        DatabaseReference mReferenceMeasurementData = referenceUser.child("user_dailyMeasurementData");
        mReferenceMeasurementData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    if (date != null) {
                        bodyMeasurementDatesList.add(date);
                    }
                }
                MainMethods.setBodyMeasurementDates(bodyMeasurementDatesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR",error.getMessage());
            }
        });

        DatabaseReference mReferenceDailyEatenData = referenceUser.child("user_dailyEatenFoodsData");
        mReferenceDailyEatenData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        if (date != null) {
                            dailyEatenFoodsDatesList.add(date);
                        }
                    }
                    MainMethods.setDailyEatenFoodDates(dailyEatenFoodsDatesList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR",error.getMessage());
            }
        });

        DatabaseReference mReferenceDailyProgramData = referenceUser.child("user_dailyFitnessProgramData");
        mReferenceDailyProgramData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        if (dateSnapshot.exists()) {
                            String date = dateSnapshot.getKey();
                            if (date != null) {
                                dailyProgramDatesList.add(date);
                            }
                        }
                    }
                    MainMethods.setDailyProgramDates(dailyProgramDatesList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR",error.getMessage());
            }
        });
    }

    private void getUserBfiDataFromFirebase(DatabaseReference referenceUser) {
        DatabaseReference mReferenceMeasurementData = referenceUser.child("user_dailyMeasurementData");
        mReferenceMeasurementData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    if (date != null) {
                        for (DataSnapshot measurementSnapshot : dateSnapshot.getChildren()) {
                            String measurement = measurementSnapshot.getKey();
                            if (measurement != null) {
                                if (measurement.equals("abdomen")) {
                                    abdomenData = measurementSnapshot.getValue(String.class);
                                }else if (measurement.equals("neck")) {
                                    neckData = measurementSnapshot.getValue(String.class);
                                }else if (measurement.equals("weight")) {
                                    wData = measurementSnapshot.getValue(String.class);
                                }else if (measurement.equals("length")) {
                                    lData = measurementSnapshot.getValue(String.class);
                                }
                            }
                        }
                    }
                    if (!abdomenData.equals("empty") && !neckData.equals("empty") && !wData.equals("empty") && !lData.equals("empty")) {
                        if (hashMapBfi.isEmpty()) {
                            hashMapBfi.put(date,abdomenData + "_" + neckData + "_" + wData + "_" + lData);
                        }else {
                            Map.Entry<String, String> entry = hashMapBfi.entrySet().iterator().next();
                            String key = entry.getKey();

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                            try {
                                Date keyDate = sdf.parse(key);
                                Date dateToCompare = sdf.parse(date);

                                if (dateToCompare.after(keyDate)) {
                                    hashMapBfi.clear();
                                    hashMapBfi.put(date,abdomenData + "_" + neckData + "_" + wData + "_" + lData);
                                }
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                }
                MainMethods.setHashMapBfi(hashMapBfi);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}