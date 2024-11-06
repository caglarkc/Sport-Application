package com.example.caglarkc;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
/**
 * AddExerciseToNewProgramActivity: This activity allows users to create a new workout program by adding exercises to each day.
 * Users select exercises from a predefined list for each day, specify the number of sets, and save the program.
 */

public class AddExerciseToNewProgramActivity extends AppCompatActivity {
    SharedPreferences sharedUser, sharedProgram;
    DatabaseReference mReferenceProgram, mReferenceUser, mReferenceExercises;

    LinearLayout container, mondayContainer, tuesdayContainer, wednesdayContainer, thursdayContainer, fridayContainer, saturdayContainer, sundayContainer;
    Button buttonAddExercises;

    Spinner spinner;
    ExerciseAdapter adapter;

    String sharedUserUid, programName, uuid, exerciseName;
    int mondayExerciseValue, tuesdayExerciseValue, wednesdayExerciseValue, thursdayExerciseValue, fridayExerciseValue, saturdayExerciseValue, sundayExerciseValue,
            oneHundredFiftyDp = 150, oneHundredFiftyPixel, twoHundredFiftyDp = 250, twoHundredFiftyPixel, sixtyDp = 60, sixtyPixel, thirtyDp = 30, thirtyPixel,
            twentyFiveDp = 25, twentyFivePixel, fiveDp = 5, fivePixel, twentyDp = 20, twentyPixel;
    boolean isEnteredAllData = true, isAnyExerciseDuplicated = false, isMonday = false, isTuesday = false, isWednesday = false, isThursday = false, isFriday = false, isSaturday = false, isSunday = false;
    List<String> mondayExercises = new ArrayList<>();
    List<String> tuesdayExercises = new ArrayList<>();
    List<String> wednesdayExercises = new ArrayList<>();
    List<String> thursdayExercises = new ArrayList<>();
    List<String> fridayExercises = new ArrayList<>();
    List<String> saturdayExercises = new ArrayList<>();
    List<String> sundayExercises = new ArrayList<>();
    List<Exercise> exerciseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_exercise_to_new_program);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        buttonAddExercises = findViewById(R.id.buttonAddExercises);
        container = findViewById(R.id.container);

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");
        sharedProgram = getSharedPreferences("program_data",MODE_PRIVATE);

        uuid = UUID.randomUUID().toString();

        programName = sharedProgram.getString("program_name","");
        mondayExerciseValue = sharedProgram.getInt("monday",0);
        tuesdayExerciseValue = sharedProgram.getInt("tuesday",0);
        wednesdayExerciseValue = sharedProgram.getInt("wednesday",0);
        thursdayExerciseValue = sharedProgram.getInt("thursday",0);
        fridayExerciseValue = sharedProgram.getInt("friday",0);
        saturdayExerciseValue = sharedProgram.getInt("saturday",0);
        sundayExerciseValue = sharedProgram.getInt("sunday",0);
        if (mondayExerciseValue != 0 ){
            isMonday = true;
        }
        if (tuesdayExerciseValue != 0 ){
            isTuesday = true;
        }
        if (wednesdayExerciseValue != 0 ){
            isWednesday = true;
        }
        if (thursdayExerciseValue != 0 ){
            isThursday = true;
        }
        if (fridayExerciseValue != 0 ){
            isFriday = true;
        }
        if (saturdayExerciseValue != 0 ){
            isSaturday = true;
        }
        if (sundayExerciseValue != 0 ){
            isSunday = true;
        }

        mReferenceProgram = FirebaseDatabase.getInstance().getReference("Programs").child("Fitness");
        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);

        oneHundredFiftyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                oneHundredFiftyDp,
                getResources().getDisplayMetrics()
        );
        twentyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                twentyDp,
                getResources().getDisplayMetrics()
        );
        fivePixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                fiveDp,
                getResources().getDisplayMetrics()
        );
        twoHundredFiftyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                twoHundredFiftyDp,
                getResources().getDisplayMetrics()
        );
        sixtyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                sixtyDp,
                getResources().getDisplayMetrics()
        );
        thirtyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                thirtyDp,
                getResources().getDisplayMetrics()
        );
        twentyFivePixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                twentyFiveDp,
                getResources().getDisplayMetrics()
        );

        exerciseList = Exercise.getExerciseList();







        takeDataFromShared();

        Space space = new Space(AddExerciseToNewProgramActivity.this);
        space.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                sixtyPixel
        ));
        container.addView(space);

        buttonAddExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Spinner> spinnerList = new ArrayList<>();
                spinnerList = getAllSpinnersInContainer(container);

                List<EditText> editTextList = new ArrayList<>();
                editTextList = getAllEditTextsInContainer(container);

                for (int x = 0; x < spinnerList.size() ; x++){
                    Spinner spinner = spinnerList.get(x);
                    exerciseName = spinner.getSelectedItem().toString();
                    if (exerciseName != null){
                        if (exerciseName.equals("Choose Exercise")){
                            isEnteredAllData = false;
                            Toast.makeText(AddExerciseToNewProgramActivity.this,"Please enter all data...",Toast.LENGTH_SHORT).show();
                            break;
                        }else {
                            isEnteredAllData = true;
                        }
                    }else {
                        isEnteredAllData = false;
                        Toast.makeText(AddExerciseToNewProgramActivity.this,"Please enter all data...",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                if (isEnteredAllData){
                    for (int b = 0; b < editTextList.size(); b++){
                        EditText editText = editTextList.get(b);
                        String txt = editText.getText().toString();
                        if (TextUtils.isEmpty(txt)){
                            isEnteredAllData = false;
                            Toast.makeText(AddExerciseToNewProgramActivity.this,"Please enter all data...",Toast.LENGTH_SHORT).show();
                            break;
                        }else {
                            isEnteredAllData = true;
                        }
                    }
                }

                if (isEnteredAllData){
                    int counter = 0;
                    for (int i = 0 ; i < editTextList.size() ; i+=1){
                        String value;
                        Spinner spinner = spinnerList.get(i);
                        int selectedPosition = spinner.getSelectedItemPosition();
                        exerciseName = exerciseList.get(selectedPosition).getName();
                        value = editTextList.get(i).getText().toString();
                        if (!TextUtils.isEmpty(exerciseName) && !TextUtils.isEmpty(value)){
                            if  (mondayExerciseValue !=0 && counter < mondayExerciseValue){
                                mondayExercises.add(exerciseName+"_"+value);
                            }else if (mondayExerciseValue !=0 && counter == mondayExerciseValue){
                                counter =0;
                                mondayExerciseValue = 0;
                            }
                            if (mondayExerciseValue == 0 && tuesdayExerciseValue != 0 && counter <tuesdayExerciseValue){
                                tuesdayExercises.add(exerciseName+"_"+value);
                            }else if (mondayExerciseValue == 0 && tuesdayExerciseValue != 0 && counter == tuesdayExerciseValue){
                                counter = 0;
                                tuesdayExerciseValue = 0;
                            }
                            if (mondayExerciseValue == 0 && tuesdayExerciseValue == 0 && wednesdayExerciseValue != 0 && counter < wednesdayExerciseValue){
                                wednesdayExercises.add(exerciseName+"_"+value);
                            }else if (mondayExerciseValue == 0 && tuesdayExerciseValue == 0 && wednesdayExerciseValue != 0 && counter == wednesdayExerciseValue){
                                counter = 0;
                                wednesdayExerciseValue = 0;
                            }
                            if (mondayExerciseValue == 0 && tuesdayExerciseValue == 0 && wednesdayExerciseValue == 0
                                    && thursdayExerciseValue != 0 && counter < thursdayExerciseValue){
                                thursdayExercises.add(exerciseName+"_"+value);
                            }else if (mondayExerciseValue == 0 && tuesdayExerciseValue == 0 && wednesdayExerciseValue == 0
                                    && thursdayExerciseValue != 0 && counter == thursdayExerciseValue){
                                counter = 0;
                                thursdayExerciseValue = 0;
                            }
                            if (mondayExerciseValue == 0 && tuesdayExerciseValue == 0 && wednesdayExerciseValue == 0
                                    && thursdayExerciseValue == 0
                                    && fridayExerciseValue != 0 && counter < fridayExerciseValue){
                                fridayExercises.add(exerciseName+"_"+value);
                            }else if (mondayExerciseValue == 0 && tuesdayExerciseValue == 0 && wednesdayExerciseValue == 0
                                    && thursdayExerciseValue == 0
                                    && fridayExerciseValue != 0 && counter == fridayExerciseValue){
                                counter = 0;
                                fridayExerciseValue = 0;
                            }
                            if (mondayExerciseValue == 0 && tuesdayExerciseValue == 0 && wednesdayExerciseValue == 0
                                    && thursdayExerciseValue == 0 && fridayExerciseValue == 0
                                    && saturdayExerciseValue != 0 && counter < saturdayExerciseValue){
                                saturdayExercises.add(exerciseName+"_"+value);
                            }else if (mondayExerciseValue == 0 && tuesdayExerciseValue == 0 && wednesdayExerciseValue == 0
                                    && thursdayExerciseValue == 0 && fridayExerciseValue == 0
                                    && saturdayExerciseValue != 0 && counter == saturdayExerciseValue){
                                counter = 0;
                                saturdayExerciseValue = 0;
                            }
                            if (mondayExerciseValue == 0 && tuesdayExerciseValue == 0 && wednesdayExerciseValue == 0
                                    && thursdayExerciseValue == 0 && fridayExerciseValue == 0 && saturdayExerciseValue == 0
                                    && sundayExerciseValue != 0 && counter < sundayExerciseValue){
                                sundayExercises.add(exerciseName+"_"+value);
                            }else if (mondayExerciseValue == 0 && tuesdayExerciseValue == 0 && wednesdayExerciseValue == 0
                                    && thursdayExerciseValue == 0 && fridayExerciseValue == 0 && saturdayExerciseValue == 0
                                    && sundayExerciseValue != 0 && counter == sundayExerciseValue){
                                counter = 0;
                                sundayExerciseValue = 0;
                            }
                            counter++;
                        }
                    }
                    if (!isAnyDuplicateExist(mondayExercises) && !isAnyDuplicateExist( tuesdayExercises) && !isAnyDuplicateExist( wednesdayExercises) && !isAnyDuplicateExist( thursdayExercises)
                            && !isAnyDuplicateExist( fridayExercises) && !isAnyDuplicateExist( saturdayExercises) && !isAnyDuplicateExist( sundayExercises)){
                        isAnyExerciseDuplicated = false;
                        Toast.makeText(AddExerciseToNewProgramActivity.this,"Added program with successfully...",Toast.LENGTH_SHORT).show();
                        if (isMonday){
                            saveToFirebase("Monday",mondayExercises);
                        }
                        if (isTuesday){
                            saveToFirebase("Tuesday",tuesdayExercises);
                        }
                        if (isWednesday){
                            saveToFirebase("Wednesday",wednesdayExercises);
                        }
                        if (isThursday){
                            saveToFirebase("Thursday",thursdayExercises);
                        }
                        if (isFriday){
                            saveToFirebase("Friday",fridayExercises);
                        }
                        if (isSaturday){
                            saveToFirebase("Saturday",saturdayExercises);
                        }
                        if (isSunday){
                            saveToFirebase("Sunday",sundayExercises);
                        }

                        Intent intent = new Intent(AddExerciseToNewProgramActivity.this,AddNewProgramActivity.class);
                        startActivity(intent);
                        finish();

                    }else {
                        isAnyExerciseDuplicated = true;

                        Toast.makeText(AddExerciseToNewProgramActivity.this,"Cant add same exercises to same day...",Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddExerciseToNewProgramActivity.this,AddNewProgramActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private boolean isAnyDuplicateExist(List<String> list){
        HashSet<String> set = new HashSet<>();

        for (String item : list) {
            String[] array = item.split("_");
            String test = array[0];
            if (!set.add(test)) {
                set.clear();
                list.clear();
                return true;
            }
        }

        return false;
    }

    private void takeDataFromShared(){
        if (mondayExerciseValue != 0){
            mondayContainer = createEntryLayout("Monday",mondayExerciseValue);
            container.addView(mondayContainer);
        }
        if (tuesdayExerciseValue != 0){
            tuesdayContainer = createEntryLayout("Tuesday",tuesdayExerciseValue);
            container.addView(tuesdayContainer);
        }
        if (wednesdayExerciseValue != 0){
            wednesdayContainer = createEntryLayout("Wednesday",wednesdayExerciseValue);
            container.addView(wednesdayContainer);
        }
        if (thursdayExerciseValue != 0){
            thursdayContainer = createEntryLayout("Thursday",thursdayExerciseValue);
            container.addView(thursdayContainer);
        }
        if (fridayExerciseValue != 0){
            fridayContainer = createEntryLayout("Friday",fridayExerciseValue);
            container.addView(fridayContainer);
        }
        if (saturdayExerciseValue != 0){
            saturdayContainer = createEntryLayout("Saturday",saturdayExerciseValue);
            container.addView(saturdayContainer);
        }
        if (sundayExerciseValue != 0){
            sundayContainer = createEntryLayout("Sunday",sundayExerciseValue);
            container.addView(sundayContainer);
        }

    }

    private LinearLayout createEntryLayout(String dayName, int exerciseValue){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.topMargin = 15;

        LinearLayout linearLayoutDayContainer = new LinearLayout(AddExerciseToNewProgramActivity.this);
        linearLayoutDayContainer.setLayoutParams(layoutParams);
        linearLayoutDayContainer.setOrientation(LinearLayout.VERTICAL);

        TextView textViewDay = new TextView(AddExerciseToNewProgramActivity.this);
        textViewDay.setLayoutParams(new ViewGroup.LayoutParams(oneHundredFiftyPixel,thirtyPixel));
        textViewDay.setTextColor(Color.WHITE);
        textViewDay.setTypeface(null, Typeface.BOLD);
        textViewDay.setGravity(Gravity.CENTER);
        textViewDay.setTextSize(20);
        textViewDay.setText(dayName);

        Space spaceHeight = new Space(AddExerciseToNewProgramActivity.this);
        spaceHeight.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                fivePixel));

        linearLayoutDayContainer.addView(textViewDay);
        linearLayoutDayContainer.addView(spaceHeight);

        for (int i = 0; i < exerciseValue ; i++){
            RelativeLayout relativeLayoutExercise = new RelativeLayout(AddExerciseToNewProgramActivity.this);

            LinearLayout.LayoutParams relativeLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT
                    ,twentyFivePixel);
            relativeLayoutParams.bottomMargin = 5;
            relativeLayoutExercise.setLayoutParams(relativeLayoutParams);

            RelativeLayout.LayoutParams spinnerParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    twentyFivePixel);
            spinnerParams.addRule(RelativeLayout.ALIGN_PARENT_START);

            adapter = new ExerciseAdapter(AddExerciseToNewProgramActivity.this,exerciseList);

            spinner = new Spinner(this);
            spinner.setLayoutParams(spinnerParams);
            spinner.setAdapter(adapter);
            spinner.setBackgroundResource(R.drawable.bg_spinner_exercise);


            RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(sixtyPixel,twentyFivePixel);
            editTextParams.addRule(RelativeLayout.ALIGN_PARENT_END);

            EditText editTextSetValue = new EditText(AddExerciseToNewProgramActivity.this);
            editTextSetValue.setLayoutParams(editTextParams);
            editTextSetValue.setHint("Set val");
            editTextSetValue.setGravity(Gravity.CENTER);
            editTextSetValue.setPadding(0,0,0,0);
            editTextSetValue.setHintTextColor(getResources().getColor(R.color.hint_color));
            editTextSetValue.setTextColor(Color.WHITE);
            editTextSetValue.setInputType(InputType.TYPE_CLASS_NUMBER);
            editTextSetValue.setBackgroundResource(R.drawable.edit_text_exercise_border);

            relativeLayoutExercise.addView(spinner);
            relativeLayoutExercise.addView(editTextSetValue);

            linearLayoutDayContainer.addView(relativeLayoutExercise);
        }





        return linearLayoutDayContainer;
    }

    private List<EditText> getAllEditTextsInContainer(LinearLayout container) {
        List<EditText> editTextList = new ArrayList<>();

        for (int i = 0; i < container.getChildCount(); i++) {
            View childView = container.getChildAt(i);

            if (childView instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) childView;

                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View innerChildView = linearLayout.getChildAt(j);

                    if (innerChildView instanceof RelativeLayout) {
                        RelativeLayout layout = (RelativeLayout) innerChildView;

                        for (int k = 0; k < layout.getChildCount(); k++){
                            View inInnerChildView = layout.getChildAt(k);

                            if (inInnerChildView instanceof EditText){
                                editTextList.add((EditText) inInnerChildView);
                            }
                        }
                    }
                }
            }
        }

        return editTextList;
    }

    private List<Spinner> getAllSpinnersInContainer(LinearLayout container){
        List<Spinner> spinnerList = new ArrayList<>();

        for (int i = 0; i < container.getChildCount(); i++) {
            View childView = container.getChildAt(i);

            if (childView instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) childView;

                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View innerChildView = linearLayout.getChildAt(j);

                    if (innerChildView instanceof RelativeLayout) {
                        RelativeLayout layout = (RelativeLayout) innerChildView;

                        for (int k = 0; k < layout.getChildCount(); k++){
                            View inInnerChildView = layout.getChildAt(k);

                            if (inInnerChildView instanceof Spinner){
                                spinnerList.add((Spinner) inInnerChildView);
                            }
                        }
                    }
                }
            }
        }

        return spinnerList;
    }

    private void saveToFirebase(String day, List<String> list){
        for (String name : list){
            String[] array = name.split("_");
            String exerciseName = array[0];
            String exerciseValue = array[1];


            mReferenceUser.child("user_fitnessPrograms").child(programName).child(day).child(exerciseName).setValue(exerciseValue + " Set");
            mReferenceProgram.child(programName).child(day).child(exerciseName).setValue(exerciseValue + " Set");

            mReferenceProgram.child(programName).child("Creator").setValue(sharedUserUid);
            mReferenceProgram.child(programName).child("Uid").setValue(uuid);
        }

    }
}