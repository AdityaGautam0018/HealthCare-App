package com.example.healthcare_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewName,  textViewHeight, textViewWeight, textViewBMI,textViewBMICategory ;
    private String name,height,weight;
    private FirebaseAuth authProfile;
    private LineChart lineChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        textViewName = findViewById(R.id.show_name);
        textViewHeight = findViewById(R.id.show_height);
        textViewWeight = findViewById(R.id.show_weight);
        textViewBMI = findViewById(R.id.textViewBMI);
        textViewBMICategory = findViewById(R.id.textViewBMICategory);
        lineChart = findViewById(R.id.lineChart);
        Button buttonCalculateBMI = findViewById(R.id.button_calculate_bmi);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Healthcare");
        }


        if (firebaseUser == null) {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }else {
            showUserProfile(firebaseUser);
            setupWeightHistoryChart();
        }
        buttonCalculateBMI.setOnClickListener(v -> {
            String heightStr = textViewHeight.getText().toString();
            String weightStr = textViewWeight.getText().toString();

            if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
                double height = Double.parseDouble(heightStr);
                double weight = Double.parseDouble(weightStr);
                calculateBMI(height, weight);
            } else {
                Toast.makeText(this, "Height or Weight is missing!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupWeightHistoryChart() {
        List<Entry> weightEntries = new ArrayList<>();

        // Example data points for the past week
        weightEntries.add(new Entry(0, 70)); // Day 0
        weightEntries.add(new Entry(1, 71)); // Day 1
        weightEntries.add(new Entry(2, 70.5f)); // Day 2
        weightEntries.add(new Entry(3, 71)); // Day 3
        weightEntries.add(new Entry(4, 70.8f)); // Day 4
        weightEntries.add(new Entry(5, 71.2f)); // Day 5
        weightEntries.add(new Entry(6, 70.9f)); // Day 6

        LineDataSet lineDataSet = new LineDataSet(weightEntries, "Weight History");
        lineDataSet.setColor(getResources().getColor(R.color.color_53B2FE));
        lineDataSet.setValueTextColor(getResources().getColor(R.color.black));

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        Description description = new Description();
        description.setText("Weight History Over the Past Week");
        lineChart.setDescription(description);
        lineChart.invalidate(); // refresh the chart
    }
    private void calculateBMI(double height, double weight) {
        height = height / 100; // convert height to meters
        double bmi = weight / (height * height);
        textViewBMI.setText(String.format("Your BMI: %.2f", bmi));

        String bmiCategory;
        if (bmi < 18.5) {
            bmiCategory = "Underweight";
        } else if (bmi < 24.9) {
            bmiCategory = "Normal Weight";
        } else if (bmi < 29.9) {
            bmiCategory = "Overweight";
        } else {
            bmiCategory = "Obese";
        }

        textViewBMICategory.setText("Category: " + bmiCategory);
    }
    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered users");
        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                if (userDetails != null){
                    name = userDetails.Name;
                    height = userDetails.Height;
                    weight = userDetails.Weight;
                    textViewName.setText(name);
                    textViewHeight.setText(height);
                    textViewWeight.setText(weight);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_log_out) {
            authProfile.signOut();
            Toast.makeText(this, "Signed Out!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Something Went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}