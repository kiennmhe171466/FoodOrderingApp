package com.example.foodorderingapp.Activities.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Helpers.FirebaseUserInfoHelper;
import com.example.foodorderingapp.Model.User;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ActivityProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        userId = "lvk31C6jYvhLut9Phubrnpqwbvb2"; // Example userId

        initToolbar();
        getUserInfo(ProfileActivity.this);

        binding.cardViewOrders.setOnClickListener(view -> {
            Intent intent1 = new Intent(ProfileActivity.this, OrderActivity.class);
            intent1.putExtra("userId", userId);
            startActivity(intent1);
        });

        binding.change.setOnClickListener(view -> showEditProfileDialog());
    }

    private void initToolbar() {
        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void getUserInfo(Context mContext) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    binding.userName.setText(user.getUserName());
                    binding.userEmail.setText(user.getEmail());
                    binding.userPhoneNumber.setText(user.getPhoneNumber());
                    binding.userDob.setText(user.getBirthDate());
                    Glide.with(mContext.getApplicationContext())
                            .load(user.getAvatarURL())
                            .placeholder(R.drawable.default_avatar)
                            .into(binding.userAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }

    private void showEditProfileDialog() {
        ViewHolder holder = new ViewHolder(R.layout.dialog_edit_profile);
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(holder)
                .setExpanded(true, LinearLayout.LayoutParams.WRAP_CONTENT) // Adjust as needed
                .create();

        // Access the dialog's content view directly from the holder
        View dialogView = holder.getInflatedView();

        EditText editUserName = dialogView.findViewById(R.id.edit_user_name);
        EditText editUserEmail = dialogView.findViewById(R.id.edit_user_email);
        EditText editUserPhone = dialogView.findViewById(R.id.edit_user_phone);
        EditText editUserDob = dialogView.findViewById(R.id.edit_user_dob);
        Button saveButton = dialogView.findViewById(R.id.save_button);

        // Populate existing user data in the EditTexts
        editUserName.setText(binding.userName.getText());
        editUserEmail.setText(binding.userEmail.getText());
        editUserPhone.setText(binding.userPhoneNumber.getText());
        editUserDob.setText(binding.userDob.getText());

        // Handle the save button click
        saveButton.setOnClickListener(v -> {
            String userName = editUserName.getText().toString().trim();
            String userEmail = editUserEmail.getText().toString().trim();
            String userPhone = editUserPhone.getText().toString().trim();
            String userDob = editUserDob.getText().toString().trim();

            // Create User object
            User user = new User();
            user.setUserId(userId);
            user.setUserName(userName);
            user.setEmail(userEmail);
            user.setPhoneNumber(userPhone);
            user.setBirthDate(userDob);

            // Update user info in Firebase
            FirebaseUserInfoHelper firebaseHelper = new FirebaseUserInfoHelper(this);
            firebaseHelper.updateUserInfo(userId, user);

            // Dismiss dialog
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

}
