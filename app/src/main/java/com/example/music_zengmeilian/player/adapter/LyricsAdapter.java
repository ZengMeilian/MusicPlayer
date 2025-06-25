package com.example.music_zengmeilian.player.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.LyricsInfo;
import java.util.List;

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.LyricViewHolder> {

    private List<LyricsInfo> lyricsList;
    private int currentLyricIndex = -1;

    public LyricsAdapter(List<LyricsInfo> lyricsList) {
        this.lyricsList = lyricsList;
    }

    @NonNull
    @Override
    public LyricViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new LyricViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return lyricsList.size();
    }

    public class LyricViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public LyricViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    public void setCurrentLyricIndex(int index) {
        this.currentLyricIndex = index;
    }

    @Override
    public void onBindViewHolder(@NonNull LyricViewHolder holder, int position) {
        LyricsInfo lyricsInfo = lyricsList.get(position);
        holder.textView.setText(lyricsInfo.getContent());

        // 高亮当前播放的歌词
        if (position == currentLyricIndex) {
            holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccent));
            holder.textView.setTextSize(18);
        } else {
            holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
            holder.textView.setTextSize(16);
        }
    }

}