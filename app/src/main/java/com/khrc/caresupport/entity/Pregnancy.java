package com.khrc.caresupport.entity;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "pregnancy")
public class Pregnancy extends BaseObservable {

    @NotNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id;

    @Expose
    @NotNull
    public String tel;

    @ColumnInfo(name = "insertdate")
    public Date insertdate;

    @Expose
    public Date first_ga_date;

    @Expose
    public Integer first_ga_wks;

    @Expose
    public Date edd;

    @Expose
    public Date next_anc_schedule_date;

    @Expose
    public String planned_anc_facility;
    @Expose
    public String planned_delivery_place;

    @Expose
    public Date outcome_date;

    @ColumnInfo(name = "complete")
    public Integer complete;

    public Pregnancy() {
      }

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @NotNull
    public Integer getId() {
        return id;
    }

    public void setId(@NotNull Integer id) {
        this.id = id;
    }

    public String getFirst_ga_date() {
        if (first_ga_date == null) return null;
        return f.format(first_ga_date);
    }

    public void setFirst_ga_date(String FIRST_GA_DATE) {
        try {
            Date newRecordedDate = f.parse(FIRST_GA_DATE);
            Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.setTime(newRecordedDate);
            calendar.add(Calendar.WEEK_OF_YEAR, 36);
            // Update the recordedDate field
            this.edd = calendar.getTime();

            this.first_ga_date = newRecordedDate;
        } catch (ParseException e) {
            // Handle the ParseException
        }
    }

    public String getOutcome_date() {
        if (outcome_date == null) return null;
        return f.format(outcome_date);
    }

    public void setOutcome_date(String outcome_date) {
        if (outcome_date == null ) this.outcome_date=null;
        else
        try {
            this.outcome_date = f.parse(outcome_date);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }


    public String getNext_anc_schedule_date() {
        if (next_anc_schedule_date == null) return null;
        return f.format(next_anc_schedule_date);
    }

    public void setNext_anc_schedule_date(String next_anc_schedule_date) {
        if (next_anc_schedule_date == null ) this.next_anc_schedule_date=null;
        else
        try {
            this.next_anc_schedule_date = f.parse(next_anc_schedule_date);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }

    public String getEdd() {
        if (edd == null) return null;
        return f.format(edd);
    }

    public void setEdd(String edd) {
        if(edd == null ) this.edd=null;
        else
        try {
            this.edd = f.parse(edd);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }

    public String getInsertdate() {
        if (insertdate == null) return null;
        return f.format(insertdate);
    }

    public void setInsertdate(String insertdate) {
        if (insertdate == null ) this.insertdate=null;
        else
        try {
            this.insertdate = f.parse(insertdate);
        } catch (ParseException e) {
            System.out.println(" Date Error " + e.getMessage());
        }
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getFirst_ga_wks() {
        return first_ga_wks == null ? "" : String.valueOf(first_ga_wks);
    }

    public void setFirst_ga_wks(String first_ga_wks) {

        try {
            this.first_ga_wks = (first_ga_wks == null) ? null : Integer.valueOf(first_ga_wks);
        } catch (NumberFormatException e) {
        }
    }

    public String getPlanned_anc_facility() {
        return planned_anc_facility;
    }

    public void setPlanned_anc_facility(String planned_anc_facility) {
        this.planned_anc_facility = planned_anc_facility;
    }

    public String getPlanned_delivery_place() {
        return planned_delivery_place;
    }

    public void setPlanned_delivery_place(String planned_delivery_place) {
        this.planned_delivery_place = planned_delivery_place;
    }
}
