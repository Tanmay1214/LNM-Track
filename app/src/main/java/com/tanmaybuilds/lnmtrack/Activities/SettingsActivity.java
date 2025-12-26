package com.tanmaybuilds.lnmtrack.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tanmaybuilds.lnmtrack.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView backBtn = findViewById(R.id.backBtn);
        if (backBtn != null) {
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });
        }


        setupRow(R.id.item_profile, "Profile",R.drawable.ic_user);
        setupRow(R.id.item_settings, "Settings", R.drawable.ic_settings);
        setupRow(R.id.item_suggestions, "Suggestions / Bug reports", R.drawable.ic_bug);
        setupRow(R.id.item_developer, "Developer", R.drawable.ic_code);
    }

    // Helper method
    private void setupRow(int layoutId, String title, int iconRes) {
        View rowView = findViewById(layoutId);
        if (rowView != null) {
            TextView tvTitle = rowView.findViewById(R.id.title);
            ImageView ivIcon = rowView.findViewById(R.id.icon);

            tvTitle.setText(title);
            ivIcon.setImageResource(iconRes);

            // Click handling
            rowView.setOnClickListener(v -> {
                handleClicks(title);
            });
        }
    }

    private void handleClicks(String title) {
        switch (title) {
            case "Settings":
                Intent intent = new Intent(SettingsActivity.this, ComingsoonActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case "Profile":
                Intent intent2 = new Intent(SettingsActivity.this, ProfileActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "Suggestions / Bug reports":
                Intent intent3 = new Intent(SettingsActivity.this, ComingsoonActivity.class);
                startActivity(intent3);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "Developer":
                Intent intent4 = new Intent(SettingsActivity.this, DeveloperActivity.class);
                startActivity(intent4);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }
}