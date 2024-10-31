package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Activities.ProductDetailActivity;
import com.example.foodorderingapp.Domain.Product;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ItemFoodHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FindingAdapter extends RecyclerView.Adapter implements Filterable {
    private ArrayList<Product> filteredProductList;
    private ArrayList<Product> productListAll;
    private Context mContext;
    public FindingAdapter(ArrayList<Product> ds,  Context context) {
        this.mContext = context;
        this.productListAll = ds;
        this.filteredProductList = ds;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemFoodHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product item = filteredProductList.get(position);
        if (item != null) {
            ViewHolder viewHolder = (ViewHolder) holder;

            if (position == 1) {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
                int margin = 120;
                layoutParams.setMargins(0, margin, 0, 0);
                holder.itemView.setLayoutParams(layoutParams);
            } else if (position == 0) {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
                int margin = 50;
                layoutParams.setMargins(0, margin, 0, 0);
                holder.itemView.setLayoutParams(layoutParams);
            }

            Glide.with(viewHolder.binding.getRoot())
                    .load(item.getProductImage())
                    .placeholder(R.drawable.image_default)
                    .into(viewHolder.binding.imgFood);

            viewHolder.binding.txtFoodName.setText(item.getProductName());

            viewHolder.binding.parentOfItemInHome.setOnClickListener(new View.OnClickListener() {
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
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return filteredProductList == null ? 0 : filteredProductList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String key = charSequence.toString();
                if (key.isEmpty())
                    filteredProductList = productListAll;
                else {
                    ArrayList<Product> tmp = new ArrayList<>();
                    key = key.toLowerCase();
                    for (Product item : productListAll) {
                        if (item.getProductName().toLowerCase().contains(key)) {
                            tmp.add(item);
                        }
                    }
                    filteredProductList = tmp;
                }
                FilterResults results = new FilterResults();
                results.values = filteredProductList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredProductList = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodHomeBinding binding;

        public ViewHolder(@NonNull ItemFoodHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
