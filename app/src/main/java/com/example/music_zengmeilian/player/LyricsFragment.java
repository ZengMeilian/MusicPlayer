package com.example.music_zengmeilian.player;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.MusicInfo;
import com.example.music_zengmeilian.player.PlayerActivity;

public class LyricsFragment extends Fragment {
    private static final String TAG = "LyricFragment";

    private TextView tvLyric;
    // 添加播放控制相关的变量
    private ImageButton btnPlayPause, btnPrevious, btnNext;
    private SeekBar seekbarProgress;
    private TextView tvCurrentTime, tvTotalTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_lyrics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置PlayerActivity的引用
        PlayerActivity activity = (PlayerActivity) getActivity();
        if (activity != null) {
            activity.setLyricsFragment(this);
        }
        initViews(view);
        initEvents();
        updateLyric();
    }

    private void initViews(View view) {
        // 歌词显示控件
        tvLyric = view.findViewById(R.id.tv_lyrics);

        // 播放控制区域
        View playerControl = view.findViewById(R.id.player_control_lyrics);
        if (playerControl != null) {
            btnPlayPause = playerControl.findViewById(R.id.btn_play_pause);
            btnPrevious = playerControl.findViewById(R.id.btn_play_previous);
            btnNext = playerControl.findViewById(R.id.btn_play_next);
            seekbarProgress = playerControl.findViewById(R.id.seekbar_progress);
            tvCurrentTime = playerControl.findViewById(R.id.tv_current_time);
            tvTotalTime = playerControl.findViewById(R.id.tv_total_time);
        }

        // 初始化时间显示
        if (tvCurrentTime != null) {
            tvCurrentTime.setText("00:00");
        }
        if (tvTotalTime != null) {
            tvTotalTime.setText("00:00");
        }
    }

    private void initEvents() {
        // 播放/暂停按钮
        if (btnPlayPause != null) {
            btnPlayPause.setOnClickListener(v -> {
                Log.d(TAG, "歌词页面-播放暂停按钮被点击");
                PlayerActivity activity = (PlayerActivity) getActivity();
                if (activity != null) {
                    activity.playPause();
                    updatePlayButtonIcon(activity.isPlaying());
                }
            });
        }

        // 上一首按钮
        if (btnPrevious != null) {
            btnPrevious.setOnClickListener(v -> {
                Log.d(TAG, "歌词页面-上一首按钮被点击");
                PlayerActivity activity = (PlayerActivity) getActivity();
                if (activity != null) {
                    activity.previous();
                    updateLyric(); // 切歌后更新歌词
                }
            });
        }

        // 下一首按钮
        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                Log.d(TAG, "歌词页面-下一首按钮被点击");
                PlayerActivity activity = (PlayerActivity) getActivity();
                if (activity != null) {
                    activity.next();
                    updateLyric(); // 切歌后更新歌词
                }
            });
        }

        // 进度条拖动
        if (seekbarProgress != null) {
            seekbarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        PlayerActivity activity = (PlayerActivity) getActivity();
                        if (activity != null) {
                            activity.seekTo(progress);
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    private void updateLyric() {
        PlayerActivity activity = (PlayerActivity) getActivity();
        if (activity == null || activity.currentMusic == null) {
            Log.e(TAG, "currentMusic 为 null");
            return;
        }

        MusicInfo currentMusic = activity.currentMusic;

        if (tvLyric != null) {
            String musicName = currentMusic.getMusicName();
            String singer = currentMusic.getAuthor();

            Log.d(TAG, "歌词页面 - 歌曲名: " + musicName);
            Log.d(TAG, "歌词页面 - 歌手: " + singer);

            // 构建显示文本
            StringBuilder lyricText = new StringBuilder();
            lyricText.append(musicName != null ? musicName : "未知歌曲");

            if (singer != null && !singer.trim().isEmpty()) {
                lyricText.append(" - ").append(singer.trim());
            } else {
                lyricText.append(" - 未知歌手");
            }

            lyricText.append("\n\n暂无歌词数据...");

            tvLyric.setText(lyricText.toString());
            Log.d(TAG, "设置歌词文本: " + lyricText.toString());
        }
    }

    private void updatePlayButtonIcon(boolean isPlaying) {
        if (btnPlayPause != null) {
            if (isPlaying) {
                btnPlayPause.setImageResource(R.mipmap.ic_player_pause);
            } else {
                btnPlayPause.setImageResource(R.mipmap.ic_player_playing);
            }
        }
    }

    // 供PlayerActivity调用的方法
    public void onPlayStateChanged(boolean isPlaying) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            updatePlayButtonIcon(isPlaying);
        });
    }

    public void onProgressUpdate(int currentPosition, int duration) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            if (seekbarProgress != null) {
                seekbarProgress.setMax(duration);
                seekbarProgress.setProgress(currentPosition);
            }

            if (tvCurrentTime != null) {
                tvCurrentTime.setText(formatTime(currentPosition));
            }

            if (tvTotalTime != null) {
                tvTotalTime.setText(formatTime(duration));
            }
        });
    }

    public void onSongChanged() {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            updateLyric();
        });
    }

    // 格式化时间
    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}