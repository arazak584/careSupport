package com.khrc.caresupport.Client.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.khrc.caresupport.R;
import com.khrc.caresupport.Utility.Handler;
import com.khrc.caresupport.ViewModel.MedHistoryViewModel;
import com.khrc.caresupport.databinding.ActivityMedBinding;
import com.khrc.caresupport.entity.MedHistory;

import java.util.concurrent.ExecutionException;

public class MedActivity extends AppCompatActivity {

    private ActivityMedBinding binding;
    private MedHistoryViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MedHistoryViewModel.class);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");

        if (!TextUtils.isEmpty(phoneNumber)) {
            try {
                // Find MomProfile based on the phone number
                MedHistory data = viewModel.finds(phoneNumber);
                // Check if data is not null
                if (data != null) {
                    // MomProfile exists, populate the binding
                    binding.setMed(data);
                } else {
                    MedHistory medHistory = new MedHistory();
                    medHistory.setTel(phoneNumber);
                    // Populate the new profile with default or necessary data
                    //medHistory.setInsertDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));


                    // Set the new profile in the binding
                    binding.setMed(medHistory);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // Handle the case where the phone number is empty
            // This could be an error condition in your application
            Log.e("ProfileActivity", "Phone number is empty");
        }

        binding.buttonSaveClose.setOnClickListener(v -> {

            save(true, true, viewModel);
        });

        binding.buttonClose.setOnClickListener(v -> {

            save(false, true, viewModel);
        });
    }

    private void save(boolean save, boolean close, MedHistoryViewModel viewModel) {

        if (save) {
            MedHistory finalData = binding.getMed();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.MEDLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(this, R.string.incompletenotsaved, Toast.LENGTH_LONG).show();
                return;
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
}