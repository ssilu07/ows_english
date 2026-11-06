package com.royals.englishtrickyvocab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

//import com.github.barteksc.pdfviewer.PDFView;

public class Study10Activity extends AppCompatActivity {

    PDFView study10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study10);

        study10=(PDFView) findViewById(R.id.pdfView10);
        study10.fromAsset("Vocab 10_compressed.pdf").load();

    }
}