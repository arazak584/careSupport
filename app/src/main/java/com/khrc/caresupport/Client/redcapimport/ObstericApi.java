package com.khrc.caresupport.Client.redcapimport;

import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.khrc.caresupport.Dao.ObstericDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.Obsteric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ObstericApi {


    private static final String TEL = "";
    private ObstericDao dao;
    AppDatabase appDatabase;

    public interface ObstericApiCallback {
        void onSuccess(List<JsonObsteric> result);
        void onError(String error);
    }

    public ObstericApi(Context context) {
        // Initialize the Room database and DAO
        appDatabase = AppDatabase.getDatabase(context);
        dao = appDatabase.obstericDao();  // Assuming the DAO method is named profileDao()
    }

    public void loadAndInsertObstericData(String phoneNumber, ObstericApiCallback callback, Context context) {
        new LoadAndInsertDataTask(callback, context, phoneNumber).execute();
    }

    private class LoadAndInsertDataTask extends AsyncTask<Void, Void, List<JsonObsteric>> {
        private final ObstericApiCallback callback;
        private final Context context;
        private final String phoneNumber;

        public LoadAndInsertDataTask(ObstericApiCallback callback, Context context, String phoneNumber) {
            this.callback = callback;
            this.context = context;
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected List<JsonObsteric> doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Construct the request body
                String requestBody = "token=" + API_TOKEN +
                        "&content=record&format=json&type=flat&records=" + phoneNumber;
                Log.d("API", "API" + requestBody);

                OutputStream os = urlConnection.getOutputStream();
                os.write(requestBody.getBytes());
                os.flush();
                os.close();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line).append("\n");
                    }

                    // Parse the JSON response
                    List<JsonObsteric> items = parseJsonResponse(response.toString());

                    // Insert the items into the Room database
                    if (items != null && !items.isEmpty()) {
                        inserObstericIntoDatabase(items);
                    }

                    return items;
                } else {
                    callback.onError("Error: " + responseCode);
                    return null;
                }
            } catch (IOException e) {
                callback.onError("Error making API request");
                Log.e("RedcapApiClient", "Error making API request", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(List<JsonObsteric> result) {
            if (result != null) {
                callback.onSuccess(result);
            }
        }

        private void inserObstericIntoDatabase(List<JsonObsteric> items) {
            // Convert json_profile objects to MomProfileEntity objects
            List<Obsteric> obsterics = convertToObstericEntities(items);
            // Insert the MomProfileEntity objects into the Room database
            dao.insert(obsterics);
        }

        private List<Obsteric> convertToObstericEntities(List<JsonObsteric> items) {
            // Convert json_profile objects to MomProfileEntity objects
            List<Obsteric> obsterics = new ArrayList<>();

            for (JsonObsteric item : items) {
                Obsteric obsteric = new Obsteric();
                obsteric.setTel(item.getTel());
                obsteric.setParity(item.getParity());
                obsteric.setGravidity(item.getGravidity());
                obsteric.setSpontaneous_abortions(item.getSpontaneous_abortions());
                obsteric.setInduced_abortions(item.getInduced_abortions());

                obsterics.add(obsteric);
            }

            return obsterics;
        }

    }


    private List<JsonObsteric> parseJsonResponse(String jsonResponse) {
        List<JsonObsteric> jsonItems = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            Set<String> uniqueTels = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItemObject = jsonArray.getJSONObject(i);

                JsonObsteric item = new JsonObsteric();
                item.setTel(jsonItemObject.optString("tel", null));
                item.setParity(jsonItemObject.optString("parity", null));
                item.setGravidity(jsonItemObject.optString("gravidity", null));
                item.setSpontaneous_abortions(jsonItemObject.optString("spontaneous_abortions", null));
                item.setInduced_abortions(jsonItemObject.optString("induced_abortions", null));

                //jsonItems.add(item);
                if (uniqueTels.add(item.getTel())) {
                    jsonItems.add(item);
                }
            }
        } catch (JSONException e) {
            Log.e("RedcapApiClient", "Error parsing JSON response", e);
        }

        return jsonItems;
    }


}
