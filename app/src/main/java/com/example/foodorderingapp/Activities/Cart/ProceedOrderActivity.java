package com.example.foodorderingapp.Activities.Cart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorderingapp.Activities.HomeActivity;
import com.example.foodorderingapp.Adapter.OrderSummaryAdapter;
import com.example.foodorderingapp.Model.Address;
import com.example.foodorderingapp.Model.Cart;
import com.example.foodorderingapp.Model.CartInfo;
import com.example.foodorderingapp.Model.Order;
import com.example.foodorderingapp.Model.Product;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ActivityProceedOrderBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ProceedOrderActivity extends AppCompatActivity {
    private ActivityProceedOrderBinding binding;
    private OrderSummaryAdapter orderSummaryAdapter;
    private ArrayList<CartInfo> cartInfoList;
    private String totalPriceDisplay;
    private String userId;
    private ActivityResultLauncher<Intent> changeAddressLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set binding
        binding = ActivityProceedOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolbar();
        binding.recyclerViewOrderProduct.setLayoutManager(new LinearLayoutManager(this));
        // Retrieves Data from Intent
        cartInfoList = (ArrayList<CartInfo>) getIntent().getSerializableExtra("buyProducts");
        // Set adapter
        orderSummaryAdapter = new OrderSummaryAdapter(this, cartInfoList);
        binding.recyclerViewOrderProduct.setAdapter(orderSummaryAdapter);
        // Display total price
        totalPriceDisplay = getIntent().getStringExtra("totalPrice");
        binding.totalPrice.setText(totalPriceDisplay);

        userId = getIntent().getStringExtra("userId");

        // onclick for proceed order
        binding.complete.setOnClickListener(v -> processOrder());

    }

    private void processOrder() {
        // Get input values from the delivery info fields
        String name = binding.inputName.getText().toString().trim();
        String address = binding.inputAddress.getText().toString().trim();
        String phone = binding.inputPhoneNo.getText().toString().trim();

        // Check that all fields are filled
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create adress
        // Create order with adress link to it
        // Create order info

        // Filter new map to add to bills
        HashMap<String, CartInfo> cartInfoMap = new HashMap<>();
        for (CartInfo cartInfo : cartInfoList) {
            cartInfoMap.put(String.valueOf(cartInfo.getProductId()), cartInfo);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        // Tính tổng giá trị của giỏ hàng
        FirebaseDatabase.getInstance().getReference().child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalPrice = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    CartInfo cartInfo = cartInfoMap.get(product.getProductId());
                    if (cartInfo != null) {
                        totalPrice += (double) product.getProductPrice() * cartInfo.getAmount();
                    }
                }
                // Tạo address
                String addressId = FirebaseDatabase.getInstance().getReference().child("Addresses").push().getKey();
                Address newAddress = new Address(addressId,address , userId, name, phone);
                FirebaseDatabase.getInstance().getReference().child("Addresses").child(addressId).setValue(newAddress);

                // Tạo Order và lưu vào Firebase
                String orderId = FirebaseDatabase.getInstance().getReference().child("Orders").push().getKey();
                Order order = new Order(addressId, orderId, formatter.format(date), "New", userId, totalPrice, "no");
                FirebaseDatabase.getInstance().getReference().child("Orders").child(orderId).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Tạo OrderInfo cho từng CartInfo
                            for (CartInfo cartInfo : cartInfoList) {
                                String orderInfoId = FirebaseDatabase.getInstance().getReference("OrderInfos").push().getKey();
                                HashMap<String, Object> orderInfoMap = new HashMap<>();
                                orderInfoMap.put("amount", cartInfo.getAmount());
                                orderInfoMap.put("orderInfoId", orderInfoId);
                                orderInfoMap.put("productId", cartInfo.getProductId());

                                // Lưu OrderInfo theo cấu trúc
                                FirebaseDatabase.getInstance().getReference().child("OrderInfos").child(orderId).child(orderInfoId).setValue(orderInfoMap);

                                // Update Carts and CartInfos
                                FirebaseDatabase.getInstance().getReference().child("Carts").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            Cart cart = ds.getValue(Cart.class);
                                            if (cart.getUserId().equals(userId)) {
                                                FirebaseDatabase.getInstance().getReference().child("CartInfos").child(cart.getCartId()).child(cartInfo.getCartInfoId()).removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            // Cập nhật giỏ hàng
                            FirebaseDatabase.getInstance().getReference().child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    FirebaseDatabase.getInstance().getReference().child("Carts").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                            for (DataSnapshot ds : snapshot2.getChildren()) {
                                                Cart cart = ds.getValue(Cart.class);
                                                if (cart.getUserId().equals(userId)) {
                                                    FirebaseDatabase.getInstance().getReference().child("Carts").child(cart.getCartId()).child("totalAmount").setValue(0);
                                                    FirebaseDatabase.getInstance().getReference().child("Carts").child(cart.getCartId()).child("totalPrice").setValue(0);
                                                }
                                            }
                                            Toast.makeText(ProceedOrderActivity.this, "Order created successfully!", Toast.LENGTH_SHORT).show();


                                            cartInfoList.clear();
                                            Intent intent = new Intent(ProceedOrderActivity.this, HomeActivity.class);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void initToolbar() {
        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Proceed order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}