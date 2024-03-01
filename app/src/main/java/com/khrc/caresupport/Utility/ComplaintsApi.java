package com.khrc.caresupport.Utility;


import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.khrc.caresupport.Dao.ComplaintsDao;
import com.khrc.caresupport.entity.Complaints;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComplaintsApi {


    private static final String TEL = "";
    private ComplaintsDao dao;
    AppDatabase appDatabase;

    public interface CompApiCallback {
        void onSuccess(List<JsonComplaints> result);
        void onError(String error);
    }

    public ComplaintsApi(Context context) {
        // Initialize the Room database and DAO
        appDatabase = AppDatabase.getDatabase(context);
        dao = appDatabase.complaintsDao();  // Assuming the DAO method is named profileDao()
    }

    public void loadAndInsertCompData(CompApiCallback callback, Context context) {
        new LoadAndInsertDataTask(callback, context).execute();
    }

    private class LoadAndInsertDataTask extends AsyncTask<Void, Void, List<JsonComplaints>> {
        private final CompApiCallback callback;
        private final Context context;

        public LoadAndInsertDataTask(CompApiCallback callback, Context context) {
            this.callback = callback;
            this.context = context;
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
                        "&content=record&format=json&type=flat" ;
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
            List<Complaints> dailyConditions = convertToCompEntities(items);
            // Insert the MomProfileEntity objects into the Room database
            dao.insert(dailyConditions);

            int insertedRecords = dailyConditions.size();
            Log.d("ComplaintsApi", "ComplaintsApi Successfully inserted " + insertedRecords + " records into the database");
            for (Complaints dailyCondition : dailyConditions) {
                Log.d("ComplaintsApi", "ComplaintsApi Successfully inserted record with id " + dailyCondition.getTel() + " into the database");
            }

            if (insertedRecords < 1 ){
                Toast.makeText(context, "No Feedback Yet", Toast.LENGTH_SHORT).show();
            }
        }

        private List<Complaints> convertToCompEntities(List<JsonComplaints> items) {
            // Convert json_profile objects to MomProfileEntity objects
            List<Complaints> dailyConditions = new ArrayList<>();

            for (JsonComplaints item : items) {
                Complaints dailyCondition = new Complaints();

                dailyCondition.setId(item.getId());
                dailyCondition.setRecord_id(item.getRecord_id());
                dailyCondition.setTel(item.getTel());
                dailyCondition.setComplaints_date(item.getComplaints_date());
                dailyCondition.setGen_hlth(item.getGen_hlth());
                dailyCondition.setComplts(item.getComplts());
                dailyCondition.setProviders_name(item.getProviders_name());
                dailyCondition.setResponse_txt(item.getResponse_txt());
                dailyCondition.setHfac(item.getHfac());
                dailyCondition.setMothn(item.getMothn());
                dailyCondition.setResponse_date(item.getResponse_date());

                dailyConditions.add(dailyCondition);
            }

            return dailyConditions;
        }

    }


    private List<JsonComplaints> parseJsonResponse(String jsonResponse) {
        List<JsonComplaints> jsonItems = new ArrayList<>();
        int addedRecordsCount = 0;  // Counter for added records

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            Set<String> uniqueTels = new HashSet<>();
            // Create a mapping for tel to name
            Map<String, String> telToNameMap = new HashMap<>();
            Map<String, String> telToFacMap = new HashMap<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItemObject = jsonArray.getJSONObject(i);
                String tel = jsonItemObject.optString("tel", "");
                String nm = jsonItemObject.optString("mothn", "");
                String fac = jsonItemObject.optString("hfac", "");

                // Populate the mapping
                if (!tel.isEmpty() && !nm.isEmpty()) {
                    telToNameMap.put(tel, nm);
                }
                if (!tel.isEmpty() && !fac.isEmpty()) {
                    telToFacMap.put(tel, fac);
                }
                JsonComplaints item = new JsonComplaints();
                String recordId = jsonItemObject.optString("record_id", "");

                if (tel != null && recordId != null) {
                    String concatenatedId = tel + "_" + recordId;
                    String nam = telToNameMap.get(tel);  // Get name based on tel
                    String hf = telToFacMap.get(tel);

                    item.setId(concatenatedId);
                    item.setRecord_id(jsonItemObject.optInt("record_id", 0));
                    item.setTel(tel);
                    item.setComplaints_date(jsonItemObject.optString("complaints_date", null));
                    item.setGen_hlth(jsonItemObject.optInt("gen_hlth", 0));
                    item.setComplts(jsonItemObject.optString("complts", null));
                    item.setProviders_name(jsonItemObject.optString("providers_name", null));
                    item.setResponse_txt(jsonItemObject.optString("response_txt", null));
                    item.setResponse_date(jsonItemObject.optString("response_date", null));
                    item.setMothn(nam);
                    item.setHfac(hf);

                    if (item.getComplts() != null && !item.getComplts().equals("")) {
                        jsonItems.add(item);
                        addedRecordsCount++;  // Increment the counter for added records
                        System.out.println("Added record with id: " + item.getMothn());
                    }
                }
            }

            System.out.println("Total added records count: " + addedRecordsCount);
        } catch (JSONException e) {
            Log.e("ComplaintApiClient", "Error parsing JSON response", e);
        }

        return jsonItems;
    }



}
