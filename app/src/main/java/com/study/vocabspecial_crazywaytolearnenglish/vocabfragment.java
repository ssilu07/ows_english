package com.study.vocabspecial_crazywaytolearnenglish;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class vocabfragment extends Fragment implements BookmarkManager.OnBookmarkChangeListener {

    private RecyclerView deleteNoticeRecycler;
    private ProgressBar progressBar;
    private LinearLayout emptyView;
    private TextView emptyIconTv, emptyTextTitle, emptyTextSubtitle;
    private TextView wordCountTv, countBadgeTv;
    private EditText searchEditText;
    private ImageView btnClearSearch;

    private MaterialCardView cardStartFlashcards, cardStartQuiz;
    private TextView tabAllWords, tabStarredWords;
    private LinearLayout alphabetContainer;

    private ArrayList<NoticeData> list = new ArrayList<>();
    private NoticeAdapter adapter;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private BookmarkManager bookmarkManager;
    private String selectedLetter = "ALL";
    private boolean showOnlyStarred = false;
    private final ArrayList<TextView> alphabetChipViews = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocabfragment, container, false);

        Context ctx = getContext();
        if (ctx != null) {
            bookmarkManager = BookmarkManager.getInstance(ctx);
            bookmarkManager.addListener(this);
        }

        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupActionHub();
        setupFilterTabs();
        setupAlphabetChips();

        // 1. Instant offline load from assets
        loadLocalAssets();

        // 2. Realtime sync from Firebase Notice
        reference = FirebaseDatabase.getInstance().getReference().child("Notice");
        loadNoticeFromFirebase();

        return view;
    }

    private void initViews(View view) {
        deleteNoticeRecycler = view.findViewById(R.id.deleteNoticeRecycler);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);
        emptyIconTv = view.findViewById(R.id.emptyIconTv);
        emptyTextTitle = view.findViewById(R.id.emptyTextTitle);
        emptyTextSubtitle = view.findViewById(R.id.emptyTextSubtitle);
        wordCountTv = view.findViewById(R.id.wordCountTv);
        countBadgeTv = view.findViewById(R.id.countBadgeTv);
        searchEditText = view.findViewById(R.id.searchEditText);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);

        cardStartFlashcards = view.findViewById(R.id.cardStartFlashcards);
        cardStartQuiz = view.findViewById(R.id.cardStartQuiz);
        tabAllWords = view.findViewById(R.id.tabAllWords);
        tabStarredWords = view.findViewById(R.id.tabStarredWords);
        alphabetContainer = view.findViewById(R.id.alphabetContainer);
    }

    private void setupRecyclerView() {
        deleteNoticeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        deleteNoticeRecycler.setHasFixedSize(true);

        adapter = new NoticeAdapter(getContext(), new ArrayList<>());
        adapter.setOnItemBookmarkListener((item, isBookmarked) -> {
            updateStarredTabBadge();
            if (showOnlyStarred) {
                applyCurrentFilters();
            }
        });
        deleteNoticeRecycler.setAdapter(adapter);
    }

    private void setupActionHub() {
        if (cardStartFlashcards != null) {
            cardStartFlashcards.setOnClickListener(v -> {
                Context ctx = getContext();
                if (ctx != null) {
                    Intent intent = new Intent(ctx, FlashcardsActivity.class);
                    intent.putExtra("only_starred", showOnlyStarred && bookmarkManager.getBookmarkedCount() > 0);
                    startActivity(intent);
                }
            });
        }

        if (cardStartQuiz != null) {
            cardStartQuiz.setOnClickListener(v -> {
                Context ctx = getContext();
                if (ctx != null) {
                    Intent intent = new Intent(ctx, QuizActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setupFilterTabs() {
        updateStarredTabBadge();
        styleTabs();

        if (tabAllWords != null) {
            tabAllWords.setOnClickListener(v -> {
                if (showOnlyStarred) {
                    showOnlyStarred = false;
                    styleTabs();
                    applyCurrentFilters();
                }
            });
        }

        if (tabStarredWords != null) {
            tabStarredWords.setOnClickListener(v -> {
                if (!showOnlyStarred) {
                    showOnlyStarred = true;
                    styleTabs();
                    applyCurrentFilters();
                }
            });
        }
    }

    private void styleTabs() {
        if (tabAllWords == null || tabStarredWords == null) return;

        GradientDrawable selectedBg = new GradientDrawable();
        selectedBg.setShape(GradientDrawable.RECTANGLE);
        selectedBg.setCornerRadius(16);
        selectedBg.setColor(Color.parseColor("#0284C7"));

        GradientDrawable unselectedBg = new GradientDrawable();
        unselectedBg.setShape(GradientDrawable.RECTANGLE);
        unselectedBg.setCornerRadius(16);
        unselectedBg.setColor(Color.parseColor("#E2E8F0"));

        if (!showOnlyStarred) {
            tabAllWords.setBackground(selectedBg);
            tabAllWords.setTextColor(Color.WHITE);

            tabStarredWords.setBackground(unselectedBg);
            tabStarredWords.setTextColor(Color.parseColor("#475569"));
        } else {
            tabAllWords.setBackground(unselectedBg);
            tabAllWords.setTextColor(Color.parseColor("#475569"));

            selectedBg.setColor(Color.parseColor("#F59E0B"));
            tabStarredWords.setBackground(selectedBg);
            tabStarredWords.setTextColor(Color.WHITE);
        }
    }

    private void updateStarredTabBadge() {
        if (tabStarredWords != null && bookmarkManager != null) {
            int count = bookmarkManager.getBookmarkedCount();
            tabStarredWords.setText("⭐ Starred (" + count + ")");
        }
    }

    private void setupAlphabetChips() {
        if (alphabetContainer == null) return;
        alphabetContainer.removeAllViews();
        alphabetChipViews.clear();

        String[] letters = {"ALL", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        for (String letter : letters) {
            TextView chip = new TextView(getContext());
            chip.setText(letter);
            chip.setTextSize(12);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dpToPx(12), dpToPx(5), dpToPx(12), dpToPx(5));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(dpToPx(3), 0, dpToPx(3), 0);
            chip.setLayoutParams(lp);

            chip.setOnClickListener(v -> {
                selectedLetter = letter;
                updateAlphabetChipStyles();
                applyCurrentFilters();
            });

            alphabetContainer.addView(chip);
            alphabetChipViews.add(chip);
        }

        updateAlphabetChipStyles();
    }

    private void updateAlphabetChipStyles() {
        for (TextView chip : alphabetChipViews) {
            String letter = chip.getText().toString();
            boolean isSelected = letter.equalsIgnoreCase(selectedLetter);

            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.RECTANGLE);
            gd.setCornerRadius(dpToPx(14));

            if (isSelected) {
                gd.setColor(Color.parseColor("#0284C7"));
                chip.setTextColor(Color.WHITE);
                chip.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                gd.setColor(Color.parseColor("#F1F5F9"));
                gd.setStroke(dpToPx(1), Color.parseColor("#CBD5E1"));
                chip.setTextColor(Color.parseColor("#475569"));
                chip.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
            chip.setBackground(gd);
        }
    }

    private void setupSearch() {
        if (searchEditText != null) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s != null ? s.toString().trim() : "";
                    if (btnClearSearch != null) {
                        btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                    applyCurrentFilters();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            btnClearSearch.setOnClickListener(v -> {
                searchEditText.setText("");
                searchEditText.clearFocus();
                hideKeyboard();
            });

            searchEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                    searchEditText.clearFocus();
                    hideKeyboard();
                    return true;
                }
                return false;
            });
        }
    }

    private void hideKeyboard() {
        Context ctx = getContext();
        if (ctx != null && searchEditText != null) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            }
        }
    }

    private void applyCurrentFilters() {
        if (adapter == null) return;

        String query = searchEditText != null ? searchEditText.getText().toString().trim() : "";
        adapter.filter(query, selectedLetter, showOnlyStarred);

        int countResult = adapter.getItemCount();

        // Update count badge & subtitle
        if (countBadgeTv != null) {
            if (query.isEmpty() && selectedLetter.equalsIgnoreCase("ALL") && !showOnlyStarred) {
                countBadgeTv.setText(list.size() + " Words");
            } else {
                countBadgeTv.setText(countResult + " Found");
            }
        }

        if (wordCountTv != null) {
            if (showOnlyStarred) {
                wordCountTv.setText("⭐ Starred Revision List");
            } else if (!selectedLetter.equalsIgnoreCase("ALL")) {
                wordCountTv.setText("Words starting with '" + selectedLetter + "'");
            } else {
                wordCountTv.setText("SSC Previous Year Questions (A to Z)");
            }
        }

        // Empty state configuration
        if (emptyView != null) {
            if (countResult == 0) {
                emptyView.setVisibility(View.VISIBLE);
                if (showOnlyStarred) {
                    emptyIconTv.setText("⭐");
                    emptyTextTitle.setText("No Starred Words Yet");
                    emptyTextSubtitle.setText("Tap the star icon on any word to save it here for fast revision!");
                } else if (!query.isEmpty()) {
                    emptyIconTv.setText("🔍");
                    emptyTextTitle.setText("No Results for \"" + query + "\"");
                    emptyTextSubtitle.setText("Try another keyword or tap clear.");
                } else {
                    emptyIconTv.setText("📚");
                    emptyTextTitle.setText("No Words for Letter '" + selectedLetter + "'");
                    emptyTextSubtitle.setText("Select 'ALL' to view all words.");
                }
            } else {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    private void loadLocalAssets() {
        executorService.execute(() -> {
            Context ctx = getContext();
            if (ctx == null) return;

            ArrayList<NoticeData> assetList = new ArrayList<>();
            try {
                InputStream is = ctx.getAssets().open("ssc_ows.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                String jsonStr = new String(buffer, StandardCharsets.UTF_8);
                JSONArray jsonArray = new JSONArray(jsonStr);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    NoticeData item = new NoticeData(
                            obj.optString("title"),
                            obj.optString("image"),
                            obj.optString("key"),
                            obj.optString("description"),
                            obj.optString("key_value")
                    );
                    assetList.add(item);
                }

                sortAlphabetically(assetList);

            } catch (Exception e) {
                e.printStackTrace();
            }

            mainHandler.post(() -> {
                if (!isAdded() || assetList.isEmpty()) return;

                if (list.isEmpty()) {
                    list = assetList;
                    if (adapter != null) {
                        adapter.updateList(list);
                        applyCurrentFilters();
                    }
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (countBadgeTv != null) {
                        countBadgeTv.setText(list.size() + " Words");
                    }
                }
            });
        });
    }

    private void loadNoticeFromFirebase() {
        if (progressBar != null && list.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
        }

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isAdded()) return;

                ArrayList<NoticeData> fbList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NoticeData data = snapshot.getValue(NoticeData.class);
                    if (data != null) {
                        fbList.add(data);
                    }
                }

                if (!fbList.isEmpty()) {
                    sortAlphabetically(fbList);
                    list = fbList;
                    if (adapter != null) {
                        adapter.updateList(list);
                        applyCurrentFilters();
                    }
                    if (countBadgeTv != null) {
                        countBadgeTv.setText(list.size() + " Words");
                    }
                }

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!isAdded()) return;
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        };

        reference.addValueEventListener(valueEventListener);
    }

    private void sortAlphabetically(ArrayList<NoticeData> items) {
        if (items == null) return;
        Collections.sort(items, (a, b) -> {
            String t1 = a.getTitle() != null ? a.getTitle().trim() : "";
            String t2 = b.getTitle() != null ? b.getTitle().trim() : "";
            return t1.compareToIgnoreCase(t2);
        });
    }

    @Override
    public void onBookmarkChanged(String title, boolean isBookmarked) {
        updateStarredTabBadge();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStarredTabBadge();
        if (showOnlyStarred) {
            applyCurrentFilters();
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (reference != null && valueEventListener != null) {
            reference.removeEventListener(valueEventListener);
        }
        if (bookmarkManager != null) {
            bookmarkManager.removeListener(this);
        }
    }
}
