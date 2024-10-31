package com.example.foodorderingapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.Activities.Order.OrderActivity;
import com.example.foodorderingapp.Domain.Order;
import com.example.foodorderingapp.Adapter.OrderAdapter;
import com.example.foodorderingapp.databinding.FragmentHistoryOrderBinding;

import java.util.ArrayList;


public class HistoryOrderFragment extends Fragment {
    private FragmentHistoryOrderBinding binding;
    private ArrayList<Order> dsOrder;
    private String userId;

    public HistoryOrderFragment(ArrayList<Order> ds, String id) {
        dsOrder = ds;
        userId = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryOrderBinding.inflate(inflater,container,false);

        OrderAdapter adapter=new OrderAdapter(getContext(), dsOrder, OrderActivity.HISTORY_ORDER,userId);
        binding.ryc.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        binding.ryc.setAdapter(adapter);

        return binding.getRoot();
    }
}