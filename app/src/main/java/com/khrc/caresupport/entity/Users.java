package com.khrc.caresupport.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "users")
public class Users extends BaseObservable implements Parcelable {

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "tel")
    public String tel;

    @ColumnInfo(name = "hfac")
    public String hfac;

    @ColumnInfo(name = "pin")
    public String pin;

    @ColumnInfo(name = "mothn")
    public String mothn;

    public Users() {
        this.tel = tel;
    }

    @NotNull
    public String getTel() {
        return tel;
    }

    public void setTel(@NotNull String tel) {
        this.tel = tel;
    }

    public String getHfac() {
        return hfac;
    }

    public void setHfac(String hfac) {
        this.hfac = hfac;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getMothn() {
        return mothn;
    }

    public void setMothn(String mothn) {
        this.mothn = mothn;
    }

    protected Users(Parcel in) {
        this.tel = in.readString();
        this.hfac = in.readString();
        this.pin = in.readString();
        this.mothn = in.readString();
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tel);
        dest.writeString(this.hfac);
        dest.writeString(this.pin);
        dest.writeString(this.mothn);
    }

    @Override
    public String toString() {
        return tel ;
    }
}
