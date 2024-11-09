package com.example.caglarkc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.text.DecimalFormat;

public class BfiCalculatorActivity extends AppCompatActivity {
    // vucut agırlıgı / boy^2 (bmi)
    SharedPreferences sharedUser;
    DatabaseReference mReferenceUser;

    TextView textViewBFI, textViewBMI, textViewDate;

    String sharedUserUid;
    HashMap<String, String> hashMapBfi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bfi_calculator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");

        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);

        textViewBFI = findViewById(R.id.textViewBFI);
        textViewBMI = findViewById(R.id.textViewBMI);
        textViewDate = findViewById(R.id.textViewDate);

        if (!MainMethods.getHashMapBfi().isEmpty()) {
            hashMapBfi = MainMethods.getHashMapBfi();
            Map.Entry<String, String> entry = hashMapBfi.entrySet().iterator().next();
            String date = entry.getKey();
            String[] array = entry.getValue().split("_");
            String abdomenData = array[0];
            abdomenData = abdomenData.replace("cm","");
            String neckData = array[1];
            neckData = neckData.replace("cm","");
            String wData = array[2];
            wData = wData.replace("kg","");
            String lData = array[3];
            lData = lData.replace("cm","");

            double bfi = bfiCalculator(Integer.parseInt(abdomenData) , Integer.parseInt(neckData) , Integer.parseInt(lData));
            double bmi = bmiCalculator(Integer.parseInt(wData) , Integer.parseInt(lData));

            Log.d("erebus","a:" + abdomenData);
            Log.d("erebus","n:" + neckData);
            Log.d("erebus","w:" + wData);
            Log.d("erebus","l:" + lData);
            textViewDate.setText("Data taked from " + date);
            textViewBFI.setText("Your BFI value is: " + bfi);
            textViewBMI.setText("Your BMI value is: " + bmi);



        }else {
            Toast.makeText(BfiCalculatorActivity.this,"Doesn't exist data...",Toast.LENGTH_SHORT).show();
            Toast.makeText(BfiCalculatorActivity.this,"Please enter bodymeasurement (length , neck ,w ,l) ...",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BfiCalculatorActivity.this,AddBodyMeasurementActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BfiCalculatorActivity.this,UserMenuActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private double bfiCalculator(int a, int n, int l) {
        // Vücut Yağ Yüzdesi (%) = 86.010 × log10(bel çevresi - boyun çevresi) - 70.041 × log10(boy) + 36.76

        // Bel çevresi - Boyun çevresi
        double waistNeckDifference = a - n;

        // BFI hesaplaması
        double bfi = 86.010 * Math.log10(waistNeckDifference) - 70.041 * Math.log10(l) + 36.76;

        // DecimalFormat ile bir ondalık haneye yuvarla
        DecimalFormat df = new DecimalFormat("#.#");
        String formattedBfi = df.format(bfi).replace(",", "."); // Virgülü nokta ile değiştir
        return Double.parseDouble(formattedBfi);
    }

    private double bmiCalculator(double weight, double height) {
        // BMI formülü: BMI = weight / (height^2)

        //translate m from cm
        height = height / 100;
        // BMI hesaplaması
        double bmi = weight / Math.pow(height, 2);

        // DecimalFormat ile bir ondalık haneye yuvarla
        DecimalFormat df = new DecimalFormat("#.#");
        String formattedBmi = df.format(bmi).replace(",", "."); // Virgülü nokta ile değiştir
        return Double.parseDouble(formattedBmi);
    }

}