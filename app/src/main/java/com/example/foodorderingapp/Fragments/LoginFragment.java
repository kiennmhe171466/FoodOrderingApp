package com.example.foodorderingapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.foodorderingapp.Activities.ForgotPasswordActivity;
import com.example.foodorderingapp.Activities.HomeActivity;
import com.example.foodorderingapp.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private static final String TAG = "firebase - LOGIN";

    // SharedPreferences keys
    private static final String PREF_NAME = "loginPrefs";
    private static final String KEY_REMEMBER_ME = "rememberMe";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Load saved preferences
        loadSavedPreferences();

        // Handle Login button click
        binding.btnReset.setOnClickListener(view1 -> handleLogin());

        // Handle Forgot Password click
        binding.forgotpassText.setOnClickListener(view2 -> {
            Intent intent = new Intent(getContext(), ForgotPasswordActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void handleLogin() {
        String email = binding.edtEmail.getText().toString().trim();
        String password = binding.edtPasswordLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all the information", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Invalid Email format", Toast.LENGTH_SHORT).show();
        } else {
            // Show loading indicator here
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                saveCredentials(email, password);  // Save the credentials if "Remember Me" is checked
                                fetchUserDataAndNavigate();
                            } else {
                                Log.w(TAG, "Login failed", task.getException());
                                Toast.makeText(getContext(), "Wrong Email or Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (binding.checkboxRememberMe.isChecked()) {
            editor.putBoolean(KEY_REMEMBER_ME, true);
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PASSWORD, password);
        } else {
            editor.clear();  // Clear saved data if "Remember Me" is unchecked
        }
        editor.apply();
    }

    private void fetchUserDataAndNavigate() {
        String idCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(idCurrentUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Toast.makeText(getContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Login successful");
                        Intent intent = new Intent(getContext(), HomeActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Database error: " + error.getMessage());
                    }
                });
    }

    private void loadSavedPreferences() {
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
        if (rememberMe) {
            // Load saved email and password
            String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");

            binding.edtEmail.setText(savedEmail);
            binding.edtPasswordLogin.setText(savedPassword);
            binding.checkboxRememberMe.setChecked(true);
        }
    }
}

