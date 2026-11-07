package com.study.vocabspecial_crazywaytolearnenglish;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class vocabfragment extends Fragment {

    private RecyclerView deleteNoticeRecycler;
    private ProgressBar progressBar;
    private TextView emptyView, wordCountTv, countBadgeTv;
    private EditText searchEditText;
    private ImageView btnClearSearch;

    private ArrayList<NoticeData> list = new ArrayList<>();
    private NoticeAdapter adapter;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocabfragment, container, false);

        deleteNoticeRecycler = view.findViewById(R.id.deleteNoticeRecycler);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);
        wordCountTv = view.findViewById(R.id.wordCountTv);
        countBadgeTv = view.findViewById(R.id.countBadgeTv);
        searchEditText = view.findViewById(R.id.searchEditText);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);

        deleteNoticeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        deleteNoticeRecycler.setHasFixedSize(true);

        adapter = new NoticeAdapter(getContext(), new ArrayList<>());
        deleteNoticeRecycler.setAdapter(adapter);

        setupSearch();

        // 1. Instant offline load from assets
        loadLocalAssets();

        // 2. Realtime sync from Firebase Notice
        reference = FirebaseDatabase.getInstance().getReference().child("Notice");
        loadNoticeFromFirebase();

        return view;
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
                    if (adapter != null) {
                        adapter.filter(query);
                        int countResult = adapter.getItemCount();
                        if (countBadgeTv != null) {
                            if (query.isEmpty()) {
                                countBadgeTv.setText(list.size() + " Words");
                            } else {
                                countBadgeTv.setText(countResult + " Found");
                            }
                        }
                        if (emptyView != null) {
                            emptyView.setVisibility(countResult == 0 ? View.VISIBLE : View.GONE);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (btnClearSearch != null) {
            btnClearSearch.setOnClickListener(v -> {
                if (searchEditText != null) {
                    searchEditText.setText("");
                    searchEditText.clearFocus();
                    Context ctx = getContext();
                    if (ctx != null) {
                        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                        }
                    }
                }
            });
        }

        if (searchEditText != null) {
            searchEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                    searchEditText.clearFocus();
                    Context ctx = getContext();
                    if (ctx != null) {
                        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                        }
                    }
                    return true;
                }
                return false;
            });
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

                // Sort alphabetically A to Z
                sortAlphabetically(assetList);

            } catch (Exception e) {
                e.printStackTrace();
            }

            mainHandler.post(() -> {
                if (!isAdded() || assetList.isEmpty()) return;

                // Only set if list hasn't already been populated by Firebase
                if (list.isEmpty()) {
                    list = assetList;
                    if (adapter != null) {
                        adapter.updateList(list);
                    }
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (emptyView != null) {
                        emptyView.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
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
                    // Sort alphabetically A to Z
                    sortAlphabetically(fbList);

                    list = fbList;
                    if (adapter != null) {
                        String currentQuery = searchEditText != null ? searchEditText.getText().toString().trim() : "";
                        adapter.updateList(list);
                        if (!currentQuery.isEmpty()) {
                            adapter.filter(currentQuery);
                        }
                    }

                    if (countBadgeTv != null) {
                        countBadgeTv.setText(list.size() + " Words");
                    }
                }

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (emptyView != null) {
                    emptyView.setVisibility(adapter != null && adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!isAdded()) return;

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                // If local data already loaded, no need to alarm user
                if (list.isEmpty() && getContext() != null) {
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onDestroyView() {
        super.onDestroyView();
        if (reference != null && valueEventListener != null) {
            reference.removeEventListener(valueEventListener);
        }
    }
}
