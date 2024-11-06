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

public class CheckDietPlansActivity extends AppCompatActivity {
    DatabaseReference mReferenceDietPlans, mReferenceUser;
    SharedPreferences sharedUser;

    LinearLayout container;

    String sharedUserUid, takenDietPlanName;
    int twoHundredThirtyDp = 230, twoHundredThirtyPixel, tenDp = 10, tenPixel, hundredTwentyDp = 120, hundredTwentyPixel,
            fortyFiveDp = 45, fortyFivePixel, programWorkdayValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_diet_plans);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        container = findViewById(R.id.container);
        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");

        mReferenceDietPlans = FirebaseDatabase.getInstance().getReference("DietPlans");
        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);

        mReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot detailSnapshot : snapshot.getChildren()) {
                    String detail = detailSnapshot.getKey();
                    if (detail != null && detail.equals("user_currentDietPlan")) {
                        takenDietPlanName = detailSnapshot.getValue(String.class);
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

        hundredTwentyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                hundredTwentyDp,
                getResources().getDisplayMetrics()
        );
        fortyFivePixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                fortyFiveDp,
                getResources().getDisplayMetrics()
        );

        takeDietPlans();


    }

    private void takeDietPlans() {
        mReferenceDietPlans.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot planNameSnapshot : snapshot.getChildren()) {
                    String planName = planNameSnapshot.getKey();
                    for (DataSnapshot detailSnapshot : planNameSnapshot.getChildren()) {
                        String detail = detailSnapshot.getKey();
                        if (detail != null && detail.equals("dietPlan_averageCalorie")) {
                            Long x = detailSnapshot.getValue(Long.class);
                            if (x != null) {
                                String averageCal = x + "";
                                if (planName != null &&  planName.equals(takenDietPlanName)) {
                                    LinearLayout linearLayout = new LinearLayout(CheckDietPlansActivity.this);
                                    linearLayout = createEntryLayout(planName,averageCal,true);
                                    container.addView(linearLayout);
                                }else {
                                    LinearLayout linearLayout = new LinearLayout(CheckDietPlansActivity.this);
                                    linearLayout = createEntryLayout(planName,averageCal,false);
                                    container.addView(linearLayout);
                                }
                            }
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private LinearLayout createEntryLayout(String name, String cal, boolean isTaken){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.topMargin = 10;
        LinearLayout linearLayout = new LinearLayout(CheckDietPlansActivity.this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);

        Button button = new Button(CheckDietPlansActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckDietPlansActivity.this);
                builder.setTitle("Choose");
                builder.setMessage("Do you want choose this plan or check details ?");
                builder.setPositiveButton("Check Details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(CheckDietPlansActivity.this, DietPlanDetailsActivity.class);
                        intent.putExtra("back_activity","CheckDietPlansActivity");
                        intent.putExtra("dietPlan_name",name);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("Choose Plan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mReferenceUser.child("user_currentDietPlan").setValue(name);
                        Toast.makeText(CheckDietPlansActivity.this,"Plan is taken with successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CheckDietPlansActivity.this, DietPlanActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();

            }
        });

        Space space = new Space(CheckDietPlansActivity.this);
        space.setLayoutParams(new ViewGroup.LayoutParams(
                tenPixel,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        TextView textView = new TextView(CheckDietPlansActivity.this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                hundredTwentyPixel,
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
        textView.setText(cal);
        textView.setTypeface(null, Typeface.ITALIC);

        linearLayout.addView(button);
        linearLayout.addView(space);
        linearLayout.addView(textView);

        return  linearLayout;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CheckDietPlansActivity.this,DietPlanActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}