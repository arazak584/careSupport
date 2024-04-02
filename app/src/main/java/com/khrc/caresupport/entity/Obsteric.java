package com.khrc.caresupport.entity;


import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Entity(tableName = "obs")
public class Obsteric extends BaseObservable {

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "tel")
    public String tel;

    @ColumnInfo(name = "parity")
    public Integer parity;
    @ColumnInfo(name = "gravidity")
    public Integer gravidity;

    @ColumnInfo(name = "spontaneous_abortions")
    public Integer spontaneous_abortions;

    @ColumnInfo(name = "induced_abortions")
    public Integer induced_abortions;

    @ColumnInfo(name = "complete")
    public Integer complete;

    public Obsteric(){}


    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @NotNull
    public String getTel() {
        return tel;
    }

    public void setTel(@NotNull String tel) {
        this.tel = tel;
    }

    public String getParity() {
        return parity == null ? "" : String.valueOf(parity);
    }

    public void setParity(String parity) {
        try {
        this.parity = (parity == null) ? null : Integer.valueOf(parity);
        } catch (NumberFormatException e) {
        }
    }

    public String getGravidity() {
        return gravidity == null ? "" : String.valueOf(gravidity);
    }

    public void setGravidity(String gravidity) {
        try {
            this.gravidity = (gravidity == null) ? null : Integer.valueOf(gravidity);
        } catch (NumberFormatException e) {
        }
    }

    public String getSpontaneous_abortions() {
        return spontaneous_abortions == null ? "" : String.valueOf(spontaneous_abortions);
    }

    public void setSpontaneous_abortions(String spontaneous_abortions) {
        try {
            this.spontaneous_abortions = (spontaneous_abortions == null) ? null : Integer.valueOf(spontaneous_abortions);
        } catch (NumberFormatException e) {
        }
    }

    public String getInduced_abortions() {
        return induced_abortions == null ? "" : String.valueOf(induced_abortions);
    }

    public void setInduced_abortions(String induced_abortions) {
        try {
            this.induced_abortions = (induced_abortions == null) ? null : Integer.valueOf(induced_abortions);
        } catch (NumberFormatException e) {
        }
    }
}
