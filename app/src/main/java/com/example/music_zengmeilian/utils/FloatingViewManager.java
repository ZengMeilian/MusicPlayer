package com.example.music_zengmeilian.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.MusicInfo;
import com.example.music_zengmeilian.player.PlayerActivity;
import com.example.music_zengmeilian.player.adapter.MusicListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FloatingViewManager {
    private View floatingView;
    private Context context;
    private MusicInfo currentMusic;
    private List<MusicInfo> musicList;
    private boolean isPlaying;

    // 播放/暂停回调接口
    private OnPlayPauseListener playPauseListener;
    // 播放指定音乐回调接口
    private OnPlayMusicListener playMusicListener;

    // 定义播放/暂停回调接口
    public interface OnPlayPauseListener {
        void onPlayPause();
    }

    // 定义播放指定音乐回调接口
    public interface OnPlayMusicListener {
        void onPlayMusic(MusicInfo musicInfo);
    }

    public FloatingViewManager(Context context, ViewGroup parent) {
        this.context = context;
        this.musicList = new ArrayList<>(); // 初始化为空列表
        initializeFloatingView(parent);
    }

    public FloatingViewManager(Context context, ViewGroup parent, List<MusicInfo> musicList) {
        this.context = context;
        this.musicList = musicList;
        initializeFloatingView(parent);
    }

    // 初始化悬浮窗视图
    private void initializeFloatingView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        floatingView = inflater.inflate(R.layout.floating_view, parent, false);
        parent.addView(floatingView);

        // 设置点击事件 - 点击整个浮动视图打开播放器
        floatingView.setOnClickListener(v -> {
            openPlayerActivity();
        });
    }

    // 更新当前音乐信息和播放状态
    public void updateCurrentMusic(MusicInfo musicInfo, boolean isPlaying) {
        this.currentMusic = musicInfo;
        this.isPlaying = isPlaying;

        if (floatingView == null) return;

        // 获取悬浮窗中的UI组件
        TextView songName = floatingView.findViewById(R.id.song_name);
        TextView artistName = floatingView.findViewById(R.id.artist_name);
        ImageView songImage = floatingView.findViewById(R.id.song_image);
        ImageButton playButton = floatingView.findViewById(R.id.floating_view_play_button);
        ImageButton playListButton = floatingView.findViewById(R.id.floating_view_playlist_button);

        // 更新音乐信息显示
        songName.setText(musicInfo.getMusicName());
        artistName.setText(musicInfo.getAuthor());
        loadImage(musicInfo.getCoverUrl(), songImage);

        // 更新播放按钮图标
        updatePlayButtonIcon(playButton);

        // 设置播放/暂停按钮点击事件
        playButton.setOnClickListener(v -> {
            // 触发播放/暂停回调
            if (playPauseListener != null) {
                playPauseListener.onPlayPause();
            }
            // 更新本地播放状态
            this.isPlaying = !this.isPlaying;
            // 更新按钮图标
            updatePlayButtonIcon(playButton);
        });

        // 设置播放列表按钮点击事件
        playListButton.setOnClickListener(v -> {
            showPlaylistDialog();
        });
    }

    // 更新播放按钮图标
    private void updatePlayButtonIcon(ImageButton playButton) {
        if (playButton != null) {
            playButton.setImageResource(isPlaying ?
                    R.mipmap.ic_player_pause : R.mipmap.ic_player_playing);
        }
    }

    // 设置播放/暂停回调监听器
    public void setPlayPauseListener(OnPlayPauseListener listener) {
        this.playPauseListener = listener;
    }

    // 设置播放音乐回调监听器
    public void setPlayMusicListener(OnPlayMusicListener listener) {
        this.playMusicListener = listener;
    }

    // 打开播放器Activity
    private void openPlayerActivity() {
        if (currentMusic == null) return;

        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("currentMusic", currentMusic);
        intent.putExtra("music_list", new ArrayList<>(musicList));
        intent.putExtra("current_index", findCurrentIndex());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // 查找当前音乐在列表中的位置
    private int findCurrentIndex() {
        if (currentMusic == null || musicList == null) return 0;
        for (int i = 0; i < musicList.size(); i++) {
            if (musicList.get(i).getId() == currentMusic.getId()) {
                return i;
            }
        }
        return 0;
    }

    // 显示播放列表对话框
    private void showPlaylistDialog() {
        if (musicList == null || musicList.isEmpty()) return;

        // 创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_playlist, null);
        builder.setView(dialogView);

        // 获取RecyclerView
        RecyclerView recyclerView = dialogView.findViewById(R.id.dialog_recycleview);

        // 设置RecyclerView布局和适配器
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // 使用MusicListAdapter
        MusicListAdapter adapter = new MusicListAdapter(musicList, findCurrentIndex());
        recyclerView.setAdapter(adapter);

        // 创建对话框实例
        AlertDialog dialog = builder.create();

        // 设置列表项点击事件
        adapter.setOnItemClickListener((position, musicInfo) -> {
            currentMusic = musicInfo;
            // 触发播放指定音乐回调
            if (playMusicListener != null) {
                playMusicListener.onPlayMusic(musicInfo);
            }
            dialog.dismiss();  // 现在可以正确访问dialog变量
        });

        // 显示对话框
        dialog.show();
    }

    // 移除悬浮窗
    public void removeView(ViewGroup parent) {
        if (floatingView != null) {
            parent.removeView(floatingView);
            floatingView = null;
        }
    }

    // 加载图片
    private void loadImage(String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }
}