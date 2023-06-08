package com.example.totshopping.Model;

import java.util.List;

public class HomePageModel {
    public static final int BANNER_SLIDER=0;
    public static final int STRIP_AD_SLIDER=1;
    public static final int HORIZONTAL_PRODUCT_VIEW=2;
    public static final int GRID_PRODUCT_VIEW=3;

    private int type;
    private String backgroundColor;

    ///////////// banner slider
    List<SliderModel> sliderModelList;

    public HomePageModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SliderModel> getSliderModelList() {
        return sliderModelList;
    }

    public void setSliderModelList(List<SliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }
    ///////////// banner slider

    ///////////// Strip Add
    private String resource;

    public HomePageModel(int type, String resource, String backgroundColor) {
        this.type = type;
        this.resource = resource;
        this.backgroundColor = backgroundColor;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    ///////////// Strip Add

    ///////////// Horizontal Scroll Layout  && Grid Layout
    private String title;
    private List<HorizontalScrollModel> horizontalScrollModelList;
    private List<WishlistModel> viewProductList;

    ///////////// Horizontal Scroll Layout
    public HomePageModel(int type,String title,String backgroundColor, List<HorizontalScrollModel> horizontalScrollModelList,List<WishlistModel> viewProductList) {
        this.type=type;
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.horizontalScrollModelList = horizontalScrollModelList;
        this.viewProductList = viewProductList;
    }

    public List<WishlistModel> getViewProductList() {
        return viewProductList;
    }

    public void setViewProductList(List<WishlistModel> viewProductList) {
        this.viewProductList = viewProductList;
    }
    ///////////// Horizontal Scroll Layout

    public HomePageModel(int type, String title, String backgroundColor, List<HorizontalScrollModel> horizontalScrollModelList) {
        this.type=type;
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.horizontalScrollModelList = horizontalScrollModelList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HorizontalScrollModel> getHorizontalScrollModelList() {
        return horizontalScrollModelList;
    }

    public void setHorizontalScrollModelList(List<HorizontalScrollModel> horizontalScrollModelList) {
        this.horizontalScrollModelList = horizontalScrollModelList;
    }
    ///////////// Horizontal Scroll Layout && Grid Layout
}
