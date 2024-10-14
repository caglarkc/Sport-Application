package com.example.caglarkc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class AddNewProgramActivity extends AppCompatActivity {
    CheckBox checkBoxMonday, checkBoxTuesday, checkBoxWednesday, checkBoxThursday, checkBoxFriday, checkBoxSaturday, checkBoxSunday;
    SharedPreferences sharedProgram;
    DatabaseReference mReferenceProgram;
    SharedPreferences.Editor programEditor;

    EditText editTextProgramName, editTextWorkdaysValue, editTextSetValueMonday, editTextSetValueTuesday, editTextSetValueWednesday, editTextSetValueThursday, editTextSetValueFriday
            , editTextSetValueSaturday, editTextSetValueSunday;
    Button buttonContinue;

    String stringProgramName, stringWorkdaysValue, stringExerciseValue;
    int intWorkdaysValue, intExerciseValue;
    boolean isMonday, isTuesday, isWednesday, isThursday, isFriday, isSaturday, isSunday, isExerciseValuesOk = true, isEnteredExerciseValue = true;
    HashMap<String, Integer> hashMapDayExerciseValues = new HashMap<>();

    List<String> programNamesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_program);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedProgram = getSharedPreferences("program_data",MODE_PRIVATE);
        programEditor = sharedProgram.edit();

        mReferenceProgram = FirebaseDatabase.getInstance().getReference("Programs");



        checkBoxMonday = findViewById(R.id.checkBoxMonday);
        checkBoxTuesday = findViewById(R.id.checkBoxTuesday);
        checkBoxWednesday = findViewById(R.id.checkBoxWednesday);
        checkBoxThursday = findViewById(R.id.checkBoxThursday);
        checkBoxFriday = findViewById(R.id.checkBoxFriday);
        checkBoxSaturday = findViewById(R.id.checkBoxSaturday);
        checkBoxSunday = findViewById(R.id.checkBoxSunday);

        editTextSetValueMonday = findViewById(R.id.editTextSetValueMonday);
        editTextSetValueTuesday = findViewById(R.id.editTextSetValueTuesday);
        editTextSetValueWednesday = findViewById(R.id.editTextSetValueWednesday);
        editTextSetValueThursday = findViewById(R.id.editTextSetValueThursday);
        editTextSetValueFriday = findViewById(R.id.editTextSetValueFriday);
        editTextSetValueSaturday = findViewById(R.id.editTextSetValueSaturday);
        editTextSetValueSunday = findViewById(R.id.editTextSetValueSunday);


        editTextProgramName = findViewById(R.id.editTextProgramName);
        editTextWorkdaysValue = findViewById(R.id.editTextWorkdaysValue);
        buttonContinue = findViewById(R.id.buttonContinue);


        makeInvisibleAllCheckBoxes(true);
        takeProgramNames();
        checkBox();

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isExerciseValuesOk = true;
                isEnteredExerciseValue = true;
                stringProgramName = editTextProgramName.getText().toString();
                stringWorkdaysValue = editTextWorkdaysValue.getText().toString();
                if (!TextUtils.isEmpty(stringProgramName) && !TextUtils.isEmpty(stringWorkdaysValue) && !stringWorkdaysValue.equals("0")){
                    if (!programNamesList.contains(stringProgramName)){
                        intWorkdaysValue = Integer.parseInt(stringWorkdaysValue);
                        isExerciseValuesOk = true;
                        int checkedDays = putExerciseValuesToHashMap();
                        if (checkedDays == intWorkdaysValue && isExerciseValuesOk){
                            programEditor.putString("program_name",stringProgramName);
                            programEditor.putString("value_of_workdays",stringWorkdaysValue);
                            programEditor.putInt("monday",hashMapDayExerciseValues.get("Monday"));
                            programEditor.putInt("tuesday",hashMapDayExerciseValues.get("Tuesday"));
                            programEditor.putInt("wednesday",hashMapDayExerciseValues.get("Wednesday"));
                            programEditor.putInt("thursday",hashMapDayExerciseValues.get("Thursday"));
                            programEditor.putInt("friday",hashMapDayExerciseValues.get("Friday"));
                            programEditor.putInt("saturday",hashMapDayExerciseValues.get("Saturday"));
                            programEditor.putInt("sunday",hashMapDayExerciseValues.get("Sunday"));
                            programEditor.apply();

                            Intent intent = new Intent(AddNewProgramActivity.this, AddExerciseToNewProgramActivity.class);
                            startActivity(intent);
                            finish();
                        }else if (checkedDays == intWorkdaysValue && !isExerciseValuesOk){
                            Toast.makeText(AddNewProgramActivity.this,"Exercise value must be smaller than 12...",Toast.LENGTH_SHORT).show();
                        }else if (checkedDays == 0 ){

                        }else {
                            Toast.makeText(AddNewProgramActivity.this,"Value of workdays and checked days are must be same...",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(AddNewProgramActivity.this,"This program name is taken...",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(AddNewProgramActivity.this,"Please enter name and value of workdays...",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddNewProgramActivity.this,FitnessProgramActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void takeProgramNames(){
        mReferenceProgram.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot typeSnapshot : snapshot.getChildren()){
                    for (DataSnapshot programNameSnapshot : typeSnapshot.getChildren()){
                        String programName = programNameSnapshot.getKey();
                        programNamesList.add(programName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private int putExerciseValuesToHashMap(){
        int checkedDays = 0;
        isCheckedBoxes();
        if (isMonday){
            stringExerciseValue = editTextSetValueMonday.getText().toString();
            if (!TextUtils.isEmpty(stringExerciseValue)){
                intExerciseValue = Integer.parseInt(stringExerciseValue);
                if (intExerciseValue > 16){
                    isExerciseValuesOk = false;
                }else {
                    checkedDays +=1;
                }
                hashMapDayExerciseValues.put("Monday",intExerciseValue);
            }else {
                isEnteredExerciseValue = false;
                hashMapDayExerciseValues.put("Monday",0);
            }

        }else {
            hashMapDayExerciseValues.put("Monday",0);
        }
        if (isTuesday){
            stringExerciseValue = editTextSetValueTuesday.getText().toString();
            if (!TextUtils.isEmpty(stringExerciseValue)){
                intExerciseValue = Integer.parseInt(stringExerciseValue);
                if (intExerciseValue > 16){
                    isExerciseValuesOk = false;
                }else {
                    checkedDays +=1;
                }
                hashMapDayExerciseValues.put("Tuesday",intExerciseValue);
            }else {
                isEnteredExerciseValue = false;
                hashMapDayExerciseValues.put("Tuesday",0);
            }

        }else {
            hashMapDayExerciseValues.put("Tuesday",0);
        }
        if (isWednesday){
            stringExerciseValue = editTextSetValueWednesday.getText().toString();
            if (!TextUtils.isEmpty(stringExerciseValue)){
                intExerciseValue = Integer.parseInt(stringExerciseValue);
                if (intExerciseValue > 16){
                    isExerciseValuesOk = false;
                }else {
                    checkedDays +=1;
                }
                hashMapDayExerciseValues.put("Wednesday",intExerciseValue);
            }else {
                isEnteredExerciseValue = false;
                hashMapDayExerciseValues.put("Wednesday",0);
            }

        }else {
            hashMapDayExerciseValues.put("Wednesday",0);
        }
        if (isThursday){
            stringExerciseValue = editTextSetValueThursday.getText().toString();
            if (!TextUtils.isEmpty(stringExerciseValue)){
                intExerciseValue = Integer.parseInt(stringExerciseValue);
                if (intExerciseValue > 16){
                    isExerciseValuesOk = false;
                }else {
                    checkedDays +=1;
                }
                hashMapDayExerciseValues.put("Thursday",intExerciseValue);
            }else {
                isEnteredExerciseValue = false;
                hashMapDayExerciseValues.put("Thursday",0);
            }

        }else {
            hashMapDayExerciseValues.put("Thursday",0);
        }
        if (isFriday){
            stringExerciseValue = editTextSetValueFriday.getText().toString();
            if (!TextUtils.isEmpty(stringExerciseValue)){
                intExerciseValue = Integer.parseInt(stringExerciseValue);
                if (intExerciseValue > 16){
                    isExerciseValuesOk = false;
                }else {
                    checkedDays +=1;
                }
                hashMapDayExerciseValues.put("Friday",intExerciseValue);
            }else {
                isEnteredExerciseValue = false;
                hashMapDayExerciseValues.put("Friday",0);
            }

        }else {
            hashMapDayExerciseValues.put("Friday",0);
        }
        if (isSaturday){
            stringExerciseValue = editTextSetValueSaturday.getText().toString();
            if (!TextUtils.isEmpty(stringExerciseValue)){
                intExerciseValue = Integer.parseInt(stringExerciseValue);
                if (intExerciseValue > 16){
                    isExerciseValuesOk = false;
                }else {
                    checkedDays +=1;
                }
                hashMapDayExerciseValues.put("Saturday",intExerciseValue);
            }else {
                isEnteredExerciseValue = false;
                hashMapDayExerciseValues.put("Saturday",0);
            }

        }else {
            hashMapDayExerciseValues.put("Saturday",0);
        }
        if (isSunday){
            stringExerciseValue = editTextSetValueSunday.getText().toString();
            if (!TextUtils.isEmpty(stringExerciseValue)){
                intExerciseValue = Integer.parseInt(stringExerciseValue);
                if (intExerciseValue > 16){
                    isExerciseValuesOk = false;
                }else {
                    checkedDays +=1;
                }
                hashMapDayExerciseValues.put("Sunday",intExerciseValue);
            }else {
                isEnteredExerciseValue = false;
                hashMapDayExerciseValues.put("Sunday",0);
            }

        }else {
            hashMapDayExerciseValues.put("Sunday",0);
        }
        if (!isEnteredExerciseValue){
            Toast.makeText(AddNewProgramActivity.this,"Exercise value cant be empty...",Toast.LENGTH_SHORT).show();
        }
        if (!isExerciseValuesOk){
            Toast.makeText(AddNewProgramActivity.this,"Exercise value must be smaller than 16...",Toast.LENGTH_SHORT).show();
        }
        return checkedDays;
    }

    private void isCheckedBoxes(){
        isMonday = checkBoxMonday.isChecked();
        isTuesday = checkBoxTuesday.isChecked();
        isWednesday = checkBoxWednesday.isChecked();
        isThursday = checkBoxThursday.isChecked();
        isFriday = checkBoxFriday.isChecked();
        isSaturday = checkBoxSaturday.isChecked();
        isSunday = checkBoxSunday.isChecked();
    }
    private void makeInvisibleAllCheckBoxes(Boolean choose){
        if (choose){
            editTextSetValueMonday.setVisibility(View.INVISIBLE);
            editTextSetValueTuesday.setVisibility(View.INVISIBLE);
            editTextSetValueWednesday.setVisibility(View.INVISIBLE);
            editTextSetValueThursday.setVisibility(View.INVISIBLE);
            editTextSetValueFriday.setVisibility(View.INVISIBLE);
            editTextSetValueSunday.setVisibility(View.INVISIBLE);
            editTextSetValueSaturday.setVisibility(View.INVISIBLE);

        }else {
            editTextSetValueMonday.setVisibility(View.VISIBLE);
            editTextSetValueTuesday.setVisibility(View.VISIBLE);
            editTextSetValueWednesday.setVisibility(View.VISIBLE);
            editTextSetValueThursday.setVisibility(View.VISIBLE);
            editTextSetValueFriday.setVisibility(View.VISIBLE);
            editTextSetValueSunday.setVisibility(View.VISIBLE);
            editTextSetValueSaturday.setVisibility(View.VISIBLE);
        }
    }

    private void checkBox(){
        checkBoxMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editTextSetValueMonday.setVisibility(View.VISIBLE);
                }else {
                    editTextSetValueMonday.setVisibility(View.INVISIBLE);
                }
            }
        });

        checkBoxTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editTextSetValueTuesday.setVisibility(View.VISIBLE);
                }else {
                    editTextSetValueTuesday.setVisibility(View.INVISIBLE);
                }
            }
        });


        checkBoxWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editTextSetValueWednesday.setVisibility(View.VISIBLE);
                }else {
                    editTextSetValueWednesday.setVisibility(View.INVISIBLE);
                }
            }
        });


        checkBoxThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editTextSetValueThursday.setVisibility(View.VISIBLE);
                }else {
                    editTextSetValueThursday.setVisibility(View.INVISIBLE);
                }
            }
        });


        checkBoxFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editTextSetValueFriday.setVisibility(View.VISIBLE);
                }else {
                    editTextSetValueFriday.setVisibility(View.INVISIBLE);
                }
            }
        });


        checkBoxSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editTextSetValueSunday.setVisibility(View.VISIBLE);
                }else {
                    editTextSetValueSunday.setVisibility(View.INVISIBLE);
                }
            }
        });


        checkBoxSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editTextSetValueSaturday.setVisibility(View.VISIBLE);
                }else {
                    editTextSetValueSaturday.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}