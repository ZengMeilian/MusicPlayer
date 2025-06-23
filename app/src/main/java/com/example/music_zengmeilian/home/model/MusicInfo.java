package com.example.music_zengmelian.model;

/**
 * 音乐信息数据模型
 * 对应API返回的单条音乐数据
 */
public class MusicInfo {
    private long id;            // 音乐ID
    private String musicName;   // 音乐名称
    private String author;      // 作者/歌手
    private String coverUrl;    // 封面图URL
    private String musicUrl;    // 音乐文件URL
    private String lyricUrl;    // 歌词文件URL

    // 构造方法
    public MusicInfo(long id, String musicName, String author,
                     String coverUrl, String musicUrl, String lyricUrl) {
        this.id = id;
        this.musicName = musicName;
        this.author = author;
        this.coverUrl = coverUrl;
        this.musicUrl = musicUrl;
        this.lyricUrl = lyricUrl;
    }

    // Getter方法
    public long getId() { return id; }
    public String getMusicName() { return musicName; }
    public String getAuthor() { return author; }
    public String getCoverUrl() { return coverUrl; }
    public String getMusicUrl() { return musicUrl; }
    public String getLyricUrl() { return lyricUrl; }
}