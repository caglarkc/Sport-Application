package com.example.caglarkc;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UsernameFragment extends Fragment {
    public UsernameFragment() {}

    Button buttonContinue;
    EditText editTextUsername;

    String stringUsername;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_username, container, false);


        buttonContinue = view.findViewById(R.id.buttonContinue);
        editTextUsername = view.findViewById(R.id.editTextUsername);

        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                stringUsername = editTextUsername.getText().toString();

                if (charSequence.length()>0 && !TextUtils.isEmpty(stringUsername)){
                    buttonContinue.setBackgroundResource(R.drawable.full_button_border);
                    buttonContinue.setTextColor(Color.WHITE);
                }else {
                    buttonContinue.setBackgroundResource(R.drawable.empty_button_border);
                    buttonContinue.setTextColor(getResources().getColor(R.color.half_light_text_color));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String val = editTextUsername.getText().toString();
                if (!TextUtils.isEmpty(val)){
                    ((RegisterActivity) requireActivity()).getUsername(val);

                }else {
                    Toast.makeText(getActivity(),"Enter a value...",Toast.LENGTH_SHORT).show();
                }
            }
        });



        return view;
    }


}
