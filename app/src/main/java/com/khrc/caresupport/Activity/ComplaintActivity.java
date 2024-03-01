package com.khrc.caresupport.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.ViewModel.ComplaitViewModel;
import com.khrc.caresupport.databinding.ActivityComplaintBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ComplaintActivity extends AppCompatActivity {

    private ActivityComplaintBinding binding;
    private Users profile;

    private ComplaitViewModel viewModel;
    private Complaints selectedComplaint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityComplaintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ComplaitViewModel.class);
        binding.setComp(new Complaints());
        selectedComplaint = getIntent().getParcelableExtra("selectedComplaint");

        // Set up the lifecycle owner for LiveData in the ViewModel
        binding.setLifecycleOwner(this);

        if (!TextUtils.isEmpty(selectedComplaint.id)) {
            try {
                // Find MomProfile based on the phone number
                Complaints data = viewModel.retrieves(selectedComplaint.id);

                // Check if data is not null
                if (data != null) {
                    // MomProfile exists, populate the binding
                    binding.setComp(data);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ProfileActivity", "Phone number is empty");
        }

        binding.save.setOnClickListener(v -> {
            save(true, true, viewModel);
        });
    }

    private void save(boolean save, boolean close, ComplaitViewModel viewModel) {

        if (save) {

            Complaints finalData = binding.getComp();

            if (finalData.response_date == null){
                finalData.response_date = new Date();
            }else{
                finalData.response_date = finalData.response_date;
            }

            Log.d("RESPONSE", "Response Date " + finalData.response_date);

            finalData.complete = 1;
            viewModel.add(finalData);

        }

        if (close) {
            finish();
        }
    }
}