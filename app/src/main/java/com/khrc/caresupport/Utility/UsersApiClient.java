package com.khrc.caresupport.Utility;

import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.khrc.caresupport.Dao.UsersDao;
import com.khrc.caresupport.entity.Users;

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

public class UsersApiClient {

    private UsersDao dao;
    AppDatabase appDatabase;

    public interface RedcapApiCallback {
        void onSuccess(List<JsonProfile> result);
        void onError(String error);
    }

    public UsersApiClient(Context context) {
        // Initialize the Room database and DAO
        appDatabase = AppDatabase.getDatabase(context);
        dao = appDatabase.usersDao();  // Assuming the DAO method is named profileDao()
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
            List<Users> users = convertUserEntities(profiles);
            // Insert the MomProfileEntity objects into the Room database
            dao.insert(users);

            int insertedRecords = users.size();
            Log.d("Profile", "Successfully inserted " + insertedRecords + " records into the database");
        }

        private List<Users> convertUserEntities(List<JsonProfile> profiles) {
            // Convert json_profile objects to MomProfileEntity objects
            List<Users> users = new ArrayList<>();

            for (JsonProfile profile : profiles) {
                Users user = new Users();
                // Map fields from json_profile to MomProfileEntity
                user.setTel(profile.getTel());
                user.setHfac(profile.getHfac());
                user.setPin(profile.getPin());

                users.add(user);
            }

            return users;
        }

    }


    private List<JsonProfile> parseJsonResponse(String jsonResponse) {
        List<JsonProfile> jsonProfiles = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            Set<String> uniqueTels = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonProfileObject = jsonArray.getJSONObject(i);

                JsonProfile profile = new JsonProfile();
                profile.setTel(jsonProfileObject.optString("tel", null));
                profile.setHfac(jsonProfileObject.optString("hfac", null));
                profile.setPin(jsonProfileObject.optString("pin", null));

                if (uniqueTels.add(profile.getTel())) {
                    jsonProfiles.add(profile);
                }
            }
        } catch (JSONException e) {
            Log.e("RedcapApiClient", "Error parsing JSON response", e);
        }

        return jsonProfiles;
    }


}
