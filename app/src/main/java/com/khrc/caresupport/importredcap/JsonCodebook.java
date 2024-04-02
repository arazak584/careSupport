package com.khrc.caresupport.importredcap;

public class JsonCodebook {

    public String id;
    public String codeFeature;
    public Integer codeValue;
    public String codeLabel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodeFeature() {
        return codeFeature;
    }

    public void setCodeFeature(String codeFeature) {
        this.codeFeature = codeFeature;
    }

    public Integer getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(Integer codeValue) {
        this.codeValue = codeValue;
    }

    public String getCodeLabel() {
        return codeLabel;
    }

    public void setCodeLabel(String codeLabel) {
        this.codeLabel = codeLabel;
    }
}
