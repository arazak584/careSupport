package com.khrc.caresupport;

import android.app.Application;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.khrc.caresupport.Utility.SyncWorker;

import java.util.concurrent.TimeUnit;

public class CareSupportApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setupWorkManager();
    }

    private void setupWorkManager() {
        // Constraints: Run only when network is available
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Create a periodic work request to run every 15 minutes
        PeriodicWorkRequest syncWorkRequest =
                new PeriodicWorkRequest.Builder(SyncWorker.class, 10, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "SyncWorker",
                ExistingPeriodicWorkPolicy.KEEP, // Prevent duplicate workers
                syncWorkRequest
        );
    }
}
