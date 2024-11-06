package com.example.caglarkc;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
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
 * FoodListActivity: Displays a list of foods with their respective calorie values, and allows users to add new foods.
 * The foods are loaded from a Firebase database and displayed in a list where each item shows the food name and calorie information.
 * The activity also includes an option to add a new food item, and it navigates back to the main menu upon pressing back.
 */

public class FoodListActivity extends AppCompatActivity {
    DatabaseReference mReferenceFoods;

    Button buttonAddFood;
    LinearLayout container, buttonContainer, firstPartContainer, secondPartContainer;
    RelativeLayout capitalContainer;
    TextView textViewCapital;

    Intent intentSuccessfully;

    int twoHundredTwentyDp = 220, twoHundredTwentyPixel, threeHundredDp = 300, threeHundredPixel, thirtyDp = 30,
            thirtyPixel, ninetyDp = 90, ninetyPixel, tenDp = 10, tenPixel, twentyFiveDp = 25, twentyFivePixel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mReferenceFoods = FirebaseDatabase.getInstance().getReference("Foods");
        intentSuccessfully = new Intent(FoodListActivity.this,UserMenuActivity.class);

        buttonAddFood = findViewById(R.id.buttonAddFood);
        firstPartContainer = findViewById(R.id.firstPartContainer);
        secondPartContainer = findViewById(R.id.secondPartContainer);
        container = findViewById(R.id.container);
        buttonContainer = findViewById(R.id.buttonContainer);
        textViewCapital = findViewById(R.id.textViewCapital);
        capitalContainer = findViewById(R.id.capitalContainer);

        thirtyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                thirtyDp,
                getResources().getDisplayMetrics()
        );
        threeHundredPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                threeHundredDp,
                getResources().getDisplayMetrics()
        );
        tenPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                tenDp,
                getResources().getDisplayMetrics()
        );
        twoHundredTwentyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                twoHundredTwentyDp,
                getResources().getDisplayMetrics()
        );
        ninetyPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                ninetyDp,
                getResources().getDisplayMetrics()
        );
        twentyFivePixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                twentyFiveDp,
                getResources().getDisplayMetrics()
        );


        takeFoods();

        buttonAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodListActivity.this,AddFoodActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //Framelayout içinde hizalamalar başarılı olamdı ama goruntu acısından soırun olamdıgından dokunmadım
    private RelativeLayout createEntryLayout(String name, String calorie){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.bottomMargin = 5;

        RelativeLayout relativeLayout = new RelativeLayout(FoodListActivity.this);
        relativeLayout.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams nameButtonParams = new RelativeLayout.LayoutParams(twoHundredTwentyPixel,thirtyPixel);
        nameButtonParams.addRule(RelativeLayout.ALIGN_PARENT_START);

        RelativeLayout.LayoutParams calParams = new RelativeLayout.LayoutParams(ninetyPixel,thirtyPixel);
        calParams.addRule(RelativeLayout.ALIGN_PARENT_END);


        Button buttonFoodName = new Button(FoodListActivity.this);
        buttonFoodName.setLayoutParams(nameButtonParams);
        buttonFoodName.setPadding(0,0,0,0);
        buttonFoodName.getBackground().setTintList(null);
        buttonFoodName.setBackgroundResource(R.drawable.border);
        buttonFoodName.setTextColor(Color.WHITE);
        buttonFoodName.setTextSize(16);
        buttonFoodName.setAllCaps(false);
        buttonFoodName.setText(name);
        buttonFoodName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodListActivity.this,FoodDetailActivity.class);
                intent.putExtra("food_name",name);
                intent.putExtra("back_activity","FoodListActivity");
                startActivity(intent);
                finish();
            }
        });

        FrameLayout frameLayout = new FrameLayout(FoodListActivity.this);
        RelativeLayout.LayoutParams frameLayoutParams = new RelativeLayout.LayoutParams(ninetyPixel, thirtyPixel);
        frameLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END); // FrameLayout sağda hizalansın
        frameLayout.setLayoutParams(frameLayoutParams);

        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        imageParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;

        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.gravity = Gravity.CENTER;

        ImageView imageView = new ImageView(FoodListActivity.this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(twentyFivePixel,twentyFivePixel));
        imageView.setImageResource(R.drawable.blaze_icon);

        Button buttonCalorie = new Button(FoodListActivity.this);
        buttonCalorie.setLayoutParams(calParams);
        buttonCalorie.setPadding(0,0,0,0);
        buttonCalorie.getBackground().setTintList(null);
        buttonCalorie.setBackgroundResource(R.drawable.border);
        buttonCalorie.setTextColor(Color.WHITE);
        buttonCalorie.setTypeface(null, Typeface.BOLD);
        buttonCalorie.setTextSize(16);
        buttonCalorie.setAllCaps(false);
        buttonCalorie.setText(calorie);
        buttonCalorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodListActivity.this,FoodDetailActivity.class);
                intent.putExtra("food_name",name);
                intent.putExtra("back_activity","FoodListActivity");
                startActivity(intent);
                finish();
            }
        });

        frameLayout.addView(buttonCalorie,buttonParams);
        frameLayout.addView(imageView,imageParams);


        relativeLayout.addView(buttonFoodName);
        relativeLayout.addView(frameLayout);

        return relativeLayout;
    }

    private void takeFoods() {
        mReferenceFoods.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot foodNameSnapshot : snapshot.getChildren()) {
                    String cal = "";
                    String foodName = foodNameSnapshot.getKey();
                    for (DataSnapshot detailSnapshot : foodNameSnapshot.getChildren()) {
                        String detail = detailSnapshot.getKey();
                        if (detail != null && detail.equals("food_cal")) {
                            cal = detailSnapshot.getValue(String.class);
                            if (cal != null) {
                                cal = cal.replace("/100gr","");
                            }
                        }
                    }
                    RelativeLayout relativeLayout = createEntryLayout(foodName,cal);
                    container.addView(relativeLayout);
                }
                Space space = new Space(FoodListActivity.this);
                space.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        tenPixel*5
                ));
                container.addView(space);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FoodListActivity.this,UserMenuActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}