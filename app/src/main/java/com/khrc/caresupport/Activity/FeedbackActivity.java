package com.khrc.caresupport.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.khrc.caresupport.Adapter.NewAdapter;
import com.khrc.caresupport.Adapter.ResolvedAdapter;
import com.khrc.caresupport.R;
import com.khrc.caresupport.Utility.ComplaintsApi;
import com.khrc.caresupport.Utility.JsonComplaints;
import com.khrc.caresupport.ViewModel.ComplaitViewModel;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.Users;

import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private Complaints complaints;
    private Users users;
    private RecyclerView recyclerView;
    private ResolvedAdapter adapter;
    private ComplaitViewModel complaitViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        final Intent f = getIntent();
        final Users userData = f.getParcelableExtra(LoginActivity.USER_DATA);
        String fac = userData.getMothn();
        Log.d("FeedbackActivity", "Username of Individual " + fac);

        recyclerView = findViewById(R.id.my_recycler_view);
        adapter = new ResolvedAdapter(this, FeedbackActivity.this, complaints, userData);
        complaitViewModel = new ViewModelProvider(this).get(ComplaitViewModel.class);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Initial loading of complaints
        adapter.pull("", complaitViewModel);
        //importComplaints();

        final ImageView refresh = findViewById(R.id.menu);
        refresh.setOnClickListener(v -> {
            importComplaints();
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
                    Toast.makeText(FeedbackActivity.this, "Refresh successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    // Update the adapter with the new data
                    adapter.pull("", complaitViewModel);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(FeedbackActivity.this, "Feedback Error: " + error, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
        };

        ComplaintsApi complaintsApi = new ComplaintsApi(FeedbackActivity.this);
        complaintsApi.loadAndInsertCompData(callback, FeedbackActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.pull("", complaitViewModel);
        };
}