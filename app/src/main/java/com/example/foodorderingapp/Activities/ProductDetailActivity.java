package com.example.foodorderingapp.Activities;

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
import com.example.foodorderingapp.databinding.ActivityProductDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductDetailActivity extends AppCompatActivity {
    private ActivityProductDetailBinding binding;
    private String productId;
    private String productName;
    private double productPrice;
    private String productDescription;
    private Double ratingStar;
    private String productImage;
    private String userName;
    private String userId;
    private String publisherId;
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
        productPrice = intent.getDoubleExtra("productPrice",0);
        productImage = intent.getStringExtra("productImage");
        userName = intent.getStringExtra("userName");
        productDescription = intent.getStringExtra("productDescription");
        userId = intent.getStringExtra("userId");
        state = intent.getStringExtra("state");


        binding.titleTxt.setText(productName);
        binding.priceTxt.setText(String.valueOf("VND " + productPrice));
        binding.descriptionTxt.setText(productDescription);
        Glide.with(ProductDetailActivity.this)
                .load(productImage)
                .into(binding.productImg);

        int num = Integer.parseInt(binding.numTxt.getText().toString());

        // load data cart
        /*final boolean[] isCartExists = new boolean[1];
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
        });*/

        binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(num);
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
    /*private void updateCart(boolean isCartExists, boolean isProductExists, Cart currentCart, CartInfo currentCartInfo, int amount) {
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
    }*/

    private void addToCart(int amount) {
        FirebaseAddToCartHelper cartHelper = new FirebaseAddToCartHelper(userId, productId);
        cartHelper.readCarts(new FirebaseAddToCartHelper.DataStatus() {
            @Override
            public void DataIsLoaded(Cart cart, CartInfo cartInfo, boolean isCartExists, boolean isProductExists) {
                if (!isCartExists) {
                    // No cart exists, create new cart and add product
                    createNewCart(amount);
                } else if (!isProductExists) {
                    // Cart exists, but product is not in it
                    addProductToExistingCart(cart, amount);
                } else {
                    // Product already in cart
                    Toast.makeText(ProductDetailActivity.this, "This product is already in the cart!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void DataIsInserted() {
                Toast.makeText(ProductDetailActivity.this, "Added to your cart!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void DataIsUpdated() {
                Toast.makeText(ProductDetailActivity.this, "Cart updated successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void DataIsDeleted() {
                // Not used in addToCart flow
            }
        });
    }

    private void createNewCart(int amount) {
        Cart newCart = new Cart();
        newCart.setUserId(userId);
        newCart.setTotalAmount(amount);
        newCart.setTotalPrice(productPrice * amount);

        CartInfo newCartInfo = new CartInfo();
        newCartInfo.setProductId(productId);
        newCartInfo.setAmount(amount);

        FirebaseAddToCartHelper cartHelper = new FirebaseAddToCartHelper();
        cartHelper.addCarts(newCart, newCartInfo, new FirebaseAddToCartHelper.DataStatus() {
            @Override
            public void DataIsLoaded(Cart cart, CartInfo cartInfo, boolean isExistsCart, boolean isExistsProduct) {
                // Not used here
            }

            @Override
            public void DataIsInserted() {
                Toast.makeText(ProductDetailActivity.this, "New cart created and product added!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void DataIsUpdated() {
                // Not used here
            }

            @Override
            public void DataIsDeleted() {
                // Not used here
            }
        });
    }

    private void addProductToExistingCart(Cart existingCart, int amount) {
        FirebaseDatabase.getInstance().getReference()
                .child("Products")
                .child(productId)
                .child("remainAmount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int remainAmount = snapshot.getValue(int.class);
                        if (amount <= remainAmount) {
                            // Update cart information
                            CartInfo newCartInfo = new CartInfo();
                            newCartInfo.setProductId(productId);
                            newCartInfo.setAmount(amount);

                            existingCart.setTotalAmount(existingCart.getTotalAmount() + amount);
                            existingCart.setTotalPrice(existingCart.getTotalPrice() + amount * productPrice);

                            FirebaseAddToCartHelper cartHelper = new FirebaseAddToCartHelper();
                            cartHelper.updateCart(existingCart, newCartInfo, false, new FirebaseAddToCartHelper.DataStatus() {
                                @Override
                                public void DataIsLoaded(Cart cart, CartInfo cartInfo, boolean isExistsCart, boolean isExistsProduct) {
                                    // Not used here
                                }

                                @Override
                                public void DataIsInserted() {
                                    Toast.makeText(ProductDetailActivity.this, "Product added to cart!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void DataIsUpdated() {
                                    Toast.makeText(ProductDetailActivity.this, "Cart updated with new product!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void DataIsDeleted() {
                                    // Not used here
                                }
                            });
                        } else {
                            Toast.makeText(ProductDetailActivity.this, "Insufficient stock available.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProductDetailActivity.this, "Error retrieving product information", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void AddQuantity(View view) {
        int num = Integer.parseInt(binding.numTxt.getText().toString());
        num++;
        binding.numTxt.setText(String.valueOf(num));
        binding.totalPriceTxt.setText(String.valueOf(num * productPrice));
        if(num > 10){
            Toast.makeText(ProductDetailActivity.this, "Your max quantity of order is 10", Toast.LENGTH_SHORT).show();
            binding.numTxt.setText(String.valueOf(10));
            binding.totalPriceTxt.setText(String.valueOf(10 * productPrice));
        }
    }

    public void SubtractQuantity(View view) {
        int num = Integer.parseInt(binding.numTxt.getText().toString());
        if (num > 0) {
            num--;
            binding.numTxt.setText(String.valueOf(num));
            binding.totalPriceTxt.setText(String.valueOf(num * productPrice));
        }else{
            Toast.makeText(ProductDetailActivity.this, "Your quantity is currently 0", Toast.LENGTH_SHORT).show();
        }
    }
}
