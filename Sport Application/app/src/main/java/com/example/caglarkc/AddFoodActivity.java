package com.example.caglarkc;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.google.android.gms.tasks.Task;
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

public class AddFoodActivity extends AppCompatActivity {
    SharedPreferences sharedUser;
    DatabaseReference mReferenceFoods;
    StorageReference mReferenceStorage;

    EditText editTextFoodName, editTextCarb, editTextFat, editTextProtein, editTextCal;
    Button buttonAddFood;
    ImageButton imageButtonAddFoodImage;

    String sharedUserUid, foodName, strCarbVal, strFatVal, strProteinVal, strCalVal;
    int carbVal, fatVal, proteinVal, calVal, imageGetRequestCode = 0, imageRequestedCode = 1;;
    boolean isImageUploaded = false, isFoodExist = false;

    Bitmap chosenImage;

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
                chooseImage();
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
                    mData.put("food_cal", calVal + " / 100gr");
                    mData.put("food_carb", carbVal + " / 100gr");
                    mData.put("food_fat", fatVal + " / 100gr");
                    mData.put("food_protein", proteinVal + " / 100gr");
                    mReferenceFoods.child(foodName).setValue(mData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AddFoodActivity.this,"Food added with successfully...",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddFoodActivity.this, FoodListActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Food image is not uploaded, if you continue you can't upload image after this page...");
                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            HashMap<String, String> mData = new HashMap<>();
                            mData.put("food_cal", calVal + " / 100gr");
                            mData.put("food_carb", carbVal + " / 100gr");
                            mData.put("food_fat", fatVal + " / 100gr");
                            mData.put("food_protein", proteinVal + " / 100gr");
                            mReferenceFoods.child(foodName).setValue(mData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(AddFoodActivity.this,"Food added with successfully...",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddFoodActivity.this, FoodListActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    });

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

    private void chooseImage(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES},imageGetRequestCode);
        }else {
            Intent getImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(getImage,imageRequestedCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == imageGetRequestCode){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent getImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(getImage,imageRequestedCode);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == imageRequestedCode){
            if (resultCode == RESULT_OK && data != null){
                Uri imageUri = data.getData();

                try {
                    ImageDecoder.Source imageSource = ImageDecoder.createSource(this.getContentResolver(), imageUri);
                    chosenImage = ImageDecoder.decodeBitmap(imageSource);
                    imageButtonAddFoodImage.setImageBitmap(chosenImage);
                    uploadImage(imageUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(Uri imageUri) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        chosenImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] willSaveImage =  outputStream.toByteArray();
        StorageReference storageReference = mReferenceStorage.child("Foods").child(foodName);
        UploadTask uploadTask = storageReference.putBytes(willSaveImage);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                saveImageUrlToDatabase(String.valueOf(imageUri));
                isImageUploaded = true;
                Toast.makeText(AddFoodActivity.this,"Image is uploaded with successfully...",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(AddFoodActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

        });

    }

    private void saveImageUrlToDatabase(String imageUrl) {
        mReferenceFoods.child(foodName).child("food_imageUrl").setValue(imageUrl);
    }
}