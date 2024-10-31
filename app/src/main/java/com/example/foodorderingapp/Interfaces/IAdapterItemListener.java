package com.example.foodorderingapp.Interfaces;

import com.example.foodorderingapp.Model.CartInfo;

import java.util.ArrayList;

public interface IAdapterItemListener {
    void onCheckedItemCountChanged(int count, long price, ArrayList<CartInfo> selectedItems);
    void onAddClicked();
    void onSubtractClicked();
    void onDeleteProduct();
}
