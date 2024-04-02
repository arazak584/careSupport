package com.khrc.caresupport.Client.redcapimport;

import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.khrc.caresupport.Dao.ChatDao;
import com.khrc.caresupport.Dao.ObstericDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.ChatResponse;
import com.khrc.caresupport.entity.DailyCondition;
import com.khrc.caresupport.entity.Obsteric;
import com.khrc.caresupport.importredcap.JsonChatresponse;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatApi {


    private static final String TEL = "";
    private ChatDao dao;
    AppDatabase appDatabase;

    public interface ChatApiCallback {
        void onSuccess(List<JsonChatresponse> result);
        void onError(String error);
    }

    public ChatApi(Context context) {
        // Initialize the Room database and DAO
        appDatabase = AppDatabase.getDatabase(context);
        dao = appDatabase.chatDao();  // Assuming the DAO method is named profileDao()
    }

    public void loadAndInsertChatData(String phoneNumber, ChatApiCallback callback, Context context) {
        new LoadAndInsertDataTask(callback, context, phoneNumber).execute();
    }

    private class LoadAndInsertDataTask extends AsyncTask<Void, Void, List<JsonChatresponse>> {
        private final ChatApiCallback callback;
        private final Context context;
        private final String phoneNumber;

        public LoadAndInsertDataTask(ChatApiCallback callback, Context context, String phoneNumber) {
            this.callback = callback;
            this.context = context;
            this.phoneNumber = phoneNumber;
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
            List<ChatResponse> chats = convertToChatEntities(items);
            // Insert the MomProfileEntity objects into the Room database
            dao.insert(chats);

            int insertedRecords = chats.size();
            Log.d("ChatsApi", "ChatsApi Successfully inserted " + insertedRecords + " records into the database");
            for (ChatResponse chatResponse : chats) {
                Log.d("ChatsApi", "ChatsApi Successfully inserted record with id " + chatResponse.getTel() + " into the database");
            }
        }

        private List<ChatResponse> convertToChatEntities(List<JsonChatresponse> items) {
            // Convert json_profile objects to MomProfileEntity objects
            List<ChatResponse> chats = new ArrayList<>();

            for (JsonChatresponse item : items) {
                ChatResponse chat = new ChatResponse();
                chat.setTel(item.getTel());
                chat.setId(item.getId());
                chat.setRecord_id(item.getRecord_id());
                chat.setResponse_date(item.getResponse_date());
                chat.setProviders_name(item.getProviders_name());
                chat.setResponse_text(item.getResponse_text());

                chats.add(chat);
            }

            return chats;
        }

    }


    private List<JsonChatresponse> parseJsonResponse(String jsonResponse) {
        List<JsonChatresponse> jsonItems = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            Set<String> uniqueTels = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItemObject = jsonArray.getJSONObject(i);

                String tel = jsonItemObject.optString("tel", "");
                String recordId = jsonItemObject.optString("response_id", "");
                if (tel != null && recordId != null) {
                    String concatenatedId = tel + "_" + recordId;
                    JsonChatresponse item = new JsonChatresponse();
                    item.setTel(tel);
                    item.setId(jsonItemObject.optInt("response_id", 0));
                    item.setRecord_id(concatenatedId);
                    item.setResponse_date(jsonItemObject.optString("date_respondent", null));
                    item.setProviders_name(jsonItemObject.optString("respondent", null));
                    item.setResponse_text(jsonItemObject.optString("response_text", null));

                    //jsonItems.add(item);
                    if (uniqueTels.add(item.getRecord_id()) && (item.getResponse_text() != null && !item.getResponse_text().equals(""))) {
                        jsonItems.add(item);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("RedcapApiClient", "Error parsing JSON response", e);
        }

        return jsonItems;
    }


}
