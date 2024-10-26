package com.example.foodorderingapp.Fragments.Home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.foodorderingapp.Domain.Cart;
import com.example.foodorderingapp.Domain.User;
import com.example.foodorderingapp.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {
                    String phone = binding.edtPhone.getText().toString();
                    String name = binding.edtName.getText().toString();
                    String email = binding.edtEmail.getText().toString();
                    String pass = binding.edtPass.getText().toString();

                    // Show loading indicator here
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        User tmp = new User(task.getResult().getUser().getUid(), email, "", name, "01/01/2000", phone);
                                        Cart cart = new Cart(FirebaseDatabase.getInstance().getReference().push().getKey(), 0, 0, task.getResult().getUser().getUid());
                                        FirebaseDatabase.getInstance().getReference("Users").child(tmp.getUserId())
                                                .setValue(tmp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseDatabase.getInstance().getReference("Carts").child(cart.getCartId()).setValue(cart);
                                                            Toast.makeText(getContext(), "Create account successfully", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(getContext(), "Create account unsuccessfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error occurred";
                                        createDialog(errorMessage).show();
                                    }
                                    // Hide loading indicator here
                                }
                            });
                }
            }
        });

        return view;
    }

    public boolean check() {
        String phone = binding.edtPhone.getText().toString();
        String name = binding.edtName.getText().toString();
        String email = binding.edtEmail.getText().toString();
        String pass = binding.edtPass.getText().toString();
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            createDialog("Điền đầy đủ thông tin").show();
            return false;
        } else if (!email.matches(String.valueOf(Patterns.EMAIL_ADDRESS))) {
            createDialog("Email không đúng định dạng").show();
            return false;
        } else if (!phone.matches("(03|05|07|08|09|01[2689])[0-9]{8}\\b")) {
            createDialog("Số điện thoại không hợp lệ").show();
            return false;
        }
        // Add password strength validation here if needed
        return true;
    }

    public AlertDialog createDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setTitle("Notice");
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }
}
