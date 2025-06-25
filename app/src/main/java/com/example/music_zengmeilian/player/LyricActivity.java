package com.example.music_zengmeilian.player;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.player.adapter.LyricsAdapter;
import com.example.music_zengmeilian.model.LyricsInfo;

import java.util.ArrayList;
import java.util.List;

public class LyricActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvMusicName, tvSinger;
    private LyricsAdapter lyricAdapter;
    private List<LyricsInfo> lyricsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_lyrics);

        initViews();
        setupRecyclerView();
        loadDefaultLyrics();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
//        tvMusicName = findViewById(R.id.tvMusicName);
//        tvSinger = findViewById(R.id.tvSinger);
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
}