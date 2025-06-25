package com.example.music_zengmeilian.player;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.MusicInfo;

/**
 * 播放器歌词页 Fragment，显示歌曲歌词
 */
public class LyricsFragment extends BasePlayerFragment {
    private static final String TAG = "LyricFragment";
    private TextView tvLyric; // 歌词显示控件

    @Override
    public int getPlayerControlId() {
        return R.id.player_control_lyrics; // 返回歌词页的播放控制区域 ID
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_lyrics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置 Activity 对当前 Fragment 的引用
        PlayerActivity activity = (PlayerActivity) getActivity();
        if (activity != null) {
            activity.setLyricsFragment(this);
        }

        tvLyric = view.findViewById(R.id.tv_lyrics);
        updateLyric(); // 初始化歌词显示
    }

    /**
     * 更新歌词显示
     */
    private void updateLyric() {
        PlayerActivity activity = (PlayerActivity) getActivity();
        if (activity == null || activity.currentMusic == null) {
            Log.e(TAG, "currentMusic 为 null");
            return;
        }

        MusicInfo currentMusic = activity.currentMusic;
        if (tvLyric != null) {
            // 拼接歌曲名和歌手信息
            StringBuilder lyricText = new StringBuilder();
            lyricText.append(currentMusic.getMusicName() != null ? currentMusic.getMusicName() : "未知歌曲");
            lyricText.append(" - ").append(currentMusic.getAuthor() != null ? currentMusic.getAuthor() : "未知歌手");
            lyricText.append("\n\n暂无歌词数据..."); // 默认提示
            tvLyric.setText(lyricText.toString());
        }
    }

    /**
     * 切歌时更新歌词
     */

    public void onSongChanged() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(this::updateLyric);
    }
}