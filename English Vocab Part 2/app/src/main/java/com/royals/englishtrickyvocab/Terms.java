package com.royals.englishtrickyvocab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class Terms extends AppCompatActivity {

    WebView mwebview1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);


        mwebview1 = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = mwebview1.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mwebview1.loadUrl("file:///android_asset/termsenglish.html");


    }
}