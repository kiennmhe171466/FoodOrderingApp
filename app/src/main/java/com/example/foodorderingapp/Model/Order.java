package com.example.foodorderingapp.Model;

import java.io.Serializable;

public class Order implements Serializable {
    private String addressId;
    private String orderId;
    private String orderDate;
    private String orderStatus;
    private String userId;
    private long totalPrice;
    private String imageUrl;

    public Order(String addressId, String orderId, String orderDate, String orderStatus, String userId, long totalPrice, String imageUrl) {
        this.addressId = addressId;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.imageUrl = imageUrl;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
