package com.example.caglarkc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
/**
 * ExerciseDetailsActivity: This activity displays detailed information about a selected exercise.
 * It retrieves and shows the exercise name, region, and additional details from the database.
 * The data is dynamically fetched based on the exercise and region selected, providing users with specific information on each exercise.
 * Users can navigate back to the ExerciseListActivity if they came from there.
 */

public class ExerciseDetailsActivity extends AppCompatActivity {
    DatabaseReference mReferenceExercise;

    TextView textViewExerciseDetail, textViewExerciseRegion, textViewExerciseName;
    ProgressBar progressBar;
    LinearLayout activityContainer;

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
        activityContainer = findViewById(R.id.activityContainer);
        progressBar = findViewById(R.id.progressBar);


        textViewExerciseName.setText(exerciseName);
        textViewExerciseRegion.setText(region);




        activityContainer.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mReferenceExercise = FirebaseDatabase.getInstance().getReference("Exercises").child(region).child(exerciseName);
        mReferenceExercise.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot detailSnapshot: snapshot.getChildren()){
                    detail = detailSnapshot.getValue(String.class);
                    textViewExerciseDetail.setText(detail);

                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activityContainer.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                },300);
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