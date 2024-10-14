package com.example.caglarkc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.StyleableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDailyProgramActivity extends AppCompatActivity {
    SharedPreferences sharedUser;
    DatabaseReference mReferenceUser, mReferenceProgram, mReferenceData;

    TextView textViewDate, textViewDay;
    Button buttonContinue, buttonAddAnotherDayProgram, buttonAddAnotherDateProgram;
    LinearLayout programContainer, temporaryContainer, contentLayout;
    ProgressBar progressBar;

    String sharedUserUid, todayDate, todayDay, programName, stringWorkDays, strSetValues = "", strExerciseNames = "";
    int tenDp = 10, tenPixel, thirtyPixel, fortyPixel, fiftyPixel, seventyPixel, eightyPixel;
    int hintColor, exerciseValue = 0, setValue = 0;
    List<String> daysOfWeek = new ArrayList<>();
    List<String> workDays = new ArrayList<>();
    HashMap<String, List<String>> haspMapWorkDayExercises = new HashMap<>();
    Intent intentSuccessfully ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_daily_program);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");
        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);
        hintColor = getResources().getColor(R.color.hint_color);
        stringWorkDays = "";

        buttonContinue = findViewById(R.id.buttonContinue);
        programContainer = findViewById(R.id.programContainer);
        textViewDate = findViewById(R.id.textViewDate);
        textViewDay = findViewById(R.id.textViewDay);
        buttonAddAnotherDayProgram = findViewById(R.id.buttonAddAnotherDayProgram);
        buttonAddAnotherDateProgram = findViewById(R.id.buttonAddAnotherDateProgram);
        contentLayout = findViewById(R.id.contentLayout);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        mReferenceData = mReferenceUser.child("user_dailyFitnessProgramData");

        mReferenceProgram = FirebaseDatabase.getInstance().getReference();

        intentSuccessfully = new Intent(AddDailyProgramActivity.this,DailyCheckActivity.class);

        daysOfWeek.add("Monday");
        daysOfWeek.add("Tuesday");
        daysOfWeek.add("Wednesday");
        daysOfWeek.add("Thursday");
        daysOfWeek.add("Friday");
        daysOfWeek.add("Saturday");
        daysOfWeek.add("Sunday");

        todayDate = getCurrentDateTime();
        textViewDate.setText(todayDate);
        todayDay = getCurrentDay();
        textViewDay.setText(todayDay);

        tenPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                tenDp,
                getResources().getDisplayMetrics()
        );
        thirtyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                tenDp*3,
                getResources().getDisplayMetrics()
        );
        fortyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                tenDp* 4 ,
                getResources().getDisplayMetrics()
        );
        fiftyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                tenDp*5,
                getResources().getDisplayMetrics()
        );
        seventyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                tenDp* 7,
                getResources().getDisplayMetrics()
        );
        eightyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                tenDp * 8 ,
                getResources().getDisplayMetrics()
        );

        getCurrentProgram();


        buttonAddAnotherDayProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddDailyProgramActivity.this);
                builder.setTitle("Select Workday");
                builder.setMessage("Please enter workday you want, \n" + "Workdays: " + stringWorkDays);
                EditText editText = new EditText(AddDailyProgramActivity.this);
                editText.setHintTextColor(hintColor);
                editText.setHint("Like Monday");
                builder.setView(editText);
                builder.setPositiveButton("Select WorkDay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = editText.getText().toString();
                        if (!TextUtils.isEmpty(input)) {
                            if (!input.equals(todayDay)) {
                                if (workDays.contains(input)) {
                                    todayDay = input;
                                    textViewDay.setText(input);
                                    programContainer.removeAllViews();
                                    createContentLayout(input);
                                    Toast.makeText(AddDailyProgramActivity.this,"Workday is changed with successfully...",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(AddDailyProgramActivity.this,"Please enter a valid workday",Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(AddDailyProgramActivity.this,"Please enter a different workday",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(AddDailyProgramActivity.this,"Please enter a valid workday",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });

        buttonAddAnotherDateProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddDailyProgramActivity.this);
                builder.setTitle("Select Date");
                builder.setMessage("Please enter date you want...");
                EditText editText = new EditText(AddDailyProgramActivity.this);
                editText.setHintTextColor(hintColor);
                editText.setHint("13-03-2003");
                builder.setView(editText);
                builder.setPositiveButton("Select Date", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = editText.getText().toString();
                        if (!TextUtils.isEmpty(input)) {
                            if (isDateFormatValid(input,"dd-MM-yyyy")) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                try {
                                    Date inputDate = dateFormat.parse(input);
                                    Date today = dateFormat.parse(todayDate);
                                    if (inputDate.after(today)) {
                                        Toast.makeText(AddDailyProgramActivity.this,"Cant enter future date...",Toast.LENGTH_SHORT).show();
                                    }else {
                                        if (!input.equals(todayDate)) {
                                            String[] array = input.split("-");
                                            int year = Integer.parseInt(array[2]);
                                            if (year > 2002) {
                                                todayDate = input;
                                                textViewDate.setText(input);
                                                programContainer.removeAllViews();
                                                createContentLayout(todayDay);
                                                Toast.makeText(AddDailyProgramActivity.this,"Date is changed with successfully...",Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(AddDailyProgramActivity.this,"Year must be bigger than 2002",Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            Toast.makeText(AddDailyProgramActivity.this,"Please enter different date from today...",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }catch (ParseException e) {
                                    Toast.makeText(AddDailyProgramActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(AddDailyProgramActivity.this,"Date format must be dd-MM-yyyy",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(AddDailyProgramActivity.this,"Please enter a valid date",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllEditTextsEntered()) {
                    saveToData();
                }else {

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(intentSuccessfully);
        finish();
        super.onBackPressed();
    }

    private boolean isDateFormatValid(String input, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);

        try {
            Date date = sdf.parse(input);
            return true; // Başarıyla çevrildiyse, tarih formatı doğrudur.
        } catch (ParseException e) {
            return false; // Hata alındıysa, tarih formatı doğru değildir.
        }
    }

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        Date currentDateTime = calendar.getTime();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateTimeFormat.format(currentDateTime);
    }

    private String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            default:
                return "Unknown Day";
        }
    }

    private void getCurrentProgram() {
        DatabaseReference mReference;
        mReference = mReferenceUser.child("user_currentFitnessProgram");
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                programName = snapshot.getValue(String.class);
                if (programName != null) {
                    mReferenceProgram = FirebaseDatabase.getInstance().getReference("Programs").child("Fitness").child(programName);
                    mReferenceProgram.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot daySnapshot : snapshot.getChildren()) {
                                String day = daySnapshot.getKey();
                                if (day != null && daysOfWeek.contains(day)) {
                                    workDays.add(day);
                                    stringWorkDays = stringWorkDays + day + " ";
                                    List<String> workDayExercises = new ArrayList<>();
                                    for (DataSnapshot exerciseSnapshot : daySnapshot.getChildren()) {
                                        String exerciseName = exerciseSnapshot.getKey();
                                        String exerciseValue = exerciseSnapshot.getValue(String.class);
                                        if (exerciseName != null && exerciseValue != null) {
                                            exerciseValue = exerciseValue.replace(" Set","");
                                            workDayExercises.add(exerciseName + "_" + exerciseValue);
                                            haspMapWorkDayExercises.put(day,workDayExercises);
                                        }
                                    }
                                }
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    contentLayout.setVisibility(View.VISIBLE);
                                    if (checkWorkDay()) {
                                        createContentLayout(todayDay);
                                    }else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(AddDailyProgramActivity.this);
                                        builder.setTitle("OffDay");
                                        builder.setMessage("Today off day, if you want add a program to today, please enter workday \n" + "Workdays: " + stringWorkDays);
                                        EditText editText = new EditText(AddDailyProgramActivity.this);
                                        editText.setHintTextColor(hintColor);
                                        editText.setHint("Like Monday");
                                        builder.setView(editText);
                                        builder.setPositiveButton("Select WorkDay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String input = editText.getText().toString();
                                                if (!TextUtils.isEmpty(input)) {
                                                    if (workDays.contains(input)) {
                                                        todayDay = input;
                                                        textViewDay.setText(input);
                                                        createContentLayout(input);
                                                        Toast.makeText(AddDailyProgramActivity.this,"Workday is selected with successfully...",Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(AddDailyProgramActivity.this,"Please enter a valid workday from 'Add another day' button...",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else {
                                                    Toast.makeText(AddDailyProgramActivity.this,"Please enter a valid workday from 'Add another day' button...",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Toast.makeText(AddDailyProgramActivity.this,"Returning menu...",Toast.LENGTH_SHORT).show();
                                                startActivity(intentSuccessfully);
                                            }
                                        });
                                        builder.show();
                                    }
                                }
                            },500);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private Boolean checkWorkDay() {
        return workDays.contains(todayDay);
    }

    private void createContentLayout(String day) {
        sortWorkDays();

        strExerciseNames = "";
        strSetValues = "";
        setValue = 0;
        exerciseValue = 0;
        List<String> dayExercises = haspMapWorkDayExercises.get(day);
        if (dayExercises != null) {
            for (String exercise : dayExercises) {
                String[] array = exercise.split("_");
                String exerciseName = array[0];
                String exerciseSet = array[1];
                int currentSetVal = Integer.parseInt(exerciseSet);
                strExerciseNames = strExerciseNames + exerciseName + "_";
                strSetValues = strSetValues + exerciseSet + "_";
                setValue += currentSetVal;
                exerciseValue += 1;

                createEntryLayout(exerciseName , currentSetVal);

            }
            strExerciseNames = strExerciseNames.substring(0,strExerciseNames.length()-1);
            strSetValues = strSetValues.substring(0,strSetValues.length()-1);
        }



    }

    private void sortWorkDays() {
        Collections.sort(workDays, new Comparator<String>() {
            @Override
            public int compare(String day1, String day2) {
                return Integer.compare(daysOfWeek.indexOf(day1),daysOfWeek.indexOf(day2));
            }
        });
    }

    private void createEntryLayout(String exerciseName, int setValue){
        temporaryContainer = new LinearLayout(AddDailyProgramActivity.this);
        temporaryContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        temporaryContainer.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParamsFirst = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParamsFirst.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams paramsContainer = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsContainer.gravity = Gravity.CENTER;
        paramsContainer.topMargin = 30;
        paramsContainer.bottomMargin = 30;

        LinearLayout exerciseNameContainer = new LinearLayout(AddDailyProgramActivity.this);
        exerciseNameContainer.setLayoutParams(paramsContainer);
        exerciseNameContainer.setOrientation(LinearLayout.HORIZONTAL);
        exerciseNameContainer.setBackgroundResource(R.drawable.border);
        exerciseNameContainer.setPadding(16,10,16,10);

        TextView textViewExercise = new TextView(AddDailyProgramActivity.this);
        textViewExercise.setLayoutParams(layoutParamsFirst);
        textViewExercise.setTextSize(20);
        textViewExercise.setTextColor(Color.WHITE);
        textViewExercise.setTypeface(null,Typeface.ITALIC);
        textViewExercise.setText("Exercise:  ");

        TextView textViewExerciseName = new TextView(AddDailyProgramActivity.this);
        textViewExerciseName.setLayoutParams(layoutParamsFirst);
        textViewExerciseName.setTextSize(20);
        textViewExerciseName.setTextColor(Color.WHITE);
        textViewExerciseName.setTypeface(null,Typeface.BOLD_ITALIC);
        textViewExerciseName.setText(exerciseName);

        exerciseNameContainer.addView(textViewExercise);
        exerciseNameContainer.addView(textViewExerciseName);

        temporaryContainer.addView(exerciseNameContainer);
        for (int i = 0 ; i < setValue ; i ++){

            LinearLayout detailContainer = new LinearLayout(AddDailyProgramActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    fiftyPixel
            );
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            detailContainer.setLayoutParams(layoutParams);
            detailContainer.setOrientation(LinearLayout.HORIZONTAL);
            detailContainer.setPadding(10,10,10,10);

            TextView textViewSet = new TextView(AddDailyProgramActivity.this);
            textViewSet.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    thirtyPixel
            ));
            textViewSet.setTextSize(18);
            textViewSet.setGravity(Gravity.CENTER);
            textViewSet.setTextColor(Color.WHITE);
            textViewSet.setTypeface(null,Typeface.ITALIC);
            textViewSet.setText(i+1 + ".Set ");

            Space space3x = new Space(AddDailyProgramActivity.this);
            space3x.setLayoutParams(new ViewGroup.LayoutParams(
                    thirtyPixel,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));

            LinearLayout kilogramContainer = new LinearLayout(AddDailyProgramActivity.this);
            kilogramContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            kilogramContainer.setOrientation(LinearLayout.HORIZONTAL);
            kilogramContainer.setBackgroundResource(R.drawable.weight_repeat_border);
            kilogramContainer.setPadding(12,5,24,5);

            EditText editTextKg = new EditText(AddDailyProgramActivity.this);
            editTextKg.setLayoutParams(new ViewGroup.LayoutParams(
                    eightyPixel,
                    thirtyPixel
            ));
            editTextKg.setGravity(Gravity.CENTER);
            editTextKg.setPadding(4,6,4,6);
            editTextKg.setTextColor(Color.WHITE);
            editTextKg.setHintTextColor(hintColor);
            editTextKg.setTextSize(18);
            editTextKg.setHint("Weight     ");
            editTextKg.setTypeface(null, Typeface.ITALIC);
            editTextKg.setBackground(null);
            editTextKg.setInputType(InputType.TYPE_CLASS_NUMBER);

            TextView textViewKg = new TextView(AddDailyProgramActivity.this);
            textViewKg.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    thirtyPixel
            ));
            textViewKg.setGravity(Gravity.CENTER);
            textViewKg.setTextColor(Color.WHITE);
            textViewKg.setTextSize(20);
            textViewKg.setText("Kg");

            kilogramContainer.addView(editTextKg);
            kilogramContainer.addView(textViewKg);

            Space space4x = new Space(AddDailyProgramActivity.this);
            space4x.setLayoutParams(new ViewGroup.LayoutParams(
                    fortyPixel,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            EditText editTextRepeat = new EditText(AddDailyProgramActivity.this);
            editTextRepeat.setLayoutParams(new ViewGroup.LayoutParams(
                    seventyPixel,
                    thirtyPixel
            ));

            LinearLayout repeatContainer = new LinearLayout(AddDailyProgramActivity.this);
            repeatContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            repeatContainer.setOrientation(LinearLayout.HORIZONTAL);
            repeatContainer.setBackgroundResource(R.drawable.weight_repeat_border);
            repeatContainer.setPadding(12,5,12,5);

            editTextRepeat.setGravity(Gravity.CENTER);
            editTextRepeat.setPadding(12,8,10,8);
            editTextRepeat.setTextColor(Color.WHITE);
            editTextRepeat.setHintTextColor(hintColor);
            editTextRepeat.setTextSize(18);
            editTextRepeat.setHint("Repeat");
            editTextRepeat.setBackground(null);
            editTextRepeat.setTypeface(null, Typeface.ITALIC);
            editTextRepeat.setInputType(InputType.TYPE_CLASS_NUMBER);

            repeatContainer.addView(editTextRepeat);

            Space space4xTwo = new Space(AddDailyProgramActivity.this);
            space4xTwo.setLayoutParams(new ViewGroup.LayoutParams(
                    fortyPixel,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));

            detailContainer.addView(textViewSet);
            detailContainer.addView(space3x);
            detailContainer.addView(kilogramContainer);
            detailContainer.addView(space4x);
            detailContainer.addView(repeatContainer);
            detailContainer.addView(space4xTwo);

            temporaryContainer.addView(detailContainer);

        }

        programContainer.addView(temporaryContainer);

    }

    private boolean isAllEditTextsEntered() {
        List<EditText> editTextList = getAllEditTextsFromViewGroup(programContainer);
        for (EditText editText : editTextList) {
            String txt = editText.getText().toString();
            if (TextUtils.isEmpty(txt)) {
                Toast.makeText(AddDailyProgramActivity.this,"Please enter all data...",Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if (txt.startsWith("0")) {
                    Toast.makeText(AddDailyProgramActivity.this,"Data cant start with 0...",Toast.LENGTH_SHORT).show();
                    return false;
                }
                int val = Integer.parseInt(txt);
                if (val == 0 || val > 999) {
                    Toast.makeText(AddDailyProgramActivity.this,"Values cant be 0 or bigger than 999...",Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    private void saveToData() {
        // edittextler tek tek listede dönüyor sırayla ve str ile valuelker de dogru domuyor tek yapman gereken sırayla alıp for dongusnu kurup bilgileri kaydetmek kg
        List<EditText> editTextList = getAllEditTextsFromViewGroup(programContainer);
        int editTextIndex = 0;
        String[] array = strSetValues.split("_");
        String[] arrayNames = strExerciseNames.split("_");
        int exerciseCounter = 0;
        int setV;
        // array'deki her sayı için işlem yap
        for (String str : array) {
            String exerciseName = arrayNames[exerciseCounter];
            int setCount = Integer.parseInt(str); // Array'den alınan her değeri int'e çeviriyoruz
            // O değere göre gereken EditText'leri işleme al
            for (int i = 0; i < setCount * 2; i += 2) { // i iki iki artacak şekilde ayarlandı
                setV = i / 2;
                setV++;
                if (editTextIndex + 1 < editTextList.size()) {
                    // Mevcut EditText'lerin içeriğini al
                    EditText currentEditText1 = editTextList.get(editTextIndex);
                    EditText currentEditText2 = editTextList.get(editTextIndex + 1);

                    String editTextValue1 = currentEditText1.getText().toString();
                    String editTextValue2 = currentEditText2.getText().toString();

                    // Burada alınan verileri kaydetmek için gerekli işlemi yap
                    // saveDataToDatabase(editTextValue1, editTextValue2);  // Örnek bir kaydetme işlemi
                    mReferenceData.child(todayDate).child(exerciseName).child("Set" + setV).setValue(editTextValue1 + "kg_" + editTextValue2 + "repeat").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddDailyProgramActivity.this,"Data is added with successfully...",Toast.LENGTH_SHORT).show();
                                startActivity(intentSuccessfully);
                                finish();
                            }else {
                                Toast.makeText(AddDailyProgramActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    // Sonraki iki EditText'e geçmek için sayaç artırıyoruz
                    editTextIndex += 2;
                }
            }
            exerciseCounter++;
        }

    }


    private List<EditText> getAllEditTextsFromViewGroup(ViewGroup root) {
        List<EditText> editTextList = new ArrayList<>();

        // Root ViewGroup içindeki tüm alt bileşenleri dolaş
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);

            // Eğer bileşen bir EditText ise listeye ekle
            if (child instanceof EditText) {
                editTextList.add((EditText) child);
            }
            // Eğer bileşen bir ViewGroup ise, onun altındaki EditText'leri de kontrol et (rekürsif çağrı)
            else if (child instanceof ViewGroup) {
                editTextList.addAll(getAllEditTextsFromViewGroup((ViewGroup) child));
            }
        }

        return editTextList;
    }

}