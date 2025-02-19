package com.khrc.caresupport.entity;

import android.os.Parcel;
import android.os.Parcelable;

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

@Entity(tableName = "complaints")
public class Complaints extends BaseObservable implements Parcelable {

    @NotNull
    @PrimaryKey()
    @ColumnInfo(name = "id")
    public String id;

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
    public String mothn;

    @Expose
    public String hfac;

    @Expose
    public Integer cpl_status;

    @Expose
    public Integer complete;

    public Complaints() {
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getComplts() {
        return complts;
    }

    public void setComplts(String complts) {
        this.complts = complts;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    public String getMothn() {
        return mothn;
    }

    public void setMothn(String mothn) {
        this.mothn = mothn;
    }

    public String getHfac() {
        return hfac;
    }

    public void setHfac(String hfac) {
        this.hfac = hfac;
    }

    public Integer getCpl_status() {
        return cpl_status;
    }

    public void setCpl_status(Integer cpl_status) {
        this.cpl_status = cpl_status;
    }

    protected Complaints(Parcel in) {
        this.id = in.readString();
        this.record_id = in.readInt();
        this.tel = in.readString();
        this.hfac = in.readString();
        this.complaints_date = (Date) in.readSerializable();
        this.complts = in.readString();
        this.mothn = in.readString();
    }

    public static final Creator<Complaints> CREATOR = new Creator<Complaints>() {
        @Override
        public Complaints createFromParcel(Parcel in) {
            return new Complaints(in);
        }

        @Override
        public Complaints[] newArray(int size) {
            return new Complaints[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.record_id);
        dest.writeString(this.tel);
        dest.writeString(this.hfac);
        dest.writeSerializable(this.complaints_date);
        dest.writeString(this.complts);
        dest.writeString(this.mothn);

    }

    @Override
    public String toString() {
        return id ;
    }

}
