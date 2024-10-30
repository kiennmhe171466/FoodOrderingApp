package com.example.foodorderingapp.Activities.Cart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorderingapp.R;
import com.example.foodorderingapp.Domain.Cart;
import  com.example.foodorderingapp.databinding.ActivityCartBinding;
import com.example.foodorderingapp.Domain.CartInfo;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private String userId;
    private ActivityCartBinding binding;

    // private CartProductAdapter cartProductAdapter;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Get cart products
    private void getCartProducts() {
    }

    private void initToolbar() {
        binding.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initProceedOrderLauncher() {
        // Init launcher
        proceedOrderLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                finish();
            }
        });
    }

}