package com.example.music_zengmeilian.player;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.MusicInfo;

public class LyricsFragment extends Fragment {
    private static final String TAG = "LyricFragment";

    private TextView tvLyric;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lyrics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvLyric = view.findViewById(R.id.tv_lyric);
        updateLyric();
    }

    private void updateLyric() {
        MusicInfo currentMusic = PlayerActivity.currentMusic;
        if (currentMusic == null) {
            Log.e(TAG, "currentMusic 为 null");
            return;
        }

        if (tvLyric != null) {
            String musicName = currentMusic.getMusicName();
            String singer = currentMusic.getSinger();

            Log.d(TAG, "歌词页面 - 歌曲名: " + musicName);
            Log.d(TAG, "歌词页面 - 歌手: " + singer);

            // 构建显示文本
            StringBuilder lyricText = new StringBuilder();
            lyricText.append(musicName != null ? musicName : "未知歌曲");

            if (singer != null && !singer.trim().isEmpty()) {
                lyricText.append(" - ").append(singer.trim());
            } else {
                lyricText.append(" - 未知歌手");
            }

            lyricText.append("\n\n暂无歌词数据...");

            tvLyric.setText(lyricText.toString());
            Log.d(TAG, "设置歌词文本: " + lyricText.toString());
        }
    }
}