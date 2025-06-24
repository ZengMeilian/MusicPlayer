package com.example.music_zengmeilian.model;

import java.util.ArrayList;
import java.util.List;

public class PlayListInfo {
    private List<MusicInfo> musicList;
    private int currentIndex;
    private PlayMode playMode;

    public PlayListInfo() {
        this.musicList = new ArrayList<>();
        this.currentIndex = 0;
        this.playMode = PlayMode.SEQUENCE;
    }

    public void addMusic(MusicInfo musicInfo) {
        musicList.add(musicInfo);
    }

    public MusicInfo getCurrentMusic() {
        if (musicList.isEmpty() || currentIndex < 0 || currentIndex >= musicList.size()) {
            return null;
        }
        return musicList.get(currentIndex);
    }

    public void next() {
        if (!musicList.isEmpty()) {
            currentIndex = (currentIndex + 1) % musicList.size();
        }
    }

    public void previous() {
        if (!musicList.isEmpty()) {
            currentIndex = currentIndex > 0 ? currentIndex - 1 : musicList.size() - 1;
        }
    }

    // Getter和Setter
    public List<MusicInfo> getMusicList() { return musicList; }
    public int getCurrentIndex() { return currentIndex; }
    public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex; }
    public PlayMode getPlayMode() { return playMode; }
    public void setPlayMode(PlayMode playMode) { this.playMode = playMode; }
}