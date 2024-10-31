package com.example.foodorderingapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.foodorderingapp.Adapter.FoodItemAdapter;
import com.example.foodorderingapp.Model.Product;
import com.example.foodorderingapp.databinding.FragmentFoodHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FoodListFragment extends Fragment {
    private FragmentFoodHomeBinding binding;
    private ArrayList<Product> foodList;
    private FoodItemAdapter adapter;

    public FoodListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFoodHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initData();
        initUI();
        return view;
    }

    private void initUI() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                getActivity().getApplicationContext(),
                2
        );
        binding.rycFoodHome.setLayoutManager(gridLayoutManager);

        adapter = new FoodItemAdapter(foodList, getContext());
        binding.rycFoodHome.setAdapter(adapter);
        binding.rycFoodHome.setHasFixedSize(true);
    }

    private void initData() {
        foodList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    if (product != null) {
                        foodList.add(product);
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