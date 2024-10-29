package com.example.foodorderingapp.Fragments.Home;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.foodorderingapp.Adapter.Home.CategoryItemAdapter;
import com.example.foodorderingapp.Adapter.Home.FoodAdapter;
import com.example.foodorderingapp.Adapter.Home.FoodCategoryAdapter;
import com.example.foodorderingapp.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private String userId;

    public HomeFragment(String id) {
        userId = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initUI();
        return binding.getRoot();
    }
    private void initUI() {
        FoodAdapter foodAdapter = new FoodAdapter(HomeFragment.this, userId);
        binding.foodListSlider.setAdapter(foodAdapter);
        binding.foodListSlider.setUserInputEnabled(false);

        FoodCategoryAdapter categoryAdapter = new FoodCategoryAdapter(HomeFragment.this);
        binding.categoryListSlider.setAdapter(categoryAdapter);
        binding.categoryListSlider.setUserInputEnabled(false);
//        binding.layoutSearchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), FindActivity.class);
//                intent.putExtra("userId", userId);
//                startActivity(intent);
//            }
//        });

    }
}