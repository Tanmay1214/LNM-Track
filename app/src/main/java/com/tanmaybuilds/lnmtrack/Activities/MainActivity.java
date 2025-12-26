package com.tanmaybuilds.lnmtrack.Activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.tanmaybuilds.lnmtrack.Adapters.SubjectAdapter;
import com.tanmaybuilds.lnmtrack.ApiInterface.ApiService;
import com.tanmaybuilds.lnmtrack.ApiInterface.RetrofitClient;
import com.tanmaybuilds.lnmtrack.DataModels.ApiResponse;
import com.tanmaybuilds.lnmtrack.DataModels.CryptoHelper;
import com.tanmaybuilds.lnmtrack.DataModels.LoginRequest;
import com.tanmaybuilds.lnmtrack.DataModels.SubjectModel;
import com.tanmaybuilds.lnmtrack.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private View overlayLayout;

    private RecyclerView recyclerView;
    private SubjectAdapter adapter;
    private ProgressBar progressBar;

    private ImageView gridBtn,addBtn;

    private Button pSubmit,retryBtn;


    // Initialize list here to avoid NullPointerException
    private List<SubjectModel> subjectList = new ArrayList<>();

    private LinearLayout mainContent,retryLayout;

    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        scrollView = findViewById(R.id.scroll_view);
        recyclerView = findViewById(R.id.recyclerView);
        ChipNavigationBar bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setItemSelected(R.id.attendance, true);
        overlayLayout = findViewById(R.id.overlay_layout);
        mainContent = findViewById(R.id.main_content);
        progressBar = findViewById(R.id.progress_dashboard);
        EditText pEmail = findViewById(R.id.portal_email);
        EditText pPass = findViewById(R.id.portal_password);
        pSubmit = findViewById(R.id.btn_portal_submit);
        addBtn = findViewById(R.id.addBtn);
        retryLayout = findViewById(R.id.retry_layout);
        ImageView infoIcon = findViewById(R.id.info_encryption);
        retryBtn = findViewById(R.id.retryBtn);
        gridBtn = findViewById(R.id.grid_button);
        //AI BOT SETUP
        ExtendedFloatingActionButton fabAi = findViewById(R.id.fab_ai);

        fabAi.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AIChatActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.no_animation);
        });

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY + 10 && fabAi.isExtended()) {
                fabAi.shrink();
            } else if (scrollY < oldScrollY - 10 && !fabAi.isExtended()) {
                fabAi.extend();
            }
        });

        //Security Policy
        infoIcon.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Security & Privacy")
                    .setMessage("We use AES-256 encryption to secure your portal credentials. Your password is encrypted locally and is never stored in a readable format on our servers, ensuring that only your device can access it for portal synchronization.")
                    .setPositiveButton("I Understand", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ComingsoonActivity.class);
                startActivity(intent);
            }
        });

        bottomNav.setOnItemSelectedListener(id -> {
            if (id == R.id.attendance) {

            } else {
                Intent intent = new Intent(MainActivity.this, ComingsoonActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });


        String userId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance("https://lnm-track-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("Users")
                .child(userId);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectAdapter(subjectList);
        recyclerView.setAdapter(adapter);

        checkUserStatusAndLoadData();

        gridBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


        pSubmit.setOnClickListener(v -> {
            String email = pEmail.getText().toString().trim();
            String pass = pPass.getText().toString().trim();
            try {
                if (!email.isEmpty() && !pass.isEmpty()) {
                    String encryptedPass = CryptoHelper.encrypt(pass);
                    savePortalCredentials(email, encryptedPass);

                    fetchAttendance(email, pass);
                } else {
                    Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Encryption failed!", Toast.LENGTH_SHORT).show();
            }
        });

        retryBtn.setOnClickListener(v -> {
            Log.d("LNM_DEBUG", "Retry clicked. Restarting flow...");
            checkUserStatusAndLoadData();
        });
    }

    private void showPortalPopup() {
        overlayLayout.setVisibility(VISIBLE);
        mainContent.setAlpha(0.1f);
    }

    private void savePortalCredentials(String email, String encryptedPass) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("portalEmail", email);
        map.put("portalPassword", encryptedPass);

        mDatabase.updateChildren(map).addOnSuccessListener(aVoid -> {
            overlayLayout.setVisibility(GONE);
            mainContent.setAlpha(1.0f);
            Toast.makeText(MainActivity.this, "Portal Linked Successfully!", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkUserStatusAndLoadData() {

        retryLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        Log.d("LNM_DEBUG", "checkUserStatusAndLoadData: Fetching from Firebase...");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String mode = snapshot.child("mode").getValue(String.class);
                    String savedEmail = snapshot.child("portalEmail").getValue(String.class);
                    String encPass = snapshot.child("portalPassword").getValue(String.class);

                    boolean hasPortalCreds = (savedEmail != null && encPass != null);

                    if ("Auto".equals(mode)) {
                        if (!hasPortalCreds) {
                            progressBar.setVisibility(View.GONE);
                            showPortalPopup();
                        } else {
                            overlayLayout.setVisibility(View.GONE);
                            mainContent.setAlpha(1.0f);
                            try {
                                String decryptedPass = CryptoHelper.decrypt(encPass);
                                fetchAttendance(savedEmail, decryptedPass);
                            } catch (Exception e) {
                                progressBar.setVisibility(View.GONE);
                                showPortalPopup();
                            }
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    Log.e("LNM_DEBUG", "No snapshot exists");
                    progressBar.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchAttendance(String email, String password) {
        pSubmit.setEnabled(false);
        pSubmit.setText("Fetching...");

        // DEBUG: Check what we are sending
        Log.d("LNM_DEBUG", "Starting API Call for: " + email);

        LoginRequest loginRequest = new LoginRequest(email, password);
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getAttendance(loginRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                pSubmit.setEnabled(true);
                pSubmit.setText("SUBMIT");

                // DEBUG: Check HTTP Status Code (e.g., 200, 404, 500)
                Log.d("LNM_DEBUG", "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Log.d("LNM_DEBUG", "API Status: " + apiResponse.getStatus());

                    if ("success".equals(apiResponse.getStatus())) {
                        List<SubjectModel> data = apiResponse.getData();

                        if (data != null && !data.isEmpty()) {
                            Log.d("LNM_DEBUG", "Subjects Received: " + data.size());
                            subjectList.clear();
                            subjectList.addAll(data);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(GONE);
                            overlayLayout.setVisibility(View.GONE);
                            mainContent.setAlpha(1.0f);
                        } else {
                            // DEBUG: Success but list is empty
                            Log.w("LNM_DEBUG", "Success but Data List is Empty/Null");
                            Toast.makeText(MainActivity.this, "No attendance data found on portal", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(GONE);
                        }
                    } else {
                        // DEBUG: API returned error status (e.g., wrong password in portal)
                        Log.e("LNM_DEBUG", "API Error Status: " + apiResponse.getStatus());
                        Toast.makeText(MainActivity.this, "Portal Error: " + apiResponse.getStatus(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(GONE);
                        overlayLayout.setVisibility(VISIBLE);
                    }
                } else {
                    // DEBUG: Server error or invalid JSON
                    Log.e("LNM_DEBUG", "Response Unsuccessful or Body Null");
                    Toast.makeText(MainActivity.this, "Server error. Try again later.", Toast.LENGTH_SHORT).show();
                    retryLayout.setVisibility(VISIBLE);
                    progressBar.setVisibility(GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                pSubmit.setEnabled(true);
                pSubmit.setText("SUBMIT");

                // DEBUG: Connection issues or timeout
                Log.e("LNM_DEBUG", "Retrofit Failure: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Due to server limitations data cannot be loaded please try again in a minute or so", Toast.LENGTH_LONG).show();
                retryLayout.setVisibility(VISIBLE);
                progressBar.setVisibility(GONE);
            }
        });
    }
}