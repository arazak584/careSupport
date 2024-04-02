package com.khrc.caresupport.importredcap;

import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.khrc.caresupport.Dao.MedHistoryDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.MedHistory;

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

public class HistoryApi {


    private static final String TEL = "";
    private MedHistoryDao dao;
    AppDatabase appDatabase;

    public interface HistoryApiCallback {
        void onSuccess(List<JsonHistory> result);
        void onError(String error);
    }

    public HistoryApi(Context context) {
        // Initialize the Room database and DAO
        appDatabase = AppDatabase.getDatabase(context);
        dao = appDatabase.medHistoryDao();  // Assuming the DAO method is named profileDao()
    }

    public void loadAndInsertHistoryData(HistoryApiCallback callback, Context context) {
        new LoadAndInsertDataTask(callback, context).execute();
    }

    private class LoadAndInsertDataTask extends AsyncTask<Void, Void, List<JsonHistory>> {
        private final HistoryApiCallback callback;
        private final Context context;

        public LoadAndInsertDataTask(HistoryApiCallback callback, Context context) {
            this.callback = callback;
            this.context = context;
        }

        @Override
        protected List<JsonHistory> doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Construct the request body
                String requestBody = "token=" + API_TOKEN +
                        "&content=record&format=json&type=flat";
                Log.d("HISTORYAPI", "HISTORYAPI" + requestBody);

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
                    List<JsonHistory> items = parseJsonResponse(response.toString());

                    // Insert the items into the Room database
                    if (items != null && !items.isEmpty()) {
                        insertHistoryIntoDatabase(items);
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
        protected void onPostExecute(List<JsonHistory> result) {
            if (result != null) {
                callback.onSuccess(result);
            }
        }

        private void insertHistoryIntoDatabase(List<JsonHistory> items) {
            // Convert json_profile objects to MomProfileEntity objects
            List<MedHistory> hist = convertHistoryEntities(items);
            // Insert the MomProfileEntity objects into the Room database
            dao.insert(hist);

            int insertedRecords = items.size();
            Log.d("History", "History Successfully inserted " + insertedRecords + " records into the database");
        }

        private List<MedHistory> convertHistoryEntities(List<JsonHistory> items) {
            // Convert json_profile objects to MomProfileEntity objects
            List<MedHistory> medHistories = new ArrayList<>();

            for (JsonHistory item : items) {
                MedHistory medHistory = new MedHistory();
                medHistory.setTel(item.getTel());
                medHistory.setMh1(item.getMh1());
                medHistory.setMh2(item.getMh2());
                medHistory.setMh3(item.getMh3());
                medHistory.setMh4(item.getMh4());
                medHistory.setMh5(item.getMh5());
                medHistory.setMh6(item.getMh6());
                medHistory.setMh7(item.getMh7());
                medHistory.setMh8(item.getMh8());
                medHistory.setMh9(item.getMh9());
                medHistory.setMh10(item.getMh10());
                medHistory.setMh11(item.getMh11());
                medHistory.setMh12(item.getMh12());
                medHistory.setMh13(item.getMh13());
                medHistory.setOth(item.getOth());
                medHistory.setFh1(item.getFh1());
                medHistory.setFh2(item.getFh2());
                medHistory.setFh3(item.getFh3());
                medHistory.setFh4(item.getFh4());
                medHistory.setFh5(item.getFh5());
                medHistory.setFh6(item.getFh6());
                medHistory.setFh7(item.getFh7());
                medHistory.setFh8(item.getFh8());
                medHistory.setOth_2(item.getOth_2());

                medHistories.add(medHistory);
            }

            return medHistories;
        }

    }


    private List<JsonHistory> parseJsonResponse(String jsonResponse) {
        List<JsonHistory> jsonItems = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            Set<String> uniqueTels = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItemObject = jsonArray.getJSONObject(i);

                JsonHistory item = new JsonHistory();
                item.setTel(jsonItemObject.optString("tel", null));
                item.setMh1(jsonItemObject.optInt("mh1", 0));
                item.setMh2(jsonItemObject.optInt("mh2", 0));
                item.setMh3(jsonItemObject.optInt("mh3", 0));
                item.setMh4(jsonItemObject.optInt("mh4", 0));
                item.setMh5(jsonItemObject.optInt("mh5", 0));
                item.setMh6(jsonItemObject.optInt("mh6", 0));
                item.setMh7(jsonItemObject.optInt("mh7", 0));
                item.setMh8(jsonItemObject.optInt("mh8", 0));
                item.setMh9(jsonItemObject.optInt("mh9", 0));
                item.setMh10(jsonItemObject.optInt("mh10", 0));
                item.setMh11(jsonItemObject.optInt("mh11", 0));
                item.setMh12(jsonItemObject.optInt("mh12", 0));
                item.setMh13(jsonItemObject.optInt("mh13", 0));
                item.setOth(jsonItemObject.optString("oth", null));
                item.setFh1(jsonItemObject.optInt("fh1", 0));
                item.setFh2(jsonItemObject.optInt("fh2", 0));
                item.setFh3(jsonItemObject.optInt("fh3", 0));
                item.setFh4(jsonItemObject.optInt("fh4", 0));
                item.setFh5(jsonItemObject.optInt("fh5", 0));
                item.setFh6(jsonItemObject.optInt("fh6", 0));
                item.setFh7(jsonItemObject.optInt("fh7", 0));
                item.setFh8(jsonItemObject.optInt("fh8", 0));
                item.setOth_2(jsonItemObject.optString("oth_2", null));


//                jsonItems.add(item);
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
