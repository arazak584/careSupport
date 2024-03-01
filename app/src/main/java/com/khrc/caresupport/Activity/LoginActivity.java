package com.khrc.caresupport.Activity;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.khrc.caresupport.entity.LogBook;
import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.R;
import com.khrc.caresupport.Utility.JsonProfile;
import com.khrc.caresupport.Utility.UsersApiClient;
import com.khrc.caresupport.ViewModel.LogViewModel;
import com.khrc.caresupport.ViewModel.UsersViewModel;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Users profile;

    private EditText username;
    public static final String USER_DATA = "com.khrc.caresupport.activity.MainActivity.USER_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final UsersViewModel profileViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        final LogViewModel logViewModel = new ViewModelProvider(this).get(LogViewModel.class);
        final Button start = findViewById(R.id.btnLogin);
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        username = findViewById(R.id.text_username);
        final EditText password = findViewById(R.id.text_password);

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
            final Intent i = new Intent(this, MainActivity.class);
            i.putExtra(USER_DATA, profile);
            startActivity(i);

        });

        Button dw = findViewById(R.id.btn_bckup);
        dw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform the import and insert operation when the button is clicked
                String phoneNumber = username.getText().toString().trim();
                importUser(phoneNumber);

            }
        });
    }

    private void importUser(String phoneNumber) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Downloading User Data...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        UsersApiClient.RedcapApiCallback callback = new UsersApiClient.RedcapApiCallback() {

            @Override
            public void onSuccess(List<JsonProfile> result) {
                // Handle success, if needed
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "User Download successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
            @Override
            public void onError(String error) {
                // Handle error, if needed
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Download Error: " + error, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
        };

        // Start the import and insert process
        UsersApiClient redcapApiClient = new UsersApiClient(LoginActivity.this);
        redcapApiClient.loadAndInsertProfileData(phoneNumber, callback, LoginActivity.this);
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
                        try{
                            LoginActivity.this.finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}