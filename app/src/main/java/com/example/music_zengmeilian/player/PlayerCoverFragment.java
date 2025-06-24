package com.example.music_zengmeilian.player;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_cover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initEvents();
        initRotateAnimation();
        updateUI();
    }

    private void initViews(View view) {
        ivCover = view.findViewById(R.id.iv_cover);
        tvMusicName = view.findViewById(R.id.tv_song_title);
        tvSinger = view.findViewById(R.id.tv_singer_player);

        // 播放按钮在include的布局中
        View playerControl = view.findViewById(R.id.player_control_cover);
        if (playerControl != null) {
            btnPlayPause = playerControl.findViewById(R.id.btn_play_pause);
        }
    }

    private void updateUI() {
        MusicInfo currentMusic = PlayerActivity.currentMusic;
        if (currentMusic == null) return;

        Log.d(TAG, "开始更新UI");

        if (tvMusicName != null) {
            tvMusicName.setText(currentMusic.getMusicName());
            Log.d(TAG, "✅ 设置音乐名称: " + currentMusic.getMusicName());
        }

        if (tvSinger != null) {
            String singer = currentMusic.getSinger() != null ? currentMusic.getSinger() : "未知歌手";
            tvSinger.setText(singer);
            tvSinger.setTextColor(android.graphics.Color.BLACK);
            tvSinger.setTextSize(16);
            Log.d(TAG, "✅ 设置歌手: " + singer);
        } else {
            Log.e(TAG, "❌ tvSinger 为 null");
        }

        // 调试MusicInfo数据
        Log.d(TAG, "🎵 歌曲名: " + currentMusic.getMusicName());
        Log.d(TAG, "🎤 歌手名: " + currentMusic.getSinger());

        if (ivCover != null && getContext() != null) {
            Glide.with(getContext())
                    .load(currentMusic.getCoverUrl())
                    .into(ivCover);
            Log.d(TAG, "✅ 开始加载封面");
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
        if (rotateAnimator != null && !rotateAnimator.isRunning()) {
            rotateAnimator.start();
        }
    }

    private void stopRotation() {
        if (rotateAnimator != null && rotateAnimator.isRunning()) {
            rotateAnimator.pause();
        }
    }

    private void initEvents() {
        if (btnPlayPause != null) {
            btnPlayPause.setOnClickListener(v -> {
                if (rotateAnimator != null) {
                    if (rotateAnimator.isRunning()) {
                        stopRotation();
                    } else {
                        startRotation();
                    }
                }
            });
        }

        if (ivCover != null) {
            ivCover.setOnClickListener(v -> startRotation());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startRotation();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRotation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rotateAnimator != null) {
            rotateAnimator.cancel();
        }
    }
}