// BackgroundSyncManager.java
package com.khrc.caresupport.Utility;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class BackgroundSyncManager {
    private static final String SYNC_WORK_TAG = "syncWork";
    private static final long SYNC_INTERVAL_MINUTES = 1; // Adjust as needed

    public static void setupBackgroundSync(Context context, long interval, TimeUnit timeUnit) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)  // Add battery constraint
                .build();

        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(
                SyncWorker.class,
                interval,
                timeUnit)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)  // Add backoff policy
                .addTag(SYNC_WORK_TAG)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                syncWorkRequest
        );
    }

    public static void cancelBackgroundSync(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_TAG);
    }
}