package com.example.caglarkc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
/**
 * AddExerciseActivity: This activity allows users to add new exercises with details to a selected muscle group (region).
 * Users enter the exercise name, select the target region from a spinner, and optionally provide exercise details.
 */

public class AddExerciseActivity extends AppCompatActivity {
    DatabaseReference mReferenceExercise;

    Button buttonAddExercise;
    EditText editTextExerciseName, editTextDetail;
    Spinner spinnerRegion;

    RegionAdapter adapter;
    List<Region> regionList = new ArrayList<>();
    String spinnerText, exerciseName, detail, backActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_exercise);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mReferenceExercise = FirebaseDatabase.getInstance().getReference("Exercises");
        Intent getIntent = getIntent();
        backActivity = getIntent.getStringExtra("back_activity");

        buttonAddExercise = findViewById(R.id.buttonAddExercise);
        editTextExerciseName = findViewById(R.id.editTextExerciseName);
        editTextDetail = findViewById(R.id.editTextDetail);
        spinnerRegion = findViewById(R.id.spinnerRegion);

        regionList = Region.getRegionList();

        adapter = new RegionAdapter(this,regionList);

        spinnerRegion.setAdapter(adapter);
        spinnerRegion.setBackgroundResource(R.drawable.bg_spinner_exercise);
        spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    spinnerText = "Chest";
                }else if (position == 2) {
                    spinnerText = "Back";
                }else if (position == 3) {
                    spinnerText = "Leg";
                }else if (position == 4) {
                    spinnerText = "Abs";
                }else if (position == 5) {
                    spinnerText = "Shoulder";
                }else if (position == 6) {
                    spinnerText = "Triceps";
                }else if (position == 7) {
                    spinnerText = "Biceps";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buttonAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseName = editTextExerciseName.getText().toString();
                detail = editTextDetail.getText().toString();
                if (!TextUtils.isEmpty(exerciseName)){
                    if (!TextUtils.isEmpty(spinnerText)){
                        if (TextUtils.isEmpty(detail)){
                            detail = "Detail is empty...";
                            if (!spinnerText.equals("Choose Region")){
                                mReferenceExercise.child(spinnerText).child(exerciseName).child("exercise_detail").setValue(detail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Exercise exercise = new Exercise(exerciseName,spinnerText);
                                                    Toast.makeText(AddExerciseActivity.this,"Exercise Added With Successfully...",Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(AddExerciseActivity.this, ExerciseListActivity.class);
                                                    intent.putExtra("back_activity",backActivity);
                                                    startActivity(intent);
                                                    finish();
                                                }else {
                                                    Toast.makeText(AddExerciseActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }else {
                                Toast.makeText(AddExerciseActivity.this,"Please choose region...",Toast.LENGTH_SHORT).show();
                            }
                        }else if (!TextUtils.isEmpty(detail)){
                            if (!spinnerText.equals("Choose Region")){
                                mReferenceExercise.child(spinnerText).child(exerciseName).child("exercise_detail").setValue(detail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Exercise exercise = new Exercise(exerciseName,spinnerText);
                                                    Toast.makeText(AddExerciseActivity.this,"Exercise Added With Successfully...",Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(AddExerciseActivity.this, ExerciseListActivity.class);
                                                    intent.putExtra("back_activity",backActivity);
                                                    startActivity(intent);
                                                    finish();
                                                }else {
                                                    Toast.makeText(AddExerciseActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }else {
                        Toast.makeText(AddExerciseActivity.this,"Spinner text empty...",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(AddExerciseActivity.this,"Please enter exercise name...",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddExerciseActivity.this,ExerciseListActivity.class);
        intent.putExtra("back_activity",backActivity);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}