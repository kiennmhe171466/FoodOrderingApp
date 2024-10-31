package com.example.foodorderingapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.foodorderingapp.Fragments.FoodListFragment;

public class FoodAdapter extends FragmentStateAdapter {
    private final String userId;

    public FoodAdapter(@NonNull Fragment fragment, String id) {
        super(fragment);
        userId = id;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
            return new FoodListFragment(userId);
    }
    @Override
    public int getItemCount() {
        return 1;
    }
}