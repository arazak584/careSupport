package com.khrc.caresupport.importredcap;


import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.khrc.caresupport.Dao.ChatDao;
import com.khrc.caresupport.Dao.ComplaintsDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.ChatResponse;
import com.khrc.caresupport.entity.Complaints;

import org.jetbrains.annotations.NotNull;
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResponseApi {


    private static final String TEL = "";
    private ChatDao dao;
    AppDatabase appDatabase;

    public interface ChatApiCallback {
        void onSuccess(List<JsonChatresponse> result);
        void onError(String error);
    }

    public ResponseApi(Context context) {
        // Initialize the Room database and DAO
        appDatabase = AppDatabase.getDatabase(context);
        dao = appDatabase.chatDao();  // Assuming the DAO method is named profileDao()
    }

    public void loadAndInsertChatData(ChatApiCallback callback, Context context) {
        new LoadAndInsertDataTask(callback, context).execute();
    }

    private class LoadAndInsertDataTask extends AsyncTask<Void, Void, List<JsonChatresponse>> {
        private final ChatApiCallback callback;
        private final Context context;

        public LoadAndInsertDataTask(ChatApiCallback callback, Context context) {
            this.callback = callback;
            this.context = context;
        }

        @Override
        protected List<JsonChatresponse> doInBackground(Void... voids) {
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
                    List<JsonChatresponse> items = parseJsonResponse(response.toString());

                    // Insert the items into the Room database
                    if (items != null && !items.isEmpty()) {
                        insertChatIntoDatabase(items);
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
        protected void onPostExecute(List<JsonChatresponse> result) {
            if (result != null) {
                callback.onSuccess(result);
            }
        }

        private void insertChatIntoDatabase(List<JsonChatresponse> items) {
            // Convert json_profile objects to MomProfileEntity objects
            List<ChatResponse> chatResponses = convertToChatEntities(items);
            // Insert the MomProfileEntity objects into the Room database
            dao.insert(chatResponses);

            int insertedRecords = chatResponses.size();
            Log.d("ResponseApi", "ResponseApi Successfully inserted " + insertedRecords + " records into the database");
            for (ChatResponse chatResponse : chatResponses) {
                Log.d("ResponseApi", "ResponseApi Successfully inserted record with id " + chatResponse.getTel() + " into the database");
            }

            if (insertedRecords < 1 ){
                Toast.makeText(context, "No Response Yet", Toast.LENGTH_SHORT).show();
            }
        }

        private List<ChatResponse> convertToChatEntities(List<JsonChatresponse> items) {
            // Convert json_profile objects to MomProfileEntity objects
            List<ChatResponse> chatResponses = new ArrayList<>();

            for (JsonChatresponse item : items) {
                ChatResponse chatResponse = new ChatResponse();

                chatResponse.setId(item.getId());
                chatResponse.setRecord_id(item.getRecord_id());
                chatResponse.setTel(item.getTel());
                chatResponse.setResponse_date(item.getResponse_date());
                chatResponse.setProviders_name(item.getProviders_name());
                chatResponse.setResponse_text(item.getResponse_text());
                chatResponse.setRes_status(item.getRes_status());

                chatResponses.add(chatResponse);
            }

            return chatResponses;
        }

    }


    private List<JsonChatresponse> parseJsonResponse(String jsonResponse) {
        List<JsonChatresponse> jsonItems = new ArrayList<>();
        int addedRecordsCount = 0;  // Counter for added records

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            Set<Integer> uniqueTels = new HashSet<>();
            // Create a mapping for tel to name

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItemObject = jsonArray.getJSONObject(i);
                String tel = jsonItemObject.optString("tel", "");
                String recordId = jsonItemObject.optString("response_id", "");

                if (tel != null && recordId != null) {
                    String concatenatedId = tel + "_" + recordId;
                    JsonChatresponse item = new JsonChatresponse();
                    item.setId(concatenatedId);
                    item.setTel(tel);
                    item.setRecord_id(jsonItemObject.optInt("response_id", 0));
                    item.setResponse_date(jsonItemObject.optString("date_respondent", null));
                    item.setProviders_name(jsonItemObject.optString("respondent", null));
                    item.setResponse_text(jsonItemObject.optString("response_text", null));
                    item.setRes_status(jsonItemObject.optInt("res_status", 0));

                    if (item.getResponse_text() != null && !item.getResponse_text().equals("")) {
                        jsonItems.add(item);
                        addedRecordsCount++;  // Increment the counter for added records
                        System.out.println("Added record with id: " + item.getRecord_id());
                    }

                }
            }

            System.out.println("Total added records count: " + addedRecordsCount);
        } catch (JSONException e) {
            Log.e("ResponseApiClient", "Error parsing JSON response", e);
        }

        return jsonItems;
    }



}
