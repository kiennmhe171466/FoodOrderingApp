package com.example.foodorderingapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import com.example.foodorderingapp.Activities.Cart.CartActivity;
import com.example.foodorderingapp.Activities.Cart.EmptyCartActivity;
import com.example.foodorderingapp.Activities.Order.OrderActivity;
import com.example.foodorderingapp.Model.Cart;

import com.bumptech.glide.Glide;


import com.example.foodorderingapp.Fragments.HomeFragment;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ActivityHomeBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String userId;
    private ActivityHomeBinding binding;
    private LinearLayout layoutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        initUI();
        loadUserInfoToHeader();
    }
    protected void onStart() {
        super.onStart();
        loadUserInfoToHeader();
    }
    private void initUI() {
        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));
        binding.navigationLeft.bringToFront();
        createActionBar();
        setCartNavigation();
        layoutMain = binding.layoutMain;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(layoutMain.getId(), new HomeFragment())
                .commit();
        binding.navigationLeft.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_top, menu);
        return true;
    }

    private void createActionBar() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
    }

    private void setCartNavigation() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.cart_menu) {
                FirebaseDatabase.getInstance().getReference().child("Carts")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    Cart cart = ds.getValue(Cart.class);
                                    if (cart != null && cart.getUserId().equals(userId)) {
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("CartInfos").child(cart.getCartId())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.getChildrenCount() == 0) {
                                                            // Navigate to EmptyCartActivity if cart is empty
                                                            startActivity(new Intent(HomeActivity.this, EmptyCartActivity.class));
                                                        } else {
                                                            // Navigate to CartActivity if cart has items
                                                            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                                                            intent.putExtra("userId", userId);
                                                            startActivity(intent);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(HomeActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profileMenu) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        } else if (item.getItemId() == R.id.orderMenu) {
            Intent intent1 = new Intent(this, OrderActivity.class);
            intent1.putExtra("userId", userId);
            startActivity(intent1);
        }else if (item.getItemId() == R.id.chwg_pwd) {

            startActivity(new Intent(this, ChangePasswordActivity.class));
        } else if (item.getItemId() == R.id.logoutMenu) {
            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Logout")
                    .setMessage("Do you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(HomeActivity.this, "Logout successfully!", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        binding.drawLayoutHome.close();
        return true;
    }
    private void loadUserInfoToHeader() {
        // Get the header view from the NavigationView
        View headerView = binding.navigationLeft.getHeaderView(0);

        // Find the views inside the header layout
        ShapeableImageView imgAvatar = headerView.findViewById(R.id.imgAvatarInNavigationBar);
        TextView txtName = headerView.findViewById(R.id.txtNameInNavigationBar);
        TextView txtGreeting = headerView.findViewById(R.id.txtHelloInNavigationBar);

        // Load user data from Firebase
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = snapshot.child("userName").getValue(String.class);
                            String avatarUrl = snapshot.child("avatarURL").getValue(String.class);

                            // Set the user's name and greeting
                            txtName.setText("Hi, " + name);
                            txtGreeting.setText("Have a good day!");

                            // Load the avatar using Glide
                            Glide.with(HomeActivity.this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.user)  // Default avatar if URL is null
                                    .into(imgAvatar);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomeActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            binding.drawLayoutHome.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}