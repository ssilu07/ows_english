package com.royals.englishtrickyvocab;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

//import com.github.barteksc.pdfviewer.PDFView;

public class Study7Activity extends AppCompatActivity {

    PDFView study7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study7);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        study7=(PDFView) findViewById(R.id.pdfView7);
        study7.fromAsset("Vocab 7_compressed.pdf").load();

    }
}