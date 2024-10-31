package com.example.foodorderingapp.Interfaces;

import com.example.foodorderingapp.Model.CartInfo;

import java.util.ArrayList;

public interface IAdapterItemListener {
    void onAddClicked(double amount);
    void onSubtractClicked(double amount);
    void onDeleteProduct();
}
