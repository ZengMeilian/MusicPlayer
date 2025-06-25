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
import com.example.music_zengmeilian.model.MusicInfo;

import java.net.URL;
import java.util.List;

/**
 * Banner轮播适配器
 * 支持无限循环滚动效果
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private Context context;
    private List<MusicInfo> musicList;
    private static final int MULTIPLIER = 1000; // 用于实现无限循环的乘数

    public BannerAdapter(Context context, List<MusicInfo> musicList) {
        this.context = context;
        this.musicList = musicList;
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

        //加载封面
        Glide.with(context)
                .load(music.getCoverUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)      // 加载中显示
                .error(android.R.drawable.ic_menu_close_clear_cancel)  // 加载失败显示
                .centerCrop()   // 图片使用centerCrop裁剪
                .timeout(15000) // 15秒超时
                .into(holder.imageView);

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
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
     * Banner ViewHolder
     */
    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        BannerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.homeBannerImage);
        }
    }
}