package com.example.foodorderingapp.Activities.Order;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.Adapter.OrderViewPaperAdapter;
import com.example.foodorderingapp.Model.Order;
import com.example.foodorderingapp.databinding.ActivityOrderBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    private String userId;
    private ActivityOrderBinding binding;
    public static final int CURRENT_ORDER = 10001;
    public static final int HISTORY_ORDER = 10002;
    private ArrayList<Order> dsCurrentOrder = new ArrayList<>();
    private ArrayList<Order> dsHistoryOrder = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));

        userId = getIntent().getStringExtra("userId");


        initData();

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUI() {
        OrderViewPaperAdapter viewPaperAdapter = new OrderViewPaperAdapter(OrderActivity.this, dsCurrentOrder, dsHistoryOrder, userId);
        binding.viewPaper2.setAdapter(viewPaperAdapter);
        binding.viewPaper2.setUserInputEnabled(false);
        new TabLayoutMediator(binding.tabLayout, binding.viewPaper2, ((tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Current Order");
                    break;
                case 1:
                    tab.setText("History Order");
                    break;
            }
        })).attach();
    }

    private void initData() {
        FirebaseDatabase.getInstance().getReference("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dsCurrentOrder.clear();
                dsHistoryOrder.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Order tmp = item.getValue(Order.class);
                    if(tmp.getUserId().equalsIgnoreCase(userId)) {
                        if (!tmp.getOrderStatus().equalsIgnoreCase("Completed")) {
                            dsCurrentOrder.add(tmp);
                        } else
                            dsHistoryOrder.add(tmp);
                    }
                }
                initUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}