package com.example.foodorderingapp.Activities.Order;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.Activities.Feedback.FeedBackActivity;
import com.example.foodorderingapp.Order.OrderDetailAdapter;
import com.example.foodorderingapp.Domain.Bill;
import com.example.foodorderingapp.Domain.BillInfo;
import com.example.foodorderingapp.Domain.Notification;
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

    private ArrayList<BillInfo> dsBillInfo = new ArrayList<>();
    private Bill currentBill;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));

        // Get Intent
        Intent intent = getIntent();
        // Initialize data
        currentBill = (Bill) intent.getSerializableExtra("Bill");
        userId = intent.getStringExtra("userId");
    }

    @Override
    protected void onStart() {
        super.onStart();
        dsBillInfo.clear();
        FirebaseDatabase.getInstance().getReference("Bills").child(currentBill.getBillId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentBill = snapshot.getValue(Bill.class);
                initData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void initData() {
        FirebaseDatabase.getInstance().getReference("BillInfos").child(currentBill.getBillId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    BillInfo tmp = item.getValue(BillInfo.class);
                    dsBillInfo.add(tmp);
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

    private void initUI() {
        String status = currentBill.getOrderStatus();
        if (status.equalsIgnoreCase("Completed")) {
            binding.lnOderDetail.btn.setVisibility(View.VISIBLE);
            binding.imgStatus.setImageResource(R.drawable.line_status_completed);
        } else if (status.equalsIgnoreCase("Shipping")) {
            binding.imgStatus.setImageResource(R.drawable.line_status_shipping);
        } else {
            binding.imgStatus.setImageResource(R.drawable.line_status_confirmed);
        }

        OrderDetailAdapter adapter = new OrderDetailAdapter(this, dsBillInfo);
        binding.lnOderDetail.ryc.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.lnOderDetail.ryc.setAdapter(adapter);
        binding.lnOderDetail.ryc.setHasFixedSize(true);
        binding.lnOderDetail.txtTotalPrice.setText(convertToMoney(currentBill.getTotalPrice()) + "đ");
        binding.txtId.setText(currentBill.getBillId());
        binding.imgBack.setOnClickListener(view -> finish());

        // If all BillInfos have been feedbacked, disable feedback button
        if (currentBill.isCheckAllComment()) {
            binding.lnOderDetail.btn.setEnabled(false);
            binding.lnOderDetail.btn.setBackgroundResource(R.drawable.background_feedback_disnable_button);
        } else {
            binding.lnOderDetail.btn.setEnabled(true);
            binding.lnOderDetail.btn.setBackgroundResource(R.drawable.background_feedback_enable_button);
        }

        // Set click event for feedback button
        binding.lnOderDetail.btn.setOnClickListener(view -> {
            filterItemChecked();
            Intent intent = new Intent(OrderDetailActivity.this, FeedBackActivity.class);
            intent.putExtra("Current Bill", currentBill);
            intent.putExtra("List of billInfo", dsBillInfo);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private void filterItemChecked() {
        Iterator<BillInfo> iterator = dsBillInfo.iterator();
        while (iterator.hasNext()) {
            BillInfo billInfo = iterator.next();
            if (billInfo.isCheck()) {
                iterator.remove();
            }
        }
    }

    private String convertToMoney(long price) {
        String temp = String.valueOf(price);
        String output = "";
        int count = 3;
        for (int i = temp.length() - 1; i >= 0; i--) {
            count--;
            if (count == 0) {
                count = 3;
                output = "," + temp.charAt(i) + output;
            } else {
                output = temp.charAt(i) + output;
            }
        }

        if (output.charAt(0) == ',') {
            return output.substring(1);
        }

        return output;
    }
}
