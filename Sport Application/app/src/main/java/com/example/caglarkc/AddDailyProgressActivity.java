package com.example.caglarkc;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.service.media.MediaBrowserService;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import java.util.ArrayList;
import java.util.List;

/**
 * AddDailyProgressActivity allows users to select and upload images for daily progress tracking.
 * This activity provides functionality for:
 * - Selecting multiple images from the gallery and displaying the first selected image.
 * - Saving images with unique filenames to Firebase Storage.
 * - Saving image download URLs to Firebase Realtime Database under a specific date.
 * - Setting the date for images through year, month, and day spinners.
 * - Handling image selection and upload progress with a progress bar and feedback messages.
 */

public class AddDailyProgressActivity extends AppCompatActivity {
    SharedPreferences sharedUser;
    DatabaseReference mReferenceUser;
    StorageReference mReferenceStorage;

    ActivityResultLauncher<Intent> resultLauncher ;
    ImageView imageView; // İlk resmi göstermek için kullanıyoruz
    ArrayList<Uri> imageUris = new ArrayList<>(); // Seçilen resimleri saklamak için liste
    List<Task<?>> uploadTasks = new ArrayList<>(); // Task'leri saklamak için

    ImageButton imageButtonAddPhoto;
    Button buttonSaveImage, buttonCancel;
    Spinner yearSpinner, monthSpinner, daySpinner;
    ConstraintLayout constraintLayoutParent;
    ProgressBar progressBar;

    String sharedUserUid, selectedDate;
    boolean isYearSpinnerInitial = true, isMonthSpinnerInitial = true, isDaySpinnerInitial = true, isPhotoSelected = false;

