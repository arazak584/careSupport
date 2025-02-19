package com.khrc.caresupport.Client.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.khrc.caresupport.Client.Adapter.ChatsAdapter;
import com.khrc.caresupport.Client.redcapexport.ExportComplaints;
import com.khrc.caresupport.Client.redcapexport.ExportHistory;
import com.khrc.caresupport.Client.redcapexport.ExportObsteric;
import com.khrc.caresupport.Client.redcapexport.ExportPregnancy;
import com.khrc.caresupport.Client.redcapexport.ExportProfile;
import com.khrc.caresupport.R;
import com.khrc.caresupport.ViewModel.ChatViewModel;
import com.khrc.caresupport.ViewModel.ComplaitViewModel;
import com.khrc.caresupport.ViewModel.DailyConditionViewModel;
import com.khrc.caresupport.ViewModel.MedHistoryViewModel;
import com.khrc.caresupport.ViewModel.PregnancyViewModel;
import com.khrc.caresupport.databinding.ActivityChatsBinding;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.DailyCondition;
import com.khrc.caresupport.entity.MedHistory;
import com.khrc.caresupport.entity.Pregnancy;
import com.khrc.caresupport.Client.redcapexport.ExportLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ChatsActivity extends AppCompatActivity {


    private ActivityChatsBinding binding;
    private ComplaitViewModel viewModel;
    private ChatViewModel chatViewModel;
    private DailyConditionViewModel dviewModel;
    private Complaints selectedComplaint;
    private TextView ph;

    private ChatsAdapter chatsAdapter;
    private RecyclerView recyclerView;
    private SimpleDateFormat dateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");

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

        viewModel = new ViewModelProvider(this).get(ComplaitViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        dviewModel = new ViewModelProvider(this).get(DailyConditionViewModel.class);
        binding.setComp(new DailyCondition());

        // Set up the lifecycle owner for LiveData in the ViewModel
        binding.setLifecycleOwner(this);
        ph.setText(phoneNumber);
        final TextView wks = findViewById(R.id.textInMiddle);
        final TextView next = findViewById(R.id.next_date);
        final TextView lm = findViewById(R.id.lmp_title);

        recyclerView = findViewById(R.id.chat_recycler_view);
        chatsAdapter = new ChatsAdapter(this, phoneNumber);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatsAdapter);
        recyclerView.scrollToPosition(chatsAdapter.getItemCount() - 1);

        //Initial loading of complaints
        chatsAdapter.pull("", dviewModel, chatViewModel);

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
                chatsAdapter.pull(newText, dviewModel, chatViewModel);
                return false;
            }
        });

        initiateBackgroundTask();

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        PregnancyViewModel viewModels = new ViewModelProvider(this).get(PregnancyViewModel.class);
        try {
            Pregnancy pregnancy = viewModels.finds(phoneNumber);

            if (pregnancy != null && pregnancy.first_ga_date!=null) {

                if(pregnancy.next_anc_schedule_date!=null) {
                    String dateString = pregnancy.getNext_anc_schedule_date();
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

                    Date date = inputFormat.parse(dateString);
                    String formattedDate = outputFormat.format(date);

                    next.setText("Next ANC " + formattedDate);
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
            MedHistory med = mviewModel.finds(phoneNumber);
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
            MedHistory med = mviewModel.finds(phoneNumber);
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

        binding.messageSendBtn.setOnClickListener(v -> {
            save(true, true, dviewModel);
        });


    }

    private void save(boolean save, boolean close, DailyConditionViewModel dviewModel) {

        if (save) {

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String phoneNumber = sharedPreferences.getString("phoneNumber", "");

            String com = binding.getComp().complts;

            DailyCondition finalData = new DailyCondition();
            finalData.complaints_date = new Date();
            finalData.tel = phoneNumber;
            finalData.setComplts(com);

            //Log.d("RESPONSE", "User Response Date " + finalData.response_date);

            finalData.complete = 1;
            finalData.cpl_status = 0;
            dviewModel.add(finalData);

            // Clear the text in the EditText
            EditText chatMessageInput = findViewById(R.id.chat_message_input);
            chatMessageInput.setText("");
            recreate(); // This recreates the activity, effectively refreshing its state
        }

    }

    private void initiateBackgroundTask() {
        // Check for internet connection
        if (isNetworkAvailable()) {
            // Internet is available, initiate the background task
            //showToast("Syncing data in the background...");

            // Perform the API call in the background
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ExportProfile importRecords = new ExportProfile(ChatsActivity.this);
                    importRecords.fetchMomProfilesAndPost();
                    return null;
                }
            }.execute();

            // Perform the API call for Complaints in the background
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ExportComplaints importComplaints = new ExportComplaints(ChatsActivity.this);
                    importComplaints.fetchComplaintsAndPost();
                    return null;
                }
            }.execute();

            // Perform the API call for Log in the background
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ExportLog importLog = new ExportLog(ChatsActivity.this);
                    importLog.fetchLogAndPost();
                    return null;
                }
            }.execute();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ExportPregnancy importPregnancy = new ExportPregnancy(ChatsActivity.this);
                    importPregnancy.fetchPregnancyAndPost();
                    return null;
                }
            }.execute();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ExportHistory importHistory = new ExportHistory(ChatsActivity.this);
                    importHistory.fetchHistoryAndPost();
                    return null;
                }
            }.execute();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ExportObsteric importObsteric = new ExportObsteric(ChatsActivity.this);
                    importObsteric.fetchObstericAndPost();
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

    private Date parseDateString(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onResume() {
        super.onResume();
        chatsAdapter.pull("", dviewModel, chatViewModel);
        initiateBackgroundTask();
    }
}