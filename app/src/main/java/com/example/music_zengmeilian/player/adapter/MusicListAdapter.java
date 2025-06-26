package com.example.music_zengmeilian.player.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.MusicInfo;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {
    private List<MusicInfo> musicList;
    private int currentIndex;
    private OnItemClickListener onItemClickListener;

    // 定义点击事件接口
    public interface OnItemClickListener {
        void onItemClick(int position, MusicInfo musicInfo);
    }

    public MusicListAdapter(List<MusicInfo> musicList, int currentIndex) {
        this.musicList = musicList;
        this.currentIndex = currentIndex;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_item_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicInfo musicInfo = musicList.get(position);
        holder.tvSongName.setText(musicInfo.getMusicName());
        holder.tvArtist.setText(musicInfo.getAuthor());

        // 高亮显示当前播放的音乐
        if (position == currentIndex) {
            holder.tvSongName.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorPrimary));
            holder.tvArtist.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorPrimary));
        } else {
            holder.tvSongName.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
            holder.tvArtist.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
        }

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position, musicInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }

    // 设置点击监听器
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName;
        TextView tvArtist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongName = itemView.findViewById(R.id.dialog_musicName);
            tvArtist = itemView.findViewById(R.id.dialog_author);
        }
    }
}
