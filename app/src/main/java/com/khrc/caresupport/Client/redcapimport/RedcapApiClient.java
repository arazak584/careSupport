package com.khrc.caresupport.Client.redcapimport;

import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.khrc.caresupport.Dao.ProfileDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.MomProfile;
import com.khrc.caresupport.importredcap.JsonProfile;

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

public class RedcapApiClient {

    private ProfileDao dao;
    AppDatabase appDatabase;

    public interface RedcapApiCallback {
        void onSuccess(List<JsonProfile> result);
        void onError(String error);
    }

    public RedcapApiClient(Context context) {
        // Initialize the Room database and DAO
        appDatabase = AppDatabase.getDatabase(context);
        dao = appDatabase.profileDao();  // Assuming the DAO method is named profileDao()
    }

    public void loadAndInsertProfileData(String phoneNumber, RedcapApiCallback callback, Context context) {
        new LoadAndInsertDataTask(callback, context, phoneNumber).execute();
    }

    private class LoadAndInsertDataTask extends AsyncTask<Void, Void, List<JsonProfile>> {
        private final RedcapApiCallback callback;
        private final Context context;
        private final String phoneNumber;

        public LoadAndInsertDataTask(RedcapApiCallback callback, Context context, String phoneNumber) {
            this.callback = callback;
            this.context = context;
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected List<JsonProfile> doInBackground(Void... voids) {
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

//                String requestBody = "token=" + API_TOKEN +
//                        "&content=record&format=json&type=flat" +
//                        "&filterLogic=[{\"field_name\":\"tel\",\"comparison\":\"=\",\"value\":\"" + phoneNumber + "\"}]";


                Log.d("MAINAPI", "MAINAPI" + requestBody);

                OutputStream os = urlConnection.getOutputStream();
                os.write(requestBody.getBytes());
                os.flush();
                os.close();

                int responseCode = urlConnection.getResponseCode();
                Log.d("MAINAPI", "MAINAPI Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line).append("\n");
                    }

                    // Parse the JSON response
                    List<JsonProfile> profiles = parseJsonResponse(response.toString());

                    // Insert the profiles into the Room database
                    if (profiles != null && !profiles.isEmpty()) {
                        insertProfilesIntoDatabase(profiles);
                    }

                    return profiles;
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
        protected void onPostExecute(List<JsonProfile> result) {
            super.onPostExecute(result);

            if (callback != null && context != null) {
                if (result != null) {
                    // UI operations should be done on the main thread
                    ((Activity) context).runOnUiThread(() -> callback.onSuccess(result));
                } else {
                    ((Activity) context).runOnUiThread(() -> callback.onError("Error message"));
                }
            }
        }



        private void insertProfilesIntoDatabase(List<JsonProfile> profiles) {
            // Convert json_profile objects to MomProfileEntity objects
            List<MomProfile> momProfiles = convertToMomProfileEntities(profiles);
            // Insert the MomProfileEntity objects into the Room database
            dao.insert(momProfiles);

            int insertedRecords = momProfiles.size();
            Log.d("Profile", "Successfully inserted " + insertedRecords + " records into the database");
        }

        private List<MomProfile> convertToMomProfileEntities(List<JsonProfile> profiles) {
            // Convert json_profile objects to MomProfileEntity objects
            List<MomProfile> momProfiles = new ArrayList<>();

            for (JsonProfile profile : profiles) {
                MomProfile momProfile = new MomProfile();
                // Map fields from json_profile to MomProfileEntity

                momProfile.setTel(profile.getTel());
                momProfile.setTels(profile.getTels());
                momProfile.setHfac(profile.getHfac());
                momProfile.setDoi(profile.getDoi());
                momProfile.setNhisno(profile.getNhisno());
                momProfile.setMothn(profile.getMothn());
                momProfile.setCommunity(profile.getCommunity());
                momProfile.setAddr(profile.getAddr());
                momProfile.setLma(profile.getLma());
                momProfile.setDis(profile.getDis());
                momProfile.setMstatus(profile.getMstatus());
                momProfile.setEdul(profile.getEdul());
                momProfile.setOccu(profile.getOccu());
//                momProfile.setEduls(profile.getEduls());
//                momProfile.setOccus(profile.getOccus());
                momProfile.setG6pd(profile.getG6pd());
                momProfile.setNos(profile.getNos());
                momProfile.setDob(profile.getDob());
                momProfile.setDoe(profile.getDoe());
                momProfile.setAddrs(profile.getAddrs());
                momProfile.setLmas(profile.getLmas());
                momProfile.setDiss(profile.getDiss());
                momProfile.setContactn(profile.getContactn());
                momProfile.setContele(profile.getContele());
                momProfile.setPin(profile.getPin());

                momProfiles.add(momProfile);
            }

            return momProfiles;
        }

    }


    private List<JsonProfile> parseJsonResponse(String jsonResponse) {
        List<JsonProfile> userProfiles = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            Set<String> uniqueTels = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonProfileObject = jsonArray.getJSONObject(i);

                JsonProfile profile = new JsonProfile();
                profile.setDoe(jsonProfileObject.optString("doe", null));
                profile.setTel(jsonProfileObject.optString("tel", null));
                profile.setTels(jsonProfileObject.optString("tels", null));
                profile.setHfac(jsonProfileObject.optString("hfac", null));
                profile.setDoi(jsonProfileObject.optString("doi", null));
                profile.setNhisno(jsonProfileObject.optString("nhisno", null));
                profile.setMothn(jsonProfileObject.optString("mothn", null));
                profile.setCommunity(jsonProfileObject.optString("community", null));
                profile.setAddr(jsonProfileObject.optString("addr", null));
                profile.setLma(jsonProfileObject.optString("lma", null));
                profile.setDis(jsonProfileObject.optString("dis", null));
                profile.setMstatus(jsonProfileObject.optInt("mstatus", 0));
                profile.setEdul(jsonProfileObject.optInt("edul", 0));
                profile.setOccu(jsonProfileObject.optInt("occu", 0));
                profile.setG6pd(jsonProfileObject.optString("g6pd", null));
                profile.setNos(jsonProfileObject.optString("nos", null));
                profile.setDob(jsonProfileObject.optString("dob", null));
                profile.setAddrs(jsonProfileObject.optString("addrs", null));
                profile.setLmas(jsonProfileObject.optString("lmas", null));
                profile.setDiss(jsonProfileObject.optString("diss", null));
                profile.setContactn(jsonProfileObject.optString("contactn", null));
                profile.setContele(jsonProfileObject.optString("contele", null));
                profile.setPin(jsonProfileObject.optString("pin", null));

                if (uniqueTels.add(profile.getTel())) {
                    userProfiles.add(profile);
                }
            }
        } catch (JSONException e) {
            Log.e("RedcapApiClient", "Error parsing JSON response", e);
        }

        return userProfiles;
    }


}
