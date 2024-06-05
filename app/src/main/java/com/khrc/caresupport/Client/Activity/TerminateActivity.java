package com.khrc.caresupport.Client.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
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
import com.khrc.caresupport.Utility.DatePickerFragment;
import com.khrc.caresupport.Utility.Handler;
import com.khrc.caresupport.Utility.KeyValuePair;
import com.khrc.caresupport.ViewModel.CodeBookViewModel;
import com.khrc.caresupport.ViewModel.PregnancyViewModel;
import com.khrc.caresupport.databinding.ActivityAncBinding;
import com.khrc.caresupport.databinding.ActivityTerminateBinding;
import com.khrc.caresupport.entity.Pregnancy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class TerminateActivity extends AppCompatActivity {

    private ActivityTerminateBinding binding;
    private PregnancyViewModel viewModel;
    private final String TAG = "WOMAN.TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminate);

        binding = ActivityTerminateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //CHOOSING THE DATE
        getSupportFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported
            if (bundle != null) {
                if (bundle.containsKey(TerminateActivity.DATE_BUNDLES.INSERTDATE1.getBundleKey())) {
                    final String result = bundle.getString(TerminateActivity.DATE_BUNDLES.INSERTDATE1.getBundleKey());
                    binding.OUTCOMEDATE.setText(result);
                }

            }
        });


        binding.buttonAnc.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DialogFragment newFragment = new DatePickerFragment(TerminateActivity.DATE_BUNDLES.INSERTDATE1.getBundleKey(), c);
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
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ProfileActivity", "Phone number is empty");
        }

        //LOAD SPINNERS
        loadCodeData(binding.outcometype, "outcome");

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
                if (!binding.OUTCOMEDATE.getText().toString().trim().isEmpty() && !binding.PREGDATE.getText().toString().trim().isEmpty()) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date currentDate = new Date();
                    Date edate = f.parse(binding.OUTCOMEDATE.getText().toString().trim());
                    Date preg = f.parse(binding.PREGDATE.getText().toString().trim());
                    if (edate.after(currentDate)) {
                        //binding.NEXTANCSCHEDULEDATE.setError(R.string.ancerror);
                        Toast.makeText(this, R.string.ancerrors, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (preg.after(edate)) {
                        //binding.NEXTANCSCHEDULEDATE.setError(R.string.ancerror);
                        Toast.makeText(this, R.string.anc, Toast.LENGTH_LONG).show();
                        return;
                    }
                    // clear error if validation passes
                    binding.OUTCOMEDATE.setError(null);
                }
            } catch (ParseException e) {
                Toast.makeText(this, "Error parsing date", Toast.LENGTH_LONG).show();
                e.printStackTrace();
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

    private enum DATE_BUNDLES {
        INSERTDATE1 ("INSERTDATE1");
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