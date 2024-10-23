package com.example.foodorderingapp.Fragments.Home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.foodorderingapp.R;
import com.example.foodorderingapp.Activities.Home.HomeActivity;
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
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnReset.setOnClickListener(view1 -> {
            if (binding.edtPasswordLogin.getText().toString().isEmpty() || binding.edtEmail.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please fill all the information", Toast.LENGTH_SHORT).show();

        } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.edtEmail.getText().toString(),binding.edtPasswordLogin.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String idCurrentUser=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseDatabase.getInstance().getReference("Users").child(idCurrentUser).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Toast.makeText(getContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "đăng nhập thành công", task.getException());
                                    Intent intent=new Intent(getContext(), HomeActivity.class);
                                    startActivity(intent);
                                    requireActivity().finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        } else {
                            Log.w(TAG, "đăng nhập thất bại", task.getException());
                            Toast.makeText(getContext(), "Wrong Email or Password", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

//        TextView txtForgot= view.findViewById(R.id.forgotpassText);
//        txtForgot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(getContext(), ForgotActivity.class);
//                startActivity(intent);
//            }
//        });
        return view;
    }
}