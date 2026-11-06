package com.royals.englishtrickyvocab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

//import com.github.barteksc.pdfviewer.PDFView;

public class Study3Activity extends AppCompatActivity {

    PDFView study3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        study3=(PDFView) findViewById(R.id.pdfView3);
        study3.fromAsset("Vocab 3_compressed.pdf").load();
    }
}
