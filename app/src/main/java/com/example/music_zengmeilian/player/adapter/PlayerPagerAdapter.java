package com.example.music_zengmeilian.player.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.music_zengmeilian.player.LyricsFragment;
import com.example.music_zengmeilian.player.PlayerCoverFragment;

public class PlayerPagerAdapter extends FragmentStateAdapter {

    public PlayerPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PlayerCoverFragment(); // 播放页面
            case 1:
                return new LyricsFragment(); // 歌词页面
            default:
                return new PlayerCoverFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 两个页面：播放页面 + 歌词页面
    }
}