package com.khrc.caresupport.Client.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.khrc.caresupport.R;
import com.khrc.caresupport.Utility.DatePickerFragment;
import com.khrc.caresupport.Utility.Handler;
import com.khrc.caresupport.ViewModel.PregnancyViewModel;
import com.khrc.caresupport.databinding.ActivityPregBinding;
import com.khrc.caresupport.entity.Pregnancy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class PregActivity extends AppCompatActivity {

    private ActivityPregBinding binding;
    private PregnancyViewModel viewModel;
    private final String TAG = "WOMAN.TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preg);

        //getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
       // getWindow().getDecorView().setBackgroundColor(Color.parseColor("#FF647F"));


        binding = ActivityPregBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //CHOOSING THE DATE
        getSupportFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle != null) {
                if (bundle.containsKey(DATE_BUNDLES.INSERTDATE1.getBundleKey())) {
                    final String result = bundle.getString(DATE_BUNDLES.INSERTDATE1.getBundleKey());
                    binding.GA.setText(result);
                }

//                if (bundle.containsKey(DATE_BUNDLES.INSERTDATE2.getBundleKey())) {
//                    final String result = bundle.getString(DATE_BUNDLES.INSERTDATE2.getBundleKey());
//                    binding.recorded.setText(result);
//                }

                if (bundle.containsKey(DATE_BUNDLES.INSERTDATE4.getBundleKey())) {
                    final String result = bundle.getString(DATE_BUNDLES.INSERTDATE4.getBundleKey());
                    binding.NEXTANCSCHEDULEDATE.setText(result);
                }
            }
        });

        binding.buttonGadate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.INSERTDATE1.getBundleKey(), c);
            newFragment.show(getSupportFragmentManager(), TAG);
        });

//        binding.buttonRecdate.setOnClickListener(v -> {
//            final Calendar c = Calendar.getInstance();
//            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.INSERTDATE2.getBundleKey(), c);
//            newFragment.show(getSupportFragmentManager(), TAG);
//        });

        binding.buttonAnc.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(DATE_BUNDLES.INSERTDATE4.getBundleKey(), c);
            newFragment.show(getSupportFragmentManager(), TAG);
        });



        viewModel = new ViewModelProvider(this).get(PregnancyViewModel.class);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");

        if (!TextUtils.isEmpty(phoneNumber)) {
            try {
                // Find MomProfile based on the phone number
                Pregnancy data = viewModel.finds(phoneNumber);

                // Check if data is not null
                if (data != null) {
                    // MomProfile exists, populate the binding
                    binding.setPreg(data);
                } else {
                    Pregnancy pregnancy = new Pregnancy();
                    pregnancy.setTel(phoneNumber);
                    // Populate the new profile with default or necessary data
                    pregnancy.setInsertdate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    binding.setPreg(pregnancy);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ProfileActivity", "Phone number is empty");
        }

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);

        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });
    }

    private void save(boolean save, boolean close, PregnancyViewModel viewModel) {

        if (save) {
            Pregnancy finalData = binding.getPreg();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.PREGNANCYLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(this, "Missing Fields", Toast.LENGTH_LONG).show();
                return;
            }

            //Date Error Check
            try {
                if (!binding.GA.getText().toString().trim().isEmpty() && !binding.NEXTANCSCHEDULEDATE.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date stdate = f.parse(binding.GA.getText().toString().trim());
                    Date edate = f.parse(binding.NEXTANCSCHEDULEDATE.getText().toString().trim());
                    if (stdate.after(currentDate)) {
                        binding.GA.setError("Date of Conception Cannot Be a Future Date");
                        Toast.makeText(this, "Date of Conception Cannot Be a Future Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (edate.before(stdate) || edate.equals(stdate)) {
                        binding.NEXTANCSCHEDULEDATE.setError("ANC Shedule Date Cannot Be Less than or Equal to GA Date");
                        Toast.makeText(this, "ANC Shedule Date Cannot Be Less than or Equal to GA Date", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.NEXTANCSCHEDULEDATE.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(this, "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


//            try {
//                if (!binding.recorded.getText().toString().trim().isEmpty() && !binding.GA.getText().toString().trim().isEmpty()) {
//                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//                    Date outcomeDate = f.parse(binding.recorded.getText().toString().trim());
//                    Date recordedDate = f.parse(binding.GA.getText().toString().trim());
//
//                    Calendar startCalendar = Calendar.getInstance();
//                    startCalendar.setTime(recordedDate);
//
//                    Calendar endCalendar = Calendar.getInstance();
//                    endCalendar.setTime(outcomeDate);
//
//                    int yearDiff = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
//                    int monthDiff = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
//                    int dayDiff = endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH);
//
//                    // Adjust the difference based on the day component
//                    if (dayDiff < 0) {
//                        monthDiff--;
//                    }
//
//                    // Calculate the total difference in months
//                    int totalDiffMonths = yearDiff * 12 + monthDiff;
//
//                    if (totalDiffMonths < 1 || totalDiffMonths > 12) {
//                        binding.recorded.setError("The difference between outcome and GA Date should be between 1 and 12 months");
//                        Toast.makeText(this, "The difference between outcome and GA Date should be between 1 and 12 months", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//
//                    // Clear error if validation passes
//                    binding.recorded.setError(null);
//                }
//            } catch (ParseException e) {
//                Toast.makeText(this, "Error parsing date", Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            }

            finalData.complete=1;
            viewModel.add(finalData);

        }

        if (close) {
            Intent intent = new Intent(this, ClientActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish(); // Finish the current activity to prevent it from remaining in the stack
        }

    }

    private enum DATE_BUNDLES {
        INSERTDATE1 ("INSERTDATE1"),
        INSERTDATE2 ("INSERTDATE2"),
        INSERTDATE3 ("INSERTDATE3"),
        INSERTDATE4 ("INSERTDATE4"),
        DOB ("DOB");

        private final String bundleKey;

        DATE_BUNDLES(String bundleKey) {
            this.bundleKey = bundleKey;

        }

        public String getBundleKey() {
            return bundleKey;
        }

        @Override
        public String toString() {
            return bundleKey;
        }
    }
}