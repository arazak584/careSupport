package com.khrc.caresupport.Client.redcapexport;

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
import com.khrc.caresupport.Dao.PregnancyDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.Pregnancy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ImportPregnancy {

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
    private PregnancyApiPush pregnancyApiPush;

    private PregnancyDao dao;
    private AppDatabase appDatabase;
    private AppCompatActivity activity;

    public interface PregnancyApiPush {
        void onSuccess(String response);
        void onError(String error);
    }

    public void setPregnancyApiPush(PregnancyApiPush listener) {
        this.pregnancyApiPush = listener;
    }

    public ImportPregnancy(AppCompatActivity activity) {
        // Initialize the Room database and DAO
        appDatabase = AppDatabase.getDatabase(activity);
        dao = appDatabase.pregnancyDao();  // Assuming the DAO method is named profileDao()
        this.activity = activity;
        client = HttpClientBuilder.create().build();
    }

    private static class FetchPregnancyAsyncTask extends AsyncTask<Void, Void, List<Pregnancy>> {
        private final ImportPregnancy importRecords;

        public FetchPregnancyAsyncTask(ImportPregnancy importRecords) {
            this.importRecords = importRecords;
        }

        @Override
        protected List<Pregnancy> doInBackground(Void... voids) {
            return importRecords.appDatabase.pregnancyDao().sync();
        }

        @Override
        protected void onPostExecute(List<Pregnancy> pregnancies) {
            try {
                importRecords.onPregnancyFetched(pregnancies);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void fetchPregnancyAndPost() {
        new FetchPregnancyAsyncTask(this).execute();
    }

    // Callback method when MomProfile records are fetched
    private void onPregnancyFetched(List<Pregnancy> pregnancies) throws JSONException {
        if (pregnancies != null && !pregnancies.isEmpty()) {
            data = new JSONArray();
            for (Pregnancy pregnancy : pregnancies) {

                record = new JSONObject();
                record.put("tel", pregnancy.getTel());
                record.put("insertdate", pregnancy.getInsertdate());
                record.put("first_ga_date", pregnancy.getFirst_ga_date());
                record.put("first_ga_wks", pregnancy.getFirst_ga_wks());
                record.put("edd", pregnancy.getEdd());
                record.put("next_anc_schedule_date", pregnancy.getNext_anc_schedule_date());
                record.put("planned_anc_facility", pregnancy.getPlanned_anc_facility());
                record.put("planned_delivery_place", pregnancy.getPlanned_delivery_place());
                record.put("outcome_date", pregnancy.getOutcome_date());

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
        private final ImportPregnancy importRecords;

        public ExecuteHttpPostAsyncTask(ImportPregnancy importRecords) {
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

                Log.d("ImportRecords", "respCode: " + respCode);
                Log.d("ImportRecords", "result: " + result.toString());
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

        if (pregnancyApiPush != null) {
            // Notify the listener about the result
            if (respCode == HttpStatus.SC_OK) {
                pregnancyApiPush.onSuccess(result.toString());
            } else {
                pregnancyApiPush.onError("HTTP response code: " + respCode);
            }
        }
    }


}
