package com.example.music_zengmeilian.utils;

import com.example.music_zengmeilian.model.LyricsInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricsParser {
    private static final Pattern LYRIC_LINE_PATTERN = Pattern.compile("^\\[(\\d+):(\\d+)\\.(\\d+)\\](.*)$");

    public static List<LyricsInfo> parseLrc(String lrcText) {
        List<LyricsInfo> lyricsList = new ArrayList<>();

        if (lrcText == null || lrcText.isEmpty()) {
            lyricsList.add(new LyricsInfo(0, "暂无歌词"));
            return lyricsList;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(lrcText))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = LYRIC_LINE_PATTERN.matcher(line);
                if (matcher.find()) {
                    int minutes = Integer.parseInt(matcher.group(1));
                    int seconds = Integer.parseInt(matcher.group(2));
                    int millis = Integer.parseInt(matcher.group(3));
                    String content = matcher.group(4).trim();

                    long timeStamp = (minutes * 60 + seconds) * 1000 + millis * 10;
                    lyricsList.add(new LyricsInfo(timeStamp, content));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 如果没有解析到有效歌词，添加默认提示
        if (lyricsList.isEmpty()) {
            lyricsList.add(new LyricsInfo(0, "暂无歌词"));
        }

        return lyricsList;
    }
}