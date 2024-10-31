package com.example.foodorderingapp.Order;

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
import com.example.foodorderingapp.databinding.FragmentCurrentOrderBinding;

import java.util.ArrayList;


public class CurrentOrderFragment extends Fragment {
    private FragmentCurrentOrderBinding binding;
    private ArrayList<Order> dsOrder;
    private String userId;

    public CurrentOrderFragment(ArrayList<Order> ds, String id) {
        dsOrder = ds;
        userId = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCurrentOrderBinding.inflate(inflater,container,false);

        OrderAdapter adapter=new OrderAdapter(getContext(), dsOrder, OrderActivity.CURRENT_ORDER, userId);
        binding.ryc.setAdapter(adapter);
        binding.ryc.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));

        return binding.getRoot();
    }
}