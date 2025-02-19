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
import com.khrc.caresupport.Dao.ProfileDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.MomProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExportProfile {

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
    private RedcapApiPush redcapApiPushListener;

    private ProfileDao dao;
    private AppDatabase appDatabase;
    private Context context;

    public interface RedcapApiPush {
        void onSuccess(String response);
        void onError(String error);
    }

    public void setRedcapApiPush(RedcapApiPush listener) {
        this.redcapApiPushListener = listener;
    }

    public ExportProfile(Context context) {
        // Initialize the Room database and DAO
        this.context = context;
        appDatabase = AppDatabase.getDatabase(context);
        dao = appDatabase.profileDao();  // Assuming the DAO method is named profileDao()
        client = HttpClientBuilder.create().build();
    }

    private static class FetchMomProfilesAsyncTask extends AsyncTask<Void, Void, List<MomProfile>> {
        private final ExportProfile importRecords;

        public FetchMomProfilesAsyncTask(ExportProfile importRecords) {
            this.importRecords = importRecords;
        }

        @Override
        protected List<MomProfile> doInBackground(Void... voids) {
            return importRecords.appDatabase.profileDao().sync();
        }

        @Override
        protected void onPostExecute(List<MomProfile> momProfiles) {
            try {
                importRecords.onMomProfilesFetched(momProfiles);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void fetchMomProfilesAndPost() {
        new FetchMomProfilesAsyncTask(this).execute();
    }

    // Callback method when MomProfile records are fetched
    private void onMomProfilesFetched(List<MomProfile> momProfiles) throws JSONException {
        if (momProfiles != null && !momProfiles.isEmpty()) {
            data = new JSONArray();
            for (MomProfile momProfile : momProfiles) {
                System.out.println("MomProfile: " + momProfile.toString());
                record = new JSONObject();
                record.put("tel", momProfile.getTel());
                record.put("doe", momProfile.getDoe());
                record.put("tels", momProfile.getTels());
                record.put("hfac", momProfile.getHfac());
                record.put("doi", momProfile.getDoi());
                record.put("nhisno", momProfile.getNhisno());
                record.put("mothn", momProfile.getMothn());
                record.put("community", momProfile.getCommunity());
                record.put("addr", momProfile.getAddr());
                record.put("lma", momProfile.getLma());
                record.put("dis", momProfile.getDis());
                record.put("mstatus", momProfile.getMstatus());
                record.put("edul", momProfile.getEdul());
                record.put("occu", momProfile.getOccu());
//                record.put("eduls", momProfile.getEduls());
//                record.put("occus", momProfile.getOccus());
                record.put("g6pd", momProfile.getG6pd());
                record.put("nos", momProfile.getNos());
                record.put("dob", momProfile.getDob());
                record.put("addrs", momProfile.getAddrs());
                record.put("lmas", momProfile.getLmas());
                record.put("diss", momProfile.getDiss());
                record.put("contactn", momProfile.getContactn());
                record.put("contele", momProfile.getContele());
                record.put("pin", momProfile.getPin());

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
        private final ExportProfile importRecords;

        public ExecuteHttpPostAsyncTask(ExportProfile importRecords) {
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

        if (redcapApiPushListener != null) {
            // Notify the listener about the result
            if (respCode == HttpStatus.SC_OK) {
                redcapApiPushListener.onSuccess(result.toString());
            } else {
                redcapApiPushListener.onError("HTTP response code: " + respCode);
            }
        }
    }


}
