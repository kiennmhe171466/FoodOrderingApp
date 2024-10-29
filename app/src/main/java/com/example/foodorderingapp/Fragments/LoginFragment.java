package com.example.foodorderingapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnReset.setOnClickListener(view1 -> {
            String email = binding.edtEmail.getText().toString();
            String password = binding.edtPasswordLogin.getText().toString();

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
                                } else {
                                    Log.w(TAG, "Login failed", task.getException());
                                    Toast.makeText(getContext(), "Wrong Email or Password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
    }
}
