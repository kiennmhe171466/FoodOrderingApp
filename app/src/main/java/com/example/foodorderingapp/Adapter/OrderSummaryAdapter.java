package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Model.CartInfo;
import com.example.foodorderingapp.Model.Product;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ItemOrderSummaryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder> {
    private Context mContext;
    private List<CartInfo> mCartInfos;

    public OrderSummaryAdapter(Context mContext, List<CartInfo> mCartInfos) {
        this.mContext = mContext;
        this.mCartInfos = mCartInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderSummaryAdapter.ViewHolder(ItemOrderSummaryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartInfo cartInfo = mCartInfos.get(position);

        FirebaseDatabase.getInstance().getReference().child("Products").child(String.valueOf(cartInfo.getProductId())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                holder.binding.orderProductName.setText(product.getProductName());
                holder.binding.itemPrice.setText(product.getProductPrice().toString());
                Glide.with(mContext.getApplicationContext()).load(product.getProductImage()).placeholder(R.mipmap.ic_launcher).into(holder.binding.productImage2);
                holder.binding.amountOfProduct.setText(String.valueOf("Count: "+ cartInfo.getAmount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mCartInfos == null ? 0 : mCartInfos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderSummaryBinding binding;

        public ViewHolder(ItemOrderSummaryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
