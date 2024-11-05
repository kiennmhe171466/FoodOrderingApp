package com.example.foodorderingapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Model.Cart;
import com.example.foodorderingapp.Model.CartInfo;
import com.example.foodorderingapp.Helpers.FirebaseAddToCartHelper;
import com.example.foodorderingapp.databinding.ActivityProductDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductDetailActivity extends AppCompatActivity {
    private ActivityProductDetailBinding binding;
    private int productId;
    private String productName;
    private double productPrice;
    private String productDescription;
    private String productImage;
    private String userId;
    private FirebaseAddToCartHelper cartHelper;
    final boolean[] isCartExists = new boolean[1];
    final boolean[] isProductExists = new boolean[1];
    final Cart[] currentCart = {new Cart()};
    final CartInfo[] currentCartInfo = {new CartInfo()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        productId = intent.getIntExtra("productId", 0);
        productName = intent.getStringExtra("productName");
        productPrice = intent.getDoubleExtra("productPrice", 0);
        productImage = intent.getStringExtra("productImage");
        productDescription = intent.getStringExtra("productDescription");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        binding.titleTxt.setText(productName);
        binding.priceTxt.setText("$ " + productPrice);
        binding.descriptionTxt.setText(productDescription);
        Glide.with(this).load(productImage).into(binding.productImg);
        cartHelper = new FirebaseAddToCartHelper(userId, productId);
        loadCartData();

        binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(binding.numTxt.getText().toString());
                if(num == 0){
                    Toast.makeText(ProductDetailActivity.this, "You must choose quantity first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateCart(isCartExists[0], isProductExists[0], currentCart[0], currentCartInfo[0], num);
            }
        });

        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void loadCartData() {
        // load data cart

        new FirebaseAddToCartHelper(userId, productId).readCarts(new FirebaseAddToCartHelper.DataStatus() {

            @Override
            public void DataIsLoaded(Cart cart, CartInfo cartInfo, boolean isExistsCart, boolean isExistsProduct) {
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
    }

    public void AddQuantity(View view) {
        int num = Integer.parseInt(binding.numTxt.getText().toString());
        num++;
        if (num > 10) {
            num = 10;
            Toast.makeText(this, "Maximum quantity is 10", Toast.LENGTH_SHORT).show();
        }
        binding.numTxt.setText(String.valueOf(num));
        binding.totalPriceTxt.setText(String.format("$ %.2f", num * productPrice));
    }

    public void SubtractQuantity(View view) {
        int num = Integer.parseInt(binding.numTxt.getText().toString());
        if (num > 1) {
            num--;
        } else {
            Toast.makeText(this, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
        }
        binding.numTxt.setText(String.valueOf(num));
        binding.totalPriceTxt.setText(String.format("$ %.2f", num * productPrice));
    }

    public void updateCart(boolean isCartExists, boolean isProductExists, Cart currentCart, CartInfo currentCartInfo, int amount) {
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

                }

                @Override
                public void DataIsInserted() {
                    Toast.makeText(ProductDetailActivity.this, "Added to your favourite list", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void DataIsUpdated() {
                }

                @Override
                public void DataIsDeleted() {
                }
            });
        } else {
            // truong hop chua co san pham hien tai trong gio hang
            if (!isProductExists) {
                CartInfo cartInfo = new CartInfo();
                cartInfo.setProductId(productId);
                cartInfo.setAmount(amount);
                currentCart.setTotalAmount(currentCart.getTotalAmount() + amount);
                currentCart.setTotalPrice(currentCart.getTotalPrice() + amount * productPrice);
                new FirebaseAddToCartHelper().updateCart(currentCart, cartInfo, false, new FirebaseAddToCartHelper.DataStatus() {

                    @Override
                    public void DataIsLoaded(Cart cart, CartInfo cartInfo, boolean isExistsCart, boolean isExistsProduct) {

                    }

                    @Override
                    public void DataIsInserted() {
                        Toast.makeText(ProductDetailActivity.this, "This product has been added in the cart!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void DataIsUpdated() {
                    }

                    @Override
                    public void DataIsDeleted() {

                    }
                });

            } else {
                // truong hop da co san pham hien tai trong gio hang
                currentCartInfo.setAmount(currentCartInfo.getAmount() + amount);
                currentCart.setTotalAmount(currentCart.getTotalAmount() + amount);
                currentCart.setTotalPrice(currentCart.getTotalPrice() + amount * productPrice);

                new FirebaseAddToCartHelper().updateCart(currentCart, currentCartInfo, true, new FirebaseAddToCartHelper.DataStatus() {
                    @Override
                    public void DataIsLoaded(Cart cart, CartInfo cartInfo, boolean isExistsCart, boolean isExistsProduct) {
                        // Handle data load if necessary
                    }

                    @Override
                    public void DataIsInserted() {
                        // Not applicable here
                    }

                    @Override
                    public void DataIsUpdated() {
                        Toast.makeText(ProductDetailActivity.this, "The product quantity has been updated in the cart!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void DataIsDeleted() {
                        // Not applicable here
                    }
                });
            }
        }
    }
}
