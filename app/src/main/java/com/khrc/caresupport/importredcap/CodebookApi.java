package com.khrc.caresupport.importredcap;


import static com.khrc.caresupport.Utility.AppConstants.API_TOKEN_CODEBOOK;
import static com.khrc.caresupport.Utility.AppConstants.API_URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.khrc.caresupport.Dao.CodeBookDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.CodeBook;

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
import java.util.List;

public class CodebookApi {


    private CodeBookDao dao;
    AppDatabase appDatabase;

    public interface CodebookApiCallback {
        void onSuccess(List<JsonCodebook> result);

        void onError(String error);
    }

    public CodebookApi(Context context) {
        // Initialize the Room database and DAO
        appDatabase = AppDatabase.getDatabase(context);
        dao = appDatabase.codeBookDao();  // Assuming the DAO method is named profileDao()
    }


    public void loadAndInsertCodeData(CodebookApiCallback callback, Context context) {
        new LoadAndInsertDataTask(callback, context).execute();
    }

    private class LoadAndInsertDataTask extends AsyncTask<Void, Void, List<JsonCodebook>> {
        private final CodebookApiCallback callback;
        private final Context context;

        public LoadAndInsertDataTask(CodebookApiCallback callback, Context context) {
            this.callback = callback;
            this.context = context;
        }

        @Override
        protected List<JsonCodebook> doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Construct the request body
                String requestBody = "token=" + API_TOKEN_CODEBOOK +
                        "&content=record&format=json&type=flat";
                Log.d("API", "CODEBOOK" + requestBody);
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
                    List<JsonCodebook> codebooks = parseJsonResponse(response.toString());

                    // Insert the profiles into the Room database
                    if (codebooks != null && !codebooks.isEmpty()) {
                        insertCodeIntoDatabase(codebooks);
                    }

                    return codebooks;
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
        protected void onPostExecute(List<JsonCodebook> result) {
            if (result != null) {
                callback.onSuccess(result);
            }
        }

        private void insertCodeIntoDatabase(List<JsonCodebook> codebooks) {
            // Convert json_codebook objects to Entity objects
            List<CodeBook> codeBooks = convertToEntities(codebooks);

            // Insert the objects into the Room database
            dao.insert(codeBooks);
        }

        private List<CodeBook> convertToEntities(List<JsonCodebook> codebooks) {
            // Convert json_codebook objects to MomProfileEntity objects
            List<CodeBook> codeBooks = new ArrayList<>();

            for (JsonCodebook item : codebooks) {
                CodeBook codeBook = new CodeBook();
                // Map fields from json_codebook to Entity
                codeBook.setId(item.getId());
                codeBook.setCodeFeature(item.getCodeFeature());
                codeBook.setCodeLabel(item.getCodeLabel());
                codeBook.setCodeValue(item.getCodeValue());

                codeBooks.add(codeBook);
            }

            return codeBooks;
        }

    }


    private List<JsonCodebook> parseJsonResponse(String jsonResponse) {
        List<JsonCodebook> jsonItem = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                JsonCodebook item = new JsonCodebook();
                item.setId(jsonObject.optString("id", null));
                item.setCodeFeature(jsonObject.optString("codefeature", null));
                item.setCodeLabel(jsonObject.optString("codelabel", null));
                item.setCodeValue(jsonObject.optInt("codevalue", 0));
                jsonItem.add(item);
            }
        } catch (JSONException e) {
            Log.e("RedcapApiClient", "Error parsing JSON response", e);
        }

        return jsonItem;
    }
}
