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
//Vücut Yağ Yüzdesi (%) = 86.010 × log10(bel çevresi - boyun çevresi) - 70.041 × log10(boy) + 36.76
// Macro tricker da ekleyeceig zve iyecekleri favoriye ekleme de yapacagım


//Günlük random motivasyon sözleri çıkan bir widget ekle
//Günlük programı gösteren bir widget ekle.
//ilk tıkladıgında mail ve şifreni isteyip onları kayedeidp bir sonraki tıklamalrında direk giriş yapıp menuyu acan widgetı ekle


//Günlük yapılan egzersiz sonucu ykaılan calori hesaplanıp yediig kalori ile hesaplanıp bir hedef belirleyebilir ve ordan takibi ypaılabilcek bi sayfa

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mReference, mReferenceExercises;

    EditText editTextEmail, editTextPassword;
    Button buttonRegister, buttonLogin, buttonLoginUser;
    ImageButton imageButtonShowPassword;

    SharedPreferences sharedUser;
    SharedPreferences.Editor editor;

    String stringEmail, stringPassword;

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



}