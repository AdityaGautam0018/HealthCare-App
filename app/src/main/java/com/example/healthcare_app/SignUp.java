package com.example.healthcare_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private EditText editTextSignUpName, editTextSignUpEmail, editTextSignupDOB, editTextSignUpHeight, editTextSignupPassword, editTextSignUpConfirmPassword ,editTextSignUpWeight;
    private RadioGroup genderRadioGroup;
    private RadioButton genderRadioButton;
    private DatePickerDialog picker;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editTextSignUpName = findViewById(R.id.signupName);
        editTextSignUpEmail = findViewById(R.id.signupEmail);
        editTextSignupDOB = findViewById(R.id.signupDOB);
        editTextSignUpHeight = findViewById(R.id.signupHeight);
        editTextSignUpWeight = findViewById(R.id.signupWeight);
        editTextSignupPassword = findViewById(R.id.signup_password);
        editTextSignUpConfirmPassword = findViewById(R.id.signup_confirm_password);
        genderRadioGroup = findViewById(R.id.signupGender);
        genderRadioGroup.clearCheck();

        editTextSignupDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(SignUp.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextSignupDOB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();

            }
        });

        Button signUpButton;
        TextView loginButton;
        loginButton = findViewById(R.id.signUp_to_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LoginActivity.class);
                startActivity(intent);

            }
        });
        signUpButton = findViewById(R.id.signUp_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
                genderRadioButton = findViewById(selectedGenderId);
                String textName = editTextSignUpName.getText().toString();
                String textEmail = editTextSignUpEmail.getText().toString();
                String textDOB = editTextSignupDOB.getText().toString();
                String textHeight = editTextSignUpHeight.getText().toString();
                String textWeight = editTextSignUpWeight.getText().toString();
                String textPassWord = editTextSignupPassword.getText().toString();
                String textConfirmPassword = editTextSignUpConfirmPassword.getText().toString();
                String textGender;

                if (TextUtils.isEmpty(textName)) {
                    Toast.makeText(SignUp.this, "Please enter your name", Toast.LENGTH_LONG).show();
                    editTextSignUpName.setError("Name is required");
                    editTextSignUpName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(SignUp.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editTextSignUpEmail.setError("Email is required");
                    editTextSignUpEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(SignUp.this, "Please enter valid email", Toast.LENGTH_LONG).show();
                    editTextSignUpEmail.setError("Valid Email is required");
                    editTextSignUpEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDOB)) {
                    Toast.makeText(SignUp.this, "Please enter your Date-Of-Birth", Toast.LENGTH_LONG).show();
                    editTextSignupDOB.setError("Date Of Birth is required");
                    editTextSignupDOB.requestFocus();
                } else if (genderRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(SignUp.this, "Please select your gender", Toast.LENGTH_LONG).show();
                    editTextSignupDOB.setError("Gender is required");
                    editTextSignupDOB.requestFocus();
                } else if (TextUtils.isEmpty(textHeight)) {
                    Toast.makeText(SignUp.this, "Please enter your height", Toast.LENGTH_LONG).show();
                    editTextSignUpHeight.setError("Height is required");
                    editTextSignUpHeight.requestFocus();
                } else if (TextUtils.isEmpty(textPassWord)) {
                    Toast.makeText(SignUp.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                    editTextSignupPassword.setError("Password is required");
                    editTextSignupPassword.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPassword)) {
                    Toast.makeText(SignUp.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    editTextSignUpConfirmPassword.setError("Confirm password is required");
                    editTextSignUpConfirmPassword.requestFocus();
                } else if (textPassWord.length() < 6) {
                    Toast.makeText(SignUp.this, "Password must be of 6 digits.", Toast.LENGTH_LONG).show();
                    editTextSignupPassword.setError("Password too weak");
                    editTextSignupPassword.requestFocus();
                } else if (!textPassWord.equals(textConfirmPassword)) {
                    Toast.makeText(SignUp.this, "Password does not matches.", Toast.LENGTH_LONG).show();
                    editTextSignUpConfirmPassword.setError("Passwords confirmation is required");
                    editTextSignUpConfirmPassword.requestFocus();
                    editTextSignupPassword.clearComposingText();
                    editTextSignUpConfirmPassword.clearComposingText();
                } else {
                    textGender = genderRadioButton.getText().toString();
                    registreUser(textName, textDOB, textGender, textEmail, textHeight,textWeight,textPassWord, textConfirmPassword);
                }
            }

            private void registreUser(String textName, String textDOB, String textGender, String textEmail, String textHeight,String textWeight, String textPassWord, String textConfirmPassword) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(textEmail, textPassWord).addOnCompleteListener(SignUp.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUp.this, "Signed up Successfully", Toast.LENGTH_LONG).show();
                                    FirebaseUser firebaseUser = auth.getCurrentUser();

                                    UserDetails userDetails = new UserDetails(textName, textDOB, textHeight,textWeight , textGender);

                                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users");
                                    referenceProfile.child(firebaseUser.getUid()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {


                                                Intent intent = new Intent(SignUp.this, ProfileActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(SignUp.this, "User Registered Failed .Please try again.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }
                            }
                        });
            }
        });

    }
}