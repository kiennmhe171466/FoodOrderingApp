package com.example.foodorderingapp.Activities.Home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPassword, newPassword, confirmPassword;
    private Button changePasswordButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);


        firebaseAuth = FirebaseAuth.getInstance();


        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
        changePasswordButton = findViewById(R.id.change_password_button);

        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPwd = currentPassword.getText().toString();
        String newPwd = newPassword.getText().toString();
        String confirmPwd = confirmPassword.getText().toString();

        // Validate input fields
        if (TextUtils.isEmpty(currentPwd) || TextUtils.isEmpty(newPwd) || TextUtils.isEmpty(confirmPwd)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPwd.length() < 6) {
            Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null && user.getEmail() != null) {

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPwd);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Change password
                    user.updatePassword(newPwd).addOnCompleteListener(passwordTask -> {
                        if (passwordTask.isSuccessful()) {
                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                        Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
