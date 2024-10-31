package com.example.foodorderingapp.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.Adapter.ViewPagerAdapter;
import com.example.foodorderingapp.databinding.ActivityIntroBinding;
import com.google.firebase.auth.FirebaseAuth;

public class IntroActivity extends AppCompatActivity {
    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up ViewPager with adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        // Connect DotsIndicator with ViewPager2
        binding.dotsIndicator.setViewPager2(binding.viewPager);

        FirebaseAuth.getInstance().signOut();  // Sign out if needed
    }
}
