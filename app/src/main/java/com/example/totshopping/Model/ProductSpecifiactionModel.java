package com.example.totshopping.Model;

public class ProductSpecifiactionModel {
    public static final int SPECIFICATION_TITLE=0;
    public static final int SPECIFICATION_BODY=1;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    ////////////specification title
    String title;

    public ProductSpecifiactionModel(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    ////////////specification title

    ////////////specification body
    String FeatureName,featureValue;

    public ProductSpecifiactionModel(int type, String featureName, String featureValue) {
        this.type = type;
        FeatureName = featureName;
        this.featureValue = featureValue;
    }

    public String getFeatureName() {
        return FeatureName;
    }

    public void setFeatureName(String featureName) {
        FeatureName = featureName;
    }

    public String getFeatureValue() {
        return featureValue;
    }

    public void setFeatureValue(String featureValue) {
        this.featureValue = featureValue;
    }
    ////////////specification body


}
