package com.example.foodorderingapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.foodorderingapp.Fragments.CurrentOrderFragment;
import com.example.foodorderingapp.Fragments.HistoryOrderFragment;
import com.example.foodorderingapp.Model.Order;

import java.util.ArrayList;

public class OrderViewPaperAdapter extends FragmentStateAdapter {
    private ArrayList<Order> dsCurrentOrder;
    private ArrayList <Order> dsHistoryOrder;
    private String userId;

    public OrderViewPaperAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Order> dsCurrentOrder, ArrayList <Order> dsHistoryOrder, String id) {
        super(fragmentActivity);
        this.dsCurrentOrder=dsCurrentOrder;
        this.dsHistoryOrder=dsHistoryOrder;
        this.userId = id;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new HistoryOrderFragment(dsHistoryOrder, userId);
        }
        return new CurrentOrderFragment(dsCurrentOrder, userId);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
