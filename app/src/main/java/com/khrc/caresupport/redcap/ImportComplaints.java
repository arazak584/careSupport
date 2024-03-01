package com.khrc.caresupport.redcap;


import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.khrc.caresupport.Dao.ComplaintsDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.Complaints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ImportComplaints {

    private List<NameValuePair> params;
    private HttpPost post;
    private HttpResponse resp;
    private HttpClient client;
    private int respCode;
    private BufferedReader reader;
    private StringBuffer result;
    private String line;
    private JSONObject record;
    private JSONArray data;
    private ComplaintsApiPush complaintsApiPush;

    private ComplaintsDao dao;
    private AppDatabase appDatabase;
    private AppCompatActivity activity;

    public interface ComplaintsApiPush {
        void onSuccess(String response);
        void onError(String error);
    }

    public void setComplaintsApiPush(ComplaintsApiPush listener) {
        this.complaintsApiPush = listener;
    }

    public ImportComplaints(AppCompatActivity activity) {
        // Initialize the Room database and DAO
        appDatabase = AppDatabase.getDatabase(activity);
        dao = appDatabase.complaintsDao();  // Assuming the DAO method is named profileDao()
        this.activity = activity;
        client = HttpClientBuilder.create().build();
    }

    private static class FetchComplaintsAsyncTask extends AsyncTask<Void, Void, List<Complaints>> {
        private final ImportComplaints importRecords;

        public FetchComplaintsAsyncTask(ImportComplaints importRecords) {
            this.importRecords = importRecords;
        }

        @Override
        protected List<Complaints> doInBackground(Void... voids) {
            return importRecords.appDatabase.complaintsDao().sync();
        }

        @Override
        protected void onPostExecute(List<Complaints> dailyConditions) {
            try {
                importRecords.onComplaintsFetched(dailyConditions);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void fetchComplaintsAndPost() {
        new FetchComplaintsAsyncTask(this).execute();
    }

    // Callback method when MomProfile records are fetched
    private void onComplaintsFetched(List<Complaints> dailyConditions) throws JSONException {
        if (dailyConditions != null && !dailyConditions.isEmpty()) {
            data = new JSONArray();
            for (Complaints item : dailyConditions) {
                record = new JSONObject();
                record.put("redcap_repeat_instrument", "complaints");
                record.put("tel", item.getTel());
                record.put("redcap_repeat_instance", item.getRecord_id());
                record.put("record_id", item.getRecord_id());
                record.put("complaints_date", item.getComplaints_date());
                record.put("gen_hlth", item.getGen_hlth());
                record.put("complts", item.getComplts());
                record.put("providers_name", item.getProviders_name());
                record.put("response_txt", item.getResponse_txt());
                record.put("response_date", item.getResponse_date());

                data.put(record);
            }
            createAndPostData();
        }else {
            // Handle the case where momProfiles is empty or null
        }
    }

    private void createAndPostData() {
        params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", API_TOKEN));
        params.add(new BasicNameValuePair("content", "record"));
        params.add(new BasicNameValuePair("format", "json"));
        params.add(new BasicNameValuePair("type", "flat"));
        params.add(new BasicNameValuePair("data", data.toString()));

        post = new HttpPost(API_URL);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            post.setEntity(new UrlEncodedFormEntity(params));
            new ExecuteHttpPostAsyncTask(this).execute(post);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception...
        }
    }

    private static class ExecuteHttpPostAsyncTask extends AsyncTask<HttpPost, Void, Void> {
        private final ImportComplaints importRecords;

        public ExecuteHttpPostAsyncTask(ImportComplaints importRecords) {
            this.importRecords = importRecords;
        }

        @Override
        protected Void doInBackground(HttpPost... posts) {
            importRecords.executeHttpPost(posts[0]);
            return null;
        }
    }

    private void executeHttpPost(HttpPost post) {
        resp = null;
        result = new StringBuffer(); // Initialize the result object

        try {
            resp = client.execute(post);

            if (resp != null) {
                respCode = resp.getStatusLine().getStatusCode();

                reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.d("ImportComplaint", "Complaint Code: " + respCode);
                Log.d("ImportComplaint", "Complaint Result: " + result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception...
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (complaintsApiPush != null) {
            // Notify the listener about the result
            if (respCode == HttpStatus.SC_OK) {
                complaintsApiPush.onSuccess(result.toString());
            } else {
                complaintsApiPush.onError("HTTP response code: " + respCode);
            }
        }
    }


}
