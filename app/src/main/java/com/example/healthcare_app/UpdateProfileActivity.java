package com.example.healthcare_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText editTextUpdateName, editTextUpdateHeight,editTextUpdateWeight;
    String textName, textHeight, textWeight;
    FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Healthcare");
        }

        editTextUpdateName = findViewById(R.id.update_name);
        editTextUpdateHeight = findViewById(R.id.update_height);
        editTextUpdateWeight = findViewById(R.id.update_weight);
        authProfile= FirebaseAuth.getInstance();
        FirebaseUser firebaseUser =  authProfile.getCurrentUser();
        showProfile(firebaseUser);
        Button updateButton;
        updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        if (TextUtils.isEmpty(textName)){
            editTextUpdateName.setError("Name is required");
            editTextUpdateName.requestFocus();
        } else if (TextUtils.isEmpty(textHeight)) {
            editTextUpdateHeight.setError("Height is required");
            editTextUpdateHeight.requestFocus();
        } else if (TextUtils.isEmpty(textWeight)) {
            editTextUpdateWeight.setError("weight is required");
            editTextUpdateWeight.requestFocus();
        } else {
            textName = editTextUpdateName.getText().toString();
            textHeight= editTextUpdateHeight.getText().toString();
            textWeight = editTextUpdateWeight.getText().toString();

            UserDetails userDetails = new UserDetails(textName,"", textHeight,textWeight,"");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
            String userIDofRegistered = firebaseUser.getUid();
            databaseReference.child(userIDofRegistered).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        UserProfileChangeRequest userProfileChangeRequest= new UserProfileChangeRequest.Builder().build();
                        firebaseUser.updateProfile(userProfileChangeRequest);

                        Intent intent = new Intent(UpdateProfileActivity.this, ProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users");

        referenceProfile.child(userIDofRegistered).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                if (userDetails != null){
                    textName = userDetails.Name;
                    textHeight = userDetails.Height;
                    textWeight = userDetails.Weight;

                    editTextUpdateName.setText(textName);
                    editTextUpdateHeight.setText(textHeight);
                    editTextUpdateWeight.setText(textWeight);
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "SomethingWent wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh){
            startActivity(getIntent());
            finish();
        } else if (id == R.id.menu_update) {
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_log_out) {
            authProfile.signOut();
            Toast.makeText(this, "Signed Out!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateProfileActivity.this, LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Something Went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}