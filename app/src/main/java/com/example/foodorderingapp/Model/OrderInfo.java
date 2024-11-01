package com.example.foodorderingapp.Model;

import java.io.Serializable;

public class OrderInfo implements Serializable {
    private int amount;
    private String orderInfoId;
    private String productId;
    private boolean check;

    public OrderInfo() {
    }

    public OrderInfo(int amount, String orderInfoId, String productId, boolean check) {
        this.amount = amount;
        this.orderInfoId = orderInfoId;
        this.productId = productId;
        this.check = check;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOrderInfoId() {
        return orderInfoId;
    }

    public void setOrderInfoId(String orderInfoId) {
        this.orderInfoId = orderInfoId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
