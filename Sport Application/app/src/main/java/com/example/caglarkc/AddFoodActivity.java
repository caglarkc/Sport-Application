package com.example.caglarkc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
/**
 * AddFoodActivity allows users to add a new food item with its nutritional values and an optional image.
 * Key features include:
 * - Input fields for food name, carbohydrates, fat, protein, and calories per 100 grams.
 * - Validation to ensure unique food names and numeric limits for nutritional values.
 * - Option to select an image for the food item from the gallery and upload it to Firebase Storage.
 * - Save food details to Firebase Realtime Database, including download URL for the image.
 * The activity provides feedback for each step, ensuring a smooth and user-friendly experience.
 */
public class AddFoodActivity extends AppCompatActivity {
    SharedPreferences sharedUser;
    DatabaseReference mReferenceFoods;
    StorageReference mReferenceStorage;

    ActivityResultLauncher<Intent> resultLauncher ;
    Uri imageUri, downloadUri;

    EditText editTextFoodName, editTextCarb, editTextFat, editTextProtein, editTextCal;
    Button buttonAddFood;
    ImageButton imageButtonAddFoodImage;

    String sharedUserUid, foodName, strCarbVal, strFatVal, strProteinVal, strCalVal;
    int carbVal, fatVal, proteinVal, calVal;
    boolean isImageUploaded = false, isFoodExist = false;


// GUNLUK FOOD TAKIBI VE GOSTEMRESİ EKLE
//Foodlara tıklayınca bilgi kısmını yap
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_food);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedUser = getSharedPreferences("user_data", MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");
        mReferenceFoods = FirebaseDatabase.getInstance().getReference("Foods");
        mReferenceStorage = FirebaseStorage.getInstance().getReference();

        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextCarb = findViewById(R.id.editTextCarb);
        editTextFat = findViewById(R.id.editTextFat);
        editTextProtein = findViewById(R.id.editTextProtein);
        editTextCal = findViewById(R.id.editTextCal);
        buttonAddFood = findViewById(R.id.buttonAddFood);
        imageButtonAddFoodImage = findViewById(R.id.imageButtonAddFoodImage);

        registerResult();

        buttonAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFoodExist = false;
                String input = editTextFoodName.getText().toString();
                if (!TextUtils.isEmpty(input)) {
                    mReferenceFoods.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                                String foodName = foodSnapshot.getKey();
                                if (foodName != null && foodName.equals(input)) {
                                    isFoodExist = true;
                                    break;
                                }
                            }
                            if (!isFoodExist) {
                                setButtonAddFood();
                            }else {
                                Toast.makeText(AddFoodActivity.this,"Food name is exist...",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

        imageButtonAddFoodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });


    }

    private void setButtonAddFood() {
        foodName = editTextFoodName.getText().toString();
        strCarbVal = editTextCarb.getText().toString();
        strFatVal = editTextFat.getText().toString();
        strProteinVal = editTextProtein.getText().toString();
        strCalVal = editTextCal.getText().toString();

        if (!TextUtils.isEmpty(foodName) && !TextUtils.isEmpty(strCarbVal) && !TextUtils.isEmpty(strFatVal)
                && !TextUtils.isEmpty(strProteinVal) && !TextUtils.isEmpty(strCalVal)) {
            carbVal = Integer.parseInt(strCarbVal);
            fatVal = Integer.parseInt(strFatVal);
            proteinVal = Integer.parseInt(strProteinVal);
            calVal = Integer.parseInt(strCalVal);
            if (carbVal < 9999 && fatVal < 9999 && proteinVal < 9999 && calVal < 9999) {
                if (isImageUploaded) {
                    HashMap<String, String> mData = new HashMap<>();
                    mData.put("food_cal", calVal + "/100gr");
                    mData.put("food_carb", carbVal + "/100gr");
                    mData.put("food_fat", fatVal + "/100gr");
                    mData.put("food_protein", proteinVal + "/100gr");

                    // Firebase Storage'a resim yükleme görevi
                    // Firebase Storage'a resim yükleme görevi (Task<Void> haline dönüştürme)
                    Task<Uri> task1 = mReferenceStorage.child("Foods").child(foodName).putFile(imageUri)
                            .continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    throw task.getException(); // Yükleme başarısız olursa hatayı fırlat
                                }
                                return mReferenceStorage.child("Foods").child(foodName).getDownloadUrl(); // İndirme URL'sini al
                            });

                    // Firebase Realtime Database'e yemek verisini kaydetme ve indirme URL'sini ekleme
                    task1.continueWithTask(downloadUrlTask -> {
                        if (!downloadUrlTask.isSuccessful()) {
                            throw downloadUrlTask.getException(); // Eğer indirme URL'sini alma işlemi başarısız olursa hatayı fırlat
                        }

                        // İndirme URL'sini al
                        downloadUri = downloadUrlTask.getResult();
                        mData.put("food_imageUrl", downloadUri.toString()); // İndirme URL'sini mData'ya ekle

                        // Firebase Realtime Database'e güncellenmiş mData'yı kaydet
                        return mReferenceFoods.child(foodName).setValue(mData);

                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Yükleme ve veri kaydetme işlemleri başarıyla tamamlandı
                            Food food = new Food(foodName,carbVal,fatVal,proteinVal,calVal,downloadUri.toString());
                            Toast.makeText(getApplicationContext(), "Food and image uploaded successfully...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddFoodActivity.this, FoodListActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Bir veya daha fazla işlem başarısız oldu
                            Toast.makeText(getApplicationContext(), "Some upload tasks failed...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        // Bir hata meydana geldi
                        Log.d("ERROR", e.getMessage());
                    });
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Food image is not uploaded, if you continue you can't upload image after this page...");
                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Daha önce aldığınız sabit URL
                            String imageUrl = "https://firebasestorage.googleapis.com/v0/b/caglarkc.appspot.com/o/Foods%2FcommonImage?alt=media&token=3b390e87-c1da-4c8b-bad4-97fccf4fb8f4";

                            // Yemek verilerini hazırlıyoruz
                            HashMap<String, String> mData = new HashMap<>();
                            mData.put("food_cal", calVal + "/100gr");
                            mData.put("food_carb", carbVal + "/100gr");
                            mData.put("food_fat", fatVal + "/100gr");
                            mData.put("food_protein", proteinVal + "/100gr");
                            mData.put("food_imageUrl", imageUrl); // Direkt URL'yi ekliyoruz

                            // Firebase Realtime Database'e güncellenmiş mData'yı kaydetme işlemi
                            mReferenceFoods.child(foodName).setValue(mData)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Veri kaydetme işlemi başarılı
                                            Toast.makeText(getApplicationContext(), "Food data uploaded successfully...", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(AddFoodActivity.this, FoodListActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // Veri kaydetme işlemi başarısız
                                            Toast.makeText(getApplicationContext(), "Failed to upload food data...", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Bir hata meydana geldiğinde hata mesajını logla
                                        Log.d("ERROR", e.getMessage());
                                    });
                        }
                    });
                    ;

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(AddFoodActivity.this,"Progress is canceled, for upload image click the image view...",Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                }
            }else {
                Toast.makeText(AddFoodActivity.this,"Values can't be bigger than 9999...",Toast.LENGTH_SHORT).show();
            }

        } else if (TextUtils.isEmpty(foodName)) {
            Toast.makeText(AddFoodActivity.this,"Food name can't be empty...",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(strCarbVal)) {
            Toast.makeText(AddFoodActivity.this,"Carb value can't be empty...",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(strFatVal)) {
            Toast.makeText(AddFoodActivity.this,"Fat value can't be empty...",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(strProteinVal)) {
            Toast.makeText(AddFoodActivity.this,"Protein value can't be empty...",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(strCalVal)) {
            Toast.makeText(AddFoodActivity.this,"Calorie value can't be empty...",Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false); // Çoklu seçim izni
        resultLauncher.launch(intent);
    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            // Seçilen resimleri alma
                            imageUri = result.getData().getData();
                            imageButtonAddFoodImage.setImageURI(imageUri);
                            imageButtonAddFoodImage.setBackgroundResource(R.color.black);

                            Toast.makeText(AddFoodActivity.this, "Image chosen...", Toast.LENGTH_SHORT).show();
                            isImageUploaded = true;
                        } else {
                            Toast.makeText(AddFoodActivity.this, "Image choosing is canceled...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

}