package com.royals.englishtrickyvocab;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

public class Study1Activity extends AppCompatActivity {

    PDFView study1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_study1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        study1=(PDFView) findViewById(R.id.pdfView1);
        study1.fromAsset("vocab 1_compressed.pdf").load();

       /* final Uri uri = Uri.parse("file:///android_asset/vocab 1_compressed.pdf");
        final PdfActivityConfiguration config = new PdfActivityConfiguration.Builder(getApplicationContext()).build();
        new PdfActivityConfiguration.Builder(this).setThumbnailBarMode(ThumbnailBarMode.THUMBNAIL_BAR_MODE_NONE);

        PdfActivity.showDocument(this, uri, config);*/

    }
}
