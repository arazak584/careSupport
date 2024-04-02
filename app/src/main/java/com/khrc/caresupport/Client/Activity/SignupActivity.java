package com.khrc.caresupport.Client.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.khrc.caresupport.R;
import com.khrc.caresupport.Utility.AppConstants;
import com.khrc.caresupport.Utility.Handler;
import com.khrc.caresupport.Utility.KeyValuePair;
import com.khrc.caresupport.ViewModel.CodeBookViewModel;
import com.khrc.caresupport.ViewModel.ProfileViewModel;
import com.khrc.caresupport.databinding.ActivitySignupBinding;
import com.khrc.caresupport.entity.MomProfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private MomProfile profile;

    private TextView dob;
    private ProfileViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dob = binding.dob;

        //ImageView startDateButton = findViewById(R.id.btStart);
        ImageView startDateButton = binding.btStart;
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open DatePickerDialog for start date selection
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Set the selected date on EditText
                        calendar.set(year, month, dayOfMonth);

                        dob.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding.setProfile(new MomProfile());

        // Set up the lifecycle owner for LiveData in the ViewModel
        binding.setLifecycleOwner(this);

        //LOAD SPINNERS
        loadCodeData(binding.mstatus, "marital");
        loadCodeData(binding.edul, "education");
        loadCodeData(binding.occu, "occupation");
        // Access the root view and set the click listener for the register button
        binding.register.setOnClickListener(v -> {
            save(true, true, viewModel);
        });

    }

    private void save(boolean save, boolean close, ProfileViewModel viewModel) {

        if (save) {
            // Get the date of birth from the EditText
            String dobText = dob.getText().toString();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.REGISTER, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(this, R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
            }

            // Check if DOB is less than 10 years ago
            if (!isAgeValid(dobText)) {
                // Show an error message
                Toast.makeText(this, "Age must be at least 10 years.", Toast.LENGTH_SHORT).show();
                return;  // Do not proceed with saving
            }

            // Set the date of birth in the finalData object
            String tel = binding.getProfile().getTel();
            String com = binding.getProfile().community;
            String name = binding.getProfile().mothn;
            String pin = binding.getProfile().pin;
            Integer mstatus = binding.getProfile().mstatus;
            String hf = binding.getProfile().hfac;


            MomProfile finalData = new MomProfile();
            // Set the date of birth and tel in the finalData object
            finalData.setDob(dobText);
            finalData.setTel(tel);
            finalData.setCommunity(com);
            finalData.setMothn(name);
            finalData.setPin(pin);
            finalData.setMstatus(mstatus);
            finalData.setHfac(hf);
            finalData.setComplete(1);
            viewModel.add(finalData);

        }

        if (close) {
            final Intent i = new Intent(this, LoginsActivity.class);
            startActivity(i);
        }
    }

    private boolean isAgeValid(String dob) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date dateOfBirth = sdf.parse(dob);

            Calendar dobCalendar = Calendar.getInstance();
            dobCalendar.setTime(dateOfBirth);

            Calendar todayCalendar = Calendar.getInstance();

            // Calculate age
            int age = todayCalendar.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
            if (todayCalendar.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--;  // Adjust age if the birthday hasn't occurred yet this year
            }

            // Check if age is at least 10 years
            return age >= 10;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;  // Handle invalid date format
        }
    }

    private <T> void callable(Spinner spinner, T[] array) {

        final ArrayAdapter<T> adapter = new ArrayAdapter<T>(this,
                android.R.layout.simple_spinner_item, array
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private void loadCodeData(Spinner spinner, final String codeFeature) {
        final CodeBookViewModel viewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);
        try {
            List<KeyValuePair> list = viewModel.findCodesOfFeature(codeFeature);
            KeyValuePair kv = new KeyValuePair();
            kv.codeValue = AppConstants.NOSELECT;
            kv.codeLabel = AppConstants.SPINNER_NOSELECT;
            if (list != null && !list.isEmpty()) {
                list.add(0, kv);
                callable(spinner, list.toArray(new KeyValuePair[0]));
            } else {
                list = new ArrayList<KeyValuePair>();
                list.add(kv);

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}