package com.khrc.caresupport.entity;

import android.widget.RadioGroup;

import androidx.databinding.BaseObservable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Entity(tableName = "medhistory")
public class MedHistory extends BaseObservable {

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "tel")
    public String tel;

    @ColumnInfo(name = "mh1")
    public Integer mh1;

    @ColumnInfo(name = "mh2")
    public Integer mh2;

    @ColumnInfo(name = "mh3")
    public Integer mh3;

    @ColumnInfo(name = "mh4")
    public Integer mh4;

    @ColumnInfo(name = "mh5")
    public Integer mh5;

    @ColumnInfo(name = "mh6")
    public Integer mh6;

    @ColumnInfo(name = "mh7")
    public Integer mh7;

    @ColumnInfo(name = "mh8")
    public Integer mh8;

    @ColumnInfo(name = "mh9")
    public Integer mh9;

    @ColumnInfo(name = "mh10")
    public Integer mh10;

    @ColumnInfo(name = "mh11")
    public Integer mh11;

    @ColumnInfo(name = "mh12")
    public Integer mh12;

    @ColumnInfo(name = "mh13")
    public Integer mh13;

    @ColumnInfo(name = "oth")
    public String oth;

    @ColumnInfo(name = "fh1")
    public Integer fh1;

    @ColumnInfo(name = "fh2")
    public Integer fh2;

    @ColumnInfo(name = "fh3")
    public Integer fh3;
    @ColumnInfo(name = "fh4")
    public Integer fh4;
    @ColumnInfo(name = "fh5")
    public Integer fh5;
    @ColumnInfo(name = "fh6")
    public Integer fh6;
    @ColumnInfo(name = "fh7")
    public Integer fh7;
    @ColumnInfo(name = "fh8")
    public Integer fh8;

    @ColumnInfo(name = "oth_2")
    public String oth_2;

    @ColumnInfo(name = "complete")
    public Integer complete;
    public MedHistory(){}

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @NotNull
    public String getTel() {
        return tel;
    }

    public void setTel(@NotNull String tel) {
        this.tel = tel;
    }

    public Integer getMh1() {
        return mh1;
    }

    public void setMh1(Integer mh1) {
        this.mh1 = mh1;
    }

    public Integer getMh2() {
        return mh2;
    }

    public void setMh2(Integer mh2) {
        this.mh2 = mh2;
    }

    public Integer getMh3() {
        return mh3;
    }

    public void setMh3(Integer mh3) {
        this.mh3 = mh3;
    }

    public Integer getMh4() {
        return mh4;
    }

    public void setMh4(Integer mh4) {
        this.mh4 = mh4;
    }

    public Integer getMh5() {
        return mh5;
    }

    public void setMh5(Integer mh5) {
        this.mh5 = mh5;
    }

    public Integer getMh6() {
        return mh6;
    }

    public void setMh6(Integer mh6) {
        this.mh6 = mh6;
    }

    public Integer getMh7() {
        return mh7;
    }

    public void setMh7(Integer mh7) {
        this.mh7 = mh7;
    }

    public Integer getMh8() {
        return mh8;
    }

    public void setMh8(Integer mh8) {
        this.mh8 = mh8;
    }

    public Integer getMh9() {
        return mh9;
    }

    public void setMh9(Integer mh9) {
        this.mh9 = mh9;
    }

    public Integer getMh10() {
        return mh10;
    }

    public void setMh10(Integer mh10) {
        this.mh10 = mh10;
    }

    public Integer getMh11() {
        return mh11;
    }

    public void setMh11(Integer mh11) {
        this.mh11 = mh11;
    }

    public Integer getMh12() {
        return mh12;
    }

    public void setMh12(Integer mh12) {
        this.mh12 = mh12;
    }

    public Integer getMh13() {
        return mh13;
    }

    public void setMh13(Integer mh13) {
        this.mh13 = mh13;
    }

    public String getOth() {
        return oth;
    }

    public void setOth(String oth) {
        this.oth = oth;
    }

    public Integer getFh1() {
        return fh1;
    }

    public void setFh1(Integer fh1) {
        this.fh1 = fh1;
    }

    public Integer getFh2() {
        return fh2;
    }

    public void setFh2(Integer fh2) {
        this.fh2 = fh2;
    }

    public Integer getFh3() {
        return fh3;
    }

    public void setFh3(Integer fh3) {
        this.fh3 = fh3;
    }

    public Integer getFh4() {
        return fh4;
    }

    public void setFh4(Integer fh4) {
        this.fh4 = fh4;
    }

    public Integer getFh5() {
        return fh5;
    }

    public void setFh5(Integer fh5) {
        this.fh5 = fh5;
    }

    public Integer getFh6() {
        return fh6;
    }

    public void setFh6(Integer fh6) {
        this.fh6 = fh6;
    }

    public Integer getFh7() {
        return fh7;
    }

    public void setFh7(Integer fh7) {
        this.fh7 = fh7;
    }

    public Integer getFh8() {
        return fh8;
    }

    public void setFh8(Integer fh8) {
        this.fh8 = fh8;
    }

    public String getOth_2() {
        return oth_2;
    }

    public void setOth_2(String oth_2) {
        this.oth_2 = oth_2;
    }

    public void setMh1(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh1 = Integer.parseInt(TAG);
        }
    }


    public void setMh2(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh2 = Integer.parseInt(TAG);
        }
    }

    public void setMh3(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh3 = Integer.parseInt(TAG);
        }
    }

    public void setMh4(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh4 = Integer.parseInt(TAG);
        }
    }

    public void setMh5(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh5 = Integer.parseInt(TAG);
        }
    }

    public void setMh6(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh6 = Integer.parseInt(TAG);
        }
    }

    public void setMh7(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh7 = Integer.parseInt(TAG);
        }
    }

    public void setMh8(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh8 = Integer.parseInt(TAG);
        }
    }

    public void setMh9(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh9 = Integer.parseInt(TAG);
        }
    }

    public void setMh10(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh10 = Integer.parseInt(TAG);
        }
    }

    public void setMh11(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh11 = Integer.parseInt(TAG);
        }
    }

    public void setMh12(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh12 = Integer.parseInt(TAG);
        }
    }

    public void setMh13(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mh13 = Integer.parseInt(TAG);
            notifyPropertyChanged(BR._all);
        }
    }

    public void setFh1(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            fh1 = Integer.parseInt(TAG);
        }
    }


    public void setFh2(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            fh2 = Integer.parseInt(TAG);
        }
    }

    public void setFh3(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            fh3 = Integer.parseInt(TAG);
        }
    }

    public void setFh4(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            fh4 = Integer.parseInt(TAG);
        }
    }

    public void setFh5(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            fh5 = Integer.parseInt(TAG);
        }
    }

    public void setFh6(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            fh6 = Integer.parseInt(TAG);
        }
    }

    public void setFh7(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            fh7 = Integer.parseInt(TAG);
        }
    }

    public void setFh8(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            fh8 = Integer.parseInt(TAG);
            notifyPropertyChanged(BR._all);
        }
    }
}
