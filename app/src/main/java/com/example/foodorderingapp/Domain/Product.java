package com.example.foodorderingapp.Domain;

import java.io.Serializable;

public class Product implements Serializable {
    private int productId;
    private String productName;
    private String productImage;
    private Double productPrice;
    private String description;
    private Double ratingStar;
    private int ratingAmount;
    private String state;
    public Product() {
    }

    public Product(int productId, String productName, String productImage, Double productPrice,   String description, Double ratingStar, int ratingAmount,String state) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.description = description;
        this.ratingStar = ratingStar;
        this.ratingAmount = ratingAmount;
        this.state = state;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {return productImage;
    }

    public void setProductImage(String productImage1) {
        this.productImage = productImage1;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRatingStar() {
        return ratingStar;
    }

    public void setRatingStar(Double ratingStar) {
        this.ratingStar = ratingStar;
    }

    public int getRatingAmount() {
        return ratingAmount;
    }

    public void setRatingAmount(int ratingAmount) {
        this.ratingAmount = ratingAmount;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Products{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productImage='" + productImage + '\'' +
                ", productPrice=" + productPrice +
                ", description='" + description + '\'' +
                ", ratingStar=" + ratingStar +
                "}";
    }
}
