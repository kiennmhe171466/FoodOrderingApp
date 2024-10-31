package com.example.foodorderingapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.foodorderingapp.Fragments.IntroFragment1;
import com.example.foodorderingapp.Fragments.IntroFragment2;
import com.example.foodorderingapp.Fragments.IntroFragment3;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new IntroFragment1();
            case 1:
                return new IntroFragment2();
            case 2:
                return new IntroFragment3();
            default:
                return new IntroFragment1();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}