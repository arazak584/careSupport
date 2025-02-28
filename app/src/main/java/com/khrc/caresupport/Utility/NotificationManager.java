package com.khrc.caresupport.Utility;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.WorkManager;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.lifecycle.ViewModelProvider;
import android.app.Application;

import com.khrc.caresupport.Activity.LoginActivity;
import com.khrc.caresupport.R;
import com.khrc.caresupport.ViewModel.ComplaitViewModel;
import com.khrc.caresupport.ViewModel.UsersViewModel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class NotificationManager {
    private static final String TAG = "NotificationManager";
    private static final String NOTIFICATION_WORK_TAG = "notificationWork";
    private static final String CHANNEL_ID = "care_support_channel";
    private static final int NOTIFICATION_ID = 1001;

    public static class NotificationWorker extends Worker {
        private final UsersViewModel usersViewModel;
        private final ComplaitViewModel complaitViewModel;

        public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
            super(context, params);
            Application app = (Application) context.getApplicationContext();
            ViewModelProvider.AndroidViewModelFactory factory = new ViewModelProvider.AndroidViewModelFactory(app);
            usersViewModel = factory.create(UsersViewModel.class);
            complaitViewModel = factory.create(ComplaitViewModel.class);
        }

        @NonNull
        @Override
        public Result doWork() {
            try {
                checkAndNotify();
                return Result.success();
            } catch (Exception e) {
                Log.e(TAG, "Notification worker failed", e);
                return Result.retry();
            }
        }

        private void checkAndNotify() throws ExecutionException, InterruptedException {
            long userCount = usersViewModel.count();
            long complaintCount = complaitViewModel.count();

            if (userCount > 0 && complaintCount > 0) {
                createAndShowNotification(
                        getApplicationContext(),
                        "New Enquiry",
                        "You have a new enquiry from a mother",
                        createNotificationIntent()
                );
            }
        }

        private PendingIntent createNotificationIntent() {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            return PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
        }
    }

    /**
     * Initialize notification system
     */
    public static void initialize(Context context) {
        createNotificationChannel(context);
        scheduleNotificationChecks(context);
    }

    /**
     * Schedule periodic notification checks
     */
    private static void scheduleNotificationChecks(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(
                NotificationWorker.class,
                5, // Check every 1 minutes
                TimeUnit.SECONDS)
                .setConstraints(constraints)
                .addTag(NOTIFICATION_WORK_TAG)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                NOTIFICATION_WORK_TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                notificationWork
        );
    }

    /**
     * Create notification channel for Android O and above
     */
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Care Support Notifications",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for new enquiries and updates");
            channel.enableVibration(true);

            android.app.NotificationManager notificationManager =
                    context.getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Create and show a notification
     */
    public static void createAndShowNotification(
            Context context,
            String title,
            String message,
            PendingIntent pendingIntent
    ) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 200, 500});

        android.app.NotificationManager notificationManager =
                context.getSystemService(android.app.NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Cancel all notifications
     */
    public static void cancelAllNotifications(Context context) {
        android.app.NotificationManager notificationManager =
                context.getSystemService(android.app.NotificationManager.class);
        notificationManager.cancelAll();
    }

    /**
     * Stop notification checks
     */
    public static void stopNotificationChecks(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(NOTIFICATION_WORK_TAG);
    }
}