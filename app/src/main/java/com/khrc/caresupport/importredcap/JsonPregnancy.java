package com.khrc.caresupport.importredcap;

import java.util.Objects;

public class JsonPregnancy {

    private String tel;
    private String insertdate;
    private String first_ga_date;
    private String first_ga_wks;
    private String edd;
    private String next_anc_schedule_date;
    private String planned_anc_facility;
    private String planned_delivery_place;
    private String outcome_date;

    private Integer preg_outcome;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getInsertdate() {
        return insertdate;
    }

    public void setInsertdate(String insertdate) {
        this.insertdate = insertdate;
    }

    public String getFirst_ga_date() {
        return first_ga_date;
    }

    public void setFirst_ga_date(String first_ga_date) {
        this.first_ga_date = first_ga_date;
    }

    public String getFirst_ga_wks() {
        return first_ga_wks;
    }

    public void setFirst_ga_wks(String first_ga_wks) {
        this.first_ga_wks = first_ga_wks;
    }

    public String getEdd() {
        return edd;
    }

    public void setEdd(String edd) {
        this.edd = edd;
    }

    public String getNext_anc_schedule_date() {
        return next_anc_schedule_date;
    }

    public void setNext_anc_schedule_date(String next_anc_schedule_date) {
        this.next_anc_schedule_date = next_anc_schedule_date;
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

    public String getOutcome_date() {
        return outcome_date;
    }

    public void setOutcome_date(String outcome_date) {
        this.outcome_date = outcome_date;
    }

    public Integer getPreg_outcome() {
        return preg_outcome;
    }

    public void setPreg_outcome(Integer preg_outcome) {
        this.preg_outcome = preg_outcome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonPregnancy that = (JsonPregnancy) o;
        return Objects.equals(tel, that.tel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tel);
    }

    @Override
    public String toString() {
        return "JsonPregnancy{" +
                "tel='" + tel + '\'' +
                '}';
    }
}
