package com.royals.englishtrickyvocab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class MainActivity extends AppCompatActivity {

    Button study1;
    Button study2;
    Button study3;
    Button study4;
    Button study5;
    Button study6;
    Button study7,study8,study9,study10,study11,study12,study13,study14,study15,study16,study17,study18,study19,study20,study21,
                  study22,study23,study24,study25;
    private final  String TAG = MainActivity.class.getSimpleName();
    private AdView mAdView1,mAdView2,mAdView3;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





            study1 = (Button) findViewById(R.id.study1);
        study1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    loadAd();
                } else {
                    Intent intent = new Intent(MainActivity.this, Study1Activity.class);
                    startActivity(intent);
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });

        study2 = (Button) findViewById(R.id.study2);
        study2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(MainActivity.this);
                        loadAd();
                    } else {
                        Intent intent = new Intent(MainActivity.this, Study2Activity.class);
                        startActivity(intent);
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }

            }
        });


        study3 = (Button) findViewById(R.id.study3);
        study3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    loadAd();
                } else {
                    Intent intent = new Intent(MainActivity.this, Study3Activity.class);
                    startActivity(intent);
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

            }
        });

        study4 = (Button) findViewById(R.id.study4);
        study4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    loadAd();
                } else {
                    Intent intent = new Intent(MainActivity.this, Study4.class);
                    startActivity(intent);
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

            }
        });

        study5 = (Button) findViewById(R.id.study5);
        study5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    loadAd();
                } else {
                    Intent intent = new Intent(MainActivity.this, Study5Activity.class);
                    startActivity(intent);
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

            }
        });



        study6 = (Button) findViewById(R.id.study6);
        study6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    loadAd();
                } else {
                    Intent intent = new Intent(MainActivity.this, Study6Activity.class);
                    startActivity(intent);
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

            }
        });

        study7 = (Button) findViewById(R.id.study7);
        study7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    loadAd();
                } else {
                    Intent intent = new Intent(MainActivity.this, Study7Activity.class);
                    startActivity(intent);
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

            }
        });

        study8 = (Button) findViewById(R.id.study8);
        study8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    loadAd();
                } else {
                    Intent intent = new Intent(MainActivity.this, Study8Activity.class);
                    startActivity(intent);
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

            }
        });

        study9 = (Button) findViewById(R.id.study9);
        study9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    loadAd();
                } else {
                    Intent intent = new Intent(MainActivity.this, Study9Activity.class);
                    startActivity(intent);
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

            }
        });

        study10 = (Button) findViewById(R.id.study10);
        study10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    loadAd();
                } else {
                    Intent intent = new Intent(MainActivity.this, Study10Activity.class);
                    startActivity(intent);
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });


        MobileAds.initialize(this, initializationStatus -> {
        });
        //banner ad
        mAdView1 = findViewById(R.id.adView1);
        mAdView2 = findViewById(R.id.adView2);
        mAdView3 = findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest);
        mAdView2.loadAd(adRequest);
        mAdView3.loadAd(adRequest);

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-1811294933992844/9254313842");

    }

    private void loadAd(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-1811294933992844/4639985605", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });

        if (mInterstitialAd != null) {
            mInterstitialAd.show(MainActivity.this);
        } else {
            Intent intent = new Intent(MainActivity.this, Study1Activity.class);
            startActivity(intent);
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main,menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {


            switch (item.getItemId()){
                case R.id.share_btn:
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String sharebody = "";
                    String shareSubject = "Your subject here";

                    sharingIntent.putExtra(Intent.EXTRA_TEXT,sharebody);
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT,shareSubject);

                    startActivity(Intent.createChooser(sharingIntent,"Share "));
                    break;

            }


            int id = item.getItemId();
            if (id==R.id.privacy_policy){

                //write logic

                Intent intent = new Intent(MainActivity.this, Privacypolicy.class);
                startActivity(intent);

                return true;
            }
            if (id==R.id.terms){
                //write own logic
                Intent intent = new Intent(MainActivity.this, Terms.class);
                startActivity(intent);
                return true;
            }


            return true;
        }


    }


