package com.khrc.caresupport.Client.redcapexport;

import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.content.Context;
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
import com.khrc.caresupport.Dao.ObstericDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.Obsteric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExportObsteric {

    private Context context;

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
    private ObstericApiPush obstericApiPush;

    private ObstericDao dao;
    private AppDatabase appDatabase;
    private AppCompatActivity activity;

    public interface ObstericApiPush {
        void onSuccess(String response);
        void onError(String error);
    }

    public void setObstericApiPush(ObstericApiPush listener) {
        this.obstericApiPush = listener;
    }

//    public ImportObsteric(AppCompatActivity activity) {
//        // Initialize the Room database and DAO
//        appDatabase = AppDatabase.getDatabase(activity);
//        dao = appDatabase.obstericDao();  // Assuming the DAO method is named profileDao()
//        this.activity = activity;
//        client = HttpClientBuilder.create().build();
//    }

    public ExportObsteric(Context context) {
        this.context = context;
        appDatabase = AppDatabase.getDatabase(context); // Use context instead of activity
        dao = appDatabase.obstericDao();
        client = HttpClientBuilder.create().build();
    }


    private static class FetchHistoryAsyncTask extends AsyncTask<Void, Void, List<Obsteric>> {
        private final ExportObsteric importRecords;

        public FetchHistoryAsyncTask(ExportObsteric importRecords) {
            this.importRecords = importRecords;
        }

        @Override
        protected List<Obsteric> doInBackground(Void... voids) {
            return importRecords.appDatabase.obstericDao().sync();
        }

        @Override
        protected void onPostExecute(List<Obsteric> obsterics) {
            try {
                importRecords.onObstericFetched(obsterics);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void fetchObstericAndPost() {
        new FetchHistoryAsyncTask(this).execute();
    }

    // Callback method when MomProfile records are fetched
    private void onObstericFetched(List<Obsteric> obsterics) throws JSONException {
        if (obsterics != null && !obsterics.isEmpty()) {
            data = new JSONArray();
            for (Obsteric obsteric : obsterics) {

                record = new JSONObject();
                record.put("tel", obsteric.getTel());
                record.put("parity", obsteric.getParity());
                record.put("gravidity", obsteric.getGravidity());
                record.put("spontaneous_abortions", obsteric.getSpontaneous_abortions());
                record.put("induced_abortions", obsteric.getInduced_abortions());

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
        private final ExportObsteric importRecords;

        public ExecuteHttpPostAsyncTask(ExportObsteric importRecords) {
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

        if (obstericApiPush != null) {
            // Notify the listener about the result
            if (respCode == HttpStatus.SC_OK) {
                obstericApiPush.onSuccess(result.toString());
            } else {
                obstericApiPush.onError("HTTP response code: " + respCode);
            }
        }
    }


}
