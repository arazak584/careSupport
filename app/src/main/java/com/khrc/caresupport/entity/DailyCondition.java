package com.khrc.caresupport.entity;

import android.widget.RadioGroup;

import androidx.databinding.BaseObservable;
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

@Entity(tableName = "daily")
public class DailyCondition extends BaseObservable {

    @NotNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id")
    public Integer record_id;

    @NotNull
    @ColumnInfo(name = "tel")
    public String tel;

    @ColumnInfo(name = "complaints_date")
    public Date complaints_date;

    @Expose
    public String complts;

    @Expose
    public Integer complete;

    public DailyCondition(){}

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public String getComplaints_date() {
        if (complaints_date == null) return null;
        return f.format(complaints_date);
    }

    public void setComplaints_date(String complaints_date) {
        if (complaints_date == null ) this.complaints_date=null;
        try {
            this.complaints_date = f.parse(complaints_date);
        } catch (ParseException e) {
            System.out.println(" Date Error " + e.getMessage());
        }
    }

    @NotNull
    public Integer getRecord_id() {
        return record_id;
    }

    public void setRecord_id(@NotNull Integer record_id) {
        this.record_id = record_id;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    public String getComplts() {
        return complts;
    }

    public void setComplts(String complts) {
        this.complts = complts;
    }

    @NotNull
    public String getTel() {
        return tel;
    }

    public void setTel(@NotNull String tel) {
        this.tel = tel;
    }



}
