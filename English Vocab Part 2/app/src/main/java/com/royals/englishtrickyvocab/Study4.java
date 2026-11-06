package com.royals.englishtrickyvocab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

//import com.github.barteksc.pdfviewer.PDFView;

public class Study4 extends AppCompatActivity {

    PDFView study4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study4);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        study4=(PDFView) findViewById(R.id.pdfView4);
        study4.fromAsset("Vocab 4_compressed.pdf").load();

    }
}