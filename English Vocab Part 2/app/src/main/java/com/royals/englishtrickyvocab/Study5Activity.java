package com.royals.englishtrickyvocab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

//import com.github.barteksc.pdfviewer.PDFView;

public class Study5Activity extends AppCompatActivity {

    PDFView study5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study5);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        study5=(PDFView) findViewById(R.id.pdfView5);
        study5.fromAsset("Vocab 5_compressed.pdf").load();
    }
}