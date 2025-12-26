package com.tanmaybuilds.lnmtrack.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.tanmaybuilds.lnmtrack.R;

public class ComingsoonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon_acitvity);

        // Back Button Logic
        findViewById(R.id.backBtn).setOnClickListener(v -> goBack());

        // Button Logic
        findViewById(R.id.btnBackHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ComingsoonActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void goBack() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
}