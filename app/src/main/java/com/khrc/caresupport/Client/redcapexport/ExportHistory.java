package com.khrc.caresupport.Client.redcapexport;

import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.khrc.caresupport.Dao.MedHistoryDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.MedHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExportHistory {

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
    private HistoryApiPush historyApiPush;

    private MedHistoryDao dao;
    private AppDatabase appDatabase;
    private Context context;

    public interface HistoryApiPush {
        void onSuccess(String response);
        void onError(String error);
    }

    public void setHistoryApiPush(HistoryApiPush listener) {
        this.historyApiPush = listener;
    }


    public ExportHistory(Context context) {
        this.context = context;
        appDatabase = AppDatabase.getDatabase(context); // Use context instead of activity
        dao = appDatabase.medHistoryDao();
        client = HttpClientBuilder.create().build();
    }

    private static class FetchHistoryAsyncTask extends AsyncTask<Void, Void, List<MedHistory>> {
        private final ExportHistory importRecords;

        public FetchHistoryAsyncTask(ExportHistory importRecords) {
            this.importRecords = importRecords;
        }

        @Override
        protected List<MedHistory> doInBackground(Void... voids) {
            return importRecords.appDatabase.medHistoryDao().sync();
        }

        @Override
        protected void onPostExecute(List<MedHistory> medHistorys) {
            try {
                importRecords.onHistoryFetched(medHistorys);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void fetchHistoryAndPost() {
        new FetchHistoryAsyncTask(this).execute();
    }

    // Callback method when MomProfile records are fetched
    private void onHistoryFetched(List<MedHistory> medHistorys) throws JSONException {
        if (medHistorys != null && !medHistorys.isEmpty()) {
            data = new JSONArray();
            for (MedHistory medHistory : medHistorys) {

                record = new JSONObject();
                record.put("tel", medHistory.getTel());
                record.put("mh1", medHistory.getMh1());
                record.put("mh2", medHistory.getMh2());
                record.put("mh3", medHistory.getMh3());
                record.put("mh4", medHistory.getMh4());
                record.put("mh5", medHistory.getMh5());
                record.put("mh6", medHistory.getMh6());
                record.put("mh7", medHistory.getMh7());
                record.put("mh8", medHistory.getMh8());
                record.put("mh9", medHistory.getMh9());
                record.put("mh10", medHistory.getMh10());
                record.put("mh11", medHistory.getMh11());
                record.put("mh12", medHistory.getMh12());
                record.put("mh13", medHistory.getMh13());
                record.put("oth", medHistory.getOth());
                record.put("fh1", medHistory.getFh1());
                record.put("fh2", medHistory.getFh2());
                record.put("fh3", medHistory.getFh3());
                record.put("fh4", medHistory.getFh4());
                record.put("fh5", medHistory.getFh5());
                record.put("fh6", medHistory.getFh6());
                record.put("fh7", medHistory.getFh7());
                record.put("fh8", medHistory.getFh8());
                record.put("oth_2", medHistory.getOth_2());

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
        private final ExportHistory importRecords;

        public ExecuteHttpPostAsyncTask(ExportHistory importRecords) {
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

        if (historyApiPush != null) {
            // Notify the listener about the result
            if (respCode == HttpStatus.SC_OK) {
                historyApiPush.onSuccess(result.toString());
            } else {
                historyApiPush.onError("HTTP response code: History " + respCode);
            }
        }
    }


}
