package com.khrc.caresupport.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.khrc.caresupport.Adapter.ChatAdapter;
import com.khrc.caresupport.Adapter.NewAdapter;
import com.khrc.caresupport.R;
import com.khrc.caresupport.ViewModel.ChatViewModel;
import com.khrc.caresupport.ViewModel.ComplaitViewModel;
import com.khrc.caresupport.databinding.ActivityChatBinding;
import com.khrc.caresupport.databinding.ActivityComplaintBinding;
import com.khrc.caresupport.entity.ChatResponse;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.entity.subentity.Chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ComplaitViewModel viewModel;
    private ChatViewModel chatViewModel;
    private Complaints selectedComplaint;
    private Users userData;
    private TextView ph;

    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ph = binding.getRoot().findViewById(R.id.other_username);

        viewModel = new ViewModelProvider(this).get(ComplaitViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        binding.setComp(new Complaints());
        selectedComplaint = getIntent().getParcelableExtra("selectedComplaint");
        userData = getIntent().getParcelableExtra(LoginActivity.USER_DATA);

        Log.d("Activity", "ProviderName Phone" + selectedComplaint.tel);
        Log.d("Activity", "ProviderName " + userData.getMothn());

        // Set up the lifecycle owner for LiveData in the ViewModel
        binding.setLifecycleOwner(this);
        ph.setText(selectedComplaint.tel);

        recyclerView = findViewById(R.id.chat_recycler_view);
        chatAdapter = new ChatAdapter(this, selectedComplaint);

        //recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        //Initial loading of complaints
        chatAdapter.pull("", viewModel);

        binding.messageSendBtn.setOnClickListener(v -> {
            save(true, true, chatViewModel);
        });


    }

    private void save(boolean save, boolean close, ChatViewModel chatViewModel) {

        if (save) {

            String com = binding.getComp().response_txt;

                ChatResponse finalData = new ChatResponse();
                finalData.response_date = new Date();
                finalData.tel = selectedComplaint.tel;
                finalData.providers_name = userData.getMothn();
                finalData.setResponse_text(com);

            Log.d("RESPONSE", "User Response Date " + finalData.response_date);

            finalData.complete = 1;
            chatViewModel.add(finalData);

            // Clear the text in the EditText
            EditText chatMessageInput = findViewById(R.id.chat_message_input);
            chatMessageInput.setText("");
            recreate(); // This recreates the activity, effectively refreshing its state
        }

    }



    protected void onResume() {
        super.onResume();
        chatAdapter.pull("", viewModel);
    }

}