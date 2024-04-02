package com.khrc.caresupport.importredcap;

public class JsonComplaints {

    private String id;
    private Integer record_id;
    private String tel;
    private String complaints_date;
    private Integer gen_hlth;
    private String complts;
    private String hfac;
    private String mothn;

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

    public String getComplaints_date() {
        return complaints_date;
    }

    public void setComplaints_date(String complaints_date) {
        this.complaints_date = complaints_date;
    }

    public Integer getGen_hlth() {
        return gen_hlth;
    }

    public void setGen_hlth(Integer gen_hlth) {
        this.gen_hlth = gen_hlth;
    }

    public String getComplts() {
        return complts;
    }

    public void setComplts(String complts) {
        this.complts = complts;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHfac() {
        return hfac;
    }

    public void setHfac(String hfac) {
        this.hfac = hfac;
    }

    public String getMothn() {
        return mothn;
    }

    public void setMothn(String mothn) {
        this.mothn = mothn;
    }
}
