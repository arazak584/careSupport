package com.khrc.caresupport.Utility;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.khrc.caresupport.Client.Activity.ClientActivity;
import com.khrc.caresupport.Client.redcapexport.*;
import com.khrc.caresupport.Client.redcapimport.ChatApi;
import com.khrc.caresupport.Client.redcapimport.ClientComplaintsApi;
import com.khrc.caresupport.Repository.UsersRepository;
import com.khrc.caresupport.ViewModel.ProfileViewModel;
import com.khrc.caresupport.ViewModel.UsersViewModel;
import com.khrc.caresupport.importredcap.ComplaintsApi;
import com.khrc.caresupport.importredcap.HistoryApi;
import com.khrc.caresupport.importredcap.JsonChatresponse;
import com.khrc.caresupport.importredcap.JsonComplaints;
import com.khrc.caresupport.importredcap.JsonHistory;
import com.khrc.caresupport.importredcap.JsonPregnancy;
import com.khrc.caresupport.importredcap.PregnancyApi;
import com.khrc.caresupport.importredcap.ResponseApi;
import com.khrc.caresupport.redcapsend.ExportChatresponse;
import com.khrc.caresupport.redcapsend.ExportChats_Old;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";
    private UsersViewModel usersViewModel;
    private ProfileViewModel profileViewModel;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Application application = (Application) getApplicationContext();
        usersViewModel = new ViewModelProvider.AndroidViewModelFactory(application).create(UsersViewModel.class);
        profileViewModel = new ViewModelProvider.AndroidViewModelFactory(application).create(ProfileViewModel.class);

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
            syncData(new ExportChats_Old(getApplicationContext()), "Chats");
            syncData(new ExportChatresponse(getApplicationContext()), "Chat Response");

            // Check if user count is greater than 0
            long count = usersViewModel.count();
            if (count > 0) {
                Log.d(TAG, "User count is greater than 0, executing API calls.");
                syncComplaints();
                syncPregnancy();
                syncChat();
                syncHistory();
            }

            // Check if user count is greater than 0
            long counts = profileViewModel.count();
            if (counts > 0) {
                Log.d(TAG, "Profile count is greater than 0, executing API calls.");
                importComplaints();
                importResponse();
            }

            // Sync Chat Data
            boolean chatSyncSuccess = syncChatData();
            if (!chatSyncSuccess) {
                Log.e(TAG, "Chat data sync failed.");
                return Result.retry();
            }

            Log.d(TAG, "Sync completed successfully.");
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Sync failed: " + e.getMessage(), e);
            return Result.retry();
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
            } else if (exporter instanceof ExportChats_Old) {
                ((ExportChats_Old) exporter).fetchComplaintsAndPost();
            } else if (exporter instanceof ExportChatresponse) {
                ((ExportChatresponse) exporter).fetchChatAndPost();
            }
            Log.d(TAG, name + " sync completed.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to sync " + name + ": " + e.getMessage(), e);
        }
    }

    private boolean syncChatData() {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};

        ResponseApi.ChatApiCallback callback = new ResponseApi.ChatApiCallback() {
            @Override
            public void onSuccess(List<JsonChatresponse> result) {
                Log.d(TAG, "Chat data sync successful.");
                success[0] = true;
                latch.countDown();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Chat data sync failed: " + error);
                success[0] = false;
                latch.countDown();
            }
        };

        ResponseApi responseApi = new ResponseApi(getApplicationContext());
        responseApi.loadAndInsertChatData(callback, getApplicationContext());

        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "Chat data sync interrupted.", e);
            return false;
        }

        return success[0];
    }

    private void syncComplaints() {
        ComplaintsApi.CompApiCallback callback = new ComplaintsApi.CompApiCallback() {
            @Override
            public void onSuccess(List<JsonComplaints> result) {
                Log.d(TAG, "Complaints sync successful.");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Complaints sync failed: " + error);
            }
        };

        ComplaintsApi complaintsApi = new ComplaintsApi(getApplicationContext());
        complaintsApi.loadAndInsertCompData(callback, getApplicationContext());
    }

    private void syncPregnancy() {
        PregnancyApi.PregnancyApiCallback callback = new PregnancyApi.PregnancyApiCallback() {
            @Override
            public void onSuccess(List<JsonPregnancy> result) {
                Log.d(TAG, "Pregnancy sync successful.");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Pregnancy sync failed: " + error);
            }
        };

        PregnancyApi pregnancyApi = new PregnancyApi(getApplicationContext());
        pregnancyApi.loadAndInsertPregnancyData(callback, getApplicationContext());
    }

    private void syncChat() {
        ResponseApi.ChatApiCallback callback = new ResponseApi.ChatApiCallback() {
            @Override
            public void onSuccess(List<JsonChatresponse> result) {
                Log.d(TAG, "Chat sync successful.");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Chat sync failed: " + error);
            }
        };

        ResponseApi responseApi = new ResponseApi(getApplicationContext());
        responseApi.loadAndInsertChatData(callback, getApplicationContext());
    }

    private void syncHistory() {
        HistoryApi.HistoryApiCallback callback = new HistoryApi.HistoryApiCallback() {
            @Override
            public void onSuccess(List<JsonHistory> result) {
                Log.d(TAG, "History sync successful.");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "History sync failed: " + error);
            }
        };

        HistoryApi historyApi = new HistoryApi(getApplicationContext());
        historyApi.loadAndInsertHistoryData(callback, getApplicationContext());
    }

    private void importComplaints() {
        // Initialize sharedPreferences within the method
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        ClientComplaintsApi.CompApiCallback callback = new ClientComplaintsApi.CompApiCallback() {
            @Override
            public void onSuccess(List<JsonComplaints> result) {
            }
            @Override
            public void onError(String error) {
            }
        };
        // Start the import and insert process
        ClientComplaintsApi complaintsApi = new ClientComplaintsApi(getApplicationContext());
        complaintsApi.loadAndInsertCompData(phoneNumber, callback, getApplicationContext());
    }

    private void importResponse() {
        // Initialize sharedPreferences within the method
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        ChatApi.ChatApiCallback callback = new ChatApi.ChatApiCallback() {
            @Override
            public void onSuccess(List<JsonChatresponse> result) {

            }
            @Override
            public void onError(String error) {
            }
        };
        // Start the import and insert process
        ChatApi chatApi = new ChatApi(getApplicationContext());
        chatApi.loadAndInsertChatData(phoneNumber, callback, getApplicationContext());
    }
}
