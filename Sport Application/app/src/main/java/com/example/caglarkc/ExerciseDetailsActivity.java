package com.example.caglarkc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class ExerciseDetailsActivity extends AppCompatActivity {
    DatabaseReference mReferenceExercise;

    TextView textViewExerciseDetail, textViewExerciseRegion, textViewExerciseName;

    String exerciseName, backActivity, region, detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent getIntent = getIntent();
        exerciseName = getIntent.getStringExtra("exercise_name");
        region = getIntent.getStringExtra("exercise_region");
        backActivity = getIntent.getStringExtra("back_activity");

        textViewExerciseName = findViewById(R.id.textViewExerciseName);
        textViewExerciseRegion = findViewById(R.id.textViewExerciseRegion);
        textViewExerciseDetail = findViewById(R.id.textViewExerciseDetail);


        textViewExerciseName.setText(exerciseName);
        textViewExerciseRegion.setText(region);




        mReferenceExercise = FirebaseDatabase.getInstance().getReference("Exercises").child(region).child(exerciseName);
        mReferenceExercise.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot detailSnapshot: snapshot.getChildren()){
                    detail = detailSnapshot.getValue(String.class);
                    textViewExerciseDetail.setText(detail);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        if (backActivity.equals("ExerciseListActivity")){
            Intent intent = new Intent(ExerciseDetailsActivity.this, ExerciseListActivity.class);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }

}