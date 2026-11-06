package com.study.vocabspecial_crazywaytolearnenglish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

public class vocabfragment extends Fragment {

    private RecyclerView deleteNoticeRecycler;
    private ProgressBar progressBar;
    private TextView emptyView;
    private ArrayList<NoticeData> list;
    private NoticeAdapter adapter;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocabfragment, container, false);

        deleteNoticeRecycler = view.findViewById(R.id.deleteNoticeRecycler);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        deleteNoticeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        deleteNoticeRecycler.setHasFixedSize(true);

        reference = FirebaseDatabase.getInstance().getReference().child("Notice");

        loadNotice();

        return view;
    }

    private void loadNotice() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isAdded()) return;

                list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NoticeData data = snapshot.getValue(NoticeData.class);
                    if (data != null) {
                        list.add(0, data);
                    }
                }

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (list.isEmpty()) {
                    if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
                } else {
                    if (emptyView != null) emptyView.setVisibility(View.GONE);
                }

                adapter = new NoticeAdapter(getContext(), list);
                deleteNoticeRecycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!isAdded()) return;

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (getContext() != null) {
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        reference.addValueEventListener(valueEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (reference != null && valueEventListener != null) {
            reference.removeEventListener(valueEventListener);
        }
    }
}
