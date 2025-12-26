package com.tanmaybuilds.lnmtrack.Activities;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.tanmaybuilds.lnmtrack.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private ProgressBar progressLogin;

    private TextView loginText;
    private ImageView googleLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://lnm-track-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();


        // Google Sign-In options configure
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginText = findViewById(R.id.login_text);
        progressLogin = findViewById(R.id.progress_login);
        googleLogin = findViewById(R.id.login_googleIcon);
        FrameLayout btnGoogle = findViewById(R.id.btnGoogleContinue);
        btnGoogle.setOnClickListener(v -> signIn());
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    validateAndFirebaseAuth(account);
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validateAndFirebaseAuth(GoogleSignInAccount acct) {
        String email = acct.getEmail();
        Log.d("LNMTrack_Debug", "Step 1: Method started for email: " + email);

        if (email != null && email.endsWith("@lnmiit.ac.in")) {
            progressLogin.setVisibility(VISIBLE);
            googleLogin.setVisibility(GONE);
            loginText.setVisibility(GONE);
            Log.d("LNMTrack_Debug", "Step 2: Domain verification successful");

            try {
                // --- DATA EXTRACTION LOGIC
                String emailPrefix = email.split("@")[0];
                String rollNumber = emailPrefix.toUpperCase();
                String batchYear = "20" + emailPrefix.substring(0, 2);
                String branchCode = emailPrefix.replaceAll("[0-9]", "").toUpperCase();
                String branchName = getBranchName(branchCode);

                Log.d("LNMTrack_Debug", "Step 3: Extraction Done -> Roll: " + rollNumber + ", Batch: " + batchYear + ", Branch: " + branchName);

                // 2. Firebase Authentication
                AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                Log.d("LNMTrack_Debug", "Step 4: Attempting Firebase Auth with Credential...");

                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                String userId = mAuth.getCurrentUser().getUid();
                                Log.d("LNMTrack_Debug", "Step 5: Auth Successful. UID: " + userId);

                                // 3. Database User Structure
                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("name", acct.getDisplayName());
                                userMap.put("email", email);
                                userMap.put("rollNumber", rollNumber);
                                userMap.put("batch", batchYear);
                                userMap.put("branch", branchName);
                                userMap.put("role", "student");

                                String mode = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                        .getString("selected_mode", "Not Selected");
                                userMap.put("mode", mode);

                                // 4. Firebase Realtime Database
                                Log.d("LNMTrack_Debug", "Step 6: Writing to Realtime Database...");

                                mDatabase.child("Users").child(userId).setValue(userMap)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                Log.d("LNMTrack_Debug", "Step 7: Database write SUCCESS. Moving to MainActivity.");
                                                Toast.makeText(LoginActivity.this, "Login Success: " + rollNumber, Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Log.e("LNMTrack_Debug", "Step 7 FAILED: Database write error", dbTask.getException());
                                                Toast.makeText(LoginActivity.this, "DB Write Failed: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                Log.e("LNMTrack_Debug", "Step 5 FAILED: Firebase Auth Error", task.getException());
                                Toast.makeText(LoginActivity.this, "Firebase Auth Failed.", Toast.LENGTH_SHORT).show();
                                progressLogin.setVisibility(GONE);
                                googleLogin.setVisibility(VISIBLE);
                                loginText.setVisibility(VISIBLE);
                            }
                        });
            } catch (Exception e) {
                Log.e("LNMTrack_Debug", "Extraction Error: Email format might be wrong", e);
                Toast.makeText(this, "Error parsing email details", Toast.LENGTH_SHORT).show();
                progressLogin.setVisibility(GONE);
                googleLogin.setVisibility(VISIBLE);
                loginText.setVisibility(VISIBLE);
            }
        } else {
            Log.w("LNMTrack_Debug", "Domain Mismatch: Sign-out triggered for " + email);
            mGoogleSignInClient.signOut();
            Toast.makeText(this, "Please use college email (@lnmiit.ac.in)", Toast.LENGTH_LONG).show();
            progressLogin.setVisibility(GONE);
            googleLogin.setVisibility(VISIBLE);
            loginText.setVisibility(VISIBLE);
        }
    }

    // Helper Method
    private String getBranchName(String code) {
        if (code.contains("UCS")) return "Computer Science";
        if (code.contains("UEC")) return "Electronics & Communication";
        if (code.contains("UCC")) return "Computer & Communication ";
        if (code.contains("UME")) return "Mechanical";
        if (code.contains("DCS")) return "Dual Degree Computer Science";
        if (code.contains("DEC")) return "Dual Degree Electronics & Communication";
        return code;
    }
}