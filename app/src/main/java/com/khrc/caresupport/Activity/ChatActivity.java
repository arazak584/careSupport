package com.khrc.caresupport.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.khrc.caresupport.Adapter.ChatAdapter;
import com.khrc.caresupport.R;
import com.khrc.caresupport.ViewModel.ChatViewModel;
import com.khrc.caresupport.ViewModel.ComplaitViewModel;
import com.khrc.caresupport.ViewModel.MedHistoryViewModel;
import com.khrc.caresupport.ViewModel.PregnancyViewModel;
import com.khrc.caresupport.databinding.ActivityChatBinding;
import com.khrc.caresupport.entity.ChatResponse;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.MedHistory;
import com.khrc.caresupport.entity.Pregnancy;
import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.redcapsend.ExportChatresponse;
import com.khrc.caresupport.redcapsend.ExportChats_Old;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ComplaitViewModel viewModel;
    private ChatViewModel chatViewModel;
    private Complaints selectedComplaint;
    private Users userData;
    private TextView ph;

    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private SimpleDateFormat dateFormat;
    private ChatResponse chatResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ph = binding.getRoot().findViewById(R.id.other_username);

        // Inside your activity or fragment
        ImageButton backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the logic to navigate back
                onBackPressed(); // This will perform the same action as pressing the back key
            }
        });

        chatResponse = new ChatResponse();

        viewModel = new ViewModelProvider(this).get(ComplaitViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        binding.setComp(new ChatResponse());
        selectedComplaint = getIntent().getParcelableExtra("selectedComplaint");
        userData = getIntent().getParcelableExtra(LoginActivity.USER_DATA);

        Log.d("Activity", "ProviderName Phone" + selectedComplaint.tel);
        Log.d("Activity", "ProviderName " + userData.getMothn());

        // Set up the lifecycle owner for LiveData in the ViewModel
        binding.setLifecycleOwner(this);
        //ph.setText(selectedComplaint.mothn +" (" +selectedComplaint.tel+ ")");
        ph.setText(selectedComplaint.mothn);
        final TextView wks = findViewById(R.id.textInMiddle);
        final TextView next = findViewById(R.id.next_date);
        final TextView lm = findViewById(R.id.lmp_title);

        recyclerView = findViewById(R.id.chat_recycler_view);
        chatAdapter = new ChatAdapter(this, selectedComplaint);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

        //Initial loading of complaints
        chatAdapter.pull("", viewModel, chatViewModel);

        final SearchView editSearch = findViewById(R.id.search);
        // below line is to call set on query text listener method.
        editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                chatAdapter.pull(newText, viewModel, chatViewModel);
                return false;
            }
        });

        initiateBackgroundTask();

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        PregnancyViewModel viewModels = new ViewModelProvider(this).get(PregnancyViewModel.class);
        try {
            Pregnancy pregnancy = viewModels.finds(selectedComplaint.tel);

            if (pregnancy != null && pregnancy.first_ga_date!=null) {

                if(pregnancy.next_anc_schedule_date!=null) {
                    String dateString = pregnancy.getNext_anc_schedule_date();
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

                    Date date = inputFormat.parse(dateString);
                    String formattedDate = outputFormat.format(date);

                    next.setText("ANC " + formattedDate);
                }

                if(pregnancy.first_ga_date!=null) {
                    String dateString = pregnancy.getFirst_ga_date();
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

                    Date date = inputFormat.parse(dateString);
                    String lmpDate = outputFormat.format(date);

                    lm.setText("LMP " + lmpDate);
                }

                // Convert date strings to Date objects
                Date firstGaDate = parseDateString(pregnancy.getFirst_ga_date());
                Date expectedDeliveryDate = parseDateString(pregnancy.getEdd());
                Date currentDate = new Date();  // Current date
                // Calculate the difference between currentDate and expectedDeliveryDate in weeks
                long differenceInMilliseconds = currentDate.getTime() - firstGaDate.getTime();
                long differenceInWeeks = TimeUnit.MILLISECONDS.toDays(differenceInMilliseconds) / 7;

                // Set the progress to your circularProgressBar
                ProgressBar circularProgressBar = findViewById(R.id.circularProgressBar);

                // Set the maximum value of the ProgressBar to the total number of pregnancy weeks
                long totalPregnancyWeeks = TimeUnit.MILLISECONDS.toDays(expectedDeliveryDate.getTime() - firstGaDate.getTime()) / 7;
                circularProgressBar.setMax((int) totalPregnancyWeeks);

                wks.setText(String.valueOf(differenceInWeeks) + " " + getString(R.string.weeks));
                // Set the progress value based on the calculated difference
                circularProgressBar.setProgress((int) Math.min(differenceInWeeks, totalPregnancyWeeks));

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        MedHistoryViewModel mviewModel = new ViewModelProvider(this).get(MedHistoryViewModel.class);
        try {
            MedHistory med = mviewModel.finds(selectedComplaint.tel);
            if (med != null){
                List<String> spinnerItems = new ArrayList<>();
                if (med.getMh1()!=null && med.getMh1()==1) {
                    spinnerItems.add("Hypertension");
                }
                if (med.getMh2()!=null && med.getMh2()==1) {
                    spinnerItems.add("Heart Disease");
                }
                if (med.getMh3()!=null && med.getMh3()==1) {
                    spinnerItems.add("Sickle Cell Disease");
                }
                if (med.getMh4()!=null && med.getMh4()==1) {
                    spinnerItems.add("Diabetes");
                }
                if (med.getMh5()!=null && med.getMh5()==1) {
                    spinnerItems.add("Epilepsy");
                }
                if (med.getMh6()!=null && med.getMh6()==1) {
                    spinnerItems.add("HIV Infection");
                }
                if (med.getMh7()!=null && med.getMh7()==1) {
                    spinnerItems.add("Asthma");
                }
                if (med.getMh8()!=null && med.getMh8()==1) {
                    spinnerItems.add("Allergies");
                }
                if (med.getMh9()!=null && med.getMh9()==1) {
                    spinnerItems.add("Respiratory Disease");
                }
                if (med.getMh10()!=null && med.getMh10()==1) {
                    spinnerItems.add("TB");
                }
                if (med.getMh11()!=null && med.getMh11()==1) {
                    spinnerItems.add("Mental Illness");
                }
                if (med.getMh12()!=null && med.getMh12()==1) {
                    spinnerItems.add("Surgery");
                }
                if (med.getMh13()!=null && med.getMh13()==1) {
                    spinnerItems.add(med.getOth());
                }

                // Check if spinnerItems is empty
                if (spinnerItems.isEmpty()) {
                    spinnerItems.add("No Medical History");
                }

                // Populate spinner with spinnerItems
                Spinner spinner = findViewById(R.id.med);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

            }

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            MedHistory med = mviewModel.finds(selectedComplaint.tel);
            if (med != null){
                List<String> spinnerItems = new ArrayList<>();
                if (med.getFh1()!=null && med.getFh1()==1) {
                    spinnerItems.add("Hypertension");
                }
                if (med.getFh2()!=null && med.getFh2()==1) {
                    spinnerItems.add("Heart Disease");
                }
                if (med.getFh3()!=null && med.getFh3()==1) {
                    spinnerItems.add("Sickle Cell Disease");
                }
                if (med.getFh4()!=null && med.getFh4()==1) {
                    spinnerItems.add("Diabetes");
                }
                if (med.getFh5()!=null && med.getFh5()==1) {
                    spinnerItems.add("Multiple Pregnancies");
                }
                if (med.getFh6()!=null && med.getFh6()==1) {
                    spinnerItems.add("Birth Defect");
                }
                if (med.getFh7()!=null && med.getFh7()==1) {
                    spinnerItems.add("Mental Health Disorder");
                }
                if (med.getFh8()!=null && med.getFh8()==1) {
                    spinnerItems.add(med.getOth_2());
                }

                // Check if spinnerItems is empty
                if (spinnerItems.isEmpty()) {
                    spinnerItems.add("No Family History");
                }

                // Populate spinner with spinnerItems
                Spinner spinner = findViewById(R.id.fam);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

            }

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            ChatResponse datae = chatViewModel.retrieveMaxTels(selectedComplaint.tel);
            if (datae != null) {

                String ids = datae.id;
                String[] parts = ids.split("_");
                String beforeUnderscore = parts[0];
                String afterUnderscore = parts[1];

                int numberAfterUnderscore = Integer.parseInt(parts[1]) + 1;
                String newId = beforeUnderscore + "_" + numberAfterUnderscore;

                Log.d("ActiveID", "New ID: " + newId);

            }else {
                String ids = selectedComplaint.tel;
                int nextID = 1;
                String newId = ids + "_" + nextID;
                Log.d("ActiveID", "New ID: " + newId);
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        binding.messageSendBtn.setOnClickListener(v -> {
            save(true, true, chatViewModel);
        });


    }

    private void save(boolean save, boolean close, ChatViewModel chatViewModel) {
        if (save) {
            // Get the response text
            String com = binding.getComp().response_text;

            // Create a new ChatResponse object with the necessary data
            ChatResponse finalData = new ChatResponse();
            finalData.response_date = new Date();
            finalData.tel = selectedComplaint.tel;
            finalData.providers_name = userData.getMothn() + " - " + userData.getTel();
            finalData.res_status = 0;
            finalData.setResponse_text(com);

            // Generate new ID
            String newId;
            int newRecordId;
            try {
                ChatResponse datae = chatViewModel.retrieveMaxTels(selectedComplaint.tel);
                if (datae != null) {
                    String ids = datae.id;
                    String[] parts = ids.split("_");
                    String beforeUnderscore = parts[0];
                    int numberAfterUnderscore = Integer.parseInt(parts[1]) + 1;
                    newId = beforeUnderscore + "_" + numberAfterUnderscore;
                    newRecordId = datae.record_id + 1; // Increment record_id
                } else {
                    newId = finalData.tel + "_1"; // If no existing data, set ID with "_1"
                    newRecordId = 1; // Start record_id from 1
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                // Default to tel + "_1" if an exception occurs
                newId = finalData.tel + "_1";
                newRecordId = 1; // Start record_id from 1
            }
            finalData.id = newId; // Set the ID in finalData
            finalData.record_id = newRecordId; // Set the record_id in finalData

            // Log the response date
            Log.d("RESPONSE", "User Response Date " + finalData.response_date);

            // Set the 'complete' property
            finalData.complete = 1;

            // Add the final data to the ChatViewModel
            chatViewModel.add(finalData);

            // Clear the text in the EditText
            EditText chatMessageInput = findViewById(R.id.chat_message_input);
            chatMessageInput.setText("");

            // Recreate the activity to refresh its state
            recreate();
        }
    }




    private Date parseDateString(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check for internet connection and initiate background task
        initiateBackgroundTask();
        chatAdapter.pull("", viewModel, chatViewModel);
    }

    //Send Data to Redcap
    private void initiateBackgroundTask() {
        // Check for internet connection
        if (isNetworkAvailable()) {
            // Perform the API call for Complaints in the background

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ExportChatresponse importChatresponse = new ExportChatresponse(ChatActivity.this);
                    importChatresponse.fetchChatAndPost();
                    return null;
                }
            }.execute();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ExportChats_Old importComplaints = new ExportChats_Old(ChatActivity.this);
                    importComplaints.fetchComplaintsAndPost();
                    return null;
                }
            }.execute();

        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}