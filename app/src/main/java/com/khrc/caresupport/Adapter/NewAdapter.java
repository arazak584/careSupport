package com.khrc.caresupport.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.khrc.caresupport.Activity.ChatActivity;
import com.khrc.caresupport.Activity.LoginActivity;
import com.khrc.caresupport.Activity.MainActivity;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.R;
import com.khrc.caresupport.ViewModel.ComplaitViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.ViewHolder> {

    MainActivity activity;
    LayoutInflater inflater;
    private Complaints complaints;
    private Users userData;
    private final ComplaitViewModel complaitViewModel;
    private final List<Complaints> complaintsList;

    public NewAdapter(Context context, MainActivity activity, Complaints complaints, Users userData,ComplaitViewModel complaitViewModel) {
        this.activity = activity;
        this.complaints = complaints;
        this.userData = userData;
        this.complaitViewModel = complaitViewModel;
        complaintsList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView names, phone, comp, date;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.names = view.findViewById(R.id.text_name);
            this.comp = view.findViewById(R.id.text_complaint);
            this.phone = view.findViewById(R.id.text_phone);
            this.cardView = view.findViewById(R.id.searchedItem);

            // Add click listener to the cardView
            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Complaints selectedComplaint = complaintsList.get(position);

                    // Open ComplaintActivity with the selected item's content
                    openComplaintActivity(selectedComplaint);
                }
            });
        }
    }

    @NonNull
    @Override
    public NewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.com_itemlist, parent, false);
        NewAdapter.ViewHolder viewHolder = new NewAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Complaints complaints = complaintsList.get(position);

        String dateString = complaints.getComplaints_date();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);

        //holder.names.setText(complaints.getMothn() + " (" + complaints.getTel() + ")");


        try {
            Date date = inputFormat.parse(dateString);
            String formattedDate = outputFormat.format(date);

            holder.comp.setText(formattedDate);
            holder.phone.setText(complaints.getHfac());

            // Assuming you have access to the necessary ViewModel instances
            long count = complaitViewModel.reply(complaints.tel);
            long counts = complaitViewModel.replys(complaints.tel);
            Log.d("Display", "Total Count " + complaints.tel + counts);

            if (counts>0) {
                holder.names.setText(complaints.getMothn() + " (" + complaints.getTel() + ")");
                holder.names.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_text_green));
            }else{
                holder.names.setText(complaints.getMothn() + " (" + complaints.getTel() + ")");
                holder.names.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
            }

        } catch (ExecutionException | InterruptedException | ParseException e) {
            e.printStackTrace();
            // Handle exceptions appropriately, for example, show an error message
            Toast.makeText(activity, "Error updating counts", Toast.LENGTH_SHORT).show();
        }
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
                List<Complaints> list = complaitViewModel.not();

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


    private void openComplaintActivity(Complaints selectedComplaint) {
        String clickedRecordId = selectedComplaint.getId();
        Log.i("Clicked Record Id", clickedRecordId);

        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("selectedComplaint", selectedComplaint);
        intent.putExtra(LoginActivity.USER_DATA, userData);
        Log.i("Clicked Name", userData.mothn);
        activity.startActivity(intent);
    }

}
