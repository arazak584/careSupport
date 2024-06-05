package com.khrc.caresupport.Client.redcapimport;

import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.khrc.caresupport.Dao.PregnancyDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.Pregnancy;
import com.khrc.caresupport.importredcap.JsonPregnancy;

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

public class ClientPregnancyApi {


    private static final String TEL = "";
    private PregnancyDao dao;
    AppDatabase appDatabase;

    public interface PregnancyApiCallback {
        void onSuccess(List<JsonPregnancy> result);
        void onError(String error);
    }

    public ClientPregnancyApi(Context context) {
        // Initialize the Room database and DAO
        appDatabase = AppDatabase.getDatabase(context);
        dao = appDatabase.pregnancyDao();  // Assuming the DAO method is named profileDao()
    }

    public void loadAndInsertPregnancyData(String phoneNumber, PregnancyApiCallback callback, Context context) {
        new LoadAndInsertDataTask(callback, context, phoneNumber).execute();
    }

    private class LoadAndInsertDataTask extends AsyncTask<Void, Void, List<JsonPregnancy>> {
        private final PregnancyApiCallback callback;
        private final Context context;
        private final String phoneNumber;

        public LoadAndInsertDataTask(PregnancyApiCallback callback, Context context, String phoneNumber) {
            this.callback = callback;
            this.context = context;
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected List<JsonPregnancy> doInBackground(Void... voids) {
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
                    List<JsonPregnancy> items = parseJsonResponse(response.toString());

                    // Insert the items into the Room database
                    if (items != null && !items.isEmpty()) {
                        inserPregnancyIntoDatabase(items);
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
        protected void onPostExecute(List<JsonPregnancy> result) {
            if (result != null) {
                callback.onSuccess(result);
            }
        }

        private void inserPregnancyIntoDatabase(List<JsonPregnancy> items) {
            // Convert json_profile objects to MomProfileEntity objects
            List<Pregnancy> pregnancies = convertToMomProfileEntities(items);
            // Insert the MomProfileEntity objects into the Room database
            dao.insert(pregnancies);

            int insertedRecords = items.size();
            Log.d("Pregnancy", "Pregnancy Successfully inserted " + insertedRecords + " records into the database");
        }

        private List<Pregnancy> convertToMomProfileEntities(List<JsonPregnancy> items) {
            // Convert json_profile objects to MomProfileEntity objects
            List<Pregnancy> pregnancies = new ArrayList<>();

            for (JsonPregnancy item : items) {
                Pregnancy pregnancy = new Pregnancy();
                pregnancy.setTel(item.getTel());
                pregnancy.setInsertdate(item.getInsertdate());
                pregnancy.setFirst_ga_date(item.getFirst_ga_date());
                pregnancy.setFirst_ga_wks(item.getFirst_ga_wks());
                pregnancy.setEdd(item.getEdd());
                pregnancy.setNext_anc_schedule_date(item.getNext_anc_schedule_date());
                pregnancy.setPlanned_anc_facility(item.getPlanned_anc_facility());
                pregnancy.setPlanned_delivery_place(item.getPlanned_delivery_place());
                pregnancy.setOutcome_date(item.getOutcome_date());
                pregnancy.setPreg_outcome(item.getPreg_outcome());

                pregnancies.add(pregnancy);
            }

            return pregnancies;
        }

    }


    private List<JsonPregnancy> parseJsonResponse(String jsonResponse) {
        Set<JsonPregnancy> jsonItems = new HashSet<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            Set<String> uniqueTels = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItemObject = jsonArray.getJSONObject(i);

                JsonPregnancy item = new JsonPregnancy();
                item.setTel(jsonItemObject.optString("tel", null));
                item.setInsertdate(jsonItemObject.optString("insertdate", null));
                item.setFirst_ga_date(jsonItemObject.optString("first_ga_date", null));
                item.setFirst_ga_wks(jsonItemObject.optString("first_ga_wks", null));
                item.setEdd(jsonItemObject.optString("edd", null));
                item.setNext_anc_schedule_date(jsonItemObject.optString("next_anc_schedule_date", null));
                item.setPlanned_anc_facility(jsonItemObject.optString("planned_anc_facility", null));
                item.setPlanned_delivery_place(jsonItemObject.optString("planned_delivery_place", null));
                item.setOutcome_date(jsonItemObject.optString("outcome_date", null));
                item.setPreg_outcome(jsonItemObject.optInt("preg_outcome", 0));

                //jsonItems.add(item);
//                if (uniqueTels.add(item.getTel())) {
//                    jsonItems.add(item);
//                }
                jsonItems.add(item);
            }
        } catch (JSONException e) {
            Log.e("RedcapApiClient", "Error parsing JSON response", e);
        }
        return new ArrayList<>(jsonItems);
        //return jsonItems;
    }


}
