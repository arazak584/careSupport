package com.khrc.caresupport.importredcap;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class JsonChatresponse {

    private String id;
    private Integer record_id;
    private String tel;
    private String response_date;
    private String providers_name;
    private String response_text;
    private Integer res_status;

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

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getResponse_date() {
        return response_date;
    }

    public void setResponse_date(String response_date) {
        this.response_date = response_date;
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

    public Integer getRes_status() {
        return res_status;
    }

    public void setRes_status(Integer res_status) {
        this.res_status = res_status;
    }
}
