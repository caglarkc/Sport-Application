package com.example.caglarkc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
 * CheckProgramsActivity: This activity allows users to browse and select available fitness programs.
 * Programs are displayed with their respective names and workday values in a list format.
 * Users can choose to view details of a selected program or make it their current program.
 * The activity highlights the user's current program with a distinct border for easy identification.
 * Upon selecting a program, users can choose to check its details or set it as their current program.
 */

public class CheckProgramsActivity extends AppCompatActivity {
    DatabaseReference mReferencePrograms, mReferenceUser;
    SharedPreferences sharedUser;

    LinearLayout container;

    String sharedUserUid, programName, takenProgramName;
    int twoHundredThirtyDp = 230, twoHundredThirtyPixel, tenDp = 10, tenPixel, hundredDp = 100, hundredPixel,
            fortyFiveDp = 45, fortyFivePixel, programWorkdayValue;
    Boolean isTakenProgram = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_programs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        container = findViewById(R.id.container);
        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");

        mReferencePrograms = FirebaseDatabase.getInstance().getReference("Programs").child("Fitness");
        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);



        mReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot detailSnapshot : snapshot.getChildren()) {
                    String detail = detailSnapshot.getKey();
                    if (detail != null && detail.equals("user_currentFitnessProgram")) {
                        takenProgramName = detailSnapshot.getValue(String.class);
                        isTakenProgram = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tenPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                tenDp,
                getResources().getDisplayMetrics()
        );
        twoHundredThirtyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                twoHundredThirtyDp,
                getResources().getDisplayMetrics()
        );

        hundredPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                hundredDp,
                getResources().getDisplayMetrics()
        );
        fortyFivePixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                fortyFiveDp,
                getResources().getDisplayMetrics()
        );

        takePrograms();
    }

    private void takePrograms() {
        mReferencePrograms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot programSnapshot : snapshot.getChildren()) {
                    programWorkdayValue = -2;
                    programName = programSnapshot.getKey();
                    for (DataSnapshot detailSnapshot : programSnapshot.getChildren()) {
                        programWorkdayValue += 1;
                    }
                    if (programName != null &&  programName.equals(takenProgramName)) {
                        LinearLayout linearLayout = new LinearLayout(CheckProgramsActivity.this);
                        linearLayout = createEntryLayout(programName,programWorkdayValue,true);
                        container.addView(linearLayout);
                    }else {
                        LinearLayout linearLayout = new LinearLayout(CheckProgramsActivity.this);
                        linearLayout = createEntryLayout(programName,programWorkdayValue,false);
                        container.addView(linearLayout);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private LinearLayout createEntryLayout(String name, int workDayValue, boolean isTaken){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.topMargin = 10;
        LinearLayout linearLayout = new LinearLayout(CheckProgramsActivity.this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);

        Button button = new Button(CheckProgramsActivity.this);
        button.setLayoutParams(new ViewGroup.LayoutParams(twoHundredThirtyPixel,fortyFivePixel));
        button.setTextColor(Color.WHITE);
        button.setPadding(7,0,7,0);
        button.getBackground().setTintList(null);
        button.setText(name);
        button.setTypeface(null, Typeface.ITALIC);
        button.setAllCaps(false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckProgramsActivity.this);
                builder.setTitle("Choose");
                builder.setMessage("Do you want choose this program or check details ?");
                builder.setPositiveButton("Check Details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(CheckProgramsActivity.this, ProgramDetailsActivity.class);
                        intent.putExtra("back_activity","CheckProgramsActivity");
                        intent.putExtra("program_name",name);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("Choose Program", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mReferenceUser.child("user_currentFitnessProgram").setValue(name);
                        Toast.makeText(CheckProgramsActivity.this,"Program is taken with successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CheckProgramsActivity.this, FitnessProgramActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();

            }
        });

        Space space = new Space(CheckProgramsActivity.this);
        space.setLayoutParams(new ViewGroup.LayoutParams(
                tenPixel,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        String val = workDayValue + "";
        TextView textView = new TextView(CheckProgramsActivity.this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                hundredPixel,
                fortyFivePixel
        ));
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        if (isTaken) {
            button.setBackgroundResource(R.drawable.taken_program_border);
            textView.setBackgroundResource(R.drawable.taken_program_border);
        }else {
            button.setBackgroundResource(R.drawable.border);
            textView.setBackgroundResource(R.drawable.border);
        }
        textView.setPadding(8,0,8,0);
        textView.setText(val);
        textView.setTypeface(null, Typeface.ITALIC);

        linearLayout.addView(button);
        linearLayout.addView(space);
        linearLayout.addView(textView);

        return  linearLayout;
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CheckProgramsActivity.this,FitnessProgramActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}