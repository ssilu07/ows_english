package com.study.vocabspecial_crazywaytolearnenglish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private FrameLayout bannerContainer;
    private ImageView btnShareApp, btnRateApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bannerContainer = findViewById(R.id.bannerContainer);
        btnShareApp = findViewById(R.id.btnShareApp);
        btnRateApp = findViewById(R.id.btnRateApp);

        // Load AdMob Banner at bottom
        if (bannerContainer != null) {
            AdManager.getInstance().loadBanner(this, bannerContainer);
        }

        // Share App Promotion
        if (btnShareApp != null) {
            btnShareApp.setOnClickListener(v -> shareApp());
        }

        // Rate App on Google Play
        if (btnRateApp != null) {
            btnRateApp.setOnClickListener(v -> rateApp());
        }
    }

    private void shareApp() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text));
        startActivity(Intent.createChooser(sendIntent, "Share App Via"));
    }

    private void rateApp() {
        String packageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }
}
