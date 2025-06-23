package com.example.music_zengmeilian.home.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.home.model.MusicInfo;

import java.net.URL;
import java.util.List;

/**
 * Banner轮播适配器
 * 支持无限循环滚动效果
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private static final String TAG = "BannerAdapter";
    private Context context;
    private List<MusicInfo> musicList;
    private static final int MULTIPLIER = 1000; // 用于实现无限循环的乘数

    public BannerAdapter(Context context, List<MusicInfo> musicList) {
        this.context = context;
        this.musicList = musicList;
        Log.d(TAG, "BannerAdapter初始化，音乐数量: " + musicList.size());
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_banner_image, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        // 使用取模运算实现无限循环
        int realPosition = position % musicList.size();
        MusicInfo music = musicList.get(realPosition);

        Log.d(TAG, "[Banner] 位置[" + position + "->实际:" + realPosition + "] " +
                "加载: " + music.getMusicName());

        // 加载封面图片
        loadBannerImageWithDebug(music.getCoverUrl(), holder.imageView,
                "Banner-" + realPosition);

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "[Banner] 点击: " + music.getMusicName());
            Toast.makeText(context, music.getMusicName() + " - " + music.getAuthor(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        // 返回一个足够大的数以实现无限循环效果
        return musicList.size() * MULTIPLIER;
    }

    /**
     * Banner专用的图片加载方法
     */
    private void loadBannerImageWithDebug(String url, ImageView imageView, String tag) {
        Log.d(TAG, "[" + tag + "] 开始加载Banner图片");
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "SDK版本: " + Build.VERSION.SDK_INT);

        // 检查URL有效性
        if (url == null || url.trim().isEmpty()) {
            Log.e(TAG, "[" + tag + "] URL为空!");
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        // 检查URL格式
        try {
            URL testUrl = new URL(url);
            Log.d(TAG, "[" + tag + "] URL格式正确，主机: " + testUrl.getHost());
        } catch (Exception e) {
            Log.e(TAG, "[" + tag + "] URL格式错误: " + e.getMessage());
            imageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            return;
        }

        // 使用Glide加载图片
        Glide.with(context)
                .load(url)
                .placeholder(android.R.drawable.ic_menu_gallery)      // 加载中显示
                .error(android.R.drawable.ic_menu_close_clear_cancel)  // 加载失败显示
                .centerCrop()   // Banner图片使用centerCrop裁剪
                .timeout(15000) // 15秒超时
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "[" + tag + "] Banner图片加载失败!");
                        Log.e(TAG, "失败URL: " + url);

                        if (e != null) {
                            Log.e(TAG, "主要错误: " + e.getMessage());

                            // 详细错误分析
                            List<Throwable> causes = e.getRootCauses();
                            for (int i = 0; i < causes.size(); i++) {
                                Throwable cause = causes.get(i);
                                String errorMsg = cause.getMessage();
                                Log.e(TAG, "根本原因[" + i + "]: " +
                                        cause.getClass().getSimpleName() + " - " + errorMsg);

                                // 特别检查常见错误
                                if (errorMsg != null) {
                                    if (errorMsg.contains("CLEARTEXT")) {
                                        Log.e(TAG, "CLEARTEXT错误! HTTP流量被阻止!");
                                    } else if (errorMsg.contains("UnknownHost")) {
                                        Log.e(TAG, "DNS解析失败! 检查网络连接!");
                                    } else if (errorMsg.contains("timeout")) {
                                        Log.e(TAG, "连接超时! 网络慢或服务器响应慢!");
                                    }
                                }
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        Log.d(TAG, "[" + tag + "] Banner图片加载成功!");
                        Log.d(TAG, "成功URL: " + url);
                        Log.d(TAG, "数据源: " + dataSource);
                        return false;
                    }
                })
                .into(imageView);
    }

    /**
     * Banner ViewHolder
     */
    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        BannerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImage);
        }
    }
}