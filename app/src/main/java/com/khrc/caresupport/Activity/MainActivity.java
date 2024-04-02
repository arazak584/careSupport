package com.khrc.caresupport.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.khrc.caresupport.Adapter.NewAdapter;
import com.khrc.caresupport.importredcap.HistoryApi;
import com.khrc.caresupport.importredcap.JsonChatresponse;
import com.khrc.caresupport.importredcap.JsonHistory;
import com.khrc.caresupport.importredcap.JsonPregnancy;
import com.khrc.caresupport.importredcap.PregnancyApi;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.R;
import com.khrc.caresupport.importredcap.ComplaintsApi;
import com.khrc.caresupport.importredcap.JsonComplaints;
import com.khrc.caresupport.ViewModel.ComplaitViewModel;
import com.khrc.caresupport.importredcap.ResponseApi;
import com.khrc.caresupport.redcapsend.ImportChatresponse;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private Complaints complaints;
    private Users users;
    private RecyclerView recyclerView;
    private NewAdapter adapter;
    private ComplaitViewModel complaitViewModel;

    public static final String USER_DATA = "com.khrc.caresupport.Activity.MainActivity.USER_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent f = getIntent();
        final Users userData = f.getParcelableExtra(LoginActivity.USER_DATA);
        String fac = userData.getMothn();

        Log.d("MainActivity", "Username of Individual " + fac);

        recyclerView = findViewById(R.id.my_recycler_view);
        complaitViewModel = new ViewModelProvider(this).get(ComplaitViewModel.class);
        adapter = new NewAdapter(this, MainActivity.this, complaints,userData,complaitViewModel);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Initial loading of complaints
        adapter.pull("", complaitViewModel);

        final ImageView refresh = findViewById(R.id.menu);
        refresh.setOnClickListener(v -> {
            initiateBackgroundTask(new Runnable() {
                @Override
                public void run() {
                    // This code will run after initiateBackgroundTask() is complete
                    importComplaints();
                    importPregnancy();
                    importHistory();
                    importChat();
                }
            });
        });

        // Locate the EditText in listview_main.xml
        final SearchView editSearch = findViewById(R.id.comp_search);
        // below line is to call set on query text listener method.
        editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                adapter.pull(newText, complaitViewModel);
                return false;
            }
        });

    }

    private void importComplaints() {
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Refreshing...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();

        ComplaintsApi.CompApiCallback callback = new ComplaintsApi.CompApiCallback() {
            @Override
            public void onSuccess(List<JsonComplaints> result) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Refresh successful", Toast.LENGTH_SHORT).show();
                    //progressDialog.dismiss();
                    // Update the adapter with the new data
                    adapter.pull("", complaitViewModel);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Feedback Error: " + error, Toast.LENGTH_SHORT).show();
                    //progressDialog.dismiss();
                });
            }
        };

        ComplaintsApi complaintsApi = new ComplaintsApi(MainActivity.this);
        complaintsApi.loadAndInsertCompData(callback, MainActivity.this);
        Log.d("ImportComplaints", "ImportComplaints method completed.");
    }

    private void importPregnancy() {
        PregnancyApi.PregnancyApiCallback callback = new PregnancyApi.PregnancyApiCallback() {
            @Override
            public void onSuccess(List<JsonPregnancy> result) {
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
        PregnancyApi pregnancyApi = new PregnancyApi(MainActivity.this);
        pregnancyApi.loadAndInsertPregnancyData(callback, MainActivity.this);
    }

    private void importChat() {
        ResponseApi.ChatApiCallback callback = new ResponseApi.ChatApiCallback() {
            @Override
            public void onSuccess(List<JsonChatresponse> result) {
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
        ResponseApi responseApi = new ResponseApi(MainActivity.this);
        responseApi.loadAndInsertChatData(callback, MainActivity.this);
    }

    private void importHistory() {
        HistoryApi.HistoryApiCallback callback = new HistoryApi.HistoryApiCallback() {
            @Override
            public void onSuccess(List<JsonHistory> result) {
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
        HistoryApi historyApi = new HistoryApi(MainActivity.this);
        historyApi.loadAndInsertHistoryData(callback, MainActivity.this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Check for internet connection and initiate background tasks sequentially
        initiateBackgroundTask(new Runnable() {
            @Override
            public void run() {
                // This code will run after initiateBackgroundTask() is complete
                importComplaints();
                importPregnancy();
                importHistory();
                importChat();
                adapter.pull("", complaitViewModel);
            }
        });
    }

    //Send Data to Redcap
    private void initiateBackgroundTask(final Runnable callback) {
        // Check for internet connection
        if (isNetworkAvailable()) {
            // Perform the API call for Complaints in the background
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ImportChatresponse importChatresponse = new ImportChatresponse(MainActivity.this);
                    importChatresponse.fetchChatAndPost();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    // Call the callback when the background task is complete
                    if (callback != null) {
                        callback.run();
                    }
                }
            }.execute();


        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}