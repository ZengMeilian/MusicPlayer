package com.example.music_zengmeilian.player;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.MusicInfo;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 播放器封面页 Fragment，显示专辑封面和旋转动画
 */
public class PlayerCoverFragment extends BasePlayerFragment {
    private static final String TAG = "PlayerCoverFragment";
    private CircleImageView ivCover;
    private TextView tvMusicName, tvSinger;
    private ObjectAnimator rotateAnimator; // 封面旋转动画

    @Override
    public int getPlayerControlId() {
        return R.id.player_control_cover; // 返回封面页的播放控制区域 ID
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_cover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置 Activity 对当前 Fragment 的引用
        PlayerActivity activity = (PlayerActivity) getActivity();
        if (activity != null) {
            activity.setPlayerCoverFragment(this);
        }

        // 初始化封面和文本控件
        ivCover = view.findViewById(R.id.iv_cover);
        tvMusicName = view.findViewById(R.id.tv_song_title);
        tvSinger = view.findViewById(R.id.tv_singer_player);

        initRotateAnimation(); // 初始化旋转动画
        updateUI();           // 更新歌曲信息

        // 封面点击事件（点击封面等同于点击播放/暂停按钮）
        if (ivCover != null) {
            ivCover.setOnClickListener(v -> {
                if (btnPlayPause != null) {
                    btnPlayPause.performClick();
                }
            });
        }
    }

    /**
     * 初始化封面旋转动画
     */
    private void initRotateAnimation() {
        if (ivCover != null) {
            rotateAnimator = ObjectAnimator.ofFloat(ivCover, "rotation", 0f, 360f);
            rotateAnimator.setDuration(20000);      // 20秒转一圈
            rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE); // 无限循环
            rotateAnimator.setRepeatMode(ObjectAnimator.RESTART);  // 重新开始
            rotateAnimator.setInterpolator(new LinearInterpolator()); // 匀速旋转
        }
    }

    /**
     * 开始旋转动画
     */
    private void startRotation() {
        if (rotateAnimator != null) {
            if (rotateAnimator.isPaused()) {
                rotateAnimator.resume();  // 从暂停恢复
            } else if (!rotateAnimator.isRunning()) {
                rotateAnimator.start(); // 首次启动
            }
        }
    }

    /**
     * 暂停旋转动画
     */
    private void stopRotation() {
        if (rotateAnimator != null && rotateAnimator.isRunning()) {
            rotateAnimator.pause();
        }
    }

    /**
     * 更新歌曲信息（封面、歌名、歌手）
     */
    private void updateUI() {
        PlayerActivity activity = (PlayerActivity) getActivity();
        if (activity == null || activity.currentMusic == null) return;

        MusicInfo currentMusic = activity.currentMusic;
        if (tvMusicName != null) {
            tvMusicName.setText(currentMusic.getMusicName());
        }
        if (tvSinger != null) {
            tvSinger.setText(currentMusic.getAuthor() != null ? currentMusic.getAuthor() : "未知歌手");
        }
        if (ivCover != null && getContext() != null) {
            Glide.with(getContext())
                    .load(currentMusic.getCoverUrl())
                    .into(ivCover);
        }
    }

    @Override
    public void onPlayStateChanged(boolean isPlaying) {
        super.onPlayStateChanged(isPlaying); // 更新播放按钮图标
        // 同步控制旋转动画
        if (isPlaying) {
            startRotation();
        } else {
            stopRotation();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rotateAnimator != null) {
            rotateAnimator.cancel(); // 释放动画资源
        }
    }

    public void onSongChanged() {
        updateUI();
    }
}