package com.example.foodorderingapp.Helpers;

import androidx.annotation.NonNull;

import com.example.foodorderingapp.Model.Order;
import com.example.foodorderingapp.Model.OrderInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseStatusOrderHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceStatusOrder;
    private List<Order> orders = new ArrayList<>();
    private String userId;
    private List<OrderInfo> orderInfoList = new ArrayList<>();
    private List<Integer> soldValueList = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<Order> orders, boolean isExistingBill);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();

    }

    public FirebaseStatusOrderHelper(String user) {
        userId = user;
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceStatusOrder = mDatabase.getReference();
    }

    public FirebaseStatusOrderHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceStatusOrder = mDatabase.getReference();
    }

    public void setShippingToCompleted(String billId,final DataStatus dataStatus) {
        mReferenceStatusOrder.child("Orders").child(billId).child("orderStatus").setValue("Completed")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (dataStatus != null) {
                            dataStatus.DataIsUpdated();
                        }
                    }
                });
    }
}
