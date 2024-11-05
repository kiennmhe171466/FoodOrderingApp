package com.example.foodorderingapp.Activities.Order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.Adapter.OrderDetailAdapter;
import com.example.foodorderingapp.Model.Address;
import com.example.foodorderingapp.Model.Order;
import com.example.foodorderingapp.Model.OrderInfo;
import com.example.foodorderingapp.Model.User;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ActivityOrderDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class OrderDetailActivity extends AppCompatActivity {
    private ActivityOrderDetailBinding binding;

    private ArrayList<OrderInfo> dsOrderInfo = new ArrayList<>();
    private Order currentOrder;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));

        Intent intent = getIntent();
        currentOrder = (Order) intent.getSerializableExtra("Order");
        userId = intent.getStringExtra("userId");
    }

    @Override
    protected void onStart() {
        super.onStart();
        dsOrderInfo.clear();
        FirebaseDatabase.getInstance().getReference("Orders")
                .child(currentOrder.getOrderId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentOrder = snapshot.getValue(Order.class);
                initData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initData() {
        FirebaseDatabase.getInstance().getReference("OrderInfos").child(currentOrder.getOrderId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    OrderInfo tmp = item.getValue(OrderInfo.class);
                    dsOrderInfo.add(tmp);
                }
                // Update UI after data is retrieved
                initUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void initUI() {
        String status = currentOrder.getOrderStatus();
        if (status.equalsIgnoreCase("Completed")) {
            binding.lnOderDetail.btn.setVisibility(View.VISIBLE);
            binding.imgStatus.setImageResource(R.drawable.line_status_completed);
        } else if (status.equalsIgnoreCase("Shipping")) {
            binding.imgStatus.setImageResource(R.drawable.line_status_shipping);
        } else {
            binding.imgStatus.setImageResource(R.drawable.line_status_confirmed);
        }

        OrderDetailAdapter adapter = new OrderDetailAdapter(this, dsOrderInfo);
        binding.lnOderDetail.ryc.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.lnOderDetail.ryc.setAdapter(adapter);
        binding.lnOderDetail.ryc.setHasFixedSize(true);
        binding.lnOderDetail.txtTotalPrice.setText(String.format("%.2fđ", currentOrder.getTotalPrice()));
        binding.txtId.setText(currentOrder.getOrderId());
        FirebaseDatabase.getInstance().getReference().child("Addresses").child(currentOrder.getAddressId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Address address = snapshot.getValue(Address.class);
                if(address != null){
                    binding.lnOderDetail.txtAddress.setText(address.getDetailAddress());
                    binding.lnOderDetail.txtPhone.setText((address.getReceiverPhoneNumber()));
                    binding.lnOderDetail.txtRecipient.setText(address.getReceiverName());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.imgBack.setOnClickListener(view -> finish());

    }

    private void filterItemChecked() {
        Iterator<OrderInfo> iterator = dsOrderInfo.iterator();
        while (iterator.hasNext()) {
            OrderInfo orderInfo = iterator.next();
            if (orderInfo.isCheck()) {
                iterator.remove();
            }
        }
    }


}
