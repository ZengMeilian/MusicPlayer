package com.example.music_zengmeilian.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music_zengmeilian.R;
import com.example.music_zengmelian.model.MusicInfo;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private Context context;
    private List<MusicInfo> musicList;

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
        MusicInfo music = musicList.get(position % musicList.size());

        Glide.with(context)
                .load(music.getCoverUrl())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, music.getMusicName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        BannerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImage);
        }
    }
}