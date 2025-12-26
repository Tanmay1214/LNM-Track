package com.tanmaybuilds.lnmtrack.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanmaybuilds.lnmtrack.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etBranch, etBatch, etRoll, etEmail;
    private AutoCompleteTextView semesterDropdown;
    private DatabaseReference mDatabase;
    private String userId;


    private ImageView backBtn,profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // 1. Initialize Views
        initViews();

        // 2. Dropdown Setup (Semester)
        setupSemesterDropdown();

        // 3. Firebase Setup
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance("https://lnm-track-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("Users")
                .child(userId);

        // 4. Load Data
        loadUserData();
        if (user != null) {
            // Google account wali photo ka URL
            Uri photoUrl = user.getPhotoUrl();

            if (photoUrl != null) {
                // Glide use karke direct image set karo
                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(profileImage);
            }
        }

        // 5. Back Button
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // 6. Update Button
        findViewById(R.id.btnSave).setOnClickListener(v -> updateSemester());
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etBranch = findViewById(R.id.etBranch);
        etBatch = findViewById(R.id.etBatch);
        etRoll = findViewById(R.id.etRoll);
        etEmail = findViewById(R.id.etEmail);
        semesterDropdown = findViewById(R.id.autoCompleteSemester);
        backBtn = findViewById(R.id.backBtn);
        profileImage  =findViewById(R.id.profileImage);
    }

    private void setupSemesterDropdown() {
        String[] semesters = {"Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, semesters);
        semesterDropdown.setAdapter(adapter);

        semesterDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                semesterDropdown.showDropDown();
            }
        });
    }

    private void loadUserData() {
        Log.d("LNM_DEBUG", "loadUserData: Started");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String branch = snapshot.child("branch").getValue(String.class);
                    String batch = snapshot.child("batch").getValue(String.class); // e.g., "2024"
                    String roll = snapshot.child("rollNumber").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String sem = snapshot.child("semester").getValue(String.class);

                    Log.d("LNM_DEBUG", "Fetched Batch: " + batch);

                    etName.setText(name != null ? name : "N/A");
                    etBranch.setText(branch != null ? branch : "N/A");
                    etBatch.setText(batch != null ? batch : "N/A");
                    etRoll.setText(roll != null ? roll : "N/A");
                    etEmail.setText(email != null ? email : "N/A");

                    // --- DYNAMIC DROPDOWN LOGIC ---
                    if (batch != null) {
                        updateSemesterListBasedOnBatch(batch);
                    }

                    if (sem != null) {
                        semesterDropdown.setText(sem, false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LNM_DEBUG", "DB Error: " + error.getMessage());
            }
        });
    }

    private void updateSemesterListBasedOnBatch(String batch) {
        List<String> semesters = new ArrayList<>();

        // Logic: Batch ke according list filter karna
        if (batch.contains("2025")) {
            semesters.add("Sem 1");
            semesters.add("Sem 2");
        } else if (batch.contains("2024")) {
            semesters.add("Sem 3");
            semesters.add("Sem 4");
        } else if (batch.contains("2023")) {
            semesters.add("Sem 5");
            semesters.add("Sem 6");
        } else if (batch.contains("2022")) {
            semesters.add("Sem 7");
            semesters.add("Sem 8");
        } else {

            semesters.addAll(Arrays.asList("Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8"));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, semesters);
        semesterDropdown.setAdapter(adapter);

        Log.d("LNM_DEBUG", "Dropdown updated for batch: " + batch + " with " + semesters.size() + " items");
    }

    private void updateSemester() {
        String selectedSem = semesterDropdown.getText().toString();
        if (selectedSem.isEmpty()) {
            Toast.makeText(this, "Please select a semester", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child("semester").setValue(selectedSem)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Semester Updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show());
    }
}