package com.example.foodorderingapp.Order;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Activities.Feedback.FeedBackActivity;
import com.example.foodorderingapp.Helpers.FirebaseNotificationHelper;
import com.example.foodorderingapp.Domain.Bill;
import com.example.foodorderingapp.Domain.BillInfo;
import com.example.foodorderingapp.Domain.Comment;
import com.example.foodorderingapp.Domain.Notification;
import com.example.foodorderingapp.Domain.Product;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.LayoutFeedbackBillifoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<BillInfo> ds;
    private final Bill currentBill;
    private final String userId;

    public FeedBackAdapter(Context mContext, ArrayList<BillInfo> ds, Bill currentBill, String id) {
        this.mContext = mContext;
        this.ds = ds;
        this.currentBill = currentBill;
        this.userId = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutFeedbackBillifoBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillInfo item = ds.get(position);

        holder.binding.edtComment.setText("");

        // Using an int array to hold the star rating value, so it can be mutable
        int[] starRating = {5};

        setEventForStar(holder, starRating);
        holder.binding.star5.performClick();

        FirebaseDatabase.getInstance().getReference("Products").child(item.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product tmp = snapshot.getValue(Product.class);
                if (tmp != null) {
                    holder.binding.lnBillInfo.txtPrice.setText(formatCurrency(item.getAmount() * tmp.getProductPrice()));
                    holder.binding.lnBillInfo.txtName.setText(tmp.getProductName());
                    holder.binding.lnBillInfo.txtCount.setText("Count: " + item.getAmount());
                    Glide.with(mContext)
                            .load(tmp.getProductImage())
                            .placeholder(R.drawable.default_image)
                            .into(holder.binding.lnBillInfo.imgFood);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        holder.binding.edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() >= 200) {
                    Toast.makeText(mContext, "Your comment's length must not be over 200 characters!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        holder.binding.btnSend.setOnClickListener(view -> {
            if (!holder.binding.edtComment.getText().toString().isEmpty()) {
                String commentId = FirebaseDatabase.getInstance().getReference().push().getKey();
                Comment comment = new Comment(holder.binding.edtComment.getText().toString().trim(), commentId, userId, starRating[0]);

                FirebaseDatabase.getInstance().getReference("Comments").child(item.getProductId()).child(commentId).setValue(comment).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Thank you for giving feedback on our product!", Toast.LENGTH_SHORT).show();
                        pushNotificationFeedBack(item);
                        updateListBillInfo(item);

                        FirebaseDatabase.getInstance().getReference().child("Products").child(item.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int ratingAmount = snapshot.child("ratingAmount").getValue(int.class) + 1;
                                double ratingStar = (snapshot.child("ratingStar").getValue(double.class) * snapshot.child("ratingAmount").getValue(int.class) + starRating[0]) / ratingAmount;

                                FirebaseDatabase.getInstance().getReference().child("Products").child(item.getProductId()).child("ratingAmount").setValue(ratingAmount);
                                FirebaseDatabase.getInstance().getReference().child("Products").child(item.getProductId()).child("ratingStar").setValue(ratingStar);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    } else {
                        Toast.makeText(mContext, "Some errors occurred!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(mContext, "Please enter a comment before submitting!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateListBillInfo(BillInfo item) {
        ds.remove(item);
        notifyDataSetChanged();
        FirebaseDatabase.getInstance().getReference("BillInfos").child(currentBill.getBillId()).child(item.getBillInfoId()).child("check").setValue(true);

        if (ds.isEmpty()) {
            FirebaseDatabase.getInstance().getReference("Bills").child(currentBill.getBillId()).child("checkAllComment")
                    .setValue(true);
            FeedBackActivity activity = getFeedBackActivity(mContext);
            if (activity != null) {
                activity.finish();
            }
        }
    }

    private void setEventForStar(ViewHolder viewHolder, int[] starRating) {
        viewHolder.binding.star1.setOnClickListener(view -> onStarClicked(view, viewHolder, starRating));
        viewHolder.binding.star2.setOnClickListener(view -> onStarClicked(view, viewHolder, starRating));
        viewHolder.binding.star3.setOnClickListener(view -> onStarClicked(view, viewHolder, starRating));
        viewHolder.binding.star4.setOnClickListener(view -> onStarClicked(view, viewHolder, starRating));
        viewHolder.binding.star5.setOnClickListener(view -> onStarClicked(view, viewHolder, starRating));
    }

    private void onStarClicked(View view, ViewHolder viewHolder, int[] starRating) {
        int clickedStarPosition = Integer.parseInt(view.getTag().toString());
        starRating[0] = clickedStarPosition;
        viewHolder.binding.star1.setImageResource(clickedStarPosition >= 1 ? R.drawable.star_filled : R.drawable.star_none);
        viewHolder.binding.star2.setImageResource(clickedStarPosition >= 2 ? R.drawable.star_filled : R.drawable.star_none);
        viewHolder.binding.star3.setImageResource(clickedStarPosition >= 3 ? R.drawable.star_filled : R.drawable.star_none);
        viewHolder.binding.star4.setImageResource(clickedStarPosition >= 4 ? R.drawable.star_filled : R.drawable.star_none);
        viewHolder.binding.star5.setImageResource(clickedStarPosition >= 5 ? R.drawable.star_filled : R.drawable.star_none);
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }

    @Override
    public int getItemCount() {
        return ds == null ? 0 : ds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LayoutFeedbackBillifoBinding binding;

        public ViewHolder(@NonNull LayoutFeedbackBillifoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void pushNotificationFeedBack(BillInfo billInfo) {
        FirebaseDatabase.getInstance().getReference().child("Products").child(billInfo.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    String title = "Product feedback";
                    String content = "Your product '" + product.getProductName() + "' just received new feedback.";
                    Notification notification = FirebaseNotificationHelper.createNotification(title, content, product.getProductImage(), String.valueOf(product.getProductId()), "None", "None", null);
                    new FirebaseNotificationHelper(mContext).addNotification(product.getPublisherId(), notification, new FirebaseNotificationHelper.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<Notification> notificationList, List<Notification> notificationListToNotify) {}

                        @Override
                        public void DataIsInserted() {}

                        @Override
                        public void DataIsUpdated() {}

                        @Override
                        public void DataIsDeleted() {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
