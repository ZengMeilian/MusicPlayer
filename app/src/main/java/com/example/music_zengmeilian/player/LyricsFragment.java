package com.example.music_zengmeilian.player;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.MusicInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 播放器歌词页 Fragment，显示歌曲歌词
 */
public class LyricsFragment extends BasePlayerFragment {
    private static final String TAG = "LyricFragment";
    private TextView tvLyric; // 歌词显示控件

    @Override
    public int getPlayerControlId() {
        return R.id.player_control_lyrics; // 返回歌词页的播放控制区域 ID
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_lyrics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置 Activity 对当前 Fragment 的引用
        PlayerActivity activity = (PlayerActivity) getActivity();
        if (activity != null) {
            activity.setLyricsFragment(this);
        }

        tvLyric = view.findViewById(R.id.tv_lyrics);
        updateLyric(); // 初始化歌词显示
    }

    /**
     * 更新歌词显示
     */
    private void updateLyric() {
        PlayerActivity activity = (PlayerActivity) getActivity();
        if (activity == null || activity.currentMusic == null) {
            Log.e(TAG, "currentMusic 为 null");
            return;
        }

        MusicInfo currentMusic = activity.currentMusic;
        if (tvLyric != null) {
            // 拼接歌曲名和歌手信息
            StringBuilder lyricText = new StringBuilder();
            lyricText.append(currentMusic.getMusicName() != null ? currentMusic.getMusicName() : "未知歌曲");
            lyricText.append(" - ").append(currentMusic.getAuthor() != null ? currentMusic.getAuthor() : "未知歌手");
            lyricText.append("\n\n");

            // 检查是否有歌词URL
            if (currentMusic.getLyricUrl() != null && !currentMusic.getLyricUrl().isEmpty()) {
                // 启动新线程加载歌词
                new Thread(() -> {
                    try {
                        URL url = new URL(currentMusic.getLyricUrl());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);

                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder lyricsContent = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            lyricsContent.append(line).append("\n");
                        }

                        reader.close();
                        connection.disconnect();

                        // 更新UI显示歌词
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                tvLyric.setText(lyricText.toString() + lyricsContent.toString());
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "加载歌词失败", e);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                tvLyric.setText(lyricText.toString() + "歌词加载失败\n" + e.getMessage());
                            });
                        }
                    }
                }).start();

                // 先显示加载中的提示
                tvLyric.setText(lyricText.toString() + "歌词加载中...");
            } else {
                // 没有歌词URL的情况
                tvLyric.setText(lyricText.toString() + "暂无歌词数据...");
            }
        }
    }

    /**
     * 切歌时更新歌词
     */

    public void onSongChanged() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(this::updateLyric);
    }
}