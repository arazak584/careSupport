package com.khrc.caresupport.entity;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.khrc.caresupport.Utility.AppConstants;
import com.khrc.caresupport.Utility.KeyValuePair;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "profile")
public class MomProfile extends BaseObservable implements Parcelable {

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "tel")
    public String tel;

    @ColumnInfo(name = "doe")
    public Date doe;
    @ColumnInfo(name = "idnum")
    public String idnum;
    @ColumnInfo(name = "hfac")
    public String hfac;
    @ColumnInfo(name = "doi")
    public Date doi;
    @ColumnInfo(name = "nhisno")
    public String nhisno;
    @ColumnInfo(name = "mothn")
    public String mothn;
    @ColumnInfo(name = "community")
    public String community;
    @ColumnInfo(name = "addr")
    public String addr;
    @ColumnInfo(name = "lma")
    public String lma;
    @ColumnInfo(name = "dis")
    public String dis = "Kintampo North Municipal";

    @ColumnInfo(name = "mstatus")
    public Integer mstatus;
    @ColumnInfo(name = "edul")
    public Integer edul;
    @ColumnInfo(name = "occu")
    public Integer occu;
    @ColumnInfo(name = "g6pd")
    public String g6pd;
    @ColumnInfo(name = "nos")
    public String nos;
    @ColumnInfo(name = "dobs")
    public Date dob;
    @ColumnInfo(name = "addrs")
    public String addrs;
    @ColumnInfo(name = "lmas")
    public String lmas;
    @ColumnInfo(name = "diss")
    public String diss;
    @ColumnInfo(name = "tels")
    public String tels;
    @ColumnInfo(name = "eduls")
    public Integer eduls;
    @ColumnInfo(name = "occus")
    public Integer occus;
    @ColumnInfo(name = "contactn")
    public String contactn;
    @ColumnInfo(name = "contele")
    public String contele;
    @ColumnInfo(name = "pin")
    public String pin;

    @ColumnInfo(name = "complete")
    public Integer complete;

    @Ignore
    public MomProfile(@NotNull String tel, String mothn) {
        this.tel = tel;
        this.mothn = mothn;
    }

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public String getDob() {
        if (dob == null) return null;
        return f.format(dob);
    }

    public void setDob(String dob) {
        if(dob == null ) this.dob=null;
        else
            try {
                this.dob = f.parse(dob);
            } catch (ParseException e) {
                System.out.println("Date Error " + e.getMessage());
            }
    }

      public String getDoe() {
        if (doe == null) return null;
        return f.format(doe);
    }

    public void setDoe(String doe) {
        if(doe == null ) this.doe=null;
        else
            try {
                this.doe = f.parse(doe);
            } catch (ParseException e) {
                System.out.println("Date Error " + e.getMessage());
            }
    }

    public String getDoi() {
        if (doi == null) return null;
        return f.format(doi);
    }

    public void setDoi(String doi) {
        if(doi == null ) this.doi=null;
        else
            try {
                this.doi = f.parse(doi);
            } catch (ParseException e) {
                System.out.println("Date Error " + e.getMessage());
            }
    }

    @NotNull
    public String getTel() {
        return tel;
    }

    public void setTel(@NotNull String tel) {
        this.tel = tel;
    }

    public String getIdnum() {
        return idnum;
    }

    public void setIdnum(String idnum) {
        this.idnum = idnum;
    }

    public String getHfac() {
        return hfac;
    }

    public void setHfac(String hfac) {
        this.hfac = hfac;
    }

    public String getNhisno() {
        return nhisno;
    }

    public void setNhisno(String nhisno) {
        this.nhisno = nhisno;
    }

    public String getMothn() {
        return mothn;
    }

    public void setMothn(String mothn) {
        this.mothn = mothn;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getLma() {
        return lma;
    }

    public void setLma(String lma) {
        this.lma = lma;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public Integer getMstatus() {
        return mstatus;
    }

    public void setMstatus(Integer mstatus) {
        this.mstatus = mstatus;
    }

    public Integer getEdul() {
        return edul;
    }

    public void setEdul(Integer edul) {
        this.edul = edul;
    }

    public Integer getOccu() {
        return occu;
    }

    public void setOccu(Integer occu) {
        this.occu = occu;
    }

    public String getG6pd() {
        return g6pd;
    }

    public void setG6pd(String g6pd) {
        this.g6pd = g6pd;
    }

    public String getNos() {
        return nos;
    }

    public void setNos(String nos) {
        this.nos = nos;
    }

    public String getAddrs() {
        return addrs;
    }

    public void setAddrs(String addrs) {
        this.addrs = addrs;
    }

    public String getLmas() {
        return lmas;
    }

    public void setLmas(String lmas) {
        this.lmas = lmas;
    }

    public String getDiss() {
        return diss;
    }

    public void setDiss(String diss) {
        this.diss = diss;
    }

    public String getTels() {
        return tels;
    }

    public void setTels(String tels) {
        this.tels = tels;
    }

    public Integer getEduls() {
        return eduls;
    }

    public void setEduls(Integer eduls) {
        this.eduls = eduls;
    }

    public Integer getOccus() {
        return occus;
    }

    public void setOccus(Integer occus) {
        this.occus = occus;
    }

    public String getContactn() {
        return contactn;
    }

    public void setContactn(String contactn) {
        this.contactn = contactn;
    }

    public String getContele() {
        return contele;
    }

    public void setContele(String contele) {
        this.contele = contele;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    public MomProfile() {
    }

    protected MomProfile(Parcel in) {
        this.tel = in.readString();
        this.doe = (Date) in.readSerializable();
        this.idnum = in.readString();
        this.hfac = in.readString();
        this.doi = (Date) in.readSerializable();
        this.nhisno = in.readString();
        this.mothn = in.readString();
        this.community = in.readString();
        this.addr = in.readString();
        this.lma = in.readString();
        this.dis = in.readString();
        this.mstatus = in.readInt();
        this.edul = in.readInt();
        this.occu = in.readInt();
        this.g6pd = in.readString();
        this.nos = in.readString();
        this.dob = (Date) in.readSerializable();
        this.addrs = in.readString();
        this.lmas = in.readString();
        this.diss = in.readString();
        this.tels = in.readString();
        this.eduls = in.readInt();
        this.occus = in.readInt();
        this.contactn = in.readString();
        this.contele = in.readString();
        this.pin = in.readString();
    }

    public static final Creator<MomProfile> CREATOR = new Creator<MomProfile>() {
        @Override
        public MomProfile createFromParcel(Parcel in) {
            return new MomProfile(in);
        }

        @Override
        public MomProfile[] newArray(int size) {
            return new MomProfile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tel);
        dest.writeSerializable(this.doe);
        dest.writeString(this.idnum);
        dest.writeString(this.hfac);
        dest.writeSerializable(this.doi);
        dest.writeString(this.nhisno);
        dest.writeString(this.mothn);
        dest.writeString(this.community);
        dest.writeString(this.addr);
        dest.writeString(this.lma);
        dest.writeString(this.dis);
        dest.writeInt(this.mstatus);
        dest.writeInt(this.edul);
        dest.writeInt(this.occu);
        dest.writeString(this.g6pd);
        dest.writeString(this.nos);
        dest.writeSerializable(this.dob);
        dest.writeString(this.addrs);
        dest.writeString(this.lmas);
        dest.writeString(this.diss);
        dest.writeString(this.tels);
        dest.writeInt(this.eduls);
        dest.writeInt(this.occus);
        dest.writeString(this.contactn);
        dest.writeString(this.contele);
        dest.writeString(this.pin);

    }

    public void setMstatus(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            mstatus = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            mstatus = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }

    }

    public void setEdul(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            edul = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            edul = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }

    }

    public void setOccu(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            occu = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            occu = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }

    }

//
//    public void setEduls(AdapterView<?> parent, View view, int position, long id) {
//
//        if (position != parent.getSelectedItemPosition()) {
//            parent.setSelection(position);
//        }
//        if (position == 0) {
//            eduls = AppConstants.NOSELECT;
//        } else {
//            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
//            eduls = kv.codeValue;
//            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
//            ((TextView) parent.getChildAt(0)).setTextSize(20);
//            notifyPropertyChanged(BR._all);
//        }
//
//    }
//
//    public void setOccus(AdapterView<?> parent, View view, int position, long id) {
//
//        if (position != parent.getSelectedItemPosition()) {
//            parent.setSelection(position);
//        }
//        if (position == 0) {
//            occus = AppConstants.NOSELECT;
//        } else {
//            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
//            occus = kv.codeValue;
//            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
//            ((TextView) parent.getChildAt(0)).setTextSize(20);
//            notifyPropertyChanged(BR._all);
//        }
//
//    }

    @Override
    public String toString() {
        return mothn ;
    }
}
