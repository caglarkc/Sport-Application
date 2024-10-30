package com.example.caglarkc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AddDailyFoodsDietFragment extends Fragment {
    //en son butun şeyleri ekledim sadece dataları alması ve ekranı kadyetmesi geri geldigine ksımları kaldı
    SharedPreferences sharedPreferences;

    static final String ARG_DAY = "day";

    TextView textViewDayName;
    ImageButton imageButtonPlus1, imageButtonRemove1;
    LinearLayout dayContainer;
    Button buttonSaveFoods;

    ArrayAdapter<String> foodAdapter;
    HashMap<String, String> hashMapData = new HashMap<>();
    HashMap<String , String> hashMapDayData = new HashMap<>();

    String day, strMeal;
    List<String> foodNameList = new ArrayList<>();
    int firstCounter = 0, tenPixel, tenDp = 10;
    HashMap<String , Integer> hashMapSecondCounters = new HashMap<>();

    public static AddDailyFoodsDietFragment newInstance(String day) {
        AddDailyFoodsDietFragment fragment = new AddDailyFoodsDietFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            day = getArguments().getString(ARG_DAY);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_daily_foods_diet, container, false);

        sharedPreferences = getActivity().getSharedPreferences("day_data",Context.MODE_PRIVATE);

        textViewDayName = view.findViewById(R.id.textViewDayName);
        imageButtonPlus1 = view.findViewById(R.id.imageButtonPlus);
        imageButtonRemove1 = view.findViewById(R.id.imageButtonRemove);
        dayContainer = view.findViewById(R.id.dayContainer);
        buttonSaveFoods = view.findViewById(R.id.buttonSaveFoods);


        List<Food> foodList = new ArrayList<>();
        foodList = Food.getFoodList();
        foodNameList.add("Choose Food");
        for (Food food : foodList) {
            foodNameList.add(food.name);
        }


        tenPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                tenDp,
                getResources().getDisplayMetrics()
        );





        textViewDayName.setText(day);

        imageButtonPlus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstCounter < 8) {
                    firstCounter ++;
                    LinearLayout.LayoutParams dinnerContainerParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    dinnerContainerParams.bottomMargin = tenPixel * 2;
                    LinearLayout dinnerContainer = new LinearLayout(getActivity());
                    dinnerContainer.setLayoutParams(dinnerContainerParams);
                    dinnerContainer.setOrientation(LinearLayout.VERTICAL);
                    dinnerContainer.setBackgroundResource(R.drawable.border);

                    LinearLayout.LayoutParams firstParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    firstParams.setMargins(tenPixel*3,tenPixel,0,tenPixel);

                    LinearLayout dinnerLine = new LinearLayout(getActivity());
                    dinnerLine.setLayoutParams(firstParams);
                    dinnerLine.setOrientation(LinearLayout.HORIZONTAL);
                    dinnerLine.setGravity(Gravity.CENTER_VERTICAL);

                    TextView textViewMeal = new TextView(getActivity());
                    textViewMeal.setLayoutParams(new ViewGroup.LayoutParams(tenPixel*8, tenPixel*4));
                    textViewMeal.setTextColor(Color.WHITE);
                    textViewMeal.setGravity(Gravity.CENTER);
                    textViewMeal.setBackgroundResource(R.drawable.border);
                    textViewMeal.setTextSize(18);
                    strMeal = "Meal " + firstCounter;
                    hashMapSecondCounters.put(strMeal , 0);
                    textViewMeal.setText(strMeal);

                    LinearLayout.LayoutParams layoutParamsImageButton = new LinearLayout.LayoutParams(tenPixel*2 , tenPixel*2);
                    layoutParamsImageButton.leftMargin = tenPixel;

                    ImageButton imageButtonPlus2 = new ImageButton(getActivity());
                    imageButtonPlus2.setLayoutParams(layoutParamsImageButton);
                    imageButtonPlus2.setBackgroundResource(R.drawable.plus_icon);

                    ImageButton imageButtonRemove2 = new ImageButton(getActivity());
                    imageButtonRemove2.setLayoutParams(layoutParamsImageButton);
                    imageButtonRemove2.setBackgroundResource(R.drawable.remove_icon);


                    imageButtonPlus2.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(View view) {
                            int secondCounter = hashMapSecondCounters.get(strMeal);
                            if (secondCounter < 8) {
                                secondCounter ++;
                                hashMapSecondCounters.put(strMeal, secondCounter);

                                LinearLayout.LayoutParams thirdParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        tenPixel*3
                                );
                                thirdParams.setMargins(tenPixel*6,0,0,tenPixel);

                                LinearLayout thirdLine = new LinearLayout(getActivity());
                                thirdLine.setLayoutParams(thirdParams);
                                thirdLine.setOrientation(LinearLayout.HORIZONTAL);
                                thirdLine.setGravity(Gravity.CENTER_VERTICAL);

                                foodAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, foodNameList);
                                foodAdapter.setDropDownViewResource(R.layout.spinner_item);
                                Spinner foodSpinner = new Spinner(getActivity());
                                foodSpinner.setLayoutParams(new ViewGroup.LayoutParams(tenPixel*12,tenPixel*3));
                                foodSpinner.setBackgroundResource(R.drawable.border);
                                foodSpinner.setPadding(8,0,8,0);
                                foodSpinner.setAdapter(foodAdapter);


                                LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(tenPixel*6,tenPixel*3);
                                editTextParams.leftMargin = tenPixel*2;

                                EditText editTextGram = new EditText(getActivity());
                                editTextGram.setLayoutParams(editTextParams);
                                editTextGram.setInputType(InputType.TYPE_CLASS_NUMBER);
                                editTextGram.setTextColor(Color.WHITE);
                                editTextGram.setTextSize(14);
                                editTextGram.setHint("Gram");
                                editTextGram.setHintTextColor(getResources().getColor(R.color.hint_color));
                                editTextGram.setPadding(0,0,0,0);
                                editTextGram.setGravity(Gravity.CENTER);
                                editTextGram.setBackgroundResource(R.drawable.border);

                                thirdLine.addView(foodSpinner);
                                thirdLine.addView(editTextGram);
                                dinnerContainer.addView(thirdLine);
                            }
                        }
                    });

                    imageButtonRemove2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int secondCounter = hashMapSecondCounters.get(strMeal);
                            if (secondCounter != 0) {
                                if (dinnerContainer.getChildCount() != 1) {
                                    secondCounter --;
                                    hashMapSecondCounters.put(strMeal , secondCounter);

                                    dinnerContainer.removeViewAt(dinnerContainer.getChildCount()-1);
                                }
                            }
                        }
                    });

                    dinnerLine.addView(textViewMeal);
                    dinnerLine.addView(imageButtonPlus2);
                    dinnerLine.addView(imageButtonRemove2);

                    dinnerContainer.addView(dinnerLine);

                    dayContainer.addView(dinnerContainer);
                }
            }
        });

        imageButtonRemove1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstCounter > 0) {
                    firstCounter --;
                    dayContainer.removeViewAt(dayContainer.getChildCount()-1);
                }
            }
        });

        buttonSaveFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hashMapData.clear();
                if (checkAllDataEntered()) {
                    Boolean isSuccessfully = true;
                    HashMap<String, HashSet<String>> mealFoodsMap = new HashMap<>();

                    for (int i = 0; i < dayContainer.getChildCount(); i++) {
                        View viewChild = dayContainer.getChildAt(i);

                        if (viewChild instanceof LinearLayout) {
                            LinearLayout innerContainer = (LinearLayout) viewChild;
                            String mealName = ""; // Mevcut Meal adını saklamak için

                            for (int j = 0; j < innerContainer.getChildCount(); j++) {
                                View innerView = innerContainer.getChildAt(j);

                                if (innerView instanceof LinearLayout) {
                                    LinearLayout lineLayout = (LinearLayout) innerView;

                                    for (int k = 0; k < lineLayout.getChildCount(); k++) {
                                        View element = lineLayout.getChildAt(k);

                                        // Eğer element bir TextView ise (yani "Meal X" gösteriyorsa)
                                        if (element instanceof TextView) {
                                            TextView textViewMeal = (TextView) element;
                                            String mealText = textViewMeal.getText().toString();

                                            // "Meal" başlığını mealName'e kaydediyoruz
                                            if (mealText.startsWith("Meal")) {
                                                mealName = mealText; // Örneğin "Meal 1"
                                                // Bu meal için bir set oluşturun (benzersiz yemek adları için)
                                                if (!mealFoodsMap.containsKey(mealName)) {
                                                    mealFoodsMap.put(mealName, new HashSet<String>());
                                                }
                                            }
                                        }

                                        // Eğer element bir Spinner ise
                                        if (element instanceof Spinner) {
                                            Spinner foodSpinner = (Spinner) element;
                                            String selectedFood = foodSpinner.getSelectedItem().toString();

                                            // Geçici olarak seçilen yemeği ve gramajı kaydetmek için
                                            String foodName = selectedFood;
                                            String gramValue = "";

                                            // Sonraki öğeleri kontrol ediyoruz
                                            if (k + 1 < lineLayout.getChildCount()) {
                                                View nextElement = lineLayout.getChildAt(k + 1);
                                                if (nextElement instanceof EditText) {
                                                    EditText editTextGram = (EditText) nextElement;
                                                    gramValue = editTextGram.getText().toString();
                                                }
                                            }

                                            // Aynı Meal altında aynı yemek var mı kontrol ediyoruz
                                            if (!mealFoodsMap.get(mealName).contains(foodName)) {
                                                // Eğer yemek adı yoksa ekliyoruz
                                                mealFoodsMap.get(mealName).add(foodName);

                                                // mealName, foodName ve gramValue'yu birleştirip listeye ekliyoruz
                                                if (!mealName.isEmpty() && !foodName.isEmpty() && !gramValue.isEmpty()) {
                                                    if (hashMapData.containsKey(mealName)) {
                                                        String x = hashMapData.get(mealName);
                                                        x = x + "_" + foodName + "," + gramValue;
                                                        hashMapData.put(mealName,x);
                                                    }else {
                                                        hashMapData.put(mealName , foodName + "," + gramValue);
                                                    }
                                                }
                                            } else {
                                                isSuccessfully = false;
                                                // Aynı yemek ismi varsa, kullanıcıya uyarı gösterilebilir
                                                Toast.makeText(getActivity(),"Cant add some food twice...",Toast.LENGTH_SHORT).show();
                                                hashMapData.clear();
                                                break;
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                    // mealDataList artık her öğün için benzersiz yemek adlarını içeriyor
                    if (isSuccessfully) {
                        for (Map.Entry<String , String> entry : hashMapData.entrySet()) {
                            String meal = entry.getKey();
                            String val = entry.getValue();
                            if (hashMapDayData.containsKey(day)) {
                                String x = hashMapDayData.get(day);
                                x = x + "+" + meal + "||" + val;
                                hashMapDayData.put(day, x);
                            }else {
                                hashMapDayData.put(day, meal + "||" + val);
                            }

                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                String val = hashMapDayData.get(day);
                                editor.putString(day,val);
                                editor.apply();

                            }
                        },20);


                        ((AddNewDietPlanActivity) requireActivity()).successfullyAdded(day);
                        // key: Monday
                        //value: Meal 1||caglari2,150_Caglariii,250+Meal 2||caglari2,13_Caglariii,23
                        //Buradan kaydetme işlemine devam et
                    }
                }
            }
        });

        /*
                buttonSaveFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAllDataEntered()) {
                    for (int i = 0 ; i < dayContainer.getChildCount() ; i++) {
                        View viewChild = dayContainer.getChildAt(i);
                        //dinnerContainera giriyoruz
                        if (viewChild instanceof LinearLayout) {
                            LinearLayout innerContainer = (LinearLayout) viewChild;
                            String mealName = "";
                            // dinnercontaineri dolanıyoruz
                            for (int j = 0; j < innerContainer.getChildCount(); j++) {
                                View innerView = innerContainer.getChildAt(j);

                                // Eğer innerView bir LinearLayout ise
                                if (innerView instanceof LinearLayout) {
                                    LinearLayout thirdline = (LinearLayout) innerView;

                                    // thirdline içindeki tüm view'ları döngü ile dolaşıyoruz
                                    for (int k = 0; k < thirdline.getChildCount(); k++) {
                                        View element = thirdline.getChildAt(k);

                                        // Eğer element bir TextView ise (yani "Meal X" gösteriyorsa)
                                        if (element instanceof TextView) {
                                            TextView textViewMeal = (TextView) element;
                                            String mealText = textViewMeal.getText().toString();

                                            // "Meal" başlığını mealName'e kaydediyoruz
                                            if (mealText.startsWith("Meal")) {
                                                mealName = mealText; // Örneğin "Meal 1"
                                            }
                                        }

                                        // Eğer element bir Spinner ise
                                        if (element instanceof Spinner) {
                                            Spinner foodSpinner = (Spinner) element;
                                            String selectedFood = foodSpinner.getSelectedItem().toString();

                                            // Geçici olarak seçilen yemeği ve gramajı kaydetmek için
                                            String foodName = selectedFood;
                                            String gramValue = "";

                                            // Sonraki öğeleri kontrol ediyoruz
                                            if (k + 1 < thirdline.getChildCount()) {
                                                View nextElement = thirdline.getChildAt(k + 1);
                                                if (nextElement instanceof EditText) {
                                                    EditText editTextGram = (EditText) nextElement;
                                                    gramValue = editTextGram.getText().toString();
                                                }
                                            }

                                            // mealName, foodName ve gramValue'yu birleştirip listeye ekliyoruz
                                            if (!mealName.isEmpty() && !foodName.isEmpty() && !gramValue.isEmpty()) {
                                                String mealEntry = mealName + "," + foodName + "," + gramValue;
                                                mealDataList.add(mealEntry);
                                                Log.d("erebus", mealEntry);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                }
            }
        });

         */

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Fragment'i geri yığından kaldır
                getParentFragmentManager().popBackStack();
            }
        });
    }



    private boolean checkAllDataEntered() {
        boolean b = true;
        for (int i = 0 ; i < dayContainer.getChildCount() ; i++) {
            View viewChild = dayContainer.getChildAt(i);
            if (viewChild instanceof LinearLayout) {
                LinearLayout innerContainer = (LinearLayout) viewChild;
                // innerContainer içindeki tüm view'ları döngü ile dolaşıyoruz
                for (int j = 0; j < innerContainer.getChildCount(); j++) {
                    View innerView = innerContainer.getChildAt(j);

                    // Eğer innerView bir LinearLayout ise
                    if (innerView instanceof LinearLayout) {
                        LinearLayout thirdline = (LinearLayout) innerView;

                        // thirdline içindeki tüm view'ları döngü ile dolaşıyoruz
                        for (int k = 0; k < thirdline.getChildCount(); k++) {
                            View element = thirdline.getChildAt(k);

                            // Eğer element bir Spinner ise
                            if (element instanceof Spinner) {
                                Spinner foodSpinner = (Spinner) element;
                                String selectedFood = foodSpinner.getSelectedItem().toString();
                                if (selectedFood.equals("Choose Food") || selectedFood.isEmpty()) {
                                    b = false;
                                }
                            }

                            // Eğer element bir EditText ise
                            if (element instanceof EditText) {
                                EditText editTextGram = (EditText) element;
                                String gramValue = editTextGram.getText().toString();
                                if (gramValue.isEmpty()) {
                                    b = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!b) {
            Toast.makeText(getActivity(),"Please enter all data...",Toast.LENGTH_SHORT).show();
        }
        return b;
    }
}
