package com.example.foodorderingapp.Model;

import java.io.Serializable;

public class CartInfo implements Serializable {
    private int amount;
    private String cartInfoId;
    private int productId;

    public CartInfo(int amount, String cartInfoId, int productId) {
        this.amount = amount;
        this.cartInfoId = cartInfoId;
        this.productId = productId;
    }

    public CartInfo() {
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCartInfoId() {
        return cartInfoId;
    }

    public void setCartInfoId(String cartInfoId) {
        this.cartInfoId = cartInfoId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
