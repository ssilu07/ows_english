package com.royals.englishtrickyvocab;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

//import com.github.barteksc.pdfviewer.PDFView;

public class Study6Activity extends AppCompatActivity {

    PDFView study6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study6);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        study6=(PDFView) findViewById(R.id.pdfView6);
        study6.fromAsset("Vocab 6_compressed.pdf").load();
    }
}