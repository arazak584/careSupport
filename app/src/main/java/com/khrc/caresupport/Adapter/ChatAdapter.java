package com.khrc.caresupport.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.khrc.caresupport.Activity.ChatActivity;
import com.khrc.caresupport.Activity.MainActivity;
import com.khrc.caresupport.R;
import com.khrc.caresupport.ViewModel.ChatViewModel;
import com.khrc.caresupport.ViewModel.ComplaitViewModel;
import com.khrc.caresupport.entity.ChatResponse;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.entity.subentity.Chat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ChatActivity activity;
    LayoutInflater inflater;
    private Context context;
    private List<Complaints> complaintsList;
    private List<ChatResponse> chatResponseList;
    private List<Chat> filter;
    private final Complaints selectedComplaint;

    private static final int VIEW_TYPE_COMPLAINT = 1;
    private static final int VIEW_TYPE_CHAT_RESPONSE = 2;

    public ChatAdapter(ChatActivity activity,Complaints selectedComplaint) {
        this.activity = activity;
        this.selectedComplaint = selectedComplaint;
        this.complaintsList = new ArrayList<>();
        this.chatResponseList = new ArrayList<>();
        inflater = LayoutInflater.from(activity);
        context = activity;
    }

    // ViewHolder classes for Complaints and ChatResponse
    public class ComplaintViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout;
        TextView leftChatTextview,leftChatDate;

        public ComplaintViewHolder(View view) {
            super(view);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            leftChatDate= itemView.findViewById(R.id.left_chat_date);
        }
    }

    public class ChatResponseViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rightChatLayout;
        TextView rightChatTextview,rightChatDate;

        public ChatResponseViewHolder(View view) {
            super(view);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            rightChatDate = itemView.findViewById(R.id.right_chat_date);

        }

    }

    @Override
    public int getItemCount() {
        int totalItemCount = complaintsList.size() + chatResponseList.size();
        // Return 0 if both lists are empty, otherwise return totalItemCount
        return (totalItemCount > 0) ? totalItemCount : 0;
    }

    @Override
    public int getItemViewType(int position) {
        // Determine the view type based on position and list sizes
        if (!complaintsList.isEmpty() && position < complaintsList.size()) {
            return VIEW_TYPE_COMPLAINT;
        } else {
            return VIEW_TYPE_CHAT_RESPONSE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout based on view type
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_COMPLAINT) {
            View complaintView = inflater.inflate(R.layout.chat_itemlist, parent, false);
            return new ComplaintViewHolder(complaintView);
        } else {
            View chatResponseView = inflater.inflate(R.layout.chat_itemlist, parent, false);
            return new ChatResponseViewHolder(chatResponseView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ComplaintViewHolder) {
            ComplaintViewHolder complaintHolder = (ComplaintViewHolder) holder;
            Complaints complaint = complaintsList.get(position);
            if (!TextUtils.isEmpty(complaint.getComplts())) {

                String dateString = complaint.getComplaints_date();
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);

                try {
                Date date = inputFormat.parse(dateString);
                String formattedDate = outputFormat.format(date);

                complaintHolder.leftChatLayout.setVisibility(View.VISIBLE);
                complaintHolder.leftChatTextview.setText(complaint.getComplts());
                complaintHolder.leftChatDate.setText(formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                complaintHolder.leftChatLayout.setVisibility(View.GONE);
            }
        } else if (holder instanceof ChatResponseViewHolder) {
            ChatResponseViewHolder chatResponseHolder = (ChatResponseViewHolder) holder;
            int adjustedPosition = position - complaintsList.size();
            ChatResponse chatResponse = chatResponseList.get(adjustedPosition);
            if (!TextUtils.isEmpty(chatResponse.getResponse_text())) {
                String dateString = chatResponse.getResponse_date();
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);

                try {
                    Date date = inputFormat.parse(dateString);
                    String formattedDate = outputFormat.format(date);
                chatResponseHolder.rightChatLayout.setVisibility(View.VISIBLE);
                chatResponseHolder.rightChatTextview.setText(chatResponse.getResponse_text());
                chatResponseHolder.rightChatDate.setText(formattedDate + " - " + chatResponse.providers_name);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                chatResponseHolder.rightChatLayout.setVisibility(View.GONE);
            }
        }
    }


    public void pull(String charText, ComplaitViewModel complaitViewModel, ChatViewModel chatViewModel) {
        complaintsList.clear();
        chatResponseList.clear();

        if (charText != null && charText.length() > 2) {
            try {
                List<Complaints> list = complaitViewModel.searchs(charText,selectedComplaint.tel);
                List<ChatResponse> chat = chatViewModel.searchs(charText,selectedComplaint.tel);

                if (list != null) {
                    complaintsList.addAll(list);
                }

                if (chat != null) {
                    chatResponseList.addAll(chat);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                List<Complaints> list = complaitViewModel.repo(selectedComplaint.tel);
                List<ChatResponse> chat = chatViewModel.repo(selectedComplaint.tel);
                Log.i("Chat Phone Id", selectedComplaint.tel);
                if (list != null) {
                    complaintsList.addAll(list);
                }
                if (chat != null) {
                    chatResponseList.addAll(chat);
                }

                if (list.isEmpty()) {
                    Toast.makeText(activity, "No Complaint Found", Toast.LENGTH_SHORT).show();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        notifyDataSetChanged();
    }

}
