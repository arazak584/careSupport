package com.khrc.caresupport.importredcap;

public class UserProfile {
    private String tel;
    private String pin;
    private String hfac;
    private String mothn;

    private Integer ustatus;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
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

    public Integer getUstatus() {
        return ustatus;
    }

    public void setUstatus(Integer ustatus) {
        this.ustatus = ustatus;
    }
}
