package com.tanmaybuilds.lnmtrack.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.tanmaybuilds.lnmtrack.R;
//import com.rajat.pdfviewer.PdfViewerActivity;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ChipNavigationBar bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setItemSelected(R.id.routine, true);

        bottomNav.setOnItemSelectedListener(id -> {
            if (id == R.id.attendance) {

                    Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
            }
            else if (id == R.id.tasks) {
                Intent intent = new Intent(CalendarActivity.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });


        // UG Calendar Card/Button click par
        findViewById(R.id.cardUG).setOnClickListener(v -> {
            openPdfFromAssets("pdf_one.pdf", "UG Academic Calendar");
        });

        // PG Calendar Card/Button click par
        findViewById(R.id.cardPG).setOnClickListener(v -> {
            openPdfFromAssets("pdf_two.pdf", "PG Academic Calendar");
        });
    }

    private void openPdfFromAssets(String fileName, String title) {


        // Transition effect
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}