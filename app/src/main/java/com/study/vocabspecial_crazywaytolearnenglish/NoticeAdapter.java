package com.study.vocabspecial_crazywaytolearnenglish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewAdapter> {
    private Context context;
    private ArrayList<NoticeData> list;
    private ArrayList<NoticeData> originalList;

    public NoticeAdapter(Context context, ArrayList<NoticeData> list) {
        this.context = context;
        this.list = list;
        this.originalList = list != null ? new ArrayList<>(list) : new ArrayList<>();
    }

    public void updateList(ArrayList<NoticeData> newList) {
        this.list = newList;
        this.originalList = newList != null ? new ArrayList<>(newList) : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            list = new ArrayList<>(originalList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            ArrayList<NoticeData> filtered = new ArrayList<>();
            for (NoticeData item : originalList) {
                boolean matchTitle = item.getTitle() != null && item.getTitle().toLowerCase().contains(lowerQuery);
                boolean matchKey = item.getKey_value() != null && item.getKey_value().toLowerCase().contains(lowerQuery);
                boolean matchDesc = item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerQuery);
                if (matchTitle || matchKey || matchDesc) {
                    filtered.add(item);
                }
            }
            list = filtered;
        }
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

        holder.Title.setText(currentItem.getTitle() != null ? currentItem.getTitle() : "");
        
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

        public NoticeViewAdapter(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.Title);
            KeyValue = itemView.findViewById(R.id.key);
            Description = itemView.findViewById(R.id.description);
        }
    }
}
