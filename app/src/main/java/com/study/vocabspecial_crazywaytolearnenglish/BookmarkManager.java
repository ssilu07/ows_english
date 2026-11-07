package com.study.vocabspecial_crazywaytolearnenglish;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookmarkManager {
    private static final String PREF_NAME = "vocab_special_prefs";
    private static final String KEY_BOOKMARKS = "bookmarked_words";
    private static final String KEY_MASTERED = "mastered_words";

    private static BookmarkManager instance;
    private final SharedPreferences prefs;
    private final Set<String> bookmarkedSet;
    private final Set<String> masteredSet;
    private final List<OnBookmarkChangeListener> listeners = new ArrayList<>();

    public interface OnBookmarkChangeListener {
        void onBookmarkChanged(String title, boolean isBookmarked);
    }

    private BookmarkManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> saved = prefs.getStringSet(KEY_BOOKMARKS, null);
        bookmarkedSet = saved != null ? new HashSet<>(saved) : new HashSet<>();

        Set<String> savedMastered = prefs.getStringSet(KEY_MASTERED, null);
        masteredSet = savedMastered != null ? new HashSet<>(savedMastered) : new HashSet<>();
    }

    public static synchronized BookmarkManager getInstance(Context context) {
        if (instance == null) {
            instance = new BookmarkManager(context);
        }
        return instance;
    }

    public synchronized boolean isBookmarked(String title) {
        if (title == null) return false;
        return bookmarkedSet.contains(title.trim().toLowerCase());
    }

    public synchronized boolean toggleBookmark(String title) {
        if (title == null) return false;
        String key = title.trim().toLowerCase();
        boolean newState;
        if (bookmarkedSet.contains(key)) {
            bookmarkedSet.remove(key);
            newState = false;
        } else {
            bookmarkedSet.add(key);
            newState = true;
        }
        saveBookmarks();
        notifyListeners(title, newState);
        return newState;
    }

    public synchronized void setBookmarked(String title, boolean bookmarked) {
        if (title == null) return;
        String key = title.trim().toLowerCase();
        if (bookmarked) {
            bookmarkedSet.add(key);
        } else {
            bookmarkedSet.remove(key);
        }
        saveBookmarks();
        notifyListeners(title, bookmarked);
    }

    private void saveBookmarks() {
        prefs.edit().putStringSet(KEY_BOOKMARKS, new HashSet<>(bookmarkedSet)).apply();
    }

    public synchronized int getBookmarkedCount() {
        return bookmarkedSet.size();
    }

    public synchronized boolean isMastered(String title) {
        if (title == null) return false;
        return masteredSet.contains(title.trim().toLowerCase());
    }

    public synchronized void setMastered(String title, boolean mastered) {
        if (title == null) return;
        String key = title.trim().toLowerCase();
        if (mastered) {
            masteredSet.add(key);
        } else {
            masteredSet.remove(key);
        }
        prefs.edit().putStringSet(KEY_MASTERED, new HashSet<>(masteredSet)).apply();
    }

    public synchronized int getMasteredCount() {
        return masteredSet.size();
    }

    public synchronized void addListener(OnBookmarkChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public synchronized void removeListener(OnBookmarkChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private void notifyListeners(String title, boolean isBookmarked) {
        for (OnBookmarkChangeListener listener : new ArrayList<>(listeners)) {
            try {
                listener.onBookmarkChanged(title, isBookmarked);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
