package com.example.music_zengmeilian.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.model.HomePageResponse.HomePageInfo;
import com.example.music_zengmeilian.model.MusicInfo;
import com.example.music_zengmeilian.player.PlayerActivity;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页内容适配器
 * 支持多种布局样式：Banner轮播、横滑大卡、单列、双列
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // ViewType常量 - 对应API返回的style值
    private static final int TYPE_BANNER = 1;           // Banner轮播
    private static final int TYPE_HORIZONTAL = 2;       // 横滑大卡
    private static final int TYPE_SINGLE_COLUMN = 3;    // 单列布局
    private static final int TYPE_TWO_COLUMN = 4;       // 双列布局

    private Context context;
    private List<HomePageInfo> homePageInfoList;

    public HomeAdapter(Context context) {
        this.context = context;
        this.homePageInfoList = new ArrayList<>();
    }

    public HomeAdapter(Context context, List<HomePageInfo> homePageInfoList) {
        this.context = context;
        this.homePageInfoList = homePageInfoList != null ? homePageInfoList : new ArrayList<>();
    }

    public void setData(List<HomePageInfo> data) {
        if (data != null) {
            this.homePageInfoList.clear();
            this.homePageInfoList.addAll(data);
            notifyDataSetChanged();
        }
    }

    //获取某个位置的模块样式
    @Override
    public int getItemViewType(int position) {
        HomePageInfo homePageInfo = homePageInfoList.get(position);
        int style = homePageInfo.getStyle();
        return style;
    }

    //创建ViewHolder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case TYPE_BANNER:
                //创建Banner ViewHolder
                View bannerView = inflater.inflate(R.layout.item_banner, parent, false);
                return new BannerViewHolder(bannerView);

            case TYPE_HORIZONTAL:
                //创建横滑大卡 ViewHolder
                View horizontalView = inflater.inflate(R.layout.item_horizontal, parent, false);
                return new HorizontalViewHolder(horizontalView);

            case TYPE_SINGLE_COLUMN:
                //创建单列 ViewHolder
                View singleView = inflater.inflate(R.layout.item_single_column, parent, false);
                return new SingleColumnViewHolder(singleView);

            case TYPE_TWO_COLUMN:
                //创建双列 ViewHolder
                View twoColumnView = inflater.inflate(R.layout.item_two_column, parent, false);
                return new TwoColumnViewHolder(twoColumnView);

            default: return null;
        }
    }

    //绑定ViewHolder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        HomePageInfo homePageInfo = homePageInfoList.get(position);
        String moduleName = homePageInfo.getModuleName();
        List<MusicInfo> musicList = homePageInfo.getMusicInfoList();

        switch (holder.getItemViewType()) {
            case TYPE_BANNER:
                ((BannerViewHolder) holder).bind(musicList);
                break;
            case TYPE_HORIZONTAL:
                ((HorizontalViewHolder) holder).bind(musicList, moduleName);
                break;
            case TYPE_SINGLE_COLUMN:
                ((SingleColumnViewHolder) holder).bind(musicList, moduleName);
                break;
            case TYPE_TWO_COLUMN:
                ((TwoColumnViewHolder) holder).bind(musicList, moduleName);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return homePageInfoList.size();
    }

    /**
     * 统一的图片加载方法
     */
    private void loadImage(String url, ImageView imageView) {

        // 使用Glide加载图片
        Glide.with(context)
                .load(url)
                .placeholder(android.R.drawable.ic_menu_gallery)      // 加载中显示
                .error(android.R.drawable.ic_menu_close_clear_cancel)  // 加载失败显示
                .timeout(15000) // 15秒超时
                .into(imageView);
    }

    //每个模块点击封面图片时，跳转到播放页面
    private void setJumpToPlayerClickListener(View view, MusicInfo currentMusic, List<MusicInfo> fullMusicList, int currentIndex){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "跳转到播放页面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("currentMusic", currentMusic);
                // 传递完整的播放列表，而不是只有一首歌
                intent.putExtra("music_list", (Serializable) fullMusicList);
                intent.putExtra("current_index", currentIndex);
                context.startActivity(intent);
            }
        });
    }

    //banner使用
    private void setJumpToPlayerClickListener(ViewPager2 viewPager, List<MusicInfo> musicList) {
        viewPager.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            int actualPosition = currentItem % musicList.size();
            MusicInfo currentMusic = musicList.get(actualPosition);

            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("currentMusic", currentMusic);
            // 传递完整的播放列表
            intent.putExtra("music_list", (Serializable) musicList);
            intent.putExtra("current_index", actualPosition);
            context.startActivity(intent);
        });
    }

    //点击按钮添加到播放列表
    private void setAddToPlayList(ImageButton imageButton, MusicInfo musicInfo){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "添加到播放列表", Toast.LENGTH_SHORT).show();

            }
        });
    }

    // ==================== ViewHolder 类定义 ====================

    /**
     * Banner轮播 ViewHolder
     * 对应 item_banner.xml
     */
    class BannerViewHolder extends RecyclerView.ViewHolder {
        TextView bannerModuleNameText;
        ViewPager2 bannerViewPager;
        DotsIndicator bannerIndicator;

        BannerViewHolder(View itemView) {
            super(itemView);
            bannerModuleNameText = itemView.findViewById(R.id.bannerModuleNameText);
            bannerViewPager = itemView.findViewById(R.id.homeBannerViewPager);
            bannerIndicator = itemView.findViewById(R.id.homeBannerIndicator);
        }

        void bind(List<MusicInfo> musicList) {
            // 设置Banner适配器
            BannerAdapter bannerAdapter = new BannerAdapter(context, musicList);
            bannerViewPager.setAdapter(bannerAdapter);

            // 连接指示器和ViewPager2
            bannerIndicator.setViewPager2(bannerViewPager);
            //点击图片，跳转到播放页面
            setJumpToPlayerClickListener(bannerViewPager,musicList);
        }
    }

    /**
     * 横滑大卡 ViewHolder
     * 对应 item_horizontal.xml
     */
    class HorizontalViewHolder extends RecyclerView.ViewHolder {
        TextView horizontalModuleNameText;
        ImageView imageView;
        View horizontalInfo;
        TextView musicName;
        TextView author;
        ImageButton buttonPlay;

        HorizontalViewHolder(View itemView) {
            super(itemView);
            horizontalModuleNameText = itemView.findViewById(R.id.horizontalModuleNameText);
            imageView = itemView.findViewById(R.id.homeHorizontalImageView);
            horizontalInfo = itemView.findViewById(R.id.homeHorizontalInfo);
            musicName = horizontalInfo.findViewById(R.id.text_music_name);
            author = horizontalInfo.findViewById(R.id.text_singer);
            buttonPlay = horizontalInfo.findViewById(R.id.button_play);
        }

        void bind(List<MusicInfo> musicList, String moduleName) {

            // 设置模块标题
            horizontalModuleNameText.setText(moduleName);

            // 显示第一首音乐的信息
            if (musicList != null && !musicList.isEmpty()) {
                MusicInfo firstMusic = musicList.get(0);
                musicName.setText(firstMusic.getMusicName());
                author.setText(firstMusic.getAuthor());

                // 加载封面图片
                loadImage(firstMusic.getCoverUrl(), imageView);
                //点击按钮，添加到播放列表,并传递musicInfo
                setAddToPlayList(buttonPlay,firstMusic);
                //点击封面，跳转到播放页面 - 传递完整列表
                setJumpToPlayerClickListener(imageView, firstMusic, musicList, 0);

            }
        }
    }

    /**
     * 单列布局 ViewHolder
     * 对应 item_single_column.xml
     */
    class SingleColumnViewHolder extends RecyclerView.ViewHolder {
        TextView singleModuleNameText;
        ImageView coverImage;
        View singleInfo;
        TextView musicName;
        TextView author;
        ImageButton buttonPlay;

        SingleColumnViewHolder(View itemView) {
            super(itemView);
            singleModuleNameText = itemView.findViewById(R.id.singleModuleNameText);
            coverImage = itemView.findViewById(R.id.singleCoverImageView);
            singleInfo = itemView.findViewById(R.id.singleInfo);
            musicName = singleInfo.findViewById(R.id.text_music_name);
            author = singleInfo.findViewById(R.id.text_singer);
            buttonPlay = singleInfo.findViewById(R.id.button_play);
        }

        void bind(List<MusicInfo> musicList, String moduleName) {

            // 设置模块标题
            singleModuleNameText.setText(moduleName);

            // 显示第一首音乐的信息
            if (musicList != null && !musicList.isEmpty()) {
                MusicInfo firstMusic = musicList.get(0);
                musicName.setText(firstMusic.getMusicName());
                author.setText(firstMusic.getAuthor());

                // 加载封面图片
                loadImage(firstMusic.getCoverUrl(), coverImage);

                //点击按钮，添加到播放列表
                setAddToPlayList(buttonPlay,firstMusic);

                //点击图片，跳转到播放界面 - 传递完整列表
                setJumpToPlayerClickListener(coverImage, firstMusic, musicList, 0);

            }
        }
    }

    /**
     * 双列布局 ViewHolder
     * 对应 item_two_column.xml
     */
    class TwoColumnViewHolder extends RecyclerView.ViewHolder {
        TextView twoColumnModuleNameText;
        ImageView coverImage1,coverImage2;
        View Info1,Info2;
        TextView musicName1,musicName2;
        TextView author1,author2;
        ImageButton buttonPlay1,buttonPlay2;

        TwoColumnViewHolder(View itemView) {
            super(itemView);
            twoColumnModuleNameText = itemView.findViewById(R.id.twoColumnModuleNameText);
            coverImage1 = itemView.findViewById(R.id.twoColumnCoverImage1);
            coverImage2 = itemView.findViewById(R.id.twoColumnCoverImage2);
            Info1 = itemView.findViewById(R.id.twoColumnInfo1);
            Info2 = itemView.findViewById(R.id.twoColumnInfo2);
            musicName1 = Info1.findViewById(R.id.text_music_name);
            author1 = Info1.findViewById(R.id.text_singer);
            musicName2 = Info2.findViewById(R.id.text_music_name);
            author2 = Info2.findViewById(R.id.text_singer);
            buttonPlay1 = Info1.findViewById(R.id.button_play);
            buttonPlay2 = Info2.findViewById(R.id.button_play);
        }

        void bind(List<MusicInfo> musicList, String moduleName) {

            // 设置模块标题
            twoColumnModuleNameText.setText(moduleName);

            // 左侧音乐信息 (第一首)
            if (musicList.size() > 0) {
                MusicInfo leftMusic = musicList.get(0);
                musicName1.setText(leftMusic.getMusicName());
                author1.setText(leftMusic.getAuthor());
                loadImage(leftMusic.getCoverUrl(), coverImage1);
                setAddToPlayList(buttonPlay1,leftMusic);
                setJumpToPlayerClickListener(coverImage1, leftMusic, musicList, 0);
            } else {
                // 如果没有数据，设置默认值
                musicName1.setText("");
                author1.setText("");
                coverImage1.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // 右侧音乐信息 (第二首)
            if (musicList.size() > 1) {
                MusicInfo rightMusic = musicList.get(1);
                musicName2.setText(rightMusic.getMusicName());
                author2.setText(rightMusic.getAuthor());
                loadImage(rightMusic.getCoverUrl(), coverImage2);
                setAddToPlayList(buttonPlay1,rightMusic);
                setJumpToPlayerClickListener(coverImage2, rightMusic, musicList, 1);
            } else {
                // 如果没有第二首音乐，设置默认值
                musicName2.setText("");
                author2.setText("");
                coverImage2.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }
    }
}