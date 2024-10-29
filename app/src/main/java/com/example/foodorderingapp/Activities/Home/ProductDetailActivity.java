package com.example.foodorderingapp.Activities.Home;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.Domain.Food;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ActivityProductDetailBinding;

public class ProductDetailActivity extends AppCompatActivity {
    private ActivityProductDetailBinding binding;
    private Food object;
    private String productId;
    private String productName;
    private int productPrice;
    private String productDescription;
    private Double ratingStar;
    private String productImage1;
    private String productImage2;
    private String productImage3;
    private String productImage4;
    private String userName;
    private String userId;
    private String publisherId;
    private int sold;
    private String productType;
    private int remainAmount;
    private int ratingAmount;
    private String state;
    private boolean own;
    private Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}
