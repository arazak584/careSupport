package com.khrc.caresupport.Client.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.khrc.caresupport.R;
import com.khrc.caresupport.Utility.AppConstants;
import com.khrc.caresupport.Utility.Handler;
import com.khrc.caresupport.Utility.KeyValuePair;
import com.khrc.caresupport.ViewModel.CodeBookViewModel;
import com.khrc.caresupport.ViewModel.ObstericViewModel;
import com.khrc.caresupport.ViewModel.ProfileViewModel;
import com.khrc.caresupport.databinding.ActivityObstericBinding;
import com.khrc.caresupport.databinding.ActivityProfileBinding;
import com.khrc.caresupport.entity.MomProfile;
import com.khrc.caresupport.entity.Obsteric;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");

        if (!TextUtils.isEmpty(phoneNumber)) {
            try {
                // Find MomProfile based on the phone number
                MomProfile data = viewModel.finds(phoneNumber);

                // Check if data is not null
                if (data != null) {
                    // MomProfile exists, populate the binding
                    binding.setProfile(data);
                } else {
                    MomProfile momProfile = new MomProfile();
                    momProfile.setTel(phoneNumber);
                    // Populate the new profile with default or necessary data
                    binding.setProfile(momProfile);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ProfileActivity", "Phone number is empty");
        }

        //LOAD SPINNERS
        loadCodeData(binding.mstatus, "marital");
        loadCodeData(binding.edul, "education");
        loadCodeData(binding.occu, "occupation");

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);

        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });
    }

    private void save(boolean save, boolean close, ProfileViewModel viewModel) {

        if (save) {
            MomProfile finalData = binding.getProfile();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.PROLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(this, "Missing Fields", Toast.LENGTH_LONG).show();
                //return;
            }

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