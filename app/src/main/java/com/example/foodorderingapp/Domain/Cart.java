package com.example.foodorderingapp.Domain;

public class Cart {
    private String cartId;
    private int totalAmount;
    private double totalPrice;
    private String userId;

    public Cart(String cartId, int totalAmount, double totalPrice, String userId) {
        this.cartId = cartId;
        this.totalAmount = totalAmount;
        this.totalPrice = totalPrice;
        this.userId = userId;
    }

    public Cart() {
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
