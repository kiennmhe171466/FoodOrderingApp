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
        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Carts");
    }

    // Method to add a new cart for a user
    public void addCart(Cart cart, OnCartAddedListener listener) {
        // Generate a unique key for the cart
        String cartId = databaseReference.push().getKey();
        cart.setCartId(cartId); // Set the generated cart ID

        // Add the cart to Firebase Realtime Database
        databaseReference.child(cartId).setValue(cart)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onCartAdded(cartId);
                    } else {
                        listener.onCartAddFailed(task.getException());
                    }
                });
    }

    // Listener interface for cart addition result
    public interface OnCartAddedListener {
        void onCartAdded(String cartId);
        void onCartAddFailed(Exception e);
    }
}
