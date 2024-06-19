package com.example.healthcare_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    private TextView textView;
    FirebaseAuth authProfile;
    private static final String TAG = "Login Activity";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginbut;
        authProfile = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.editTextLoginGmail);
        loginPassword = findViewById(R.id.editTextLoginPassword);
        loginbut = findViewById(R.id.loginbutt);
        textView = findViewById(R.id.login_to_signup);
        loginbut.setOnClickListener(v -> {
            String textEmail = loginEmail.getText().toString();
            String textPassword = loginPassword.getText().toString();

            if (TextUtils.isEmpty(textEmail)){
                Toast.makeText(LoginActivity.this, "Please Enter Your email", Toast.LENGTH_SHORT).show();
                loginEmail.setError("Email is required");
                loginEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(LoginActivity.this, "Please Enter valid email", Toast.LENGTH_SHORT).show();
                loginEmail.setError("Valid Email is required");
                loginEmail.requestFocus();
            }else if (TextUtils.isEmpty(textPassword)){
                Toast.makeText(LoginActivity.this, "Please Enter Your password", Toast.LENGTH_SHORT).show();
                loginPassword.setError("Password is required");
                loginPassword.requestFocus();
            } else {
                loginUser(textEmail, textPassword);
            }
        });


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "Your are logged in!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        loginEmail.setError("User does not exist or no longer valid");
                        loginEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        loginEmail.setError("Invalid Credentials");
                        loginEmail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(LoginActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null){
            Toast.makeText(LoginActivity.this, "Already Logged in.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            finish();
        }else {
            Toast.makeText(LoginActivity.this, "You can login now", Toast.LENGTH_SHORT).show();
        }
    }
}