package com.example.foodorderingapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.foodorderingapp.Fragments.LoginFragment;
import com.example.foodorderingapp.Fragments.SignUpFragment;

public class LoginSignUpAdapter extends FragmentStateAdapter {

    public LoginSignUpAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position==0) {
            return  new LoginFragment();
        }
        return new SignUpFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}