package com.example.foodorderingapp.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;

import com.example.foodorderingapp.Activities.FindActivity;
import com.example.foodorderingapp.Adapter.FoodAdapter;
import com.example.foodorderingapp.Adapter.FoodCategoryAdapter;
import com.example.foodorderingapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initUI();
        return binding.getRoot();
    }
    private void initUI() {
        FoodAdapter foodAdapter = new FoodAdapter(HomeFragment.this);
        binding.foodListSlider.setAdapter(foodAdapter);
        binding.foodListSlider.setUserInputEnabled(false);

        FoodCategoryAdapter categoryAdapter = new FoodCategoryAdapter(HomeFragment.this);
        binding.categoryListSlider.setAdapter(categoryAdapter);
        binding.categoryListSlider.setUserInputEnabled(false);
        binding.searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getActivity(), FindActivity.class);
                intent.putExtra("query", query); 
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false; // Do nothing on text change
            }
        });

    }
}