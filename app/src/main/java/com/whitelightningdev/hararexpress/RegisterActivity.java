package com.whitelightningdev.hararexpress;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextUsername, editTextEmail, editTextPassword, editTextRetypePassword;
    private CheckBox checkBoxTerms;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editTextName);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRetypePassword = findViewById(R.id.editTextRetypePassword);
        checkBoxTerms = findViewById(R.id.checkBoxTerms);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    public void registerUser(View view) {
        final String name = editTextName.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String retypePassword = editTextRetypePassword.getText().toString().trim();
        boolean agreedToTerms = checkBoxTerms.isChecked();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || retypePassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(retypePassword)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!agreedToTerms) {
            Toast.makeText(RegisterActivity.this, "Please agree to the Terms of Use", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user already exists
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                            if (isNewUser) {
                                firebaseAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    String userId = firebaseAuth.getCurrentUser().getUid();
                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put("name", name);
                                                    user.put("username", username);
                                                    user.put("email", email);

                                                    databaseReference.child(userId).setValue(user)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                                        startActivity(intent);
                                                                        finish(); // Finish RegisterActivity
                                                                    } else {
                                                                        Toast.makeText(RegisterActivity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                        Toast.makeText(RegisterActivity.this, "User with this email already exists.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(RegisterActivity.this, "User with this email already exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error checking existing user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
