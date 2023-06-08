package com.example.totshopping.Model;


import java.util.Date;

public class RewardModel {

    private String type, lowerLimit, upperLimit,discount, couponBody;
    private Date timestamp;
    private boolean alreadyUsed;
    private String couponId;

    public RewardModel(String couponId,String type, String lowerLimit, String upperLimit,String discount, String couponBody, Date timestamp,boolean alreadyUsed) {
        this.type = type;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.couponBody = couponBody;
        this.timestamp = timestamp;
        this.discount = discount;
        this.alreadyUsed = alreadyUsed;
        this.couponId = couponId;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public boolean isAlreadyUsed() {
        return alreadyUsed;
    }

    public void setAlreadyUsed(boolean alreadyUsed) {
        this.alreadyUsed = alreadyUsed;
    }

    public String getDiscOrAmt() {
        return discount;
    }

    public void setDiscOrAmt(String discount) {
        this.discount = discount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getCouponBody() {
        return couponBody;
    }

    public void setCouponBody(String couponBody) {
        this.couponBody = couponBody;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}