package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.foodorderingapp.Activities.ProductDetailActivity;
import com.example.foodorderingapp.Model.Product;
import com.example.foodorderingapp.databinding.ItemFoodHomeBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FoodItemAdapter extends RecyclerView.Adapter {
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "us"));
    private final ArrayList<Product> ds;
    private String userName;
    private final Context mContext;

    public FoodItemAdapter(ArrayList<Product> ds, Context context) {
        mContext = context;
        this.ds = ds;
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
                intent.putExtra("ratingAmount", item.getRatingAmount());
                intent.putExtra("state", item.getState());
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