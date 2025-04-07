package com.khrc.caresupport.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "chat")
public class ChatResponse {

    @NotNull
    @PrimaryKey()
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "record_id")
    public Integer record_id;

    @ColumnInfo(name = "tel")
    public String tel;

    @ColumnInfo(name = "response_date")
    public Date response_date;

    @Expose
    public String providers_name;

    @Expose
    public String response_text;

    @Expose
    public Integer complete;
    @Expose
    public Integer res_status =0;
    @Expose
    public Date rem_date;


    public ChatResponse() {
    }

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public String getResponse_date() {
        if (response_date == null) return null;
        return f.format(response_date);
    }

    public void setResponse_date(String response_date) {
        if (response_date == null ) this.response_date=null;
        try {
            this.response_date = f.parse(response_date);
        } catch (ParseException e) {
            System.out.println(" Date Error " + e.getMessage());
        }
    }

    @NotNull
    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        // Set the id field
        this.id = id;
        // Check if id is null
        if (id == null) {
            this.record_id = null;
        } else {
            // Split the id string by underscore
            String[] parts = id.split("_");
            // Check if the id has at least two parts (before and after underscore)
            if (parts.length >= 2) {
                // Attempt to parse the second part (the number after underscore) as an integer
                try {
                    this.record_id = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    // Handle the case where the number after underscore is not a valid integer
                    System.err.println("Invalid id format: " + id);
                }
            } else {
                // Handle the case where id does not contain an underscore
                System.err.println("Invalid id format: " + id);
            }
        }
    }



    public Integer getRecord_id() {
        return record_id;
    }

    public void setRecord_id(Integer record_id) {
        this.record_id = record_id;
    }

    @NotNull
    public String getTel() {
        return tel;
    }

    public void setTel(@NotNull String tel) {
        this.tel = tel;
    }

    public String getProviders_name() {
        return providers_name;
    }

    public void setProviders_name(String providers_name) {
        this.providers_name = providers_name;
    }

    public String getResponse_text() {
        return response_text;
    }

    public void setResponse_text(String response_text) {
        this.response_text = response_text;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    public Integer getRes_status() {
        return res_status;
    }

    public void setRes_status(Integer res_status) {
        this.res_status = res_status;
    }
}
