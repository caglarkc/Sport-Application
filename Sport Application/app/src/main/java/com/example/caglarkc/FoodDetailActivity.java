package com.example.caglarkc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FoodDetailActivity extends AppCompatActivity {
    DatabaseReference mReferenceFood;

    TextView textViewFoodName, calContainer, carbContainer, fatContainer, proteinContainer;
    ImageView imageViewFood;
    ProgressBar progressBar;
    LinearLayout detailContainer;

    String foodName, backActivity, backActivity2;
    Intent intentSuccessfully;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        foodName = intent.getStringExtra("food_name");
        backActivity = intent.getStringExtra("back_activity");
        mReferenceFood = FirebaseDatabase.getInstance().getReference("Foods").child(foodName);

        if (backActivity.equals("FoodListActivity")) {
            intentSuccessfully = new Intent(FoodDetailActivity.this,FoodListActivity.class);
        }else if (backActivity.equals("DietPlanDetailsActivity")) {
            intentSuccessfully = new Intent(FoodDetailActivity.this,DietPlanDetailsActivity.class);
            backActivity2 = intent.getStringExtra("back_activity2");
            intentSuccessfully.putExtra("back_activity",backActivity2);
        }else if (backActivity.equals("CheckDailyEatenFoodsActivity")) {
            intentSuccessfully = new Intent(FoodDetailActivity.this,CheckDailyEatenFoodsActivity.class);
        }

        progressBar = findViewById(R.id.progressBar);
        textViewFoodName = findViewById(R.id.textViewFoodName);
        calContainer = findViewById(R.id.calContainer);
        carbContainer = findViewById(R.id.carbContainer);
        fatContainer = findViewById(R.id.fatContainer);
        proteinContainer = findViewById(R.id.proteinContainer);
        imageViewFood = findViewById(R.id.imageViewFood);
        detailContainer = findViewById(R.id.detailContainer);

        detailContainer.setVisibility(View.GONE);
        textViewFoodName.setVisibility(View.GONE);
        imageViewFood.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        mReferenceFood.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot detailSnapshot : snapshot.getChildren()) {
                    String detail = detailSnapshot.getKey();
                    if (detail != null) {
                        String val = detailSnapshot.getValue(String.class);
                        if (detail.equals("food_cal")) {
                            val = val.replace("/100gr","");
                            calContainer.setText(val);
                        }else if (detail.equals("food_carb")) {
                            val = val.replace("/100gr","");
                            carbContainer.setText(val);
                        }else if (detail.equals("food_fat")) {
                            val = val.replace("/100gr","");
                            fatContainer.setText(val);
                        }else if (detail.equals("food_protein")) {
                            val = val.replace("/100gr","");
                            proteinContainer.setText(val);
                        }else if (detail.equals("food_imageUrl")) {
                            Glide.with(FoodDetailActivity.this)
                                    .load(val)
                                    .into(imageViewFood);
                        }
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textViewFoodName.setText(foodName);

                        detailContainer.setVisibility(View.VISIBLE);
                        textViewFoodName.setVisibility(View.VISIBLE);
                        imageViewFood.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                },100);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(intentSuccessfully);
        finish();
        super.onBackPressed();
    }
}