package com.study.vocabspecial_crazywaytolearnenglish;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private ImageView btnQuitQuiz, btnSpeakQuizWord;
    private TextView tvQuizScoreBadge, tvQuestionNumber, tvQuizQuestion;
    private ProgressBar quizProgressBar;

    private MaterialCardView cardOption1, cardOption2, cardOption3, cardOption4;
    private TextView tvOption1, tvOption2, tvOption3, tvOption4;

    private MaterialCardView cardExplanation;
    private TextView tvExplanationTrick, tvExplanationMeaning;
    private MaterialButton btnNextQuestion;

    private MaterialCardView layoutQuizResults;
    private TextView tvResultScore, tvResultFeedback;
    private MaterialButton btnRestartQuiz, btnGoToFlashcards, btnBackToHome;
    private FrameLayout quizBannerContainer;

    private final List<NoticeData> allWordsList = new ArrayList<>();
    private final List<QuizQuestion> quizQuestions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int userScore = 0;
    private boolean isOptionSelected = false;

    private TtsManager ttsManager;

    private static class QuizQuestion {
        String definition;
        String correctWord;
        List<String> options;
        int correctOptionIndex;
        String trick;
        String fullDescription;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        ttsManager = TtsManager.getInstance(this);

        initViews();
        setupClickListeners();
        loadAllWordsAndStartQuiz();

        if (quizBannerContainer != null) {
            AdManager.getInstance().loadBanner(this, quizBannerContainer);
        }
    }

    private void initViews() {
        btnQuitQuiz = findViewById(R.id.btnQuitQuiz);
        tvQuizScoreBadge = findViewById(R.id.tvQuizScoreBadge);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuizQuestion = findViewById(R.id.tvQuizQuestion);
        quizProgressBar = findViewById(R.id.quizProgressBar);

        cardOption1 = findViewById(R.id.cardOption1);
        cardOption2 = findViewById(R.id.cardOption2);
        cardOption3 = findViewById(R.id.cardOption3);
        cardOption4 = findViewById(R.id.cardOption4);

        tvOption1 = findViewById(R.id.tvOption1);
        tvOption2 = findViewById(R.id.tvOption2);
        tvOption3 = findViewById(R.id.tvOption3);
        tvOption4 = findViewById(R.id.tvOption4);

        cardExplanation = findViewById(R.id.cardExplanation);
        tvExplanationTrick = findViewById(R.id.tvExplanationTrick);
        tvExplanationMeaning = findViewById(R.id.tvExplanationMeaning);
        btnSpeakQuizWord = findViewById(R.id.btnSpeakQuizWord);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);

        layoutQuizResults = findViewById(R.id.layoutQuizResults);
        tvResultScore = findViewById(R.id.tvResultScore);
        tvResultFeedback = findViewById(R.id.tvResultFeedback);
        btnRestartQuiz = findViewById(R.id.btnRestartQuiz);
        btnGoToFlashcards = findViewById(R.id.btnGoToFlashcards);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        quizBannerContainer = findViewById(R.id.quizBannerContainer);
    }

    private void setupClickListeners() {
        btnQuitQuiz.setOnClickListener(v -> finish());

        cardOption1.setOnClickListener(v -> selectOption(0));
        cardOption2.setOnClickListener(v -> selectOption(1));
        cardOption3.setOnClickListener(v -> selectOption(2));
        cardOption4.setOnClickListener(v -> selectOption(3));

        btnNextQuestion.setOnClickListener(v -> {
            if (currentQuestionIndex < quizQuestions.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                showQuizResults();
            }
        });

        btnRestartQuiz.setOnClickListener(v -> {
            layoutQuizResults.setVisibility(View.GONE);
            generateQuizQuestions();
            currentQuestionIndex = 0;
            userScore = 0;
            tvQuizScoreBadge.setText("Score: 0 / 10");
            displayQuestion();
        });

        btnGoToFlashcards.setOnClickListener(v -> {
            Intent intent = new Intent(this, FlashcardsActivity.class);
            startActivity(intent);
            finish();
        });

        btnBackToHome.setOnClickListener(v -> finish());

        btnSpeakQuizWord.setOnClickListener(v -> {
            if (currentQuestionIndex < quizQuestions.size()) {
                ttsManager.speak(quizQuestions.get(currentQuestionIndex).correctWord);
            }
        });
    }

    private void loadAllWordsAndStartQuiz() {
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
                    loaded.add(new NoticeData(title, image, key, desc, keyValue));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            allWordsList.clear();
            allWordsList.addAll(loaded);

            if (allWordsList.size() >= 10) {
                generateQuizQuestions();
            }

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;

                if (allWordsList.size() < 10) {
                    Toast.makeText(this, "Not enough words to start quiz.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                currentQuestionIndex = 0;
                userScore = 0;
                displayQuestion();
            });
        }).start();
    }

    private void generateQuizQuestions() {
        quizQuestions.clear();
        List<NoticeData> pool = new ArrayList<>(allWordsList);
        Collections.shuffle(pool);

        Random random = new Random();
        int totalQuestions = Math.min(10, pool.size());
        int poolSize = allWordsList.size();

        for (int i = 0; i < totalQuestions; i++) {
            NoticeData correctItem = pool.get(i);
            String correctWord = extractWordName(correctItem.getTitle());
            String definition = correctItem.getDescription() != null ? correctItem.getDescription().trim() : "";
            if (definition.isEmpty()) {
                definition = correctItem.getTitle();
            }

            // Pick 3 distinct distractors efficiently
            List<String> options = new ArrayList<>();
            options.add(correctWord);

            int attempts = 0;
            while (options.size() < 4 && attempts < 100) {
                attempts++;
                NoticeData candidate = allWordsList.get(random.nextInt(poolSize));
                String candidateWord = extractWordName(candidate.getTitle());
                if (!candidateWord.isEmpty() && !candidateWord.equalsIgnoreCase(correctWord) && !options.contains(candidateWord)) {
                    options.add(candidateWord);
                }
            }

            // Shuffle options A, B, C, D
            Collections.shuffle(options);
            int correctIndex = options.indexOf(correctWord);

            QuizQuestion q = new QuizQuestion();
            q.definition = definition;
            q.correctWord = correctWord;
            q.options = options;
            q.correctOptionIndex = correctIndex;

            String trick = correctItem.getKey_value();
            if (trick == null || trick.isEmpty()) trick = correctItem.getKey();
            q.trick = trick != null ? trick : "Recall word by root/trick";
            q.fullDescription = correctItem.getDescription();

            quizQuestions.add(q);
        }
    }

    private String extractWordName(String title) {
        if (title == null) return "";
        String clean = title.trim();
        if (clean.contains("-")) {
            String[] parts = clean.split("-", 2);
            return parts[0].trim();
        }
        return clean;
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= quizQuestions.size()) return;

        isOptionSelected = false;
        QuizQuestion q = quizQuestions.get(currentQuestionIndex);

        tvQuestionNumber.setText("Question " + (currentQuestionIndex + 1) + " of " + quizQuestions.size());
        quizProgressBar.setProgress(currentQuestionIndex + 1);
        tvQuizQuestion.setText(q.definition);

        // Reset option card visual states
        resetOptionCard(cardOption1, tvOption1, "A. " + q.options.get(0));
        resetOptionCard(cardOption2, tvOption2, "B. " + q.options.get(1));
        resetOptionCard(cardOption3, tvOption3, "C. " + q.options.get(2));
        resetOptionCard(cardOption4, tvOption4, "D. " + q.options.get(3));

        cardExplanation.setVisibility(View.GONE);
        btnNextQuestion.setEnabled(false);
        btnNextQuestion.setAlpha(0.5f);
        btnNextQuestion.setText(currentQuestionIndex == quizQuestions.size() - 1 ? "Finish Quiz 🏁" : "Next Question ➡️");
    }

    private void resetOptionCard(MaterialCardView card, TextView textView, String text) {
        card.setCardBackgroundColor(Color.WHITE);
        card.setStrokeColor(Color.parseColor("#E2E8F0"));
        card.setStrokeWidth((int) (1.5f * getResources().getDisplayMetrics().density));
        textView.setTextColor(Color.parseColor("#0F172A"));
        textView.setText(text);
        card.setEnabled(true);
    }

    private void selectOption(int selectedIndex) {
        if (isOptionSelected || currentQuestionIndex >= quizQuestions.size()) return;
        isOptionSelected = true;

        QuizQuestion q = quizQuestions.get(currentQuestionIndex);
        boolean isCorrect = (selectedIndex == q.correctOptionIndex);

        if (isCorrect) {
            userScore++;
            tvQuizScoreBadge.setText("Score: " + userScore + " / 10");
        }

        // Disable all options
        cardOption1.setEnabled(false);
        cardOption2.setEnabled(false);
        cardOption3.setEnabled(false);
        cardOption4.setEnabled(false);

        MaterialCardView[] cards = {cardOption1, cardOption2, cardOption3, cardOption4};
        TextView[] textViews = {tvOption1, tvOption2, tvOption3, tvOption4};

        if (isCorrect) {
            // Highlight selected in Green
            cards[selectedIndex].setCardBackgroundColor(Color.parseColor("#10B981"));
            cards[selectedIndex].setStrokeColor(Color.parseColor("#059669"));
            textViews[selectedIndex].setTextColor(Color.WHITE);
        } else {
            // Highlight selected in Red
            cards[selectedIndex].setCardBackgroundColor(Color.parseColor("#EF4444"));
            cards[selectedIndex].setStrokeColor(Color.parseColor("#DC2626"));
            textViews[selectedIndex].setTextColor(Color.WHITE);

            // Highlight correct option in Green so user learns immediately
            int correctIdx = q.correctOptionIndex;
            cards[correctIdx].setCardBackgroundColor(Color.parseColor("#DEF7EC"));
            cards[correctIdx].setStrokeColor(Color.parseColor("#10B981"));
            cards[correctIdx].setStrokeWidth((int) (2.5f * getResources().getDisplayMetrics().density));
            textViews[correctIdx].setTextColor(Color.parseColor("#03543F"));
        }

        // Show Explanation Box
        cardExplanation.setVisibility(View.VISIBLE);
        tvExplanationTrick.setText("💡 Trick: " + q.trick);
        tvExplanationMeaning.setText(q.correctWord + ": " + q.fullDescription);

        btnNextQuestion.setEnabled(true);
        btnNextQuestion.setAlpha(1.0f);
    }

    private void showQuizResults() {
        layoutQuizResults.setVisibility(View.VISIBLE);
        tvResultScore.setText(userScore + " / 10");

        if (userScore == 10) {
            tvResultFeedback.setText("Outstanding! Perfect Score! 🏆\nYou've mastered these SSC words!");
        } else if (userScore >= 8) {
            tvResultFeedback.setText("Excellent Job! 🎯\nYou're almost exam ready!");
        } else if (userScore >= 6) {
            tvResultFeedback.setText("Good Effort! 📚\nA quick revision with Flashcards will get you to 10/10!");
        } else {
            tvResultFeedback.setText("Keep Practicing! 💪\nUse Flashcard mode to memorize the tricks!");
        }

        AdManager.getInstance().showInterstitialWithCounter(this, () -> {});
    }
}
