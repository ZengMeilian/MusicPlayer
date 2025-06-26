package com.example.music_zengmeilian.player;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.player.adapter.LyricsAdapter;
import com.example.music_zengmeilian.model.LyricsInfo;
import com.example.music_zengmeilian.utils.FloatingViewManager;

import java.util.ArrayList;
import java.util.List;

public class LyricActivity extends AppCompatActivity {
    private FloatingViewManager floatingViewManager;
    private RecyclerView recyclerView;
    private TextView tvMusicName, tvSinger;
    private LyricsAdapter lyricAdapter;
    private List<LyricsInfo> lyricsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_lyrics);
        // 隐藏悬浮窗
        ViewGroup rootView = findViewById(android.R.id.content);
        floatingViewManager = new FloatingViewManager(this, rootView);
        floatingViewManager.hideFloatingView();
        initViews();
        setupRecyclerView();
        loadDefaultLyrics();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {
        lyricAdapter = new LyricsAdapter(lyricsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(lyricAdapter);
    }

    private void loadDefaultLyrics() {
        lyricsList.add(new LyricsInfo(0, "暂无歌词"));
        lyricAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 重新显示悬浮窗
        if (floatingViewManager != null) {
            floatingViewManager.showFloatingView();
        }
    }
}