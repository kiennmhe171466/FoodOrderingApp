package com.example.foodorderingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Activities.Order.OrderActivity;
import com.example.foodorderingapp.Activities.Order.OrderDetailActivity;
import com.example.foodorderingapp.Helpers.FirebaseStatusOrderHelper;
import com.example.foodorderingapp.Model.Order;
import com.example.foodorderingapp.Model.OrderInfo;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ItemOrderLayoutBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Order> dsOrder;
    private int type;
    private String userId;

    public OrderAdapter(Context context, ArrayList<Order> dsOrder, int type, String id) {
        this.context = context;
        this.dsOrder = dsOrder;
        this.type = type;
        this.userId = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOrderLayoutBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order tmp = dsOrder.get(position);
        holder.binding.txtId.setText(tmp.getBillId() + "");
        holder.binding.txtDate.setText(tmp.getOrderDate() + "");
        holder.binding.txtStatus.setText(tmp.getOrderStatus());

        if (type == OrderActivity.CURRENT_ORDER) {
            holder.binding.btnSee.setText("Received");
            holder.binding.btnSee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Confirm order status update directly
                    new FirebaseStatusOrderHelper().setShippingToCompleted(tmp.getBillId(), new FirebaseStatusOrderHelper.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<Order> orders, boolean isExistingBill) {
                        }

                        @Override
                        public void DataIsInserted() {
                        }

                        @Override
                        public void DataIsUpdated() {
                            Toast.makeText(context, "Your order has been changed to completed state!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void DataIsDeleted() {
                        }
                    });
                }
            });
        } else {
            holder.binding.txtStatus.setTextColor(Color.parseColor("#48DC7D"));
            holder.binding.btnSee.setText("Feedback & Rate");
            if (tmp.isCheckAllComment()) {
                holder.binding.btnSee.setEnabled(false);
                holder.binding.btnSee.setBackgroundResource(R.drawable.background_feedback_disnable_button);
            } else {
                holder.binding.btnSee.setEnabled(true);
                holder.binding.btnSee.setBackgroundResource(R.drawable.background_feedback_enable_button);
            }
            holder.binding.btnSee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    intent.putExtra("Order", tmp);
                    intent.putExtra("userId", userId);
                    context.startActivity(intent);
                }
            });
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("Order", tmp);
                intent.putExtra("userId", userId);
                context.startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference("BillInfos").child(tmp.getBillId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                OrderInfo tmp = new OrderInfo();
                for (DataSnapshot item : snapshot.getChildren()) {
                    tmp = item.getValue(OrderInfo.class);
                    break;
                }
                FirebaseDatabase.getInstance().getReference("Products").child(tmp.getProductId()).child("productImage1").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Glide.with(context)
                                .load(snapshot.getValue(String.class))
                                .placeholder(R.drawable.default_image)
                                .into(holder.binding.imgFood);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return dsOrder == null ? 0 : dsOrder.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderLayoutBinding binding;

        public ViewHolder(@NonNull ItemOrderLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
