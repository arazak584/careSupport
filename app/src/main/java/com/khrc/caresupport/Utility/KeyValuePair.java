package com.khrc.caresupport.Utility;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class KeyValuePair implements Parcelable {
    public KeyValuePair() {
    }

    public int codeValue;
    public String codeLabel;

    protected KeyValuePair(Parcel in) {
        this.codeValue = in.readInt();
        this.codeLabel = in.readString();
    }

    public static final Creator<KeyValuePair> CREATOR = new Creator<KeyValuePair>() {
        @Override
        public KeyValuePair createFromParcel(Parcel in) {
            return new KeyValuePair(in);
        }

        @Override
        public KeyValuePair[] newArray(int size) {
            return new KeyValuePair[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.codeValue);
        dest.writeString(this.codeLabel);
    }

    @NonNull
    @Override
    public String toString() {
        return codeLabel ;
    }
}
