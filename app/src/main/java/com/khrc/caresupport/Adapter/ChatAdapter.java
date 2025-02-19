package com.khrc.caresupport.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khrc.caresupport.Activity.ChatActivity;
import com.khrc.caresupport.R;
import com.khrc.caresupport.ViewModel.ChatViewModel;
import com.khrc.caresupport.ViewModel.ComplaitViewModel;
import com.khrc.caresupport.entity.ChatResponse;
import com.khrc.caresupport.entity.Complaints;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private List<Object> mergedList;
    private final Complaints selectedComplaint;

    private static final int VIEW_TYPE_COMPLAINT = 1;
    private static final int VIEW_TYPE_CHAT_RESPONSE = 2;

    public ChatAdapter(ChatActivity activity,Complaints selectedComplaint) {
        this.activity = activity;
        this.selectedComplaint = selectedComplaint;
        this.complaintsList = new ArrayList<>();
        this.chatResponseList = new ArrayList<>();
        this.mergedList = new ArrayList<>();
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
        return mergedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mergedList.get(position) instanceof Complaints) {
            return VIEW_TYPE_COMPLAINT;
        } else if (mergedList.get(position) instanceof ChatResponse) {
            return VIEW_TYPE_CHAT_RESPONSE;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_COMPLAINT) {
            view = inflater.inflate(R.layout.chat_itemlist, parent, false);
            return new ChatAdapter.ComplaintViewHolder(view);
        } else if (viewType == VIEW_TYPE_CHAT_RESPONSE) {
            view = inflater.inflate(R.layout.chat_itemlist, parent, false);
            return new ChatAdapter.ChatResponseViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = mergedList.get(position);
        if (holder instanceof ChatAdapter.ComplaintViewHolder && item instanceof Complaints) {
            ChatAdapter.ComplaintViewHolder complaintHolder = (ChatAdapter.ComplaintViewHolder) holder;
            Complaints complaint = (Complaints) item;
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
        } else if (holder instanceof ChatAdapter.ChatResponseViewHolder && item instanceof ChatResponse) {
            ChatAdapter.ChatResponseViewHolder chatResponseHolder = (ChatAdapter.ChatResponseViewHolder) holder;
            ChatResponse chatResponse = (ChatResponse) item;
            if (!TextUtils.isEmpty(chatResponse.getResponse_text())) {
                String dateString = chatResponse.getResponse_date();
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);


                try {
                    Date date = inputFormat.parse(dateString);
                    String formattedDate = outputFormat.format(date);

                    // Create a SpannableString for the date + Nike symbols
                    SpannableString spannableString;

                    if (chatResponse.getRes_status() == 0) {
                        // Single Nike symbol in RED color
                        spannableString = new SpannableString(formattedDate + " ✓");
                        spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                                formattedDate.length() + 1, formattedDate.length() + 2,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (chatResponse.getRes_status() == 1) {
                        // Double Nike symbol in GREEN color
                        spannableString = new SpannableString(formattedDate + " ✓✓");
                        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE),
                                formattedDate.length() + 1, formattedDate.length() + 3,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        spannableString = new SpannableString(formattedDate); // No icon for other statuses
                    }


                    chatResponseHolder.rightChatLayout.setVisibility(View.VISIBLE);
                    chatResponseHolder.rightChatTextview.setText(chatResponse.getResponse_text());
                    chatResponseHolder.rightChatDate.setText(spannableString + " - " + chatResponse.providers_name);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                chatResponseHolder.rightChatLayout.setVisibility(View.GONE);
            }
        }
    }

    public void updateData(List<Complaints> complaintsList, List<ChatResponse> chatResponseList) {
        mergedList.clear();
        mergedList.addAll(complaintsList);
        mergedList.addAll(chatResponseList);
        // Sort the merged list based on date
        Collections.sort(mergedList, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Date date1 = getDateFromItem(o1);
                Date date2 = getDateFromItem(o2);
                return date1.compareTo(date2);
            }
        });
        notifyDataSetChanged();
    }

    // Helper method to extract date from complaint/chat response object
    private Date getDateFromItem(Object item) {
        if (item instanceof Complaints) {
            try {
                String dateString = ((Complaints) item).getComplaints_date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                return format.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else if (item instanceof ChatResponse) {
            try {
                String dateString = ((ChatResponse) item).getResponse_date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                return format.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
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
        updateData(complaintsList, chatResponseList);
    }

}
