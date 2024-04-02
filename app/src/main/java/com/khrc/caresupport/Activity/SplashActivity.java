package com.khrc.caresupport.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.khrc.caresupport.Client.Activity.ClientActivity;
import com.khrc.caresupport.Client.Activity.LoginsActivity;
import com.khrc.caresupport.R;
import com.khrc.caresupport.importredcap.CodebookApi;
import com.khrc.caresupport.importredcap.JsonChatresponse;
import com.khrc.caresupport.importredcap.JsonCodebook;
import com.khrc.caresupport.importredcap.ResponseApi;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Button pr = findViewById(R.id.provider);
        pr.setOnClickListener(v -> {
            // Start the Activity
            final Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        });

        Button cl = findViewById(R.id.client);
        cl.setOnClickListener(v -> {
            // Start the Activity
            final Intent i = new Intent(this, LoginsActivity.class);
            startActivity(i);
        });

        importCodebook();
    }


    private void importCodebook() {
        CodebookApi.CodebookApiCallback callback = new CodebookApi.CodebookApiCallback() {
            @Override
            public void onSuccess(List<JsonCodebook> result) {
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
        CodebookApi codebookApi = new CodebookApi(SplashActivity.this);
        codebookApi.loadAndInsertCodeData(callback, SplashActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check for internet connection and initiate background tasks sequentially
        importCodebook();
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
                            SplashActivity.this.finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}