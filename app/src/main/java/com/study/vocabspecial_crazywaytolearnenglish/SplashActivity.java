package com.study.vocabspecial_crazywaytolearnenglish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean hasProceeded = false;

    private final Runnable proceedRunnable = this::proceedToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        // Preload / check AdManager
        AdManager.getInstance().init(getApplicationContext());

        // Snappy splash duration
        handler.postDelayed(proceedRunnable, 1000);

        // Instant tap-to-continue if user taps
        findViewById(android.R.id.content).setOnClickListener(v -> proceedToMain());
    }

    private synchronized void proceedToMain() {
        if (hasProceeded) return;
        hasProceeded = true;
        handler.removeCallbacks(proceedRunnable);

        if (!isFinishing() && !isDestroyed()) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(proceedRunnable);
    }
}