    ArrayList<String> years = new ArrayList<>();
    ArrayList<String> months = new ArrayList<>();
    ArrayList<String> days = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_daily_progress);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        yearSpinner = findViewById(R.id.yearSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        progressBar = findViewById(R.id.progressBar);
        constraintLayoutParent = findViewById(R.id.constraintLayoutParent);
        daySpinner = findViewById(R.id.daySpinner);
        imageButtonAddPhoto = findViewById(R.id.imageButtonAddPhoto);
        imageView = findViewById(R.id.imageView);
        buttonSaveImage = findViewById(R.id.buttonSaveImage);
        buttonCancel = findViewById(R.id.buttonCancel);

        progressBar.setVisibility(View.GONE);
        isPhotoSelected = false;
        sharedUser = getSharedPreferences("user_data",MODE_PRIVATE);
        sharedUserUid = sharedUser.getString("user_uid","");
        mReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(sharedUserUid);
        mReferenceStorage = FirebaseStorage.getInstance().getReference("Users").child(sharedUserUid);



        createSpinners();
        registerResult();

        imageButtonAddPhoto.setOnClickListener(view -> {
            // Galeriyi açmak için intent oluştur
            openGallery();
        });

        buttonSaveImage.setOnClickListener(view -> {
            String selectedYear = (String) yearSpinner.getSelectedItem();
            String selectedMonth = (String) monthSpinner.getSelectedItem();
            String selectedDay = (String) daySpinner.getSelectedItem();

            selectedDate = selectedDay + "-" + selectedMonth + "-" + selectedYear;

            if (!imageUris.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                constraintLayoutParent.setVisibility(View.GONE);
                for (Uri imageUri : imageUris) {
                    String fileName = "image_" + System.currentTimeMillis() + ".jpg"; // Benzersiz dosya adı
                    // Geçersiz karakterleri değiştirme (örneğin, '.' yerine '_')
                    String sanitizedFileName = fileName.replace(".", "_");
                    StorageReference imageRef = mReferenceStorage.child("images/" + selectedDate + "/" + sanitizedFileName);

                    // Dosya yükleme işlemi başlatılır ve Task döndürülür
                    Task<UploadTask.TaskSnapshot> uploadTask = imageRef.putFile(imageUri);

                    // Task'ı Task listesine ekliyoruz
                    uploadTasks.add(uploadTask);

                    // Yükleme tamamlandığında indirme URL'sini al ve kaydet
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                // İndirme URL'sini al ve Firebase Realtime Database'e kaydet
                                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downloadUrl = task.getResult();
                                            saveDownloadUrlToDatabase(sanitizedFileName, downloadUrl.toString());
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(AddDailyProgressActivity.this, "Yükleme hatası: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // Tüm Task'ların tamamlanmasını bekleyin
                Tasks.whenAll(uploadTasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            // Tüm yükleme işlemleri başarıyla tamamlandı
                            Toast.makeText(AddDailyProgressActivity.this, "Tüm resimler başarıyla yüklendi!", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    constraintLayoutParent.setVisibility(View.VISIBLE);
                                }
                            },500);
                            MainMethods.addProgressDate(selectedDate);
                            startActivity(new Intent(AddDailyProgressActivity.this, DailyCheckActivity.class));
                            finish(); // Aktiviteyi sonlandır
                        } else {
                            // Tüm Task'ların başarıyla tamamlanmadığı bir durum
                            Toast.makeText(AddDailyProgressActivity.this, "Bazı yüklemeler başarısız oldu.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                Toast.makeText(AddDailyProgressActivity.this,"First choose image...",Toast.LENGTH_SHORT).show();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!imageUris.isEmpty()) {
                    imageUris.clear();
                    Toast.makeText(AddDailyProgressActivity.this,"Canceled...",Toast.LENGTH_SHORT).show();
                    imageView.setImageDrawable(null);
                    imageView.setBackgroundResource(R.drawable.profile_photo_icon);
                }
            }
        });
    }

    // İndirme URL'sini Firebase Realtime Database'e kaydetme
    private void saveDownloadUrlToDatabase(String fileName, String downloadUrl) {
        mReferenceUser.child("user_dailyProgressImages").child(selectedDate).child(fileName).setValue(downloadUrl);
    }


    // Galeri açan metot
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Çoklu seçim izni
        resultLauncher.launch(intent);
    }

    // Çoklu resim seçme sonuçlarını işleme
    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            // Seçilen resimleri alma
                            if (result.getData().getClipData() != null) {
                                ClipData clipData = result.getData().getClipData();
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    Uri imageUri = clipData.getItemAt(i).getUri();
                                    imageUris.add(imageUri); // Resmi listeye ekle
                                }

                                // İlk resmi ImageView'e yükleyelim (isteğe bağlı)
                                if (!imageUris.isEmpty()) {
                                    imageView.setImageDrawable(null);
                                    imageView.setBackgroundColor(Color.BLACK);
                                    imageView.setImageURI(imageUris.get(0));
                                }

                            } else if (result.getData().getData() != null) {
                                Uri imageUri = result.getData().getData();
                                imageUris.add(imageUri); // Tekli resim seçildiyse
                                imageView.setImageURI(imageUri); // İlk resmi göster
                            }

                            Toast.makeText(AddDailyProgressActivity.this, imageUris.size() + " image choose...", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddDailyProgressActivity.this, "Image choosing is canceled...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void createSpinners() {
        // Yıllar için veri seti (2000 - 2024 arası)
        for (int i = 2000; i <= 2024; i++) {
            years.add(i + "");
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, years);
        yearAdapter.setDropDownViewResource(R.layout.spinner_item);
        yearSpinner.setAdapter(yearAdapter);

        // Aylar için veri seti
        for (int i = 1; i <= 12; i++) {
            // Ay numarasını 01, 02 gibi formatlamak için String.format kullanıyoruz
            months.add(String.format("%02d", i));
        }

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, months);
        monthAdapter.setDropDownViewResource(R.layout.spinner_item);
        monthSpinner.setAdapter(monthAdapter);

        // Günler için veri seti (1 - 31 arası)
        for (int i = 1; i <= 31; i++) {
            days.add(String.format("%02d", i));
        }
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, days);
        dayAdapter.setDropDownViewResource(R.layout.spinner_item);
        daySpinner.setAdapter(dayAdapter);

        // Seçim işlemlerini dinleyelim
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // İlk seçimi atla
                if (isYearSpinnerInitial) {
                    isYearSpinnerInitial = false;  // İlk seçimi geçtik, artık flag'i güncelliyoruz
                    return;
                }
                // Seçilen yılı al
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // İlk seçimi atla
                if (isMonthSpinnerInitial) {
                    isMonthSpinnerInitial = false;  // İlk seçimi geçtik, artık flag'i güncelliyoruz
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // İlk seçimi atla
                if (isDaySpinnerInitial) {
                    isDaySpinnerInitial = false;  // İlk seçimi geçtik, artık flag'i güncelliyoruz
                    return;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddDailyProgressActivity.this,DailyCheckActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}