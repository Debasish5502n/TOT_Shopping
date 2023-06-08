package com.example.totshopping.Model;

import java.util.ArrayList;

public class WishlistModel {

    private String productId;
    private String productImage;
    private String productTitle;
    private long freeCoupons;
    private String ratting;
    private long totalRatting;
    private String productPrice;
    private String cuttedPrice;
    private Boolean paymentMethod;
    private boolean inStock;
    private ArrayList<String> tags;
    private boolean cod;

    public WishlistModel(String productId,String productImage, String productTitle, long freeCoupons, String ratting, long totalRatting, String productPrice, String cuttedPrice, Boolean paymentMethod,boolean inStock) {
        this.productId = productId;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.freeCoupons = freeCoupons;
        this.ratting = ratting;
        this.totalRatting = totalRatting;
        this.productPrice = productPrice;
        this.cuttedPrice = cuttedPrice;
        this.paymentMethod = paymentMethod;
        this.inStock = inStock;
    }

    public boolean isCod() {
        return cod;
    }

    public void setCod(boolean cod) {
        this.cod = cod;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public long getFreeCoupons() {
        return freeCoupons;
    }

    public void setFreeCoupons(long freeCoupons) {
        this.freeCoupons = freeCoupons;
    }

    public String getRatting() {
        return ratting;
    }

    public void setRatting(String ratting) {
        this.ratting = ratting;
    }

    public long getTotalRatting() {
        return totalRatting;
    }

    public void setTotalRatting(long totalRatting) {
        this.totalRatting = totalRatting;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }

    public Boolean getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Boolean paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
