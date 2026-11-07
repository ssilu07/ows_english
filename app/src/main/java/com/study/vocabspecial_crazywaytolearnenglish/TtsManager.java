package com.study.vocabspecial_crazywaytolearnenglish;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class TtsManager {
    private static TtsManager instance;
    private TextToSpeech tts;
    private boolean isInitialized = false;
    private final Context context;

    private TtsManager(Context context) {
        this.context = context.getApplicationContext();
        initialize();
    }

    public static synchronized TtsManager getInstance(Context context) {
        if (instance == null) {
            instance = new TtsManager(context);
        }
        return instance;
    }

    private void initialize() {
        try {
            tts = new TextToSpeech(context, status -> {
                if (status == TextToSpeech.SUCCESS && tts != null) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        result = tts.setLanguage(Locale.ENGLISH);
                    }
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts.setLanguage(Locale.getDefault());
                    }
                    tts.setSpeechRate(0.92f); // Clear, articulate pronunciation for vocabulary learning
                    tts.setPitch(1.0f);
                    isInitialized = true;
                } else {
                    isInitialized = false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            isInitialized = false;
        }
    }

    public void speak(String text) {
        if (text == null || text.trim().isEmpty()) return;

        // If not initialized yet, try re-initializing
        if (!isInitialized || tts == null) {
            initialize();
        }

        try {
            if (tts != null) {
                // If title has "Word - Definition", speak the word cleanly
                String cleanText = text.trim();
                if (cleanText.contains("-")) {
                    String[] parts = cleanText.split("-", 2);
                    cleanText = parts[0].trim();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Bundle params = new Bundle();
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "vocab_tts");
                    tts.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "vocab_tts");
                } else {
                    tts.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void speakSentence(String text) {
        if (text == null || text.trim().isEmpty()) return;

        try {
            if (tts != null && isInitialized) {
                tts.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null, "vocab_sentence");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (tts != null && isInitialized) {
                tts.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
                tts = null;
                isInitialized = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
