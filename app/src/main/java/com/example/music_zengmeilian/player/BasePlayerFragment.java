package com.example.music_zengmeilian.player;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.music_zengmeilian.R;

/**
 * 播放器基类 Fragment，封装所有 Fragment 共用的播放控制逻辑（播放/暂停、进度条、上一首/下一首等）
 */
public abstract class BasePlayerFragment extends Fragment {
    // 播放控制控件
    protected ImageButton btnPlayPause, btnPrevious, btnNext;
    protected SeekBar seekbarProgress;
    protected TextView tvCurrentTime, tvTotalTime;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPlayerControls(view); // 初始化播放控制区域
        initEvents();            // 设置按钮事件监听
    }

    /**
     * 初始化播放控制区域的控件
     * @param view Fragment 的根视图
     */
    protected void initPlayerControls(View view) {
        // 通过子类实现的 getPlayerControlId() 获取播放控制区域的布局 ID
        View playerControl = view.findViewById(getPlayerControlId());
        if (playerControl != null) {
            btnPlayPause = playerControl.findViewById(R.id.btn_play_pause);
            btnPrevious = playerControl.findViewById(R.id.btn_play_previous);
            btnNext = playerControl.findViewById(R.id.btn_play_next);
            seekbarProgress = playerControl.findViewById(R.id.seekbar_progress);
            tvCurrentTime = playerControl.findViewById(R.id.tv_current_time);
            tvTotalTime = playerControl.findViewById(R.id.tv_total_time);
        }
    }

    /**
     * 抽象方法：子类必须实现，返回播放控制区域的布局 ID
     */
    protected abstract int getPlayerControlId();

    /**
     * 初始化按钮事件监听
     */
    protected void initEvents() {
        // 播放/暂停按钮
        if (btnPlayPause != null) {
            btnPlayPause.setOnClickListener(v -> {
                PlayerActivity activity = (PlayerActivity) getActivity();
                if (activity != null) {
                    activity.playPause(); // 调用 Activity 的播放控制方法
                }
            });
        }

        // 上一首按钮
        if (btnPrevious != null) {
            btnPrevious.setOnClickListener(v -> {
                PlayerActivity activity = (PlayerActivity) getActivity();
                if (activity != null) {
                    activity.previous(); // 调用 Activity 的切歌方法
                }
            });
        }

        // 下一首按钮
        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                PlayerActivity activity = (PlayerActivity) getActivity();
                if (activity != null) {
                    activity.next(); // 调用 Activity 的切歌方法
                }
            });
        }

        // 进度条拖动事件
        if (seekbarProgress != null) {
            seekbarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        PlayerActivity activity = (PlayerActivity) getActivity();
                        if (activity != null) {
                            activity.seekTo(progress); // 拖动时跳转到指定位置
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

    /**
     * 更新播放/暂停按钮图标
     * @param isPlaying 是否正在播放
     */
    protected void updatePlayButtonIcon(boolean isPlaying) {
        if (btnPlayPause != null) {
            btnPlayPause.setImageResource(isPlaying ? R.mipmap.ic_player_pause : R.mipmap.ic_player_playing);
        }
    }

    /**
     * 格式化时间（毫秒 -> "mm:ss"）
     */
    protected String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // ---------- 供 PlayerActivity 调用的方法 ----------

    /**
     * 播放状态变化时更新 UI
     */
    public void onPlayStateChanged(boolean isPlaying) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> updatePlayButtonIcon(isPlaying));
    }

    /**
     * 更新播放进度
     */
    public void onProgressUpdate(int currentPosition, int duration) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            if (seekbarProgress != null) {
                seekbarProgress.setMax(duration);
                seekbarProgress.setProgress(currentPosition);
            }
            if (tvCurrentTime != null) tvCurrentTime.setText(formatTime(currentPosition));
            if (tvTotalTime != null) tvTotalTime.setText(formatTime(duration));
        });
    }
}