package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Domain.Category;
import com.example.foodorderingapp.databinding.ItemCategoryHomeBinding;

import java.util.ArrayList;

public class CategoryItemAdapter extends RecyclerView.Adapter {

    private final ArrayList<Category> categoryArrayList;
    private final Context mContext;
    public CategoryItemAdapter(ArrayList<Category> categoryArrayList, Context context) {
        this.categoryArrayList = categoryArrayList;
        this.mContext = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryItemAdapter.ViewHolder( ItemCategoryHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
      ViewHolder viewHolder = (ViewHolder) holder;
        Category category = categoryArrayList.get(position);
        viewHolder.binding.txtCategoryName.setText(category.getCategoryName());
        Glide.with(mContext)
                .load(category.getImagePath())
                .into(viewHolder.binding.imgCategory);
        holder.itemView.setOnClickListener(v -> {

        });
    }
    @Override
    public int getItemCount() {
        return categoryArrayList == null ? 0 : categoryArrayList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryHomeBinding binding;
        public ViewHolder(@NonNull ItemCategoryHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
