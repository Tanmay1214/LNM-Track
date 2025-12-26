package com.tanmaybuilds.lnmtrack.Activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.tanmaybuilds.lnmtrack.Adapters.WelcomeAdapter;
import com.tanmaybuilds.lnmtrack.DataModels.WelcomeItem;
import com.tanmaybuilds.lnmtrack.R;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private WelcomeAdapter welcomeAdapter;
    private LinearLayout layoutIndicators;
    private Button buttonNext,buttonAuto,buttonManual;
    private ViewPager2 viewPager;

    private Animation topAnim,bottomAnim,fadeOut,fadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        layoutIndicators = findViewById(R.id.layoutIndicators);
        buttonNext = findViewById(R.id.buttonNext);
        viewPager = findViewById(R.id.viewPager);
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        buttonManual = findViewById(R.id.btnManual);
        buttonAuto = findViewById(R.id.btnAutomatic);
        fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        setupWelcomeItems();
        setupIndicators();
        setCurrentIndicator(0);

        viewPager.setAdapter(welcomeAdapter);




        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
                buttonNext.setText("Next");
                if (position==2){
                    buttonNext.setVisibility(GONE);
                    layoutIndicators.setVisibility(GONE);
                    buttonAuto.setVisibility(VISIBLE);
                    buttonManual.setVisibility(VISIBLE);
                    buttonManual.setAnimation(bottomAnim);
                    buttonAuto.setAnimation(bottomAnim);
                }else {
                    buttonNext.setVisibility(VISIBLE);
                    layoutIndicators.setVisibility(VISIBLE);
                    buttonAuto.setVisibility(GONE);
                    buttonManual.setVisibility(GONE);
                }

                buttonAuto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveMode("Auto");
                        Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                buttonManual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveMode("Manual");
                        Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });



                buttonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewPager.getCurrentItem() + 1 < welcomeAdapter.getItemCount()) {
                            // Agli slide par jao
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        } else {
                            // Last slide ke baad next activity par jao (Example: LoginActivity)
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });
    }
    private void setupWelcomeItems() {
        List<WelcomeItem> items = new ArrayList<>();

        items.add(new WelcomeItem(
                R.drawable.img1,
                "Automation Power",
                "No more manual logins everyday. Get your attendance at a glance with real-time tracking from the LNMIIT portal."));

        items.add(new WelcomeItem(
                R.drawable.img2,
                "Track Like a Pro",
                "Calculate exactly how many classes you can skip or need to attend to maintain that 75% sweet spot"));

        items.add(new WelcomeItem(
                R.drawable.img3,
                "Choose Your Style",
                "Automatic tracking fetches data directly from the portal (You will be required to enter your LNMIIT Attendance Portal Credentials one time) \nwhile\n Manual requires you to update classes,subjects,attendance everything yourself daily. You can change this later!"));

        welcomeAdapter = new WelcomeAdapter(items);
    }

    private void saveMode(String mode) {
        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .edit()
                .putString("selected_mode", mode)
                .apply();
    }
    private void setupIndicators() {
        ImageView[] indicators = new ImageView[welcomeAdapter.getItemCount()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = layoutIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            }
        }
    }
}