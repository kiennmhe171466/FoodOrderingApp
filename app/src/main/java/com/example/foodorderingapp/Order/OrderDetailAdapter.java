package com.example.foodorderingapp.Order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Domain.BillInfo;
import com.example.foodorderingapp.Domain.Product;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ItemBillinfoBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    Context context;
    ArrayList<BillInfo> ds;

    public OrderDetailAdapter(Context context, ArrayList<BillInfo> ds) {
        this.context = context;
        this.ds = ds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemBillinfoBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillInfo billInfo = ds.get(position);
        FirebaseDatabase.getInstance().getReference("Products").child(billInfo.getProductId()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product tmp = snapshot.getValue(Product.class);
                        if (tmp != null) {
                            holder.binding.txtName.setText(tmp.getProductName());
                            double productPrice = parsePrice(String.valueOf(tmp.getProductPrice()));
                            holder.binding.txtPrice.setText(formatCurrency(productPrice * billInfo.getAmount()));
                            Glide.with(context)
                                    .load(tmp.getProductImage())
                                    .placeholder(R.drawable.default_image)
                                    .into(holder.binding.imgFood);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error appropriately
                    }
                }
        );
        holder.binding.txtCount.setText("Count: " + billInfo.getAmount());
    }

    @Override
    public int getItemCount() {
        return ds == null ? 0 : ds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemBillinfoBinding binding;

        public ViewHolder(@NonNull ItemBillinfoBinding tmp) {
            super(tmp.getRoot());
            binding = tmp;
        }
    }

    // Method to parse product price safely from String to double
    private double parsePrice(String price) {
        try {
            return Double.parseDouble(price);
        } catch (NumberFormatException e) {
            return 0.0; // return 0.0 if parsing fails
        }
    }

    // Method to format currency
    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}
