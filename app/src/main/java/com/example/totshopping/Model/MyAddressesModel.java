package com.example.totshopping.Model;

public class MyAddressesModel {

    private Boolean selected;
    private String city;
    private String locality;
    private String flatNo;
    private String pinCode;
    private String landMark;
    private String name;
    private String mobileNo;
    private String alternativeMobileNo;
    private String State;

    public MyAddressesModel(Boolean selected, String city, String locality, String flatNo, String pinCode, String landMark, String name, String mobileNo, String alternativeMobileNo, String state) {
        this.selected = selected;
        this.city = city;
        this.locality = locality;
        this.flatNo = flatNo;
        this.pinCode = pinCode;
        this.landMark = landMark;
        this.name = name;
        this.mobileNo = mobileNo;
        this.alternativeMobileNo = alternativeMobileNo;
        State = state;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getFlatNo() {
        return flatNo;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getLandMark() {
        return landMark;
    }

    public void setLandMark(String landMark) {
        this.landMark = landMark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAlternativeMobileNo() {
        return alternativeMobileNo;
    }

    public void setAlternativeMobileNo(String alternativeMobileNo) {
        this.alternativeMobileNo = alternativeMobileNo;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }
}
