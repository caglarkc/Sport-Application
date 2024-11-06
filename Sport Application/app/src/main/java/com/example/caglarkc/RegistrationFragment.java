package com.example.caglarkc;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * RegistrationFragment: Provides user registration functionality within a fragment.
 * - Handles user input for email, password, and password confirmation fields.
 * - Contains password visibility toggle for both password and confirmation fields.
 * - Includes input validation for email, password strength, and password match, displaying appropriate error messages.
 * - Calls the parent activity's registration method on successful validation.
 */


public class RegistrationFragment extends Fragment {
    public RegistrationFragment() {}

    EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    Button buttonRegister;
    ImageButton imageButtonShowPasswordFirst, imageButtonShowPasswordSecond;

    String stringEmail, stringPassword, stringConfirmPassword;


    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        buttonRegister = view.findViewById(R.id.buttonRegister);
        imageButtonShowPasswordFirst = view.findViewById(R.id.imageButtonShowPasswordFirst);
        imageButtonShowPasswordSecond = view.findViewById(R.id.imageButtonShowPasswordSecond);



        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                if (charSequence.length() > 0) {
                    if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
                        buttonRegister.setBackgroundResource(R.drawable.full_button_border);
                        buttonRegister.setTextColor(Color.WHITE);
                    }else {
                        buttonRegister.setBackgroundResource(R.drawable.empty_button_border);
                        buttonRegister.setTextColor(getResources().getColor(R.color.half_light_text_color));
                    }
                }else {
                    buttonRegister.setBackgroundResource(R.drawable.empty_button_border);
                    buttonRegister.setTextColor(getResources().getColor(R.color.half_light_text_color));
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
                String email = editTextEmail.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                if (charSequence.length() > 0) {
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(confirmPassword)) {
                        buttonRegister.setBackgroundResource(R.drawable.full_button_border);
                        buttonRegister.setTextColor(Color.WHITE);
                    }else {
                        buttonRegister.setBackgroundResource(R.drawable.empty_button_border);
                        buttonRegister.setTextColor(getResources().getColor(R.color.half_light_text_color));
                    }
                }else {
                    buttonRegister.setBackgroundResource(R.drawable.empty_button_border);
                    buttonRegister.setTextColor(getResources().getColor(R.color.half_light_text_color));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = editTextPassword.getText().toString();
                String email = editTextEmail.getText().toString();
                if (charSequence.length() > 0) {
                    if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)) {
                        buttonRegister.setBackgroundResource(R.drawable.full_button_border);
                        buttonRegister.setTextColor(Color.WHITE);
                    }else {
                        buttonRegister.setBackgroundResource(R.drawable.empty_button_border);
                        buttonRegister.setTextColor(getResources().getColor(R.color.half_light_text_color));
                    }
                }else {
                    buttonRegister.setBackgroundResource(R.drawable.empty_button_border);
                    buttonRegister.setTextColor(getResources().getColor(R.color.half_light_text_color));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        imageButtonShowPasswordFirst.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageButtonShowPasswordFirst.setBackgroundResource(R.drawable.eye_icon);
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        return true;
                    case MotionEvent.ACTION_UP:
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        imageButtonShowPasswordFirst.setBackgroundResource(R.drawable.eye_visible_icon);
                        return true;
                }
                return false;
            }
        });

        imageButtonShowPasswordSecond.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageButtonShowPasswordSecond.setBackgroundResource(R.drawable.eye_icon);
                        editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        return true;
                    case MotionEvent.ACTION_UP:
                        editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        imageButtonShowPasswordSecond.setBackgroundResource(R.drawable.eye_visible_icon);
                        return true;
                }
                return false;
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringEmail = editTextEmail.getText().toString();
                stringPassword = editTextPassword.getText().toString();
                stringConfirmPassword = editTextConfirmPassword.getText().toString();

                if (!TextUtils.isEmpty(stringEmail) && !TextUtils.isEmpty(stringPassword) && !TextUtils.isEmpty(stringConfirmPassword)
                        && stringPassword.equals(stringConfirmPassword) && MainMethods.isPasswordStrongEnough(stringPassword)){
                    ((RegisterActivity) requireActivity()).getRegistration(stringEmail, stringPassword);

                }else if (!stringConfirmPassword.equals(stringPassword)){
                    Toast.makeText(getContext(),"Passwords are not same...",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(stringPassword)){
                    Toast.makeText(getContext(),"Password cant be empty...",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(stringEmail)){
                    Toast.makeText(getContext(),"Email cant be empty...",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(stringConfirmPassword)) {
                    Toast.makeText(getContext(),"Confirm Password...",Toast.LENGTH_SHORT).show();
                } else if (!MainMethods.isPasswordStrongEnough(stringPassword)){
                    Toast.makeText(getContext(),"Password is not strong enough, must contain capital and little alphabet and numeric",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
