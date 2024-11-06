package com.example.caglarkc;

import android.adservices.topics.Topic;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AddBodyMeasurementActivity is responsible for providing a user interface to add and save body measurements
 * for various body parts. The user can select different body regions on an image to add measurements
 * and choose a date for the measurements. The activity allows users to:
 * - Input weight and height.
 * - Select a date and validate it.
 * - Choose between inches or centimeters for measurements.
 * - Save measurements to Firebase Realtime Database.
 * - Delete the last entered measurement.
 */

public class AddBodyMeasurementActivity extends AppCompatActivity {
    SharedPreferences sharedUser;
    DatabaseReference mReferenceUserMeasurement;

    Button buttonDate;
    ImageButton imageButtonBody;
    FrameLayout frameLayout;
    EditText currentEditText, editTextWeight, editTextLength;
    Button buttonDeleteLastEditText, buttonSaveMeasurements;

    String sharedUserUid, todayDate, measurementType;
    HashMap<String , String> hashMapInput = new HashMap<>();
    HashMap<String, String> mData = new HashMap<>();

    Intent intentSuccessfully;
    int hintColor;
    boolean isShoulder = false, isChest = false, isArm = false, isForearm = false, isAbdomen = false,
            isHip = false, isQuad = false, isCalf = false, isNeck = false, isLength = false, isAllEditTextEntered;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_body_measurement);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageButtonBody = findViewById(R.id.imageButtonBody);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextLength = findViewById(R.id.editTextLength);
        buttonDeleteLastEditText = findViewById(R.id.buttonDeleteLastEditText);
        buttonSaveMeasurements = findViewById(R.id.buttonSaveMeasurements);
        frameLayout = findViewById(R.id.frameLayout);
        buttonDate = findViewById(R.id.buttonDate);
        todayDate = getCurrentDateTime();
        buttonDate.setText(todayDate);

        hintColor = getResources().getColor(R.color.hint_color);

        intentSuccessfully = new Intent(AddBodyMeasurementActivity.this,DailyCheckActivity.class);
        Toast.makeText(AddBodyMeasurementActivity.this,"For add measurements , click red points...",Toast.LENGTH_SHORT).show();


        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");

        mReferenceUserMeasurement = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid)
                .child("user_dailyMeasurementData");


        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddBodyMeasurementActivity.this);
                builder.setTitle("Select Date");
                builder.setMessage("Please enter date you want...");
                EditText editText = new EditText(AddBodyMeasurementActivity.this);
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
                                        Toast.makeText(AddBodyMeasurementActivity.this,"Cant enter future date...",Toast.LENGTH_SHORT).show();
                                    }else {
                                        if (!input.equals(todayDate)) {
                                            String[] array = input.split("-");
                                            int year = Integer.parseInt(array[2]);
                                            if (year > 2002) {
                                                todayDate = input;
                                                buttonDate.setText(input);
                                                Toast.makeText(AddBodyMeasurementActivity.this,"Date is changed with successfully...",Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(AddBodyMeasurementActivity.this,"Year must be bigger than 2002",Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            Toast.makeText(AddBodyMeasurementActivity.this,"Please enter different date from today...",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } catch (ParseException e) {
                                    Toast.makeText(AddBodyMeasurementActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(AddBodyMeasurementActivity.this,"Date format must be dd-MM-yyyy",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(AddBodyMeasurementActivity.this,"Please enter a valid date",Toast.LENGTH_SHORT).show();
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

        buttonDeleteLastEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frameLayout.getChildCount() > 1) {
                    View lastView = frameLayout.getChildAt(frameLayout.getChildCount()-1);
                    EditText lastEditText = (EditText) frameLayout.getChildAt(frameLayout.getChildCount()-1);
                    String hint = lastEditText.getHint().toString();
                    switch (hint) {
                        case "shoulder":
                            isShoulder = false;
                            break;

                        case "neck":
                            isNeck = false;
                            break;

                        case "length":
                            isLength = false;
                            break;

                        case "chest":
                            isChest = false;
                            break;

                        case "arm":
                            isArm = false;
                            break;

                        case "forearm":
                            isForearm = false;
                            break;

                        case "abdomen":
                            isAbdomen = false;
                            break;

                        case "hip":
                            isHip = false;
                            break;

                        case "quad":
                            isQuad = false;
                            break;

                        case "calf":
                            isCalf = false;
                            break;

                        default:
                            break;
                    }
                    frameLayout.removeView(lastView);
                }else {
                    Toast.makeText(AddBodyMeasurementActivity.this,"There is no view to be deleted.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //tarih varsa kontrolunu direk sayfa açılırken yapıp tarih varsa orada işlem yap böyle yine hata veriyo
        buttonSaveMeasurements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReferenceUserMeasurement.child(todayDate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddBodyMeasurementActivity.this);
                            builder.setTitle("Warning");
                            builder.setMessage("Date detail is exist, change date or delete detail...");
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mReferenceUserMeasurement.child(todayDate).removeValue();
                                }
                            });
                            builder.show();
                        }else {
                            hashMapInput.clear();
                            String w = editTextWeight.getText().toString();
                            String l = editTextLength.getText().toString();
                            if (frameLayout.getChildCount() > 1) {
                                if (!TextUtils.isEmpty(w) && !TextUtils.isEmpty(l)) {
                                    if (checkIsAllDataEntered()) {
                                        createBuilder(w,l);
                                    }else {
                                        Toast.makeText(AddBodyMeasurementActivity.this,"Bütün edittextleri doldurmalısınız...",Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(AddBodyMeasurementActivity.this,"Kilo ve boy boş bırakılamaz...",Toast.LENGTH_SHORT).show();
                                }
                            }else if (frameLayout.getChildCount() == 1 && !TextUtils.isEmpty(w) && !TextUtils.isEmpty(l)) {
                                mData.put("weight", w + "kg");
                                mData.put("length", l + "cm");

                                mReferenceUserMeasurement.child(todayDate).setValue(mData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(AddBodyMeasurementActivity.this, "Data saved successfully...", Toast.LENGTH_SHORT).show();
                                        startActivity(intentSuccessfully); // Redirect to the success activity.
                                        finish(); // Close the current activity.
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddBodyMeasurementActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else if (frameLayout.getChildCount() == 1 && TextUtils.isEmpty(w)){
                                Toast.makeText(AddBodyMeasurementActivity.this, "Kilo boş bırakılamaz...", Toast.LENGTH_SHORT).show();
                            }else if (frameLayout.getChildCount() == 1 && TextUtils.isEmpty(l)){
                                Toast.makeText(AddBodyMeasurementActivity.this, "Boy boş bırakılamaz...", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(AddBodyMeasurementActivity.this, "Kilo ve boy boş bırakılamaz...", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                      }
        });




        imageButtonBody.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();


                    if (x < 830 && y < 460 && x > 789 && y > 419) {
                        // shoulder
                        if (!isShoulder) {
                            currentEditText = createEditText(810, 440, "shoulder");
                            isShoulder = true;
                        }

                    } else if (x < 656 && y < 385 && x > 615 && y > 344) {
                        // chest
                        if (!isNeck) {
                            currentEditText = createEditText(636, 365, "neck");
                            isNeck = true;
                        }

                    } else if (x < 701 && y < 542 && x > 660 && y > 501) {
                        // chest
                        if (!isChest) {
                            currentEditText = createEditText(681, 522, "chest");
                            isChest = true;
                        }

                    } else if (x < 854 && y < 680 && x > 813 && y > 639) {
                        // arm
                        if (!isArm) {
                            currentEditText = createEditText(834, 660, "arm");
                            isArm = true;
                        }

                    } else if (x < 961 && y < 874 && x > 920 && y > 833) {
                        // forearm
                        if (!isForearm) {
                            currentEditText = createEditText(941, 854, "forearm");
                            isForearm = true;
                        }

                    } else if (x < 634 && y < 838 && x > 593 && y > 797) {
                        // abdomen
                        if (!isAbdomen) {
                            currentEditText = createEditText(614, 818, "abdomen");
                            isAbdomen = true;
                        }

                    } else if (x < 766 && y < 1013 && x > 725 && y > 972) {
                        // hip
                        if (!isHip) {
                            currentEditText = createEditText(746, 993, "hip");
                            isHip = true;
                        }

                    } else if (x < 701 && y < 1209 && x > 660 && y > 1168) {
                        // quad
                        if (!isQuad) {
                            currentEditText = createEditText(681, 1189, "quad");
                            isQuad = true;
                        }

                    } else if (x < 781 && y < 1651 && x > 740 && y > 1610) {
                        // calf
                        if (!isCalf) {
                            currentEditText = createEditText(761, 1631, "calf");
                            isCalf = true;
                        }
                    }

                    return true;
                }
                return false;
            }
        });


    }

    private void createBuilder(String w, String l) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddBodyMeasurementActivity.this);
        builder.setTitle("Choose Type");
        builder.setMessage("Do you want to add your measurements in inches or cm?");

        builder.setNegativeButton("Inch", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                measurementType = "inch"; // Set the measurement type to inch.

                for (Map.Entry<String, String> entry : hashMapInput.entrySet()) {
                    String regionName = entry.getKey();
                    String value = entry.getValue();
                    mData.put(regionName, value + measurementType);
                }

                mData.put("weight", w + "kg");
                mData.put("length", l + "cm");

                mReferenceUserMeasurement.child(todayDate).setValue(mData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AddBodyMeasurementActivity.this, "Data saved successfully...", Toast.LENGTH_SHORT).show();
                        startActivity(intentSuccessfully); // Redirect to the success activity.
                        finish(); // Close the current activity.
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddBodyMeasurementActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setPositiveButton("Cm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                measurementType = "cm"; // Set the measurement type to cm.

                for (Map.Entry<String, String> entry : hashMapInput.entrySet()) {
                    String regionName = entry.getKey();
                    String value = entry.getValue();
                    mData.put(regionName, value + measurementType);
                }

                mData.put("weight", w + "kg");
                mData.put("length", l + "cm");

                mReferenceUserMeasurement.child(todayDate).setValue(mData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AddBodyMeasurementActivity.this, "Data saved successfully...", Toast.LENGTH_SHORT).show();
                        startActivity(intentSuccessfully); // Redirect to the success activity.
                        finish(); // Close the current activity.
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddBodyMeasurementActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.show();
    }


    private boolean checkIsAllDataEntered() {
        isAllEditTextEntered = true;
        for (int i = 0; i < frameLayout.getChildCount(); i++) {
            View child = frameLayout.getChildAt(i);
            if (child instanceof EditText) {
                String str = ((EditText) child).getText().toString();
                if (TextUtils.isEmpty(str)){
                    isAllEditTextEntered = false;
                }else {
                    hashMapInput.put(((EditText) child).getHint().toString() , str);
                }
            }
        }

        return isAllEditTextEntered;
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

    @Override
    public void onBackPressed() {
        startActivity(intentSuccessfully);
        finish();
        super.onBackPressed();
    }


    private EditText createEditText(float x, float y, String hint) {
        x = x + 20;
        y = y - 35;

        EditText editText = new EditText(AddBodyMeasurementActivity.this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        editText.setLayoutParams(params);
        editText.setGravity(Gravity.CENTER);
        editText.setX(x);
        editText.setY(y);
        editText.setHint(hint);
        editText.setPadding(3,0,13,0);
        editText.setHintTextColor(getResources().getColor(R.color.black));
        editText.setTextColor(Color.WHITE);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setBackgroundResource(R.drawable.text_changed_border);

        frameLayout.addView(editText);

        return editText;
    }

}

