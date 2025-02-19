package com.khrc.caresupport.Utility;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.khrc.caresupport.Client.redcapexport.ExportComplaints;
import com.khrc.caresupport.Client.redcapexport.ExportHistory;
import com.khrc.caresupport.Client.redcapexport.ExportObsteric;
import com.khrc.caresupport.Client.redcapexport.ExportPregnancy;
import com.khrc.caresupport.Client.redcapexport.ExportProfile;
import com.khrc.caresupport.Client.redcapexport.ExportLog;
import com.khrc.caresupport.importredcap.JsonChatresponse;
import com.khrc.caresupport.importredcap.ResponseApi;
import com.khrc.caresupport.redcapsend.ExportChatresponse;
import com.khrc.caresupport.redcapsend.ExportChats_Old;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.d(TAG, "Starting sync...");

            syncData(new ExportProfile(getApplicationContext()), "Profile");
            syncData(new ExportComplaints(getApplicationContext()), "Complaints");
            syncData(new ExportLog(getApplicationContext()), "Log");
            syncData(new ExportPregnancy(getApplicationContext()), "Pregnancy");
            syncData(new ExportHistory(getApplicationContext()), "History");
            syncData(new ExportObsteric(getApplicationContext()), "Obstetric");
            syncData(new ExportChats_Old(getApplicationContext()), "complaints");
            syncData(new ExportChatresponse(getApplicationContext()), "chatresponse");

            // Call the API and wait for the result
            boolean chatSyncSuccess = syncChatData();

            if (!chatSyncSuccess) {
                Log.e(TAG, "Chat data sync failed.");
                return Result.retry(); // Retry if chat sync fails
            }

            Log.d(TAG, "Sync completed successfully.");
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Sync failed: " + e.getMessage(), e);
            return Result.retry(); // Retry if an exception occurs
        }
    }

    private void syncData(Object exporter, String name) {
        try {
            if (exporter instanceof ExportProfile) {
                ((ExportProfile) exporter).fetchMomProfilesAndPost();
            } else if (exporter instanceof ExportComplaints) {
                ((ExportComplaints) exporter).fetchComplaintsAndPost();
            } else if (exporter instanceof ExportLog) {
                ((ExportLog) exporter).fetchLogAndPost();
            } else if (exporter instanceof ExportPregnancy) {
                ((ExportPregnancy) exporter).fetchPregnancyAndPost();
            } else if (exporter instanceof ExportHistory) {
                ((ExportHistory) exporter).fetchHistoryAndPost();
            } else if (exporter instanceof ExportObsteric) {
                ((ExportObsteric) exporter).fetchObstericAndPost();
            }else if (exporter instanceof ExportChats_Old) {
                ((ExportChats_Old) exporter).fetchComplaintsAndPost();
            }else if (exporter instanceof ExportChatresponse) {
                ((ExportChatresponse) exporter).fetchChatAndPost();
            }
            Log.d(TAG, name + " sync completed.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to sync " + name + ": " + e.getMessage(), e);
        }
    }

    private boolean syncChatData() {
        CountDownLatch latch = new CountDownLatch(1); // Used to wait for async callback
        final boolean[] success = {false};

        ResponseApi.ChatApiCallback callback = new ResponseApi.ChatApiCallback() {
            @Override
            public void onSuccess(List<JsonChatresponse> result) {
                Log.d(TAG, "Chat data sync successful.");
                success[0] = true;
                latch.countDown(); // Release the latch
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Chat data sync failed: " + error);
                success[0] = false;
                latch.countDown(); // Release the latch
            }
        };

        // Start the API call
        ResponseApi responseApi = new ResponseApi(getApplicationContext());
        responseApi.loadAndInsertChatData(callback, getApplicationContext());

        try {
            latch.await(30, TimeUnit.SECONDS); // Wait for 30 seconds max
        } catch (InterruptedException e) {
            Log.e(TAG, "Chat data sync interrupted.", e);
            return false;
        }

        return success[0]; // Return true if sync was successful, false otherwise
    }
}
