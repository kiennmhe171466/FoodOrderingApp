package com.example.foodorderingapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Model.Cart;
import com.example.foodorderingapp.Model.CartInfo;
import com.example.foodorderingapp.Model.Product;
import com.example.foodorderingapp.Model.User;
import com.example.foodorderingapp.Helpers.FirebaseUserInfoHelper;
import com.example.foodorderingapp.Interfaces.IAdapterItemListener;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ItemProductCartBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ViewHolder> {
    private Context mContext;
    private List<CartInfo> mCartInfos;
    private String cartId;
    // private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private int checkedItemCount = 0;
    private long checkedItemPrice = 0;
    private IAdapterItemListener adapterItemListener;
    private String userId;
    private String userName;
    private ArrayList<CartInfo> selectedItems = new ArrayList<>();

    public CartProductAdapter(Context mContext, List<CartInfo> mCartInfos, String cartId, String id) {
        this.mContext = mContext;
        this.mCartInfos = mCartInfos;
        this.cartId = cartId;
        this.userId = id;
        // viewBinderHelper.setOpenOnlyOne(true);

        new FirebaseUserInfoHelper(mContext).readUserInfo(userId, new FirebaseUserInfoHelper.DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                userName = user.getUserName();
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemProductCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public int getItemCount()  {
        return mCartInfos == null ? 0 : mCartInfos.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get cart info from the list
        CartInfo cartInfo = mCartInfos.get(position);

        // Bind remove Product
        holder.binding.removeProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an AlertDialog Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete this product?")
                        .setMessage("Are you sure you want to remove this product from your cart?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Remove from Firebase CartInfos
                                FirebaseDatabase.getInstance().getReference().child("CartInfos")
                                        .child(cartId)
                                        .child(cartInfo.getCartInfoId())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Show a simple toast message
                                                    Toast.makeText(mContext, "Delete product successfully!", Toast.LENGTH_SHORT).show();
                                                    if (adapterItemListener != null) {
                                                        adapterItemListener.onDeleteProduct();
                                                    }
                                                }
                                            }
                                        });

                                // Update cart totals
                                FirebaseDatabase.getInstance().getReference().child("Carts")
                                        .child(cartId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Cart cart = snapshot.getValue(Cart.class);
                                                FirebaseDatabase.getInstance().getReference().child("Products")
                                                        .child(String.valueOf(cartInfo.getProductId()))
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                Product product = snapshot.getValue(Product.class);
                                                                int totalAmount = cart.getTotalAmount() - cartInfo.getAmount();
                                                                double totalPrice = cart.getTotalPrice() - (long) (product.getProductPrice() * cartInfo.getAmount());

                                                                HashMap<String, Object> map = new HashMap<>();
                                                                map.put("totalAmount", totalAmount);
                                                                map.put("totalPrice", totalPrice);
                                                                FirebaseDatabase.getInstance().getReference().child("Carts").child(cartId).updateChildren(map);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                // Handle error
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                // Handle error
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Close the dialog
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // Bind image, Bind name, Bind price for product (only once)
        //
        FirebaseDatabase.getInstance().getReference().child("Products").child(String.valueOf(cartInfo.getProductId())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                holder.binding.productName.setText(product.getProductName());
                holder.binding.productPrice.setText(product.getProductPrice().toString());
                Glide.with(mContext).load(product.getProductImage()).placeholder(R.mipmap.ic_launcher).into(holder.binding.productImage);
                holder.binding.productAmount.setText(String.valueOf(cartInfo.getAmount()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Bind add product button
        // => add into firebase CartInfo
        holder.binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // amount of product
                int amount = Integer.parseInt(holder.binding.productAmount.getText().toString());
                amount++;
                holder.binding.productAmount.setText(String.valueOf(amount));
                FirebaseDatabase.getInstance().getReference().child("CartInfos").child(cartId).child(cartInfo.getCartInfoId()).child("amount").setValue(amount);
                // update Cart
                FirebaseDatabase.getInstance().getReference().child("Carts").child(cartId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Cart cart = snapshot.getValue(Cart.class);
                        FirebaseDatabase.getInstance().getReference().child("Products").child(String.valueOf(cartInfo.getProductId())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                Product product = snapshot1.getValue(Product.class);
                                int totalAmount = cart.getTotalAmount() + 1;
                                double totalPrice = cart.getTotalPrice() + product.getProductPrice();

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("totalAmount", totalAmount);
                                map.put("totalPrice", totalPrice);
                                FirebaseDatabase.getInstance().getReference().child("Carts").child(cartId).updateChildren(map);
                                if (adapterItemListener != null) {
                                    adapterItemListener.onAddClicked(product.getProductPrice());
                                }
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
        });

        // Bind subtract product button
        // => subtract from firebase CartInfo
        holder.binding.subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // remove cart info or subtract the amount
                if (!holder.binding.productAmount.getText().toString().equals("1")) {
                    // Change display value
                    int amount = Integer.parseInt(holder.binding.productAmount.getText().toString());
                    amount--;
                    holder.binding.productAmount.setText(String.valueOf(amount));



                    // Save to firebase
                    FirebaseDatabase.getInstance().getReference().child("CartInfos").child(cartId).child(cartInfo.getCartInfoId()).child("amount").setValue(amount);

                    FirebaseDatabase.getInstance().getReference().child("Carts").child(cartId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Cart cart = snapshot.getValue(Cart.class);
                            FirebaseDatabase.getInstance().getReference().child("Products").child(String.valueOf(cartInfo.getProductId())).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    Product product = snapshot1.getValue(Product.class);
                                    int totalAmount = cart.getTotalAmount() - 1;
                                    double totalPrice = cart.getTotalPrice() - product.getProductPrice();

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("totalAmount", totalAmount);
                                    map.put("totalPrice", totalPrice);
                                    FirebaseDatabase.getInstance().getReference().child("Carts").child(cartId).updateChildren(map);
                                    if (adapterItemListener != null) {
                                        adapterItemListener.onAddClicked(product.getProductPrice());
                                    }
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
                else {
                    Toast.makeText(mContext, "Can't reduce anymore!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Show product details
//        holder.binding.itemContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseDatabase.getInstance().getReference().child("Products").child(cartInfo.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Product product = snapshot.getValue(Product.class);
//                        if (product != null) {
//                            Intent intent = new Intent(mContext, ProductInfoActivity.class);
//                            intent.putExtra("productId", product.getProductId());
//                            intent.putExtra("productName", product.getProductName());
//                            intent.putExtra("productPrice", product.getProductPrice());
//                            intent.putExtra("productImage1", product.getProductImage1());
//                            intent.putExtra("productImage2", product.getProductImage2());
//                            intent.putExtra("productImage3", product.getProductImage3());
//                            intent.putExtra("productImage4", product.getProductImage4());
//                            intent.putExtra("ratingStar", product.getRatingStar());
//                            intent.putExtra("productDescription", product.getDescription());
//                            intent.putExtra("publisherId", product.getPublisherId());
//                            intent.putExtra("sold", product.getSold());
//                            intent.putExtra("productType", product.getProductType());
//                            intent.putExtra("remainAmount", product.getRemainAmount());
//                            intent.putExtra("ratingAmount", product.getRatingAmount());
//                            intent.putExtra("state", product.getState());
//                            intent.putExtra("userId", userId);
//                            intent.putExtra("userName", userName);
//                            mContext.startActivity(intent);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        });

    }

    public void setAdapterItemListener(IAdapterItemListener adapterItemListener) {
        this.adapterItemListener = adapterItemListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductCartBinding binding;

        public ViewHolder(ItemProductCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
