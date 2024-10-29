package com.example.foodorderingapp.Adapter.Home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.foodorderingapp.Fragments.Home.FoodCategoryFragment;

public class FoodCategoryAdapter extends FragmentStateAdapter {

    public FoodCategoryAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new FoodCategoryFragment();
    }
    @Override
    public int getItemCount() {
        return 1;
    }
}