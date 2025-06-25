package com.example.music_zengmeilian.player;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.MusicInfo;
import com.example.music_zengmeilian.player.adapter.PlayerPagerAdapter;

import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    private static final String TAG = "PlayerActivity";

    private ViewPager2 viewPagerPlayer;
    private ImageButton btnClose;
    private PlayerPagerAdapter pagerAdapter;

    //MediaPlayer
    private MediaPlayer mediaPlayer;
    private LyricsFragment lyricsFragment;
    private boolean isPlaying = false;

    //进度条
    private Handler progressHandler = new Handler();
    private Runnable progressRunnable;

    private PlayerCoverFragment playerCoverFragment;

    // 音乐数据
    public MusicInfo currentMusic;
    public List<MusicInfo> musicList;
    public int currentIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_container);

        receiveData();
        initViews();
        initEvents();
        initMediaPlayer();
    }

    private void receiveData() {
        Intent intent = getIntent();
        if (intent != null) {
            currentMusic = (MusicInfo) intent.getSerializableExtra("currentMusic");
            // 修改key名称，保持一致
            musicList = (List<MusicInfo>) intent.getSerializableExtra("music_list");  // 改为 "music_list"
            currentIndex = intent.getIntExtra("current_index", 0);

            Log.d(TAG, "接收到的音乐: " + (currentMusic != null ? currentMusic.getMusicName() : "null"));
            Log.d(TAG, "音乐列表大小: " + (musicList != null ? musicList.size() : "0"));
            Log.d(TAG, "当前索引: " + currentIndex);

            if (currentMusic != null) {
                Log.d(TAG, "音频URL: " + currentMusic.getMusicUrl());
            }
        }
    }

    private void initViews() {
        viewPagerPlayer = findViewById(R.id.view_pager_player);
        btnClose = findViewById(R.id.btn_close);

        // 设置ViewPager2适配器
        pagerAdapter = new PlayerPagerAdapter(this);
        viewPagerPlayer.setAdapter(pagerAdapter);

    }

    private void initEvents() {
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }
    }

    // 添加设置Fragment引用的方法
    public void setPlayerCoverFragment(PlayerCoverFragment fragment) {
        this.playerCoverFragment = fragment;
    }

    public void setLyricsFragment(LyricsFragment fragment) {
        this.lyricsFragment = fragment;
    }

    //初始化MediaPlayer
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();

        // 设置音频流类型
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(mp -> {
            mp.start();
            isPlaying = true;
            startProgressUpdate();

            // 通知Fragment更新UI
            notifyPlayStateChanged();

            Log.d(TAG, "音乐开始播放");
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            stopProgressUpdate();

            // 通知Fragment更新UI
            notifyPlayStateChanged();

            Log.d(TAG, "音乐播放完成");
        });

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "MediaPlayer错误: what=" + what + ", extra=" + extra);
            isPlaying = false;
            notifyPlayStateChanged();
            return false;
        });

        // 开始播放当前音乐
        if (currentMusic != null) {
            playMusic(currentMusic);
        }
    }

    // 播放指定音乐
    private void playMusic(MusicInfo musicInfo) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicInfo.getMusicUrl());
            mediaPlayer.prepareAsync();
            Log.d(TAG, "开始准备播放: " + musicInfo.getMusicName());
            Log.d(TAG, "音频URL: " + musicInfo.getMusicUrl());
        } catch (Exception e) {
            Log.e("PlayerActivity", "播放音乐失败: " + e.getMessage());
        }
    }

    // 播放/暂停控制 - 供Fragment调用
    public void playPause() {
        if (mediaPlayer == null) return;

        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
                stopProgressUpdate();
                Log.d(TAG, "音乐暂停");
            } else {
                mediaPlayer.start();
                isPlaying = true;
                startProgressUpdate();
                Log.d(TAG, "音乐继续播放");
            }

            // 通知Fragment更新UI
            notifyPlayStateChanged();

        } catch (Exception e) {
            Log.e(TAG, "播放控制失败: " + e.getMessage());
        }
    }

    // 开始更新进度
    private void startProgressUpdate() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    try {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();

                        // 通知Fragment更新进度
                        notifyProgressUpdate(currentPosition, duration);

                        // 每秒更新一次
                        progressHandler.postDelayed(this, 1000);
                    } catch (Exception e) {
                        Log.e(TAG, "更新进度失败: " + e.getMessage());
                    }
                }
            }
        };
        progressHandler.post(progressRunnable);
    }

    // 停止更新进度
    private void stopProgressUpdate() {
        if (progressRunnable != null) {
            progressHandler.removeCallbacks(progressRunnable);
        }
    }

    // 更新进度的方法
    private void updateProgress(int currentPosition, int duration) {
        // 这里需要你的进度条控件ID
        // 比如：seekBar.setProgress(currentPosition);
        // 需要你提供布局文件或进度条ID
    }

    // 通知Fragment播放状态改变
    private void notifyPlayStateChanged() {
        if (playerCoverFragment != null) {
            playerCoverFragment.onPlayStateChanged(isPlaying);
        }
        if (lyricsFragment != null) {
            lyricsFragment.onPlayStateChanged(isPlaying);
        }
    }

    // 通知Fragment进度更新
    private void notifyProgressUpdate(int currentPosition, int duration) {
        if (playerCoverFragment != null) {
            playerCoverFragment.onProgressUpdate(currentPosition, duration);
        }
        if (lyricsFragment != null) {
            lyricsFragment.onProgressUpdate(currentPosition, duration);
        }
    }

    // 获取当前的PlayerCoverFragment
    private PlayerCoverFragment getCurrentPlayerFragment() {
        if (pagerAdapter != null && viewPagerPlayer.getCurrentItem() == 0) {
            return (PlayerCoverFragment) getSupportFragmentManager()
                    .findFragmentByTag("f" + 0);
        }
        return null;
    }

    // 跳转到指定位置
    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    // 上一首
    public void previous() {
        if (musicList == null || musicList.isEmpty()) return;

        currentIndex = (currentIndex - 1 + musicList.size()) % musicList.size();
        currentMusic = musicList.get(currentIndex);
        playMusic(currentMusic);

        if (playerCoverFragment != null) {
            playerCoverFragment.onSongChanged();
        }
        if (lyricsFragment != null) {
            lyricsFragment.onSongChanged();
        }
    }

    // 下一首
    public void next() {
        if (musicList == null || musicList.isEmpty()) return;

        currentIndex = (currentIndex + 1) % musicList.size();
        currentMusic = musicList.get(currentIndex);
        playMusic(currentMusic);

        if (playerCoverFragment != null) {
            playerCoverFragment.onSongChanged();
        }
        if (lyricsFragment != null) {
            lyricsFragment.onSongChanged();
        }
    }

    public boolean isPlaying() {
        return isPlaying&& mediaPlayer != null && mediaPlayer.isPlaying();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopProgressUpdate();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}