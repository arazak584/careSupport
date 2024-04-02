package com.khrc.caresupport.Client.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.khrc.caresupport.Activity.LoginActivity;
import com.khrc.caresupport.Client.redcapimport.JsonObsteric;
import com.khrc.caresupport.Client.redcapimport.ObstericApi;
import com.khrc.caresupport.R;
import com.khrc.caresupport.ViewModel.LogViewModel;
import com.khrc.caresupport.ViewModel.ProfileViewModel;
import com.khrc.caresupport.entity.LogBook;
import com.khrc.caresupport.entity.MomProfile;
import com.khrc.caresupport.Client.redcapimport.ClientHistoryApi;
import com.khrc.caresupport.Client.redcapimport.ClientPregnancyApi;
import com.khrc.caresupport.importredcap.CodebookApi;
import com.khrc.caresupport.importredcap.JsonCodebook;
import com.khrc.caresupport.importredcap.JsonHistory;
import com.khrc.caresupport.importredcap.JsonPregnancy;
import com.khrc.caresupport.importredcap.JsonProfile;
import com.khrc.caresupport.Client.redcapimport.RedcapApiClient;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoginsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private MomProfile profile;

    private TextView displayText;
    private EditText username;
    private Button cli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logins);

       final ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        final LogViewModel logViewModel = new ViewModelProvider(this).get(LogViewModel.class);
        final Button start = findViewById(R.id.btnLogin);
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        username = findViewById(R.id.text_username);
        cli = findViewById(R.id.btnprovider);
        final EditText password = findViewById(R.id.text_password);
        importCodebook();

        Button signup = findViewById(R.id.signup);
        signup.setOnClickListener(v -> {
                // Start the Activity
                final Intent i = new Intent(this, SignupActivity.class);
                startActivity(i);
        });

        cli.setOnClickListener(v -> {
            // Start the Activity
            final Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        });

        start.setOnClickListener(v -> {

            if(username.getText().toString()==null || username.getText().toString().trim().isEmpty()){
                username.setError("Invalid Phone Number");
                Toast.makeText(this,"Please provide a valid phone number", Toast.LENGTH_LONG).show();
                return;
            }

            if(password.getText().toString()==null || password.getText().toString().trim().isEmpty()){
                password.setError("Invalid User PIN");
                Toast.makeText(this,"Please provide a valid user PIN", Toast.LENGTH_LONG).show();
                return;
            }

            final String myuser = username.getText().toString().trim();
            final String mypass = password.getText().toString().trim();
            savePhoneNumber(myuser);


            try {
                profile = profileViewModel.find(myuser, mypass);
                Log.d("MyActivity", "Username: " + myuser);
                Log.d("MyActivity", "Password: " + mypass);

            } catch (ExecutionException e) {
                Toast.makeText(this,"Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                Toast.makeText(this,"Something terrible went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }

            if(profile == null){
                username.setError("User Not Registered");
                Toast.makeText(this,"User Not Registered", Toast.LENGTH_LONG).show();
                return;
            }

            if (profile != null){
                LogBook log = new LogBook();
                log.tel = myuser;
                log.logdate = new Date();
                logViewModel.add(log);
            }

            username.setError(null);
            password.setText(null);
            final Intent i = new Intent(this, ClientActivity.class);
            startActivity(i);

        });

        Button dw = findViewById(R.id.btn_bckup);
        dw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform the import and insert operation when the button is clicked
                String phoneNumber = username.getText().toString().trim();
                performImportAndInsert(phoneNumber);
                importCodebook();
                importPreg(phoneNumber);
                importHistory(phoneNumber);
                importObsteric(phoneNumber);
            }
        });

    }

    private void performImportAndInsert(String phoneNumber) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Importing Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        RedcapApiClient.RedcapApiCallback callback = new RedcapApiClient.RedcapApiCallback() {

            @Override
            public void onSuccess(List<JsonProfile> result) {
                // Handle success, if needed
                runOnUiThread(() -> {
                    Toast.makeText(LoginsActivity.this, "Profile Import successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
            @Override
            public void onError(String error) {
                // Handle error, if needed
                runOnUiThread(() -> {
                    Toast.makeText(LoginsActivity.this, "Download Error: " + error, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
        };

        // Start the import and insert process
        RedcapApiClient redcapApiClient = new RedcapApiClient(LoginsActivity.this);
        redcapApiClient.loadAndInsertProfileData(phoneNumber, callback, LoginsActivity.this);
    }

    private void importPreg(String phoneNumber) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Importing Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ClientPregnancyApi.PregnancyApiCallback callback = new ClientPregnancyApi.PregnancyApiCallback() {

            @Override
            public void onSuccess(List<JsonPregnancy> result) {
                // Handle success, if needed
                runOnUiThread(() -> {
                    Toast.makeText(LoginsActivity.this, "Pregnancy Import successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
            @Override
            public void onError(String error) {
                // Handle error, if needed
                runOnUiThread(() -> {
                    Toast.makeText(LoginsActivity.this, "Download Error: " + error, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
        };

        // Start the import and insert process
        ClientPregnancyApi pregnancyApi = new ClientPregnancyApi(LoginsActivity.this);
        pregnancyApi.loadAndInsertPregnancyData(phoneNumber, callback, LoginsActivity.this);
    }

    private void importHistory(String phoneNumber) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Importing Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ClientHistoryApi.HistoryApiCallback callback = new ClientHistoryApi.HistoryApiCallback() {

            @Override
            public void onSuccess(List<JsonHistory> result) {
                // Handle success, if needed
                runOnUiThread(() -> {
                    Toast.makeText(LoginsActivity.this, "Medical History Import successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
            @Override
            public void onError(String error) {
                // Handle error, if needed
                runOnUiThread(() -> {
                    Toast.makeText(LoginsActivity.this, "Download Error: " + error, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
        };

        // Start the import and insert process
        ClientHistoryApi historyApi = new ClientHistoryApi(LoginsActivity.this);
        historyApi.loadAndInsertHistoryData(phoneNumber, callback, LoginsActivity.this);
    }

    private void importObsteric(String phoneNumber) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Importing Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ObstericApi.ObstericApiCallback callback = new ObstericApi.ObstericApiCallback() {

            @Override
            public void onSuccess(List<JsonObsteric> result) {
                // Handle success, if needed
                runOnUiThread(() -> {
                    Toast.makeText(LoginsActivity.this, "Obsteric Import successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
            @Override
            public void onError(String error) {
                // Handle error, if needed
                runOnUiThread(() -> {
                    Toast.makeText(LoginsActivity.this, "Download Error: " + error, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
        };

        // Start the import and insert process
        ObstericApi obstericApi = new ObstericApi(LoginsActivity.this);
        obstericApi.loadAndInsertObstericData(phoneNumber, callback, LoginsActivity.this);
    }


    private void importCodebook() {
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Importing Data...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();

        CodebookApi.CodebookApiCallback callback = new CodebookApi.CodebookApiCallback() {
            @Override
            public void onSuccess(List<JsonCodebook> result) {
                // Handle success, if needed
                runOnUiThread(() -> {
                    //Toast.makeText(LoginsActivity.this, "Codebook Import successful" , Toast.LENGTH_SHORT).show();
                   // progressDialog.dismiss();
                });
            }
            @Override
            public void onError(String error) {
                // Handle error, if needed
                runOnUiThread(() -> {
                    Toast.makeText(LoginsActivity.this, "Download Error: " + error, Toast.LENGTH_SHORT).show();
                    //progressDialog.dismiss();
                });
            }
        };
        // Start the import and insert process
        CodebookApi codebookApi = new CodebookApi(LoginsActivity.this);
        codebookApi.loadAndInsertCodeData(callback, LoginsActivity.this);
    }



    private void savePhoneNumber(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();
    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit_confirmation_title))
                .setMessage(getString(R.string.exiting_lbl))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            // Finish the activity when the back key is pressed
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

}