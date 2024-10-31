package com.example.foodorderingapp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.foodorderingapp.Adapter.FindingAdapter;
import com.example.foodorderingapp.databinding.ActivityFindBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.foodorderingapp.Model.Product;

import java.util.ArrayList;

public class FindActivity extends AppCompatActivity {

    private ActivityFindBinding binding;
    private ArrayList<Product> productList = new ArrayList<>();
    private FindingAdapter adapter;
    String initialQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialQuery = getIntent().getStringExtra("query");
        adapter = new FindingAdapter(productList, this);
        binding.recycleFoodFindedList.setAdapter(adapter);

        initUI();
        initData();
    }

    private void initUI() {
        binding.searhView.setIconifiedByDefault(false);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.recycleFoodFindedList.setLayoutManager(layoutManager);
        binding.recycleFoodFindedList.setHasFixedSize(true);

        binding.btnBack.setOnClickListener(view -> finish());

        binding.searhView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void initData() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("Products");

        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        Product product = item.getValue(Product.class);
                        if (product != null) {
                            productList.add(product);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (initialQuery != null && !initialQuery.isEmpty()) {
                        binding.searhView.setQuery(initialQuery, false);
                        adapter.getFilter().filter(initialQuery);
                    }
                } else {
                    Log.d("FindActivity", "No products found in database.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FindActivity", "Database error: " + error.getMessage());
            }
        });
    }
}
