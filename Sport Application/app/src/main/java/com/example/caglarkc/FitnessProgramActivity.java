package com.example.caglarkc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
/**
 * FitnessProgramActivity: This activity allows users to view, add, or check available fitness programs.
 * - Users can view the details of their current fitness program if they have one selected.
 * - If the program limit (5 programs) is reached, users are notified and cannot add new programs.
 * - Users can navigate to view the list of available programs for selection.
 * The activity interacts with Firebase to retrieve user-specific data, such as the current program and
 * any previously added programs, and updates the UI based on the user's program status.
 */

public class FitnessProgramActivity extends AppCompatActivity {
    DatabaseReference mReferenceUser;
    SharedPreferences sharedUser;

    Button buttonProgramDetails, buttonAddNewProgram, buttonCheckPrograms;

    String sharedUserUid, programName;
    int programCounter = 0;
    boolean isProgramTaken = false, isProgramLimitFull = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fitness_program);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonProgramDetails = findViewById(R.id.buttonProgramDetails);
        buttonAddNewProgram = findViewById(R.id.buttonAddNewProgram);
        buttonCheckPrograms = findViewById(R.id.buttonCheckPrograms);

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");

        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);

        mReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot detailSnapshot : snapshot.getChildren()){
                    String detail = detailSnapshot.getKey();
                    if (detail != null && detail.equals("user_currentFitnessProgram")){
                        isProgramTaken = true;
                        programName = detailSnapshot.getValue(String.class);
                    }else if (detail != null && detail.equals("user_fitnessPrograms")){
                        for (DataSnapshot programNamesSnapshot: detailSnapshot.getChildren()){
                            if (programNamesSnapshot.exists()){
                                programCounter++;
                            }
                        }
                        if (programCounter == 5){
                            isProgramLimitFull = true;
                        }
                    }
                }
                if (!isProgramTaken){
                    buttonProgramDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(FitnessProgramActivity.this,"Please first choose a program from 'Check Programs' ...",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    buttonProgramDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(FitnessProgramActivity.this,ProgramDetailsActivity.class);
                            intent.putExtra("program_name",programName);
                            intent.putExtra("back_activity","FitnessProgramActivity");
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                if (isProgramLimitFull){
                    buttonAddNewProgram.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(FitnessProgramActivity.this,"You reach the adding program limit...",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    buttonAddNewProgram.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(FitnessProgramActivity.this,AddNewProgramActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FitnessProgramActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });




        buttonCheckPrograms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FitnessProgramActivity.this,CheckProgramsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FitnessProgramActivity.this,UserMenuActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}