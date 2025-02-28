package com.khrc.caresupport;

import android.app.Application;
import android.util.Log;
import androidx.work.Configuration;
import com.khrc.caresupport.Utility.BackgroundSyncManager;
import com.khrc.caresupport.Utility.NotificationManager;

import java.util.concurrent.TimeUnit;

public class CareSupportApp extends Application implements Configuration.Provider {
    private static final String TAG = "CareSupportApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        initializeNotifications();
        initializeBackgroundSync();
    }

    private void initializeBackgroundSync() {
        try {
            // Initialize background sync with 15-minute interval
            // You can adjust the interval based on your requirements
            BackgroundSyncManager.setupBackgroundSync(
                    this,
                    1, // interval
                    TimeUnit.MINUTES
            );
            Log.d(TAG, "Background sync initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize background sync: " + e.getMessage(), e);
        }
    }

    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(Log.INFO)
                .build();
    }

    // Optional: Method to stop background sync when needed
    public void stopBackgroundSync() {
        BackgroundSyncManager.cancelBackgroundSync(this);
    }

    private void initializeNotifications() {
        try {
            NotificationManager.initialize(this);
            Log.d(TAG, "Notification system initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize notification system: " + e.getMessage(), e);
        }
    }
}