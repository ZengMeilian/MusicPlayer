package com.example.music_zengmeilian.player;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * 切歌时更新歌词
     */

    public void onSongChanged() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(this::updateLyric);
    }

    private void updateLyric() {
        PlayerActivity activity = (PlayerActivity) getActivity();
        if (activity == null || activity.currentMusic == null) {
            Log.e(TAG, "currentMusic 为 null");
            return;
        }

        MusicInfo currentMusic = activity.currentMusic;
        if (tvLyric != null) {
            StringBuilder lyricText = new StringBuilder();
            lyricText.append(currentMusic.getMusicName() != null ? currentMusic.getMusicName() : "未知歌曲");
            lyricText.append(" - ").append(currentMusic.getAuthor() != null ? currentMusic.getAuthor() : "未知歌手");
            lyricText.append("\n\n");

            if (currentMusic.getLyricUrl() != null && !currentMusic.getLyricUrl().isEmpty()) {
                new Thread(() -> {
                    try {
                        URL url = new URL(currentMusic.getLyricUrl());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);

                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder rawLyrics = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            rawLyrics.append(line).append("\n");
                        }

                        reader.close();
                        connection.disconnect();

                        // 解析LRC歌词
                        List<PlayerActivity.LyricLine> lyricLines = parseLrcLyrics(rawLyrics.toString());

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                ((PlayerActivity) getActivity()).setLyricLines(lyricLines);
                                displayLyrics(lyricText.toString(), lyricLines, 0);
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

                tvLyric.setText(lyricText.toString() + "歌词加载中...");
            } else {
                tvLyric.setText(lyricText.toString() + "暂无歌词数据...");
            }
        }
    }

    // 解析LRC歌词方法
    private List<PlayerActivity.LyricLine> parseLrcLyrics(String rawLyrics) {
        List<PlayerActivity.LyricLine> lines = new ArrayList<>();
        String[] lyricArray = rawLyrics.split("\n");

        for (String line : lyricArray) {
            Matcher matcher = Pattern.compile("\\[(\\d+):(\\d+)\\.?(\\d+)?\\]").matcher(line);
            if (matcher.find()) {
                try {
                    int minutes = Integer.parseInt(matcher.group(1));
                    int seconds = Integer.parseInt(matcher.group(2));
                    int millis = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;

                    long time = minutes * 60 * 1000 + seconds * 1000 + millis * 10;
                    String text = line.substring(matcher.end()).trim();

                    if (!text.isEmpty()) {
                        lines.add(new PlayerActivity.LyricLine(time, text));
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, "解析歌词时间失败: " + line);
                }
            }
        }

        Collections.sort(lines, (a, b) -> Long.compare(a.time, b.time));
        return lines;
    }

    // 显示歌词方法
    private void displayLyrics(String header, List<PlayerActivity.LyricLine> lines, int currentLine) {
        SpannableStringBuilder builder = new SpannableStringBuilder(header);

        // 头部信息（歌曲名和歌手名）不会被高亮
        int headerLineCount = 2; // 头部信息占用的行数（歌曲名和歌手名 + 空行）

        for (int i = 0; i < lines.size(); i++) {
            PlayerActivity.LyricLine line = lines.get(i);
            builder.append(line.text).append("\n");

            // 高亮当前播放的歌词行（跳过头部信息）
            if (i == currentLine) {
                int start = builder.length() - line.text.length() - 1;
                int end = builder.length() - 1;
                builder.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                builder.setSpan(
                        new StyleSpan(Typeface.BOLD),
                        start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }

        tvLyric.setText(builder);
    }

    // 更新歌词位置方法
    public void updateLyricPosition(long currentPosition) {
        PlayerActivity activity = (PlayerActivity) getActivity();
        if (activity == null || activity.getLyricLines() == null) return;

        List<PlayerActivity.LyricLine> lines = activity.getLyricLines();
        int currentLine = -1;

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).time > currentPosition) {
                break;
            }
            currentLine = i;
        }

        if (currentLine >= 0) {
            StringBuilder header = new StringBuilder();
            header.append(activity.currentMusic.getMusicName() != null ?
                    activity.currentMusic.getMusicName() : "未知歌曲");
            header.append(" - ").append(activity.currentMusic.getAuthor() != null ?
                    activity.currentMusic.getAuthor() : "未知歌手");
            header.append("\n\n");

            displayLyrics(header.toString(), lines, currentLine);

            // 自动滚动
            Layout layout = tvLyric.getLayout();
            if (layout != null) {
                int scrollY = layout.getLineTop(currentLine + 2); // +2是因为header有两行
                tvLyric.scrollTo(0, scrollY);
            }
        }
    }
}