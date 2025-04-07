package com.khrc.caresupport;

import android.app.Application;
import android.util.Log;
import androidx.work.Configuration;
import com.khrc.caresupport.Utility.BackgroundSyncManager;
import com.khrc.caresupport.Utility.ChatNotificationManager;
import com.khrc.caresupport.Utility.NotificationManager;

import java.util.concurrent.TimeUnit;

public class CareSupportApp extends Application implements Configuration.Provider {
    private static final String TAG = "CareSupportApplication";
    private static final int SYNC_INTERVAL_MINUTES = 1; // Define sync interval

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application started");
        initializeNotifications();
        initializeBackgroundSync();
    }

    private void initializeBackgroundSync() {
        try {
            BackgroundSyncManager.setupBackgroundSync(
                    this,
                    SYNC_INTERVAL_MINUTES,
                    TimeUnit.MINUTES
            );
            Log.d(TAG, "Background sync initialized successfully with interval: " + SYNC_INTERVAL_MINUTES + " minutes");
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

    public void stopBackgroundSync() {
        BackgroundSyncManager.cancelBackgroundSync(this);
        Log.d(TAG, "Background sync stopped");
    }

    private void initializeNotifications() {
        try {
            NotificationManager.initialize(this);
            ChatNotificationManager.initialize(this);
            Log.d(TAG, "Notification system initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize notification system: " + e.getMessage(), e);
        }
    }
}
