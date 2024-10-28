package com.example.foodorderingapp.Activities.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.foodorderingapp.Activities.Order.OrderActivity;
import com.example.foodorderingapp.Helpers.FirebaseUserInfoHelper;
import com.example.foodorderingapp.Domain.User;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.databinding.ActivityProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private String userId;
    private Uri imageUri;  // To hold selected image URI

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

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
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .addValueEventListener(new ValueEventListener() {
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
                        Toast.makeText(ProfileActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showEditProfileDialog() {
        ViewHolder holder = new ViewHolder(R.layout.dialog_edit_profile);
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(holder)
                .setExpanded(true, LinearLayout.LayoutParams.WRAP_CONTENT)
                .create();

        View dialogView = holder.getInflatedView();

        ImageView editUserAvatar = dialogView.findViewById(R.id.edit_user_avatar);
        Button changePhotoButton = dialogView.findViewById(R.id.change_photo_button);
        EditText editUserName = dialogView.findViewById(R.id.edit_user_name);
        EditText editUserEmail = dialogView.findViewById(R.id.edit_user_email);
        EditText editUserPhone = dialogView.findViewById(R.id.edit_user_phone);
        EditText editUserDob = dialogView.findViewById(R.id.edit_user_dob);
        Button saveButton = dialogView.findViewById(R.id.save_button);

        // Load current avatar into the ImageView
        Glide.with(this).load(binding.userAvatar.getDrawable()).into(editUserAvatar);

        // Set other fields with current user information
        editUserName.setText(binding.userName.getText());
        editUserEmail.setText(binding.userEmail.getText());
        editUserPhone.setText(binding.userPhoneNumber.getText());
        editUserDob.setText(binding.userDob.getText());

        changePhotoButton.setOnClickListener(v -> openImagePicker());

        saveButton.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageToFirebase(dialog, editUserName, editUserEmail, editUserPhone, editUserDob);
            } else {
                saveUserInfoWithoutAvatar(editUserName, editUserEmail, editUserPhone, editUserDob);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.userAvatar.setImageURI(imageUri);  // Update UI with selected image
        }
    }

    private void uploadImageToFirebase(DialogPlus dialog, EditText editUserName, EditText editUserEmail,
                                       EditText editUserPhone, EditText editUserDob) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("avatars/" + userId + ".jpg");

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String avatarUrl = uri.toString();
                    saveUserInfoWithAvatar(avatarUrl, editUserName, editUserEmail, editUserPhone, editUserDob);
                    dialog.dismiss();
                })
        ).addOnFailureListener(e ->
                Toast.makeText(ProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show()
        );
    }

    private void saveUserInfoWithAvatar(String avatarUrl, EditText editUserName, EditText editUserEmail,
                                        EditText editUserPhone, EditText editUserDob) {
        User user = new User(userId, editUserEmail.getText().toString(), avatarUrl,
                editUserName.getText().toString(), editUserDob.getText().toString(),
                editUserPhone.getText().toString());

        FirebaseUserInfoHelper firebaseHelper = new FirebaseUserInfoHelper(this);
        firebaseHelper.updateUserInfo(userId, user);

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void saveUserInfoWithoutAvatar(EditText editUserName, EditText editUserEmail,
                                           EditText editUserPhone, EditText editUserDob) {
        User user = new User(userId, editUserEmail.getText().toString(), binding.userAvatar.getDrawable().toString(),
                editUserName.getText().toString(), editUserDob.getText().toString(),
                editUserPhone.getText().toString());

        FirebaseUserInfoHelper firebaseHelper = new FirebaseUserInfoHelper(this);
        firebaseHelper.updateUserInfo(userId, user);

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }
}
