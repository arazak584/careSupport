package com.khrc.caresupport.Client.redcapimport;


import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.khrc.caresupport.Dao.ComplaintsDao;
import com.khrc.caresupport.Dao.DailyConditionDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.DailyCondition;
import com.khrc.caresupport.importredcap.JsonComplaints;

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

public class ClientComplaintsApi {


    private static final String TEL = "";
    private DailyConditionDao dao;
    AppDatabase appDatabase;

    public interface CompApiCallback {
        void onSuccess(List<JsonComplaints> result);
        void onError(String error);
    }

    public ClientComplaintsApi(Context context) {
        // Initialize the Room database and DAO
        appDatabase = AppDatabase.getDatabase(context);
        dao = appDatabase.dailyConditionDao();  // Assuming the DAO method is named profileDao()
    }

    public void loadAndInsertCompData(String phoneNumber, CompApiCallback callback, Context context) {
        new LoadAndInsertDataTask(callback, context, phoneNumber).execute();
    }

    private class LoadAndInsertDataTask extends AsyncTask<Void, Void, List<JsonComplaints>> {
        private final CompApiCallback callback;
        private final Context context;
        private final String phoneNumber;

        public LoadAndInsertDataTask(CompApiCallback callback, Context context, String phoneNumber) {
            this.callback = callback;
            this.context = context;
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected List<JsonComplaints> doInBackground(Void... voids) {
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
                    List<JsonComplaints> items = parseJsonResponse(response.toString());

                    // Insert the items into the Room database
                    if (items != null && !items.isEmpty()) {
                        inserCompIntoDatabase(items);
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
        protected void onPostExecute(List<JsonComplaints> result) {
            if (result != null) {
                callback.onSuccess(result);
            }
        }

        private void inserCompIntoDatabase(List<JsonComplaints> items) {
            // Convert json_profile objects to MomProfileEntity objects
            List<DailyCondition> dailyConditions = convertToCompEntities(items);
            // Insert the MomProfileEntity objects into the Room database
            dao.insert(dailyConditions);

            int insertedRecords = dailyConditions.size();
            Log.d("ComplaintsApi", "ComplaintsApi Successfully inserted " + insertedRecords + " records into the database");
            for (DailyCondition dailyCondition : dailyConditions) {
                Log.d("ComplaintsApi", "ComplaintsApi Successfully inserted record with id " + dailyCondition.getTel() + " into the database");
            }

            if (insertedRecords < 1 ){
                Toast.makeText(context, "No Feedback Yet", Toast.LENGTH_SHORT).show();
            }
        }

        private List<DailyCondition> convertToCompEntities(List<JsonComplaints> items) {
            // Convert json_profile objects to MomProfileEntity objects
            List<DailyCondition> dailyConditions = new ArrayList<>();

            for (JsonComplaints item : items) {
                DailyCondition dailyCondition = new DailyCondition();
                dailyCondition.setRecord_id(item.getRecord_id());
                dailyCondition.setTel(item.getTel());
                dailyCondition.setComplaints_date(item.getComplaints_date());
                dailyCondition.setComplts(item.getComplts());
                dailyCondition.setCpl_status(item.getCpl_status());

                dailyConditions.add(dailyCondition);
            }

            return dailyConditions;
        }

    }


    private List<JsonComplaints> parseJsonResponse(String jsonResponse) {
        List<JsonComplaints> jsonItems = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            Set<Integer> uniqueTels = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItemObject = jsonArray.getJSONObject(i);

                JsonComplaints item = new JsonComplaints();
                item.setRecord_id(jsonItemObject.optInt("record_id", 0));
                item.setTel(jsonItemObject.optString("tel", null));
                item.setComplaints_date(jsonItemObject.optString("complaints_date", null));
                item.setGen_hlth(jsonItemObject.optInt("gen_hlth", 0));
                item.setComplts(jsonItemObject.optString("complts", null));
                item.setCpl_status(jsonItemObject.optInt("cpl_status", 0));

                //jsonItems.add(item);
                if (uniqueTels.add(item.getRecord_id()) && (item.getComplts() != null && !item.getComplts().equals(""))) {
                    jsonItems.add(item);
                }
            }
        } catch (JSONException e) {
            Log.e("ComplaintApiClient", "Error parsing JSON response", e);
        }

        return jsonItems;
    }


}
