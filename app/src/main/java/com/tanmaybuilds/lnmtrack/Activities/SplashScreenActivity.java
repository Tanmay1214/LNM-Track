package com.tanmaybuilds.lnmtrack.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tanmaybuilds.lnmtrack.R;


public class SplashScreenActivity extends AppCompatActivity {
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView title, subtitle, footer;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_splash_screen);
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        mAuth = FirebaseAuth.getInstance();

        image = findViewById(R.id.splash_logo);
        title = findViewById(R.id.title);
        subtitle= findViewById(R.id.subtitle);
        footer = findViewById(R.id.footer);

        image.setAnimation(topAnim);
        title.setAnimation(bottomAnim);
        subtitle.setAnimation(bottomAnim);
        footer.setAnimation(bottomAnim);


        new Handler().postDelayed(() -> {
            checkUserStatus();
            finish();
        }, 2500);
    }
    private void checkUserStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();

            if (email != null && email.endsWith("@lnmiit.ac.in")) {

                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            } else {

                mAuth.signOut();
                startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
            }
        } else {

            startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
        }
        finish();
    }
}