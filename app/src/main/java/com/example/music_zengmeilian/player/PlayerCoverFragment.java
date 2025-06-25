package com.example.music_zengmeilian.player;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.MusicInfo;
import com.example.music_zengmeilian.player.PlayerActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerCoverFragment extends Fragment {
    private static final String TAG = "PlayerCoverFragment";

    private CircleImageView ivCover;
    private TextView tvMusicName, tvSinger;
    private ImageButton btnPlayPause;

    // 旋转动画
    private ObjectAnimator rotateAnimator;

    //进度
    private SeekBar seekbarProgress;
    private TextView tvCurrentTime, tvTotalTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_cover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PlayerActivity activity = (PlayerActivity) getActivity();
        if (activity != null) {
            activity.setPlayerCoverFragment(this);
        }

        initViews(view);
        initEvents();
        initRotateAnimation();
        updateUI();
    }

    private void initViews(View view) {
        ivCover = view.findViewById(R.id.iv_cover);
        tvMusicName = view.findViewById(R.id.tv_song_title);
        tvSinger = view.findViewById(R.id.tv_singer_player);

        View playerControl = view.findViewById(R.id.player_control_cover);
        if (playerControl != null) {
            btnPlayPause = playerControl.findViewById(R.id.btn_play_pause);
            ImageButton btnPrevious = playerControl.findViewById(R.id.btn_play_previous);
            ImageButton btnNext = playerControl.findViewById(R.id.btn_play_next);

            // 进度相关控件
            seekbarProgress = playerControl.findViewById(R.id.seekbar_progress);
            tvCurrentTime = playerControl.findViewById(R.id.tv_current_time);
            tvTotalTime = playerControl.findViewById(R.id.tv_total_time);

            // 设置按钮点击事件
            if (btnPrevious != null) {
                btnPrevious.setOnClickListener(v -> {
                    Log.d(TAG, "上一首按钮被点击");
                    PlayerActivity activity = (PlayerActivity) getActivity();
                    if (activity != null) {
                        activity.previous();
                    }
                });
            }

            if (btnNext != null) {
                btnNext.setOnClickListener(v -> {
                    Log.d(TAG, "下一首按钮被点击");
                    PlayerActivity activity = (PlayerActivity) getActivity();
                    if (activity != null) {
                        activity.next();
                    }
                });
            }

            // 设置进度条事件
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

            // 调试日志
            Log.d(TAG, "btnPrevious: " + (btnPrevious != null));
            Log.d(TAG, "btnNext: " + (btnNext != null));
            Log.d(TAG, "seekbarProgress: " + (seekbarProgress != null));
            Log.d(TAG, "tvCurrentTime: " + (tvCurrentTime != null));
            Log.d(TAG, "tvTotalTime: " + (tvTotalTime != null));

        } else {
            Log.e(TAG, "player_control_cover 没有找到！");
        }

        // 初始化时间显示
        if (tvCurrentTime != null) {
            tvCurrentTime.setText("00:00");
        }
        if (tvTotalTime != null) {
            tvTotalTime.setText("00:00");
        }
    }

    private void updateUI() {
        MusicInfo currentMusic = ((PlayerActivity)getActivity()).currentMusic;
        if (currentMusic == null) return;

        if (tvMusicName != null) {
            tvMusicName.setText(currentMusic.getMusicName());
        }

        if (tvSinger != null) {
            String singer = currentMusic.getAuthor() != null ? currentMusic.getAuthor() : "未知歌手";
            tvSinger.setText(singer);
            tvSinger.setTextColor(android.graphics.Color.BLACK);
            tvSinger.setTextSize(16);
        }

        if (ivCover != null && getContext() != null) {
            Glide.with(getContext())
                    .load(currentMusic.getCoverUrl())
                    .into(ivCover);
        }
    }

    private void initRotateAnimation() {
        if (ivCover != null) {
            rotateAnimator = ObjectAnimator.ofFloat(ivCover, "rotation", 0f, 360f);
            rotateAnimator.setDuration(20000);
            rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            rotateAnimator.setRepeatMode(ObjectAnimator.RESTART);
            rotateAnimator.setInterpolator(new LinearInterpolator());
        }
    }

    private void startRotation() {
        if (rotateAnimator != null) {
            if (rotateAnimator.isPaused()) {
                rotateAnimator.resume(); // 如果是暂停状态，恢复动画
            } else if (!rotateAnimator.isRunning()) {
                rotateAnimator.start(); // 如果没有运行，开始动画
            }
        }
    }

    private void stopRotation() {
        if (rotateAnimator != null && rotateAnimator.isRunning()) {
            rotateAnimator.pause(); // 暂停
        }
    }

    private void initEvents() {
        if (btnPlayPause != null) {
            btnPlayPause.setOnClickListener(v -> {
                PlayerActivity activity = (PlayerActivity) getActivity();
                if (activity != null) {
                    // 调用播放控制
                    activity.playPause();
                    // 获取播放状态并更新UI
                    boolean isPlaying = activity.isPlaying();
                    updatePlayButtonIcon(isPlaying);

                    // 同步动画状态
                    if (isPlaying) {
                        startRotation();
                    } else {
                        stopRotation();
                    }
                }
            });
        }

        if (ivCover != null) {
            ivCover.setOnClickListener(v -> {
                // 点击封面也可以播放/暂停
                if (btnPlayPause != null) {
                    btnPlayPause.performClick();
                }
            });
        }
    }

    private void updatePlayButtonIcon(boolean isPlaying) {
        if (btnPlayPause != null) {
            if (isPlaying) {
                btnPlayPause.setImageResource(R.mipmap.ic_player_pause); // 播放时显示暂停图标
            } else {
                btnPlayPause.setImageResource(R.mipmap.ic_player_playing); // 暂停时显示播放图标
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PlayerActivity activity = (PlayerActivity) getActivity();
        if (activity != null && activity.isPlaying()) {
            startRotation();
            updatePlayButtonIcon(true);
        } else {
            updatePlayButtonIcon(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 只暂停动画，不影响音乐播放
        stopRotation();
    }

    public void onPlayStateChanged(boolean isPlaying) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            updatePlayButtonIcon(isPlaying);

            if (isPlaying) {
                startRotation();
            } else {
                stopRotation();
            }
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
            updateUI();
        });
    }


    // 添加格式化时间的方法
    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rotateAnimator != null) {
            rotateAnimator.cancel();
        }
    }
}