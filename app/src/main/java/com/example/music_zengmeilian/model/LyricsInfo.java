package com.example.music_zengmeilian.model;

public class LyricsInfo {
    private long timeStamp;     // 时间戳（毫秒）
    private String content;     // 歌词内容

    public LyricsInfo(long timeStamp, String content) {
        this.timeStamp = timeStamp;
        this.content = content;
    }

    public long getTimeStamp() { return timeStamp; }
    public String getContent() { return content; }
}