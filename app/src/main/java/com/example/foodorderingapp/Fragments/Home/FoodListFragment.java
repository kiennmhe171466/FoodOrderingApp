package com.example.foodorderingapp.Fragments.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorderingapp.Adapter.Home.FoodAdapter;
import com.example.foodorderingapp.Adapter.Home.FoodItemAdapter;
import com.example.foodorderingapp.Domain.Product;
import com.example.foodorderingapp.databinding.FragmentFoodHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FoodListFragment extends Fragment {
    private FragmentFoodHomeBinding binding;
    private ArrayList<Product> foodList;
    private FoodItemAdapter adapter;
    private String userId;

    public FoodListFragment(String id) {
        userId = id;
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
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getActivity().getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        binding.rycFoodHome.setLayoutManager(linearLayoutManager);
        adapter = new FoodItemAdapter(foodList, userId, getContext());
        binding.rycFoodHome.setAdapter(adapter);
        binding.rycFoodHome.setHasFixedSize(true);
//        binding.txtSeemoreDrink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), FindActivity.class);
//                intent.putExtra("userId", userId);
//                startActivity(intent);
//            }
//        });
    }

    private void initData() {
        foodList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Products")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    if (product != null && !product.getState().equals("deleted")
                            && product.getProductType().equalsIgnoreCase("Food")) {
                        foodList.add(product);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}