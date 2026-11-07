package com.study.vocabspecial_crazywaytolearnenglish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlashcardsActivity extends AppCompatActivity {

    private ImageView btnBack, btnShuffle, btnStarCard, btnSpeakCard;
    private TextView tvCounter, tvMasteredBadge;
    private ProgressBar flashcardProgressBar;
    private MaterialCardView cardFront, cardBack;
    private TextView tvFrontQuestion, tvFrontHint;
    private TextView tvBackWord, tvBackTrick, tvBackDescription;
    private MaterialButton btnPrev, btnMastered, btnNext;
    private FrameLayout flashcardBannerContainer;

    private final List<NoticeData> cardsList = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isFlipped = false;
    private boolean isAnimating = false;

    private BookmarkManager bookmarkManager;
    private TtsManager ttsManager;
    private int cardsViewedCount = 0;

    private FrameLayout cardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);

        bookmarkManager = BookmarkManager.getInstance(this);
        ttsManager = TtsManager.getInstance(this);

        initViews();
        setupCameraDistance();
        setupClickListeners();
        loadFlashcardData();

        if (flashcardBannerContainer != null) {
            AdManager.getInstance().loadBanner(this, flashcardBannerContainer);
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnShuffle = findViewById(R.id.btnShuffle);
        btnStarCard = findViewById(R.id.btnStarCard);
        btnSpeakCard = findViewById(R.id.btnSpeakCard);
        tvCounter = findViewById(R.id.tvCounter);
        tvMasteredBadge = findViewById(R.id.tvMasteredBadge);
        flashcardProgressBar = findViewById(R.id.flashcardProgressBar);
        cardContainer = findViewById(R.id.cardContainer);
        cardFront = findViewById(R.id.cardFront);
        cardBack = findViewById(R.id.cardBack);
        tvFrontQuestion = findViewById(R.id.tvFrontQuestion);
        tvFrontHint = findViewById(R.id.tvFrontHint);
        tvBackWord = findViewById(R.id.tvBackWord);
        tvBackTrick = findViewById(R.id.tvBackTrick);
        tvBackDescription = findViewById(R.id.tvBackDescription);
        btnPrev = findViewById(R.id.btnPrev);
        btnMastered = findViewById(R.id.btnMastered);
        btnNext = findViewById(R.id.btnNext);
        flashcardBannerContainer = findViewById(R.id.flashcardBannerContainer);
    }

    private void setupCameraDistance() {
        float scale = getResources().getDisplayMetrics().density;
        float distance = 8000 * scale;
        cardFront.setCameraDistance(distance);
        cardBack.setCameraDistance(distance);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Card flip on tap on container or card
        View.OnClickListener flipListener = v -> flipCard();
        cardContainer.setOnClickListener(flipListener);
        cardFront.setOnClickListener(flipListener);
        cardBack.setOnClickListener(flipListener);

        View frontInner = findViewById(R.id.frontInnerLayout);
        if (frontInner != null) frontInner.setOnClickListener(flipListener);

        View backInner = findViewById(R.id.backInnerLayout);
        if (backInner != null) backInner.setOnClickListener(flipListener);

        btnNext.setOnClickListener(v -> nextCard());
        btnPrev.setOnClickListener(v -> prevCard());

        btnMastered.setOnClickListener(v -> {
            if (cardsList.isEmpty()) return;
            NoticeData current = cardsList.get(currentIndex);
            bookmarkManager.setMastered(current.getTitle(), true);
            updateMasteredBadge();
            Toast.makeText(this, "Marked as Mastered! 🌟", Toast.LENGTH_SHORT).show();
            nextCard();
        });

        btnShuffle.setOnClickListener(v -> {
            if (cardsList.size() > 1) {
                Collections.shuffle(cardsList);
                currentIndex = 0;
                resetCardState();
                displayCurrentCard();
                Toast.makeText(this, "Deck Shuffled! 🔀", Toast.LENGTH_SHORT).show();
            }
        });

        btnStarCard.setOnClickListener(v -> {
            if (cardsList.isEmpty()) return;
            NoticeData current = cardsList.get(currentIndex);
            boolean newStatus = bookmarkManager.toggleBookmark(current.getTitle());
            updateStarIcon(newStatus);
            btnStarCard.animate().scaleX(1.3f).scaleY(1.3f).setDuration(120).withEndAction(() ->
                    btnStarCard.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start()
            ).start();
            Toast.makeText(this, newStatus ? "Saved to Starred ⭐" : "Removed from Starred", Toast.LENGTH_SHORT).show();
        });

        btnSpeakCard.setOnClickListener(v -> {
            if (cardsList.isEmpty()) return;
            NoticeData current = cardsList.get(currentIndex);
            ttsManager.speak(current.getTitle());
            btnSpeakCard.animate().scaleX(1.25f).scaleY(1.25f).setDuration(100).withEndAction(() ->
                    btnSpeakCard.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
            ).start();
        });
    }

    private void loadFlashcardData() {
        boolean onlyStarred = getIntent().getBooleanExtra("only_starred", false);

        new Thread(() -> {
            List<NoticeData> loaded = new ArrayList<>();
            try {
                InputStream is = getAssets().open("ssc_ows.json");
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                is.close();

                String jsonStr = new String(buffer, StandardCharsets.UTF_8);
                JSONArray jsonArray = new JSONArray(jsonStr);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String title = obj.optString("title");
                    String image = obj.optString("image");
                    String key = obj.optString("key");
                    String desc = obj.optString("description");
                    String keyValue = obj.optString("key_value");

                    if (onlyStarred && !bookmarkManager.isBookmarked(title)) {
                        continue;
                    }

                    loaded.add(new NoticeData(title, image, key, desc, keyValue));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                if (isFinishing()) return;
                cardsList.clear();
                cardsList.addAll(loaded);

                if (cardsList.isEmpty()) {
                    if (onlyStarred) {
                        Toast.makeText(this, "No starred words yet! Star words first.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "No flashcards found.", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    return;
                }

                currentIndex = 0;
                displayCurrentCard();
                updateMasteredBadge();
            });
        }).start();
    }

    private void displayCurrentCard() {
        if (cardsList.isEmpty() || currentIndex < 0 || currentIndex >= cardsList.size()) return;

        NoticeData item = cardsList.get(currentIndex);

        // Parse clean question/definition and word
        String fullTitle = item.getTitle() != null ? item.getTitle().trim() : "";
        String wordName = fullTitle;
        String definition = item.getDescription() != null ? item.getDescription().trim() : "";

        if (fullTitle.contains("-")) {
            String[] parts = fullTitle.split("-", 2);
            wordName = parts[0].trim();
            if (definition.isEmpty()) {
                definition = parts[1].trim();
            }
        }

        // Front Face
        tvFrontQuestion.setText(definition.isEmpty() ? fullTitle : definition);
        tvFrontHint.setText("Can you recall the One Word for this?");

        // Back Face
        tvBackWord.setText(wordName.toUpperCase());

        String trickText = item.getKey_value();
        if (trickText == null || trickText.isEmpty()) {
            trickText = item.getKey();
        }
        tvBackTrick.setText(trickText != null && !trickText.isEmpty() ? trickText : "Practice and recall definition");
        tvBackDescription.setText(item.getDescription() != null ? item.getDescription() : "");

        // Counter & Progress
        tvCounter.setText("Card " + (currentIndex + 1) + " of " + cardsList.size());
        int progress = (int) (((currentIndex + 1) * 100.0) / cardsList.size());
        flashcardProgressBar.setProgress(progress);

        // Star Icon
        updateStarIcon(bookmarkManager.isBookmarked(fullTitle));

        // Prev button state
        btnPrev.setEnabled(currentIndex > 0);
        btnPrev.setAlpha(currentIndex > 0 ? 1.0f : 0.4f);
    }

    private void flipCard() {
        if (isAnimating) return;
        isAnimating = true;

        final View visibleCard = isFlipped ? cardBack : cardFront;
        final View hiddenCard = isFlipped ? cardFront : cardBack;

        visibleCard.animate()
                .rotationY(90f)
                .setDuration(120)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    visibleCard.setVisibility(View.GONE);
                    visibleCard.setRotationY(0f);
                    hiddenCard.setRotationY(-90f);
                    hiddenCard.setVisibility(View.VISIBLE);
                    hiddenCard.animate()
                            .rotationY(0f)
                            .setDuration(120)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(() -> {
                                isFlipped = !isFlipped;
                                isAnimating = false;
                            }).start();
                }).start();
    }

    private void resetCardState() {
        isFlipped = false;
        cardFront.setRotationY(0);
        cardBack.setRotationY(0);
        cardFront.setVisibility(View.VISIBLE);
        cardBack.setVisibility(View.GONE);
    }

    private void nextCard() {
        if (currentIndex < cardsList.size() - 1) {
            currentIndex++;
            resetCardState();
            displayCurrentCard();
            checkAdTrigger();
        } else {
            Toast.makeText(this, "You've reached the end of the deck! 🎉", Toast.LENGTH_SHORT).show();
        }
    }

    private void prevCard() {
        if (currentIndex > 0) {
            currentIndex--;
            resetCardState();
            displayCurrentCard();
        }
    }

    private void updateStarIcon(boolean isBookmarked) {
        btnStarCard.setImageResource(isBookmarked ? R.drawable.ic_star_filled : R.drawable.ic_star_border);
    }

    private void updateMasteredBadge() {
        int masteredCount = bookmarkManager.getMasteredCount();
        tvMasteredBadge.setText("⭐ " + masteredCount + " Mastered");
    }

    private void checkAdTrigger() {
        cardsViewedCount++;
        if (cardsViewedCount >= 15) {
            cardsViewedCount = 0;
            AdManager.getInstance().showInterstitialWithCounter(this, () -> {});
        }
    }
}
