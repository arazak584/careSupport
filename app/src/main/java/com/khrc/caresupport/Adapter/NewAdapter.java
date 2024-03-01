package com.khrc.caresupport.Adapter;

import android.content.Context;
import android.content.Intent;
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

import com.khrc.caresupport.Activity.ComplaintActivity;
import com.khrc.caresupport.Activity.MainActivity;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.R;
import com.khrc.caresupport.ViewModel.ComplaitViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.ViewHolder> {

    MainActivity activity;
    LayoutInflater inflater;
    private Complaints complaints;
    private final List<Complaints> complaintsList;

    public NewAdapter(Context context, MainActivity activity, Complaints complaints) {
        this.activity = activity;
        this.complaints = complaints;
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

        holder.names.setText(complaints.getMothn() + " (" + complaints.getTel() + ")");
        holder.comp.setText(complaints.getComplaints_date() + " - " +complaints.getComplts());
        holder.phone.setText(complaints.getHfac());
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

        Intent intent = new Intent(activity, ComplaintActivity.class);
        intent.putExtra("selectedComplaint", selectedComplaint);
        activity.startActivity(intent);
    }

}
