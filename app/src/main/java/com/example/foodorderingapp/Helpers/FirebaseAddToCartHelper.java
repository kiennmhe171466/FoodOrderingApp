package com.example.foodorderingapp.Helpers;

import androidx.annotation.NonNull;

import com.example.foodorderingapp.Model.Cart;
import com.example.foodorderingapp.Model.CartInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseAddToCartHelper {
    private DatabaseReference databaseReference;

    public FirebaseAddToCartHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Carts");
    }

    // Method to add item to user's cart
    public void addToCart(String userId, CartInfo cartInfo, OnCartActionListener listener) {
        // Query to find if the user's cart already exists
        databaseReference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User cart exists; update existing cart with new CartInfo
                    for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                        Cart existingCart = cartSnapshot.getValue(Cart.class);
                        String cartId = cartSnapshot.getKey();
                        addCartInfoToExistingCart(cartId, cartInfo, listener);
                        break;
                    }
                } else {
                    // No existing cart; create a new one
                    String newCartId = databaseReference.push().getKey();
                    Cart newCart = new Cart(newCartId, cartInfo.getAmount(), 0.0, userId);
                    databaseReference.child(newCartId).setValue(newCart)
                            .addOnSuccessListener(aVoid -> addCartInfoToExistingCart(newCartId, cartInfo, listener))
                            .addOnFailureListener(listener::onCartActionFailed);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onCartActionFailed(databaseError.toException());
            }
        });
    }

    // Helper method to add CartInfo to an existing cart
    private void addCartInfoToExistingCart(String cartId, CartInfo cartInfo, OnCartActionListener listener) {
        DatabaseReference cartInfoRef = databaseReference.child(cartId).child("cartInfos");

        // Check if the product already exists in CartInfo for this cart
        cartInfoRef.orderByChild("productId").equalTo(cartInfo.getProductId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Product with the same productId exists; update the quantity
                            for (DataSnapshot cartInfoSnapshot : dataSnapshot.getChildren()) {
                                CartInfo existingCartInfo = cartInfoSnapshot.getValue(CartInfo.class);
                                int updatedAmount = existingCartInfo.getAmount() + cartInfo.getAmount();
                                cartInfoSnapshot.getRef().child("amount").setValue(updatedAmount)
                                        .addOnSuccessListener(aVoid -> listener.onCartUpdated(cartId))
                                        .addOnFailureListener(listener::onCartActionFailed);
                                break;
                            }
                        } else {
                            // Product does not exist; add new CartInfo
                            DatabaseReference newCartInfoRef = cartInfoRef.push();
                            cartInfo.setCartInfoId(newCartInfoRef.getKey());

                            newCartInfoRef.setValue(cartInfo)
                                    .addOnSuccessListener(aVoid -> listener.onCartUpdated(cartId))
                                    .addOnFailureListener(listener::onCartActionFailed);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onCartActionFailed(databaseError.toException());
                    }
                });
    }

    // Listener interface for handling cart actions
    public interface OnCartActionListener {
        void onCartUpdated(String cartId);
        void onCartActionFailed(Exception e);
    }
}
