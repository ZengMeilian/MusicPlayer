package com.example.music_zengmeilian.api;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.example.music_zengmeilian.model.MusicInfo;
import com.example.music_zengmeilian.model.PlayListInfo;
import com.example.music_zengmeilian.utils.MediaPlayerManager;

import java.util.List;

public class PlayerService extends Service implements MediaPlayerManager.PlayStateListener {

    private MediaPlayerManager mediaPlayerManager;
    private PlayListInfo playListInfo;
    private PlayerServiceListener serviceListener;

    public interface PlayerServiceListener {
        void onMusicChanged(MusicInfo musicInfo);
        void onPlayStateChanged(boolean isPlaying);
    }

    public class PlayerBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayerManager = MediaPlayerManager.getInstance();
        mediaPlayerManager.setPlayStateListener(this);
        playListInfo = new PlayListInfo();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerBinder();
    }

    public void playMusic(MusicInfo musicInfo) {
        if (musicInfo != null && musicInfo.getMusicUrl() != null) {
            mediaPlayerManager.playMusic(musicInfo.getMusicUrl());
        }
    }

    public void togglePlayPause() {
        if (mediaPlayerManager.isPlaying()) {
            mediaPlayerManager.pause();
        } else {
            mediaPlayerManager.resume();
        }
    }

    public void playNext() {
        playListInfo.next();
        MusicInfo nextMusic = playListInfo.getCurrentMusic();
        if (nextMusic != null) {
            playMusic(nextMusic);
            if (serviceListener != null) {
                serviceListener.onMusicChanged(nextMusic);
            }
        }
    }

    public void playPrevious() {
        playListInfo.previous();
        MusicInfo previousMusic = playListInfo.getCurrentMusic();
        if (previousMusic != null) {
            playMusic(previousMusic);
            if (serviceListener != null) {
                serviceListener.onMusicChanged(previousMusic);
            }
        }
    }

    // 回调方法
    @Override
    public void onPlayStart() {
        if (serviceListener != null) {
            serviceListener.onPlayStateChanged(true);
        }
    }

    @Override
    public void onPlayPause() {
        if (serviceListener != null) {
            serviceListener.onPlayStateChanged(false);
        }
    }

    @Override
    public void onPlayComplete() {
        playNext(); // 自动播放下一首
    }

    // 公共方法
    public PlayListInfo getPlayListInfo() { return playListInfo; }
    public boolean isPlaying() { return mediaPlayerManager.isPlaying(); }
    public int getCurrentPosition() { return mediaPlayerManager.getCurrentPosition(); }
    public int getDuration() { return mediaPlayerManager.getDuration(); }
    public void seekTo(int position) { mediaPlayerManager.seekTo(position); }
    public void setServiceListener(PlayerServiceListener listener) { this.serviceListener = listener; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayerManager.release();
    }

    public void setPlayList(List<MusicInfo> musicList, int currentIndex) {
        if (playListInfo != null) {
            playListInfo.getMusicList().clear();
            playListInfo.getMusicList().addAll(musicList);
            playListInfo.setCurrentIndex(currentIndex);
        }
    }
}