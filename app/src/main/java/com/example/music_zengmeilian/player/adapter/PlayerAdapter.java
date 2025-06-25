package com.example.music_zengmeilian.player.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.MusicInfo;
import java.util.List;

/**
 * 播放器适配器 - 使用简单的系统布局
 */
public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<MusicInfo> musicList;
    private int currentPlayingIndex = -1;
    private OnItemClickListener onItemClickListener;

    // 点击监听接口
    public interface OnItemClickListener {
        void onItemClick(MusicInfo musicInfo, int position);
    }

    public PlayerAdapter(List<MusicInfo> musicList) {
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用Android系统自带的简单布局
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        MusicInfo musicInfo = musicList.get(position);
        holder.bind(musicInfo, position);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    // 设置当前播放的音乐索引
    public void setCurrentPlayingIndex(int index) {
        int oldIndex = currentPlayingIndex;
        currentPlayingIndex = index;

        if (oldIndex != -1) {
            notifyItemChanged(oldIndex);
        }
        if (currentPlayingIndex != -1) {
            notifyItemChanged(currentPlayingIndex);
        }
    }

    // 设置点击监听
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // ViewHolder类
    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        private TextView text1, text2;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }

        public void bind(MusicInfo musicInfo, int position) {
            text1.setText(musicInfo.getMusicName());
            text2.setText(musicInfo.getAuthor());

            // 高亮当前播放的音乐
            if (position == currentPlayingIndex) {
                text1.setTextColor(itemView.getContext().getColor(android.R.color.holo_blue_bright));
                text2.setTextColor(itemView.getContext().getColor(android.R.color.holo_blue_bright));
            } else {
                text1.setTextColor(itemView.getContext().getColor(android.R.color.black));
                text2.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
            }

            // 设置点击事件
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(musicInfo, position);
                }
            });
        }
    }
}