package com.khrc.caresupport.Client.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.khrc.caresupport.Activity.MainActivity;
import com.khrc.caresupport.Client.redcapexport.ImportComplaints;
import com.khrc.caresupport.Client.redcapexport.ImportHistory;
import com.khrc.caresupport.Client.redcapexport.ImportObsteric;
import com.khrc.caresupport.Client.redcapexport.ImportPregnancy;
import com.khrc.caresupport.Client.redcapimport.ChatApi;
import com.khrc.caresupport.R;
import com.khrc.caresupport.ViewModel.MedHistoryViewModel;
import com.khrc.caresupport.ViewModel.ObstericViewModel;
import com.khrc.caresupport.ViewModel.PregnancyViewModel;
import com.khrc.caresupport.ViewModel.ProfileViewModel;
import com.khrc.caresupport.entity.CodeBook;
import com.khrc.caresupport.entity.MedHistory;
import com.khrc.caresupport.entity.MomProfile;
import com.khrc.caresupport.entity.Obsteric;
import com.khrc.caresupport.entity.Pregnancy;
import com.khrc.caresupport.importredcap.JsonChatresponse;
import com.khrc.caresupport.importredcap.JsonComplaints;
import com.khrc.caresupport.Client.redcapimport.ClientComplaintsApi;
import com.khrc.caresupport.redcapsend.ImportLog;
import com.khrc.caresupport.Client.redcapexport.ImportRecords;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ClientActivity extends AppCompatActivity {

    ImageView imageView;
    private MomProfile momProfile;
    private Pregnancy pregnancy;
    private MedHistory medHistory;
    private Obsteric obsteric;
    private CodeBook codeBook;
    Button update;
    private SimpleDateFormat dateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        momProfile = new MomProfile();
        codeBook = new CodeBook();
        pregnancy = new Pregnancy();
        medHistory = new MedHistory();
        obsteric = new Obsteric();
        initiateBackgroundTask();
        exportComplaints();
        exportChat();
        // In another activity or fragment
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");

        final TextView ind = findViewById(R.id.mothname);
        final TextView fac = findViewById(R.id.hfac);
        final Button enq = findViewById(R.id.enq_button);
        final Button med = findViewById(R.id.med_button);
        final Button preg = findViewById(R.id.preg_button);
        final Button obs = findViewById(R.id.obs_button);
        final ImageView pro = findViewById(R.id.mprofile);
        final ImageView sche = findViewById(R.id.schedule);
        final ImageView refresh = findViewById(R.id.btnrefresh);

        ProfileViewModel viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        boolean profileFragmentShown = false;
        try {
            // Call a method that might throw ExecutionException or InterruptedException
            momProfile = viewModel.finds(phoneNumber);
            if (momProfile != null) {
                // Set the 'ind' TextView with the 'name' from the found MomProfile
                ind.setText(momProfile.getMothn());
                if (momProfile.hfac!=null) {
                    fac.setText(momProfile.getHfac());
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        refresh.setOnClickListener(v -> {
            initiateBackgroundTask();
            // Execute the export methods directly here since they need to run after initiateBackgroundTask()
            exportChat();
            exportComplaints();
        });

        PregnancyViewModel pModel = new ViewModelProvider(this).get(PregnancyViewModel.class);
        MedHistoryViewModel mModel = new ViewModelProvider(this).get(MedHistoryViewModel.class);
        ObstericViewModel oModel = new ViewModelProvider(this).get(ObstericViewModel.class);

        try {
            pregnancy = pModel.finds(phoneNumber);
            medHistory = mModel.finds(phoneNumber); // Fetch medical history
            obsteric = oModel.finds(phoneNumber);   // Fetch obstetric information

            if (pregnancy == null) {
                startActivity(new Intent(this, PregActivity.class));
            } else if (medHistory == null) {
                startActivity(new Intent(this, MedActivity.class));
            } else if (obsteric == null) {
                startActivity(new Intent(this, ObstericActivity.class));
            } else {
                // Handle the case when all conditions are met
                // You may choose to start another activity or perform additional actions here
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }



        enq.setOnClickListener(v -> {
            // Start the Activity
            final Intent i = new Intent(this, ChatsActivity.class);
            startActivity(i);
        });

        med.setOnClickListener(v -> {
            // Start the Activity
            final Intent i = new Intent(this, MedActivity.class);
            startActivity(i);
        });

        preg.setOnClickListener(v -> {
            // Start the Activity
            final Intent i = new Intent(this, PregActivity.class);
            startActivity(i);
        });

        obs.setOnClickListener(v -> {
            // Start the Activity
            final Intent i = new Intent(this, ObstericActivity.class);
            startActivity(i);
        });

        ind.setOnClickListener(v -> {
            // Start the Activity
            final Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        });

        pro.setOnClickListener(v -> {
            // Start the Activity
            final Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        });

        sche.setOnClickListener(v -> {
            // Start the Activity
            final Intent i = new Intent(this, AncActivity.class);
            startActivity(i);
        });

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check for internet connection and initiate background task
        initiateBackgroundTask();
        exportChat();
        updatePregnancyData();
        exportComplaints();
    }

    private void initiateBackgroundTask() {
        // Check for internet connection
        if (isNetworkAvailable()) {
            // Internet is available, initiate the background task
            //showToast("Syncing data in the background...");

            // Perform the API call in the background
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ImportRecords importRecords = new ImportRecords(ClientActivity.this);
                    importRecords.fetchMomProfilesAndPost();
                    return null;
                }
            }.execute();

            // Perform the API call for Complaints in the background
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ImportComplaints importComplaints = new ImportComplaints(ClientActivity.this);
                    importComplaints.fetchComplaintsAndPost();
                    return null;
                }
            }.execute();

            // Perform the API call for Log in the background
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ImportLog importLog = new ImportLog(ClientActivity.this);
                    importLog.fetchLogAndPost();
                    return null;
                }
            }.execute();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ImportPregnancy importPregnancy = new ImportPregnancy(ClientActivity.this);
                    importPregnancy.fetchPregnancyAndPost();
                    return null;
                }
            }.execute();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ImportHistory importHistory = new ImportHistory(ClientActivity.this);
                    importHistory.fetchHistoryAndPost();
                    return null;
                }
            }.execute();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ImportObsteric importObsteric = new ImportObsteric(ClientActivity.this);
                    importObsteric.fetchObstericAndPost();
                    return null;
                }
            }.execute();

        }
    }
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(ClientActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private Date parseDateString(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void exportComplaints() {
        // Initialize sharedPreferences within the method
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        ClientComplaintsApi.CompApiCallback callback = new ClientComplaintsApi.CompApiCallback() {
            @Override
            public void onSuccess(List<JsonComplaints> result) {
                // Handle success, if needed
                runOnUiThread(() -> {
                });
            }
            @Override
            public void onError(String error) {
                // Handle error, if needed
                runOnUiThread(() -> {
                });
            }
        };
        // Start the import and insert process
        ClientComplaintsApi complaintsApi = new ClientComplaintsApi(ClientActivity.this);
        complaintsApi.loadAndInsertCompData(phoneNumber, callback, ClientActivity.this);
    }

    private void exportChat() {
        // Initialize sharedPreferences within the method
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        ChatApi.ChatApiCallback callback = new ChatApi.ChatApiCallback() {
            @Override
            public void onSuccess(List<JsonChatresponse> result) {
                // Handle success, if needed
                runOnUiThread(() -> {
                    Toast.makeText(ClientActivity.this, "Refresh successful", Toast.LENGTH_SHORT).show();
                });
            }
            @Override
            public void onError(String error) {
                // Handle error, if needed
                runOnUiThread(() -> {
                    Toast.makeText(ClientActivity.this, "Refresh Error: "+ error, Toast.LENGTH_SHORT).show();
                });
            }
        };
        // Start the import and insert process
        ChatApi chatApi = new ChatApi(ClientActivity.this);
        chatApi.loadAndInsertChatData(phoneNumber, callback, ClientActivity.this);
    }

    private void updatePregnancyData() {
        // Get the phone number from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");

        // Initialize ViewModel
        PregnancyViewModel viewModels = new ViewModelProvider(this).get(PregnancyViewModel.class);

        try {
            // Fetch pregnancy data based on the phone number
            Pregnancy pregnancy = viewModels.finds(phoneNumber);

            // Check if pregnancy data is not null and first_ga_date is not null
            if (pregnancy != null && pregnancy.first_ga_date != null) {
                // Update UI with pregnancy data
                updateUIWithPregnancyData(pregnancy);
            }
        } catch (ExecutionException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateUIWithPregnancyData(Pregnancy pregnancy) throws ParseException {
        // Find TextViews and ProgressBar
        TextView lmp = findViewById(R.id.lmps);
        TextView wks = findViewById(R.id.textWeeks);
        TextView next = findViewById(R.id.nextschedule);
        ProgressBar circularProgressBar = findViewById(R.id.circularProgress);

        // Set LMP text
        lmp.setText("LMP " + pregnancy.getFirst_ga_date());

        // Convert date strings to Date objects
        Date firstGaDate = parseDateString(pregnancy.getFirst_ga_date());
        Date expectedDeliveryDate = parseDateString(pregnancy.getEdd());
        Date currentDate = new Date();

        // Calculate difference in weeks
        long differenceInMilliseconds = currentDate.getTime() - firstGaDate.getTime();
        long differenceInWeeks = TimeUnit.MILLISECONDS.toDays(differenceInMilliseconds) / 7;

        // Set max value of ProgressBar
        long totalPregnancyWeeks = TimeUnit.MILLISECONDS.toDays(expectedDeliveryDate.getTime() - firstGaDate.getTime()) / 7;
        circularProgressBar.setMax((int) totalPregnancyWeeks);

        // Set progress value
        circularProgressBar.setProgress((int) Math.min(differenceInWeeks, totalPregnancyWeeks));

        // Set weeks text
        wks.setText(String.valueOf(differenceInWeeks) + " " + getString(R.string.weeks));

        // Set next ANC schedule text
        String dateString = pregnancy.getNext_anc_schedule_date();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date schedule = inputFormat.parse(dateString);
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd-MM-yyyy", Locale.ENGLISH);
        String formattedDate = outputFormat.format(schedule);
        long nextInMilliseconds = schedule.getTime() - currentDate.getTime();
        long nextInDays = TimeUnit.MILLISECONDS.toDays(nextInMilliseconds) + 1;

        if (nextInDays >= 0) {
            String notificationContent = getString(R.string.dayys) + " " + nextInDays + " " + getString(R.string.dayy) + " (" + formattedDate + ")";
            next.setText(notificationContent);
        } else {
            next.setText(R.string.kindly_schedule_next_anc);
        }
    }

    private Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormat.parse(dateString);
    }
}