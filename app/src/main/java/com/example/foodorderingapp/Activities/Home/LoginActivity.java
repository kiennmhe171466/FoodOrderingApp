package com.example.foodorderingapp.Activities.Home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.foodorderingapp.Adapter.Home.LoginSignUpAdapter;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ActivityLoginBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginSignUpAdapter myFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));
        myFragmentAdapter = new LoginSignUpAdapter(LoginActivity.this);
        binding.viewpaper2.setAdapter(myFragmentAdapter);
        TabLayout.Tab tabLogin = binding.tablayoutHome.newTab();
        tabLogin.setText("Login");
        binding.tablayoutHome.addTab(binding.tablayoutHome.newTab().setText("Login"));
        binding.tablayoutHome.addTab(binding.tablayoutHome.newTab().setText("Sign Up"));
        binding.tablayoutHome.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewpaper2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.viewpaper2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tablayoutHome.selectTab(binding.tablayoutHome.getTabAt(position));
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Sign out the user when the app is closed
        FirebaseAuth.getInstance().signOut();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
    }
}