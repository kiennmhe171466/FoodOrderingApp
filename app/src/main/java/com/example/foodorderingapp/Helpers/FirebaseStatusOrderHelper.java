package com.example.foodorderingapp.Helpers;

import androidx.annotation.NonNull;

import com.example.foodorderingapp.Domain.Order;
import com.example.foodorderingapp.Domain.OrderInfo;
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

    public void readConfirmBills(String userId, final DataStatus dataStatus)
    {
        mReferenceStatusOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders.clear();
                boolean isExistingBill = false;
                for (DataSnapshot keyNode : snapshot.child("Bills").getChildren()) {
                    if (keyNode.child("senderId").getValue(String.class).equals(userId)
                    &&  keyNode.child("orderStatus").getValue(String.class).equals("Confirm")) {
                        Order order = keyNode.getValue(Order.class);
                        orders.add(order);
                        isExistingBill = true;
                    }
                }

                if (dataStatus != null) {
                    dataStatus.DataIsLoaded(orders, isExistingBill);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void readShippingBills(String userId, final DataStatus dataStatus)
    {
        mReferenceStatusOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders.clear();
                boolean isExistingShippintBill = false;
                for (DataSnapshot keyNode : snapshot.child("Bills").getChildren())
                {
                    if (keyNode.child("senderId").getValue(String.class).equals(userId)
                            &&  keyNode.child("orderStatus").getValue(String.class).equals("Shipping"))
                    {
                        Order order = keyNode.getValue(Order.class);
                        orders.add(order);
                        isExistingShippintBill = true;
                    }
                }

                if (dataStatus != null) {
                    dataStatus.DataIsLoaded(orders, isExistingShippintBill);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void readCompletedBills(String userId,final DataStatus dataStatus) {
        mReferenceStatusOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders.clear();
                boolean isExistingBill = false;
                for (DataSnapshot keyNode : snapshot.child("Bills").getChildren()) {
                    if (keyNode.child("senderId").getValue(String.class).equals(userId)
                            && keyNode.child("orderStatus").getValue(String.class).equals("Completed")) {
                        Order order = keyNode.getValue(Order.class);
                        orders.add(order);
                        isExistingBill = true;
                    }
                }

                if (dataStatus != null) {
                    dataStatus.DataIsLoaded(orders, isExistingBill);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setConfirmToShipping(String billId,final DataStatus dataStatus) {
        mReferenceStatusOrder.child("Bills").child(billId).child("orderStatus").setValue("Shipping")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (dataStatus != null) {
                            dataStatus.DataIsUpdated();
                        }
                    }
                });
    }
    public void setShippingToCompleted(String billId,final DataStatus dataStatus) {
        mReferenceStatusOrder.child("Bills").child(billId).child("orderStatus").setValue("Completed")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (dataStatus != null) {
                            dataStatus.DataIsUpdated();
                        }
                    }
                });

        // set sold and remainAmount value of Product
        orderInfoList = new ArrayList<>();
        soldValueList = new ArrayList<>();

        mReferenceStatusOrder.child("BillInfos").child(billId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot keyNode: snapshot.getChildren())
                {
                    OrderInfo orderInfo = keyNode.getValue(OrderInfo.class);
                    orderInfoList.add(orderInfo);
                }
                readSomeInfoOfBill();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void readSomeInfoOfBill() {
        mReferenceStatusOrder.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (OrderInfo info : orderInfoList) {
                    int sold = snapshot.child(info.getProductId()).child("sold").getValue(int.class) + info.getAmount();
                    soldValueList.add(sold);
                }
                updateSoldValueOfProduct();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateSoldValueOfProduct() {
        for (int i = 0; i < orderInfoList.size(); i++) {
            mReferenceStatusOrder.child("Products").child(orderInfoList.get(i).getProductId()).child("sold").setValue(soldValueList.get(i));
        }
    }
}
