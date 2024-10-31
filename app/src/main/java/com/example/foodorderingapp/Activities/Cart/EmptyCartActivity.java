package com.example.foodorderingapp.Activities.Cart;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ActivityEmptyCartBinding;

public class EmptyCartActivity extends AppCompatActivity {
    private ActivityEmptyCartBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmptyCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolbar();
    }

    private void initToolbar() {
        binding.btnReturn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}