package com.study.vocabspecial_crazywaytolearnenglish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;

public class FullImageView extends AppCompatActivity {
    private PhotoView imageView;
    private com.google.android.material.card.MaterialCardView imageCard;
    private TextView titleTv, keyTv, descriptionTv;
    private ImageView btnBack, btnShareWord;
    private MaterialButton btnShareWordBottom;
    private FrameLayout detailBannerContainer;

    private String title = "";
    private String key = "";
    private String description = "";
    private String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image_view);

        imageView = findViewById(R.id.imageView);
        imageCard = findViewById(R.id.imageCard);
        titleTv = findViewById(R.id.title_tv);
        keyTv = findViewById(R.id.key_tv);
        descriptionTv = findViewById(R.id.description_tv);
        btnBack = findViewById(R.id.btnBack);
        btnShareWord = findViewById(R.id.btnShareWord);
        btnShareWordBottom = findViewById(R.id.btnShareWordBottom);
        detailBannerContainer = findViewById(R.id.detailBannerContainer);

        // Retrieve data from Intent
        if (getIntent() != null) {
            imageUrl = getIntent().getStringExtra("image_intent");
            title = getIntent().getStringExtra("title_intent");
            key = getIntent().getStringExtra("key_intent");
            description = getIntent().getStringExtra("description_intent");
        }

        if (title == null) title = "";
        if (key == null) key = "";
        if (description == null) description = "";

        titleTv.setText(title);
        keyTv.setText(key);
        descriptionTv.setText(description);

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            if (imageCard != null) imageCard.setVisibility(android.view.View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.vocab_icon)
                    .error(R.drawable.vocab_icon)
                    .into(imageView);
        } else {
            if (imageCard != null) imageCard.setVisibility(android.view.View.GONE);
        }

        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Share buttons
        if (btnShareWord != null) {
            btnShareWord.setOnClickListener(v -> shareWord());
        }
        if (btnShareWordBottom != null) {
            btnShareWordBottom.setOnClickListener(v -> shareWord());
        }

        // Load banner ad at bottom
        if (detailBannerContainer != null) {
            AdManager.getInstance().loadBanner(this, detailBannerContainer);
        }
    }

    private void shareWord() {
        String shareBody = getString(R.string.share_word_template, title, key, description);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Learn " + title + " with Trick!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntent, "Share Word Trick Via"));
    }
}
