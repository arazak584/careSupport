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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.khrc.caresupport.Adapter.NewAdapter;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.R;
import com.khrc.caresupport.Utility.ComplaintsApi;
import com.khrc.caresupport.Utility.JsonComplaints;
import com.khrc.caresupport.ViewModel.ComplaitViewModel;
import com.khrc.caresupport.redcap.ImportComplaints;
import com.khrc.caresupport.redcap.ImportLog;

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
        adapter = new NewAdapter(this, MainActivity.this, complaints,userData);
        complaitViewModel = new ViewModelProvider(this).get(ComplaitViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Initial loading of complaints
        adapter.pull("", complaitViewModel);

//        initiateBackgroundTask(new Runnable() {
//            @Override
//            public void run() {
//                // This code will run after initiateBackgroundTask() is complete
//                importComplaints();
//                adapter.pull("", complaitViewModel);
//                // Now, you can call adapter.pull("", complaitViewModel);
//            }
//        });


        final ImageView refresh = findViewById(R.id.menu);
        refresh.setOnClickListener(v -> {
            initiateBackgroundTask(new Runnable() {
                @Override
                public void run() {
                    // This code will run after initiateBackgroundTask() is complete
                    importComplaints();
                }
            });
        });

        final Button fdb = findViewById(R.id.view);
        fdb.setOnClickListener(v -> {
            final Intent i = new Intent(this, FeedbackActivity.class);
            i.putExtra(LoginActivity.USER_DATA, userData);
            startActivity(i);
        });

        final Button cht = findViewById(R.id.chat);
        cht.setOnClickListener(v -> {
            final Intent i = new Intent(this, ChatActivity.class);
            i.putExtra(LoginActivity.USER_DATA, userData);
            startActivity(i);
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Refreshing...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ComplaintsApi.CompApiCallback callback = new ComplaintsApi.CompApiCallback() {
            @Override
            public void onSuccess(List<JsonComplaints> result) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Refresh successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    // Update the adapter with the new data
                    adapter.pull("", complaitViewModel);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Feedback Error: " + error, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
        };

        ComplaintsApi complaintsApi = new ComplaintsApi(MainActivity.this);
        complaintsApi.loadAndInsertCompData(callback, MainActivity.this);
        Log.d("ImportComplaints", "ImportComplaints method completed.");
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
                    ImportComplaints importComplaints = new ImportComplaints(MainActivity.this);
                    importComplaints.fetchComplaintsAndPost();
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