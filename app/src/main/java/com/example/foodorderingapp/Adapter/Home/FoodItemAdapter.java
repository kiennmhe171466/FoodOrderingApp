package com.example.foodorderingapp.Adapter.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.foodorderingapp.Activities.Home.ProductDetailActivity;
import com.example.foodorderingapp.Domain.Product;
import com.example.foodorderingapp.databinding.ItemCategoryHomeBinding;
import com.example.foodorderingapp.databinding.ItemFoodHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FoodItemAdapter extends RecyclerView.Adapter {
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final ArrayList<Product> ds;
    private final String userId;
    private String userName;
    private final Context mContext;

    public FoodItemAdapter(ArrayList<Product> ds, String id, Context context) {
        mContext = context;
        this.ds = ds;
        userId = id;
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("userName").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemFoodHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false) );
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder newHolder = (ViewHolder) holder;
        Product item = ds.get(position);
        Glide.with(mContext)
                .load(item.getProductImage())
                .transform(new CenterCrop())
                .into(newHolder.binding.imgFood);
        newHolder.binding.txtFoodName.setText(item.getProductName());
        newHolder.binding.txtFoodPrice.setText(nf.format(item.getProductPrice()));
        newHolder.binding.parentOfItemInHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra("productId", item.getProductId());
                intent.putExtra("productName", item.getProductName());
                intent.putExtra("productPrice", item.getProductPrice());
                intent.putExtra("productImage", item.getProductImage());
                intent.putExtra("ratingStar", item.getRatingStar());
                intent.putExtra("productDescription", item.getDescription());
                //intent.putExtra("publisherId", item.getPublisherId());
                //intent.putExtra("sold", item.getSold());
                //intent.putExtra("productType", item.getProductType());
                //intent.putExtra("remainAmount", item.getRemainAmount());
                intent.putExtra("ratingAmount", item.getRatingAmount());
                intent.putExtra("state", item.getState());
                intent.putExtra("userId", userId);
                intent.putExtra("userName", userName);
                mContext.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return ds == null ? 0 : ds.size();
    }
    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodHomeBinding binding;
        public ViewHolder(@NonNull ItemFoodHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}