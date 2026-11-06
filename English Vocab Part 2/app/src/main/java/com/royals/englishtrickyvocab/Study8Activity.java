package com.royals.englishtrickyvocab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

//import com.github.barteksc.pdfviewer.PDFView;

public class Study8Activity extends AppCompatActivity {
    PDFView study8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study8);

        study8=(PDFView) findViewById(R.id.pdfView8);
        study8.fromAsset("Vocab 8_compressed.pdf").load();

    }
}