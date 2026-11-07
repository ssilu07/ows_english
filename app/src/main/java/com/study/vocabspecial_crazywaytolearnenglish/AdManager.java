package com.study.vocabspecial_crazywaytolearnenglish;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Collections;

public class AdManager {
    private static final String TAG = "AdManager";
    public static final boolean ENABLE_ADS = false; // Ads completely disabled for No-Ads debug/pro build
    private static AdManager instance;

    private InterstitialAd mInterstitialAd;
    private boolean isLoadingInterstitial = false;
    private int clickCount = 0;
    // Show interstitial every 3 clicks for optimal revenue + good user retention
    private static final int INTERSTITIAL_FREQUENCY = 3;

    private AdManager() {}

    public static synchronized AdManager getInstance() {
        if (instance == null) {
            instance = new AdManager();
        }
        return instance;
    }

    public boolean isAdsEnabled() {
        return ENABLE_ADS;
    }

    public void init(@NonNull Context context) {
        if (!ENABLE_ADS) {
            Log.d(TAG, "Ads are disabled (No-Ads build). MobileAds will not initialize.");
            return;
        }

        // Register emulator as safe test device
        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Collections.singletonList(AdRequest.DEVICE_ID_EMULATOR))
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);

        MobileAds.initialize(context, initializationStatus -> {
            Log.d(TAG, "MobileAds initialized");
            loadInterstitial(context.getApplicationContext());
        });
    }

    public void loadInterstitial(@NonNull Context context) {
        if (!ENABLE_ADS) {
            return;
        }

        if (mInterstitialAd != null || isLoadingInterstitial) {
            return;
        }

        isLoadingInterstitial = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        String adUnitId = context.getString(R.string.admob_interstitial_id);

        InterstitialAd.load(context, adUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                isLoadingInterstitial = false;
                Log.d(TAG, "Interstitial ad loaded successfully.");

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Interstitial ad dismissed.");
                        mInterstitialAd = null;
                        loadInterstitial(context);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                        Log.e(TAG, "Interstitial ad failed to show: " + adError.getMessage());
                        mInterstitialAd = null;
                        loadInterstitial(context);
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
                isLoadingInterstitial = false;
                Log.w(TAG, "Interstitial failed to load: " + loadAdError.getMessage());
            }
        });
    }

    public void showInterstitialWithCounter(@NonNull Activity activity, @NonNull Runnable onComplete) {
        if (!ENABLE_ADS) {
            // No ads: immediately execute action without delay or interstitial
            onComplete.run();
            return;
        }

        clickCount++;
        if (clickCount % INTERSTITIAL_FREQUENCY == 0 && mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    mInterstitialAd = null;
                    loadInterstitial(activity.getApplicationContext());
                    onComplete.run();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    mInterstitialAd = null;
                    loadInterstitial(activity.getApplicationContext());
                    onComplete.run();
                }
            });
            mInterstitialAd.show(activity);
        } else {
            // If ad is not ready or frequency not reached, proceed directly
            if (mInterstitialAd == null) {
                loadInterstitial(activity.getApplicationContext());
            }
            onComplete.run();
        }
    }

    public void loadBanner(@NonNull Activity activity, @NonNull ViewGroup container) {
        if (container == null) {
            return;
        }

        if (!ENABLE_ADS) {
            // In No-Ads mode: clear and collapse container completely so layout fills screen seamlessly
            container.removeAllViews();
            container.setVisibility(View.GONE);
            return;
        }

        try {
            container.setVisibility(View.VISIBLE);
            AdView adView = new AdView(activity);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(activity.getString(R.string.admob_banner_id));
            container.removeAllViews();
            container.addView(adView);

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.w(TAG, "Banner failed to load: " + loadAdError.getMessage());
                }

                @Override
                public void onAdLoaded() {
                    Log.d(TAG, "Banner loaded successfully.");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading banner", e);
        }
    }
}
