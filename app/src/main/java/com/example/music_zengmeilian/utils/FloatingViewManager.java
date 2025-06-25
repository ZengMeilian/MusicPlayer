package com.example.music_zengmeilian.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.MusicInfo;

public class FloatingViewManager {
    private View floatingView;
    private Context context;

    public FloatingViewManager(Context context, ViewGroup parent) {
        this.context = context;
        initializeFloatingView(parent);
    }

    private void initializeFloatingView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        floatingView = inflater.inflate(R.layout.floating_view, parent, false);
        parent.addView(floatingView);

        // 初始化其他视图和监听器
    }

    public void updateCurrentMusic(MusicInfo musicInfo) {
        if (floatingView == null) return;

        TextView songName = floatingView.findViewById(R.id.song_name);
        TextView artistName = floatingView.findViewById(R.id.artist_name);
        ImageView songImage = floatingView.findViewById(R.id.song_image);

        songName.setText(musicInfo.getMusicName());
        artistName.setText(musicInfo.getAuthor());
        loadImage(musicInfo.getCoverUrl(), songImage);
    }

    public void removeView(ViewGroup parent) {
        if (floatingView != null) {
            parent.removeView(floatingView);
            floatingView = null;
        }
    }

    private void loadImage(String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }
}