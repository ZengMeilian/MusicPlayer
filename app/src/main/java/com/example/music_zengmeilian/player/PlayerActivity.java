package com.example.music_zengmeilian.player;

import android.content.Intent;
import android.os.Bundle;
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

    // 音乐数据，传递给Fragment
    public static MusicInfo currentMusic;
    public static List<MusicInfo> musicList;
    public static int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_container);

        receiveData();
        initViews();
        initEvents();
    }

    private void receiveData() {
        Intent intent = getIntent();
        if (intent != null) {
            currentMusic = (MusicInfo) intent.getSerializableExtra("current_music");
            musicList = (List<MusicInfo>) intent.getSerializableExtra("music_list");
            currentIndex = intent.getIntExtra("current_index", 0);
        }
    }

    private void initViews() {
        viewPagerPlayer = findViewById(R.id.view_pager_player);
        btnClose = findViewById(R.id.btn_close);

        // 设置ViewPager2适配器
        pagerAdapter = new PlayerPagerAdapter(this);
        viewPagerPlayer.setAdapter(pagerAdapter);

        // 默认显示播放页面（索引0）
        viewPagerPlayer.setCurrentItem(0);
    }

    private void initEvents() {
        // 关闭按钮
        btnClose.setOnClickListener(v -> {
            // TODO: 添加关闭动画（从上往下划出）
            finish();
        });

        // ViewPager2页面切换监听
        viewPagerPlayer.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    // 播放页面
                } else if (position == 1) {
                    // 歌词页面
                }
            }
        });
    }
}