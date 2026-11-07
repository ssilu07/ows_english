package com.study.vocabspecial_crazywaytolearnenglish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;

public class FullImageView extends AppCompatActivity {
    private PhotoView imageView;
    private com.google.android.material.card.MaterialCardView imageCard;
    private TextView titleTv, keyTv, descriptionTv;
    private ImageView btnBack, btnShareWord, btnSpeakWord, btnBookmarkWord;
    private MaterialButton btnShareWordBottom;
    private FrameLayout detailBannerContainer;

    private String title = "";
    private String key = "";
    private String description = "";
    private String imageUrl = "";

    private BookmarkManager bookmarkManager;
    private TtsManager ttsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image_view);

        bookmarkManager = BookmarkManager.getInstance(this);
        ttsManager = TtsManager.getInstance(this);

        imageView = findViewById(R.id.imageView);
        imageCard = findViewById(R.id.imageCard);
        titleTv = findViewById(R.id.title_tv);
        keyTv = findViewById(R.id.key_tv);
        descriptionTv = findViewById(R.id.description_tv);
        btnBack = findViewById(R.id.btnBack);
        btnShareWord = findViewById(R.id.btnShareWord);
        btnSpeakWord = findViewById(R.id.btnSpeakWord);
        btnBookmarkWord = findViewById(R.id.btnBookmarkWord);
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

        // Bookmark setup
        updateBookmarkIcon();
        if (btnBookmarkWord != null) {
            btnBookmarkWord.setOnClickListener(v -> {
                boolean newStatus = bookmarkManager.toggleBookmark(title);
                updateBookmarkIcon();
                btnBookmarkWord.animate().scaleX(1.3f).scaleY(1.3f).setDuration(120).withEndAction(() ->
                        btnBookmarkWord.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start()
                ).start();
                Toast.makeText(this, newStatus ? "Saved to Starred ⭐" : "Removed from Starred", Toast.LENGTH_SHORT).show();
            });
        }

        // TTS Pronunciation
        if (btnSpeakWord != null) {
            btnSpeakWord.setOnClickListener(v -> {
                ttsManager.speak(title);
                btnSpeakWord.animate().scaleX(1.25f).scaleY(1.25f).setDuration(100).withEndAction(() ->
                        btnSpeakWord.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                ).start();
            });
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

    private void updateBookmarkIcon() {
        if (btnBookmarkWord != null) {
            boolean isBookmarked = bookmarkManager.isBookmarked(title);
            btnBookmarkWord.setImageResource(isBookmarked ? R.drawable.ic_star_filled : R.drawable.ic_star_border);
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
