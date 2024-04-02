package com.khrc.caresupport.Client.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.khrc.caresupport.R;
import com.khrc.caresupport.Utility.Handler;
import com.khrc.caresupport.ViewModel.ObstericViewModel;
import com.khrc.caresupport.ViewModel.PregnancyViewModel;
import com.khrc.caresupport.databinding.ActivityObstericBinding;
import com.khrc.caresupport.databinding.ActivityPregBinding;
import com.khrc.caresupport.entity.Obsteric;
import com.khrc.caresupport.entity.Pregnancy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ObstericActivity extends AppCompatActivity {


    private ActivityObstericBinding binding;
    private ObstericViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obsteric);

        binding = ActivityObstericBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ObstericViewModel.class);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");

        if (!TextUtils.isEmpty(phoneNumber)) {
            try {
                // Find MomProfile based on the phone number
                Obsteric data = viewModel.finds(phoneNumber);

                // Check if data is not null
                if (data != null) {
                    // MomProfile exists, populate the binding
                    binding.setObsteric(data);
                } else {
                    Obsteric obsteric = new Obsteric();
                    obsteric.setTel(phoneNumber);
                    // Populate the new profile with default or necessary data
                    binding.setObsteric(obsteric);
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

    private void save(boolean save, boolean close, ObstericViewModel viewModel) {

        if (save) {
            Obsteric finalData = binding.getObsteric();

            final boolean validateOnComplete = true;//finalData.complete == 1;
            boolean hasErrors = new Handler().hasInvalidInput(binding.OBSLAYOUT, validateOnComplete, false);

            if (hasErrors) {
                Toast.makeText(this, "Missing Fields", Toast.LENGTH_LONG).show();
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