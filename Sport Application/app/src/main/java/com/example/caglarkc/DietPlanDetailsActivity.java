package com.example.caglarkc;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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

public class DietPlanDetailsActivity extends AppCompatActivity {
    DatabaseReference mReferenceDietPlan;

    LinearLayout dietPlanContainer;
    TextView dietPlanNameContainer;

    String backActivity, dietPlanName;
    int tenPixel, tenDp = 10;

    Intent intentSuccessfully;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diet_plan_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dietPlanContainer = findViewById(R.id.dietPlanContainer);
        dietPlanNameContainer = findViewById(R.id.dietPlanNameContainer);

        Intent intent = getIntent();
        backActivity = intent.getStringExtra("back_activity");
        dietPlanName  = intent.getStringExtra("dietPlan_name");
        if (backActivity != null && backActivity.equals("DietPlanActivity")) {
            intentSuccessfully = new Intent(DietPlanDetailsActivity.this,DietPlanActivity.class);
        }else if (backActivity != null && backActivity.equals("CheckDietPlansActivity")) {
            intentSuccessfully = new Intent(DietPlanDetailsActivity.this,CheckDietPlansActivity.class);
        }

        mReferenceDietPlan = FirebaseDatabase.getInstance().getReference("DietPlans").child(dietPlanName);

        tenPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                tenDp,
                getResources().getDisplayMetrics()
        );


        takeDietPlan();
    }

    @Override
    public void onBackPressed() {
        startActivity(intentSuccessfully);
        finish();
        super.onBackPressed();
    }

    private void takeDietPlan() {
        mReferenceDietPlan.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot daySnapshot : snapshot.getChildren()) {
                    String day = daySnapshot.getKey();
                    if (day != null) {
                        if (!daySnapshot.equals("dietPlan_averageCalorie")) {
                            LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            dayParams.bottomMargin = tenPixel * 2;

                            LinearLayout dayLayout = new LinearLayout(DietPlanDetailsActivity.this);
                            dayLayout.setLayoutParams(dayParams);
                            dayLayout.setOrientation(LinearLayout.VERTICAL);

                            TextView textViewDay = new TextView(DietPlanDetailsActivity.this);
                            textViewDay.setLayoutParams(new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            ));
                            textViewDay.setTypeface(null, Typeface.BOLD_ITALIC);
                            textViewDay.setTextSize(25);
                            textViewDay.setTextColor(Color.WHITE);
                            textViewDay.setText(day);
                            textViewDay.setBackgroundResource(R.drawable.under_line_border);

                            dayLayout.addView(textViewDay);

                            for (DataSnapshot mealSnapshot : daySnapshot.getChildren()) {
                                String meal = mealSnapshot.getKey();
                                if (meal != null) {
                                    LinearLayout.LayoutParams mealParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    mealParams.setMargins(tenPixel*2,tenPixel,0,0);

                                    LinearLayout mealLayout = new LinearLayout(DietPlanDetailsActivity.this);
                                    mealLayout.setLayoutParams(mealParams);
                                    mealLayout.setOrientation(LinearLayout.VERTICAL);

                                    TextView textViewMeal = new TextView(DietPlanDetailsActivity.this);
                                    textViewMeal.setLayoutParams(new ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    ));
                                    textViewMeal.setTypeface(null, Typeface.ITALIC);
                                    textViewMeal.setTextSize(22);
                                    textViewMeal.setTextColor(Color.WHITE);
                                    textViewMeal.setText(meal);
                                    textViewMeal.setBackgroundResource(R.drawable.under_line_border);

                                    mealLayout.addView(textViewMeal);

                                    for (DataSnapshot foodSnapshot : mealSnapshot.getChildren()) {
                                        String foodName = foodSnapshot.getKey();
                                        String foodGram = foodSnapshot.getValue(String.class);
                                        if (foodName != null && foodGram != null){
                                            LinearLayout foodLayout = createEntryLayout(foodName,foodGram);
                                            mealLayout.addView(foodLayout);
                                        }
                                    }
                                    dayLayout.addView(mealLayout);
                                }
                            }
                            dietPlanContainer.addView(dayLayout);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private LinearLayout createEntryLayout(String foodName, String gram) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(tenPixel*4,tenPixel,0,0);

        LinearLayout linearLayout = new LinearLayout(DietPlanDetailsActivity.this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setBackgroundResource(R.drawable.border);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);


        Button buttonFoodName = new Button(DietPlanDetailsActivity.this);
        buttonFoodName.setLayoutParams(new ViewGroup.LayoutParams(tenPixel*16,tenPixel*4));
        buttonFoodName.setTypeface(null, Typeface.ITALIC);
        buttonFoodName.setTextSize(18);
        buttonFoodName.setGravity(Gravity.CENTER);
        buttonFoodName.getBackground().setTintList(null);
        buttonFoodName.setBackgroundColor(Color.TRANSPARENT);
        buttonFoodName.setTextColor(Color.WHITE);
        buttonFoodName.setText(foodName);
        buttonFoodName.setPadding(0,0,0,0);
        buttonFoodName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DietPlanDetailsActivity.this,FoodDetailActivity.class);
                intent.putExtra("back_activity","DietPlanDetailsActivity");
                intent.putExtra("back_activity2",backActivity);
                intent.putExtra("food_name",foodName);
                startActivity(intent);
                finish();
            }
        });

        Space space = new Space(DietPlanDetailsActivity.this);
        space.setLayoutParams(new ViewGroup.LayoutParams(tenPixel*2, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button buttonGram = new Button(DietPlanDetailsActivity.this);
        buttonGram.setLayoutParams(new ViewGroup.LayoutParams(tenPixel*8,tenPixel*4));
        buttonGram.setTypeface(null, Typeface.ITALIC);
        buttonGram.setTextSize(18);
        buttonGram.setGravity(Gravity.CENTER);
        buttonGram.getBackground().setTintList(null);
        buttonGram.setBackgroundColor(Color.TRANSPARENT);
        buttonGram.setTextColor(Color.WHITE);
        buttonGram.setText(gram);
        buttonGram.setPadding(0,0,0,0);
        buttonGram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DietPlanDetailsActivity.this,FoodDetailActivity.class);
                intent.putExtra("back_activity","DietPlanDetailsActivity");
                intent.putExtra("back_activity2",backActivity);
                intent.putExtra("food_name",foodName);
                startActivity(intent);
                finish();
            }
        });


        linearLayout.addView(buttonFoodName);
        linearLayout.addView(space);
        linearLayout.addView(buttonGram);


        return linearLayout;
    }
}