package com.study.vocabspecial_crazywaytolearnenglish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewAdapter> {
    private final Context context;
    private ArrayList<NoticeData> list;
    private ArrayList<NoticeData> originalList;
    private final BookmarkManager bookmarkManager;
    private final TtsManager ttsManager;
    private OnItemBookmarkListener bookmarkListener;

    public interface OnItemBookmarkListener {
        void onBookmarkToggled(NoticeData item, boolean isBookmarked);
    }

    public NoticeAdapter(Context context, ArrayList<NoticeData> list) {
        this.context = context;
        this.list = list != null ? list : new ArrayList<>();
        this.originalList = new ArrayList<>(this.list);
        this.bookmarkManager = BookmarkManager.getInstance(context);
        this.ttsManager = TtsManager.getInstance(context);
    }

    public void setOnItemBookmarkListener(OnItemBookmarkListener listener) {
        this.bookmarkListener = listener;
    }

    public void updateList(ArrayList<NoticeData> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        this.originalList = new ArrayList<>(this.list);
        notifyDataSetChanged();
    }

    public ArrayList<NoticeData> getCurrentList() {
        return list;
    }

    public void filter(String query, String letterFilter, boolean showOnlyStarred) {
        ArrayList<NoticeData> filtered = new ArrayList<>();
        String lowerQuery = query != null ? query.toLowerCase().trim() : "";
        String targetLetter = letterFilter != null ? letterFilter.trim().toUpperCase() : "";

        for (NoticeData item : originalList) {
            String title = item.getTitle() != null ? item.getTitle().trim() : "";
            
            // 1. Starred filter
            if (showOnlyStarred && !bookmarkManager.isBookmarked(title)) {
                continue;
            }

            // 2. Letter filter
            if (!targetLetter.isEmpty() && !targetLetter.equalsIgnoreCase("ALL")) {
                if (title.isEmpty() || !title.toUpperCase().startsWith(targetLetter)) {
                    continue;
                }
            }

            // 3. Search query filter
            if (!lowerQuery.isEmpty()) {
                boolean matchTitle = title.toLowerCase().contains(lowerQuery);
                boolean matchKey = item.getKey_value() != null && item.getKey_value().toLowerCase().contains(lowerQuery);
                boolean matchDesc = item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerQuery);
                if (!matchTitle && !matchKey && !matchDesc) {
                    continue;
                }
            }

            filtered.add(item);
        }

        this.list = filtered;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoticeViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newsfeed_item_layout, parent, false);
        return new NoticeViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewAdapter holder, final int position) {
        final NoticeData currentItem = list.get(position);
        final String title = currentItem.getTitle() != null ? currentItem.getTitle() : "";

        holder.Title.setText(title);

        String keyText = currentItem.getKey_value();
        if (keyText == null || keyText.isEmpty()) {
            keyText = currentItem.getKey();
        }
        if (keyText != null && !keyText.isEmpty()) {
            holder.KeyValue.setVisibility(View.VISIBLE);
            holder.KeyValue.setText("💡 " + keyText);
        } else {
            holder.KeyValue.setVisibility(View.GONE);
        }

        holder.Description.setText(currentItem.getDescription() != null ? currentItem.getDescription() : "");

        // Bookmark State
        final boolean isBookmarked = bookmarkManager.isBookmarked(title);
        holder.btnBookmark.setImageResource(isBookmarked ? R.drawable.ic_star_filled : R.drawable.ic_star_border);

        holder.btnBookmark.setOnClickListener(v -> {
            boolean newStatus = bookmarkManager.toggleBookmark(title);
            holder.btnBookmark.setImageResource(newStatus ? R.drawable.ic_star_filled : R.drawable.ic_star_border);
            
            // Micro-interaction bounce
            holder.btnBookmark.animate().scaleX(1.3f).scaleY(1.3f).setDuration(120).withEndAction(() ->
                    holder.btnBookmark.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start()
            ).start();

            Toast.makeText(context, newStatus ? "Saved to Starred ⭐" : "Removed from Starred", Toast.LENGTH_SHORT).show();

            if (bookmarkListener != null) {
                bookmarkListener.onBookmarkToggled(currentItem, newStatus);
            }
        });

        // TTS Pronunciation
        holder.btnSpeak.setOnClickListener(v -> {
            ttsManager.speak(title);

            // Micro-interaction bounce
            holder.btnSpeak.animate().scaleX(1.25f).scaleY(1.25f).setDuration(100).withEndAction(() ->
                    holder.btnSpeak.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
            ).start();
        });

        // Detail Activity on Card Click
        holder.itemView.setOnClickListener(v -> {
            Runnable openDetails = () -> {
                Intent intent = new Intent(context, FullImageView.class);
                intent.putExtra("image_intent", currentItem.getImage());
                intent.putExtra("title_intent", currentItem.getTitle());
                intent.putExtra("key_intent", currentItem.getKey_value() != null ? currentItem.getKey_value() : currentItem.getKey());
                intent.putExtra("description_intent", currentItem.getDescription());
                context.startActivity(intent);
            };

            if (context instanceof Activity) {
                AdManager.getInstance().showInterstitialWithCounter((Activity) context, openDetails);
            } else {
                openDetails.run();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class NoticeViewAdapter extends RecyclerView.ViewHolder {
        TextView Title, KeyValue, Description;
        ImageView btnBookmark, btnSpeak;

        public NoticeViewAdapter(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.Title);
            KeyValue = itemView.findViewById(R.id.key);
            Description = itemView.findViewById(R.id.description);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
            btnSpeak = itemView.findViewById(R.id.btnSpeak);
        }
    }
}
