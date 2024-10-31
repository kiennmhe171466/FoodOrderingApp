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

        binding.cartView.setHasFixedSize(true);
        // setlayout for cartview (current recylerview)
        binding.cartView.setLayoutManager(new LinearLayoutManager(this));

        cartInfoList = new ArrayList<>();

        // Get current products
        getCartProducts();

        // On click for proceed order (current not have proceed order activity)
//        binding.btnProceedOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(CartActivity.this, ProceedOrderActivity.class);
//                intent.putExtra("buyProducts", buyProducts);
//                String totalPriceDisplay = binding.totalPrice.getText().toString();
//                intent.putExtra("totalPrice", totalPriceDisplay);
//                intent.putExtra("userId",userId);
//                proceedOrderLauncher.launch(intent);
//            }
//        });

//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_cart);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    // Get cart products
    private void getCartProducts() {
        FirebaseDatabase.getInstance().getReference().child("Carts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Cart cart = ds.getValue(Cart.class);
                    if (cart.getUserId().equals(userId)) {
                        cartProductAdapter = new CartProductAdapter(CartActivity.this, cartInfoList, cart.getCartId(), isCheckAll,userId);
                        cartProductAdapter.setAdapterItemListener(new IAdapterItemListener() {
                            @Override
                            public void onCheckedItemCountChanged(int count, long price, ArrayList<CartInfo> selectedItems) {
                                binding.txtTotalAmount.setText("" + convertToMoney(price) + "đ");
                                buyProducts = selectedItems;

                                if (count > 0) {
                                    binding.btnProceedOrder.setEnabled(true);
                                }
                                else {
                                    binding.btnProceedOrder.setEnabled(false);
                                }
                            }

                            @Override
                            public void onAddClicked() {
                                reloadCartProducts();
                            }

                            @Override
                            public void onSubtractClicked() {
                                reloadCartProducts();
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

    // reload cart products

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

    private String convertToMoney(double price) {
        String temp = String.valueOf(price);
        String output = "";
        int count = 3;
        for (int i = temp.length() - 1; i >= 0; i--) {
            count--;
            if (count == 0) {
                count = 3;
                output = "," + temp.charAt(i) + output;
            }
            else {
                output = temp.charAt(i) + output;
            }
        }

        if (output.charAt(0) == ',')
            return output.substring(1);

        return output;
    }

}