package com.khrc.caresupport.Client.Adapter;

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

import com.khrc.caresupport.Client.Activity.ChatsActivity;
import com.khrc.caresupport.R;
import com.khrc.caresupport.ViewModel.ChatViewModel;
import com.khrc.caresupport.ViewModel.DailyConditionViewModel;
import com.khrc.caresupport.entity.ChatResponse;
import com.khrc.caresupport.entity.DailyCondition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ChatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ChatsActivity activity;
    LayoutInflater inflater;
    private Context context;
    private List<DailyCondition> complaintsList;
    private List<ChatResponse> chatResponseList;
    private final String phoneNumber;
    private List<Object> mergedList;
    private static final int VIEW_TYPE_COMPLAINT = 1;
    private static final int VIEW_TYPE_CHAT_RESPONSE = 2;

    public ChatsAdapter(ChatsActivity activity, String phoneNumber) {
        this.activity = activity;
        this.phoneNumber = phoneNumber;
        this.complaintsList = new ArrayList<>();
        this.chatResponseList = new ArrayList<>();
        this.mergedList = new ArrayList<>();
        inflater = LayoutInflater.from(activity);
        context = activity;
    }

    public class ComplaintViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout;
        TextView leftChatTextview, leftChatDate;

        public ComplaintViewHolder(View view) {
            super(view);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            leftChatDate = itemView.findViewById(R.id.left_chat_date);
        }
    }

    public class ChatResponseViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rightChatLayout;
        TextView rightChatTextview, rightChatDate;

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

//    @Override
//    public int getItemCount() {
//        int totalItemCount = complaintsList.size() + chatResponseList.size();
//        // Return 0 if both lists are empty, otherwise return totalItemCount
//        return (totalItemCount > 0) ? totalItemCount : 0;
//    }

    @Override
    public int getItemViewType(int position) {
        if (mergedList.get(position) instanceof DailyCondition) {
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
            view = inflater.inflate(R.layout.clientchat_itemlist, parent, false);
            return new ComplaintViewHolder(view);
        } else if (viewType == VIEW_TYPE_CHAT_RESPONSE) {
            view = inflater.inflate(R.layout.clientchat_itemlist, parent, false);
            return new ChatResponseViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = mergedList.get(position);
        if (holder instanceof ComplaintViewHolder && item instanceof DailyCondition) {
            ComplaintViewHolder complaintHolder = (ComplaintViewHolder) holder;
            DailyCondition complaint = (DailyCondition) item;
            if (!TextUtils.isEmpty(complaint.getComplts())) {

                String dateString = complaint.getComplaints_date();
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);

                try {
                    Date date = inputFormat.parse(dateString);
                    String formattedDate = outputFormat.format(date);

                    // Create a SpannableString for the date + Nike symbols
                    SpannableString spannableString;

                    if (complaint.getCpl_status() == 0) {
                        // Single Nike symbol in RED color
                        spannableString = new SpannableString(formattedDate + " ✓");
                        spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                                formattedDate.length() + 1, formattedDate.length() + 2,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (complaint.getCpl_status() == 1) {
                        // Double Nike symbol in GREEN color
                        spannableString = new SpannableString(formattedDate + " ✓✓");
                        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE),
                                formattedDate.length() + 1, formattedDate.length() + 3,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        spannableString = new SpannableString(formattedDate); // No icon for other statuses
                    }

                    // Determine the Nike symbol based on cpl_status
                    //String nikeSymbol = complaint.getCpl_status() == 0 ? " ✓" : " ✓✓";

                    complaintHolder.leftChatLayout.setVisibility(View.VISIBLE);
                    complaintHolder.leftChatTextview.setText(complaint.getComplts());
                    complaintHolder.leftChatDate.setText(spannableString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                complaintHolder.leftChatLayout.setVisibility(View.GONE);
            }
        } else if (holder instanceof ChatResponseViewHolder && item instanceof ChatResponse) {
            ChatResponseViewHolder chatResponseHolder = (ChatResponseViewHolder) holder;
            ChatResponse chatResponse = (ChatResponse) item;
            int adjustedPosition = position - complaintsList.size();
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

    public void updateData(List<DailyCondition> complaintsList, List<ChatResponse> chatResponseList) {
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
        if (item instanceof DailyCondition) {
            try {
                String dateString = ((DailyCondition) item).getComplaints_date();
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


    public void pull(String charText, DailyConditionViewModel dviewModel, ChatViewModel chatViewModel) {
        complaintsList.clear();
        chatResponseList.clear();

        if (charText != null && charText.length() > 2) {
            try {
                List<DailyCondition> list = dviewModel.searchs(charText, phoneNumber);
                List<ChatResponse> chat = chatViewModel.searchs(charText, phoneNumber);

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
                List<DailyCondition> list = dviewModel.repo(phoneNumber);
                List<ChatResponse> chat = chatViewModel.repo(phoneNumber);
                Log.i("Personal Phone Id", phoneNumber);
                if (list != null) {
                    complaintsList.addAll(list);
                }
                if (chat != null) {
                    chatResponseList.addAll(chat);
                }

                if (list.isEmpty()) {
                    Toast.makeText(activity, "No Complaint Found", Toast.LENGTH_SHORT).show();
                }

                if (chat.isEmpty()) {
                    Toast.makeText(activity, "No Response Found", Toast.LENGTH_SHORT).show();
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
