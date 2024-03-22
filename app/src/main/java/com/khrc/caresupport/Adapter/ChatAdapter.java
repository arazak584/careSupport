package com.khrc.caresupport.Adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.khrc.caresupport.ViewModel.ComplaitViewModel;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.entity.subentity.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    ChatActivity activity;
    LayoutInflater inflater;
    private Context context;
    private List<Complaints> complaintsList;
    private List<Chat> filter;
    private final Complaints selectedComplaint;

    public ChatAdapter(ChatActivity activity,Complaints selectedComplaint) {
        this.activity = activity;
        this.selectedComplaint = selectedComplaint;
        this.complaintsList = new ArrayList<>();
        inflater = LayoutInflater.from(activity);
        context = activity;
    }

    public void searchNotes(List<Chat> filterName) {
        if (filterName != null) {
            this.filter = filterName;
        } else {
            this.filter = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextview,rightChatTextview;

        public ViewHolder(View view) {
            super(view);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);

        }
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.chat_itemlist, parent, false);
        ChatAdapter.ViewHolder viewHolder = new ChatAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Complaints complaints = complaintsList.get(position);

        holder.leftChatTextview.setText(complaints.complts + " (" + complaints.complaints_date + ")");
        holder.rightChatTextview.setText(complaints.response_txt + " - " +complaints.response_date);

    }

    @Override
    public int getItemCount() {
        return complaintsList.size();
    }

    public void pull(String charText, ComplaitViewModel complaitViewModel) {
        complaintsList.clear();

        if (charText != null && charText.length() > 2) {
            try {
                List<Complaints> list = complaitViewModel.search(charText);

                if (list != null) {
                    complaintsList.addAll(list);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                List<Complaints> list = complaitViewModel.repo(selectedComplaint.tel);
                Log.i("Chat Phone Id", selectedComplaint.tel);
                if (list != null) {
                    complaintsList.addAll(list);
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
