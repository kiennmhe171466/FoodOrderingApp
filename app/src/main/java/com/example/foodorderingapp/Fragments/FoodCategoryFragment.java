package com.example.foodorderingapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorderingapp.Adapter.CategoryItemAdapter;
import com.example.foodorderingapp.Model.Category;
import com.example.foodorderingapp.databinding.FragmentCategoryHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FoodCategoryFragment  extends Fragment {


    private FragmentCategoryHomeBinding binding;
    private ArrayList<Category> categoryList;
    private CategoryItemAdapter adapter;

    public FoodCategoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initData();
        initUI();
        return view;
    }

    private void initUI() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        binding.rycCategoryFood.setLayoutManager(linearLayoutManager);
        adapter = new CategoryItemAdapter(categoryList, getContext());
        binding.rycCategoryFood.setAdapter(adapter);
        binding.rycCategoryFood.setHasFixedSize(true);
    }

    private void initData() {
        categoryList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Category category = ds.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
