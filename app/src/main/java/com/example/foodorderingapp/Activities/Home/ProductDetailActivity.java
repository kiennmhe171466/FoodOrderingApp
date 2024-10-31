package com.example.foodorderingapp.Activities.Home;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Domain.Cart;
import com.example.foodorderingapp.Domain.CartInfo;
import com.example.foodorderingapp.Helpers.FirebaseAddToCartHelper;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ActivityProductDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductDetailActivity extends AppCompatActivity {
    private ActivityProductDetailBinding binding;
    private String productId;
    private String productName;
    private int productPrice;
    private String productDescription;
    private Double ratingStar;
    private String productImage;
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

        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");
        productName = intent.getStringExtra("productName");
        productPrice = intent.getIntExtra("productPrice",0);
        productImage = intent.getStringExtra("productImage");
        //ratingStar = intent.getDoubleExtra("ratingStar",0.0);
        userName = intent.getStringExtra("userName");
        productDescription = intent.getStringExtra("productDescription");
        //publisherId = intent.getStringExtra("publisherId");
        userId = intent.getStringExtra("userId");
        //sold = intent.getIntExtra("sold",0);
        //productType = intent.getStringExtra("productType");
        //remainAmount = intent.getIntExtra("remainAmount", 0);
        //ratingAmount = intent.getIntExtra("ratingAmount", 0);
        state = intent.getStringExtra("state");

        // set up default value
        binding.titleTxt.setText(productName);
        binding.descriptionTxt.setText(productDescription);
        Glide.with(ProductDetailActivity.this)
                .load(productImage)
                .into(binding.productImg);
        //binding.txtSell.setText(String.valueOf(sold));
        //binding.ratingBar.setRating(ratingStar.floatValue());
        //binding.txtRemainAmount.setText(String.valueOf(remainAmount));

        // load data cart
        final boolean[] isCartExists = new boolean[1];
        final boolean[] isProductExists = new boolean[1];
        final Cart[] currentCart = {new Cart()};
        final CartInfo[] currentCartInfo = {new CartInfo()};
        new FirebaseAddToCartHelper(userId,productId).readCarts(new FirebaseAddToCartHelper.DataStatus() {

            @Override
            public void DataIsLoaded(Cart cart,CartInfo cartInfo, boolean isExistsCart, boolean isExistsProduct) {
                isCartExists[0] = isExistsCart;
                isProductExists[0] = isExistsProduct;
                currentCart[0] = cart;
                currentCartInfo[0] = cartInfo;
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });

        binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCart(isCartExists[0],isProductExists[0],currentCart[0],currentCartInfo[0],1);
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Function
    private void updateCart(boolean isCartExists, boolean isProductExists, Cart currentCart, CartInfo currentCartInfo, int amount) {
        // Case when the user is new and does not have a cart yet
        if (!isCartExists) {
            Cart cart = new Cart();
            cart.setTotalPrice(productPrice * amount);
            cart.setTotalAmount(amount);
            cart.setUserId(userId);

            CartInfo cartInfo = new CartInfo();
            cartInfo.setAmount(amount);
            cartInfo.setProductId(productId);

            new FirebaseAddToCartHelper().addCarts(cart, cartInfo, new FirebaseAddToCartHelper.DataStatus() {
                @Override
                public void DataIsLoaded(Cart cart, CartInfo cartInfo, boolean isExistsCart, boolean isExistsProduct) {
                    // No action needed here for this case
                }

                @Override
                public void DataIsInserted() {
                    Toast.makeText(ProductDetailActivity.this, "Added to your favourite list", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void DataIsUpdated() {
                    // No action needed here for this case
                }

                @Override
                public void DataIsDeleted() {
                    // No action needed here for this case
                }
            });
        } else {
            // Case when the cart exists but does not include the current product
            if (!isProductExists) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Products")
                        .child(productId)
                        .child("remainAmount")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int remainAmount = snapshot.getValue(int.class);
                                if (amount <= remainAmount) {
                                    CartInfo cartInfo = new CartInfo();
                                    cartInfo.setProductId(productId);
                                    cartInfo.setAmount(amount);

                                    currentCart.setTotalAmount(currentCart.getTotalAmount() + amount);
                                    currentCart.setTotalPrice(currentCart.getTotalPrice() + amount * productPrice);

                                    new FirebaseAddToCartHelper().updateCart(currentCart, cartInfo, false, new FirebaseAddToCartHelper.DataStatus() {
                                        @Override
                                        public void DataIsLoaded(Cart cart, CartInfo cartInfo, boolean isExistsCart, boolean isExistsProduct) {
                                            // No action needed here for this case
                                        }

                                        @Override
                                        public void DataIsInserted() {
                                            Toast.makeText(ProductDetailActivity.this, "Added to your cart", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void DataIsUpdated() {
                                            // No action needed here for this case
                                        }

                                        @Override
                                        public void DataIsDeleted() {
                                            // No action needed here for this case
                                        }
                                    });
                                } else {
                                    Toast.makeText(ProductDetailActivity.this, "Insufficient stock available", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ProductDetailActivity.this, "Error retrieving product information", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Case when the product is already in the cart
                Toast.makeText(ProductDetailActivity.this, "This product has already been in the cart!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
