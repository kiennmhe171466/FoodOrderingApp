package com.example.foodorderingapp.Activities.Cart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorderingapp.Adapter.CartProductAdapter;
import com.example.foodorderingapp.Interfaces.IAdapterItemListener;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.Model.Cart;
import  com.example.foodorderingapp.databinding.ActivityCartBinding;
import com.example.foodorderingapp.Model.CartInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private String userId;
    private ActivityCartBinding binding;

    private CartProductAdapter cartProductAdapter;
    private List<CartInfo> cartInfoList;

    private boolean isCheckAll = false;
    private ArrayList<CartInfo> buyProducts = new ArrayList<>();
    private ActivityResultLauncher<Intent> proceedOrderLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getStringExtra("userId");
        initToolbar();
        initProceedOrderLauncher();
        // setlayout for cartview (current recylerview)
        binding.cartView.setLayoutManager(new LinearLayoutManager(this));

        cartInfoList = new ArrayList<>();

        // Get current products
        getCartProducts();

         // On click for proceed order (current not have proceed order activity)
        binding.btnProceedOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyProducts = new ArrayList<>(cartInfoList);
                Intent intent = new Intent(CartActivity.this, ProceedOrderActivity.class);
                intent.putExtra("buyProducts", buyProducts);
                String totalPriceDisplay = binding.txtTotalAmount.getText().toString();
                intent.putExtra("totalPrice", totalPriceDisplay);
                intent.putExtra("userId",userId);
                proceedOrderLauncher.launch(intent);
            }
        });
    }

    // Get cart products
    private void getCartProducts() {
        FirebaseDatabase.getInstance().getReference().child("Carts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Cart cart = ds.getValue(Cart.class);
                    if (cart.getUserId().equals(userId)) {
                        binding.txtTotalAmount.setText(String.format("%.2f", cart.getTotalPrice()));
                        cartProductAdapter = new CartProductAdapter(CartActivity.this, cartInfoList, cart.getCartId(),userId);
                        cartProductAdapter.setAdapterItemListener(new IAdapterItemListener() {
                            @Override
                            public void onAddClicked(double amount) {
                                reloadTotal();
                            }

                            @Override
                            public void onSubtractClicked(double amount) {
                                reloadTotal();
                            }

                            @Override
                            public void onDeleteProduct() {
                                reloadCartProducts();
                            }
                        });
                        binding.cartView.setAdapter(cartProductAdapter);

                        FirebaseDatabase.getInstance().getReference().child("CartInfos").child(cart.getCartId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                cartInfoList.clear();
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    CartInfo cartInfo = ds.getValue(CartInfo.class);
                                    cartInfoList.add(cartInfo);
                                }
                                cartProductAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // reload cart total
    private void reloadTotal(){
        FirebaseDatabase.getInstance().getReference().child("Carts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Cart cart = ds.getValue(Cart.class);
                    if (cart.getUserId().equals(userId)) {
                        binding.txtTotalAmount.setText(String.format("%.2f$", cart.getTotalPrice()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // set return button
    private void initToolbar() {
        binding.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Reload cart (remove item, add, ...)
    private void reloadCartProducts() {
        FirebaseDatabase.getInstance().getReference().child("Carts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Cart cart = ds.getValue(Cart.class);
                    if (cart.getUserId().equals(userId)) {
                        binding.txtTotalAmount.setText(String.format("%.2f$", cart.getTotalPrice()));
                        FirebaseDatabase.getInstance().getReference().child("CartInfos").child(cart.getCartId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                cartInfoList.clear();
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    CartInfo cartInfo = ds.getValue(CartInfo.class);
                                    cartInfoList.add(cartInfo);
                                }
                                cartProductAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // proceed order
    private void initProceedOrderLauncher() {
        // Init launcher
        proceedOrderLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                finish();
            }
        });
    }

}