package com.translator.model.codactor.data;

public class Metadata {
    private String dataType;
    private String data;

    public Metadata(String dataType, String data) {
        this.dataType = dataType;
        this.data = data;
    }

    public String getDataType() {
        return dataType;
    }

    public String getData() {
        return data;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setData(String data) {
        this.data = data;
    }
}
