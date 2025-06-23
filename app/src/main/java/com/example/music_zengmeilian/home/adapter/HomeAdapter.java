package com.example.music_zengmeilian.home.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.music_zengmeilian.home.model.HomePageResponse.HomePageInfo;
import com.example.music_zengmeilian.home.model.MusicInfo;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页内容适配器
 * 支持多种布局样式：Banner轮播、横滑大卡、单列、双列
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeAdapter";

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
        Log.d(TAG, "HomeAdapter初始化，空数据");
    }

    public HomeAdapter(Context context, List<HomePageInfo> homePageInfoList) {
        this.context = context;
        this.homePageInfoList = homePageInfoList != null ? homePageInfoList : new ArrayList<>();
        Log.d(TAG, "HomeAdapter初始化，数据条数: " + this.homePageInfoList.size());
    }

    public void setData(List<HomePageInfo> data) {
        if (data != null) {
            this.homePageInfoList.clear();
            this.homePageInfoList.addAll(data);
            Log.d(TAG, "setData: 更新数据，条数: " + data.size());
            notifyDataSetChanged();
        }
    }

    public void cleanup() {
        Log.d(TAG, "cleanup: 清理资源");
        if (homePageInfoList != null) {
            homePageInfoList.clear();
        }
    }

    @Override
    public int getItemViewType(int position) {
        HomePageInfo homePageInfo = homePageInfoList.get(position);
        int style = homePageInfo.getStyle();

        Log.d(TAG, "位置[" + position + "] 模块: " + homePageInfo.getModuleName() +
                ", 样式: " + style);

        return style;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case TYPE_BANNER:
                Log.d(TAG, "创建Banner ViewHolder");
                View bannerView = inflater.inflate(R.layout.item_banner, parent, false);
                return new BannerViewHolder(bannerView);

            case TYPE_HORIZONTAL:
                Log.d(TAG, "创建横滑大卡 ViewHolder");
                View horizontalView = inflater.inflate(R.layout.item_horizontal, parent, false);
                return new HorizontalViewHolder(horizontalView);

            case TYPE_SINGLE_COLUMN:
                Log.d(TAG, "创建单列 ViewHolder");
                View singleView = inflater.inflate(R.layout.item_single_column, parent, false);
                return new SingleColumnViewHolder(singleView);

            case TYPE_TWO_COLUMN:
                Log.d(TAG, "创建双列 ViewHolder");
                View twoColumnView = inflater.inflate(R.layout.item_two_column, parent, false);
                return new TwoColumnViewHolder(twoColumnView);

            default:
                Log.w(TAG, "未知ViewType: " + viewType + ", 使用默认单列布局");
                View defaultView = inflater.inflate(R.layout.item_single_column, parent, false);
                return new SingleColumnViewHolder(defaultView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomePageInfo homePageInfo = homePageInfoList.get(position);
        String moduleName = homePageInfo.getModuleName();
        List<MusicInfo> musicList = homePageInfo.getMusicInfoList();

        Log.d(TAG, "绑定数据 位置[" + position + "] 模块: " + moduleName +
                ", 音乐数量: " + musicList.size());

        switch (holder.getItemViewType()) {
            case TYPE_BANNER:
                ((BannerViewHolder) holder).bind(musicList, moduleName);
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
     * 统一的图片加载方法，包含详细调试信息
     */
    private void loadImageWithDebug(String url, ImageView imageView, String tag) {
        Log.d(TAG, "[" + tag + "] 准备加载图片");
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "SDK版本: " + Build.VERSION.SDK_INT);

        // 检查URL有效性
        if (url == null || url.trim().isEmpty()) {
            Log.e(TAG, "[" + tag + "] URL为空或null!");
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
                .timeout(15000) // 15秒超时
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "[" + tag + "] 图片加载失败!");
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
                                        Log.e(TAG, "CLEARTEXT错误! HTTP流量被阻止，检查网络安全配置!");
                                    } else if (errorMsg.contains("UnknownHost")) {
                                        Log.e(TAG, "DNS解析失败! 检查网络连接!");
                                    } else if (errorMsg.contains("timeout")) {
                                        Log.e(TAG, "连接超时! 网络速度慢或服务器响应慢!");
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
                        Log.d(TAG, "[" + tag + "] 图片加载成功!");
                        Log.d(TAG, "成功URL: " + url);
                        Log.d(TAG, "数据源: " + dataSource);
                        Log.d(TAG, "图片尺寸: " + resource.getIntrinsicWidth() + "x" + resource.getIntrinsicHeight());
                        return false;
                    }
                })
                .into(imageView);
    }

    // ==================== ViewHolder 类定义 ====================

    /**
     * Banner轮播 ViewHolder
     * 对应 item_banner.xml
     */
    class BannerViewHolder extends RecyclerView.ViewHolder {
        TextView bannerModuleNameText;
        ViewPager2 viewPager;
        DotsIndicator indicator;

        BannerViewHolder(View itemView) {
            super(itemView);
            bannerModuleNameText = itemView.findViewById(R.id.bannerModuleNameText);
            viewPager = itemView.findViewById(R.id.viewPager);
            indicator = itemView.findViewById(R.id.indicator);
        }

        void bind(List<MusicInfo> musicList, String moduleName) {
            Log.d(TAG, "[Banner] 绑定数据: " + moduleName + ", 图片数量: " + musicList.size());

            // 设置模块标题
            bannerModuleNameText.setText(moduleName);

            // 设置Banner适配器
            BannerAdapter bannerAdapter = new BannerAdapter(context, musicList);
            viewPager.setAdapter(bannerAdapter);

            // 连接指示器和ViewPager2
            indicator.setViewPager2(viewPager);
        }
    }

    /**
     * 横滑大卡 ViewHolder
     * 对应 item_horizontal.xml
     */
    class HorizontalViewHolder extends RecyclerView.ViewHolder {
        TextView horizontalModuleNameText;
        ImageView imageView;
        TextView horizontalTitleText;

        HorizontalViewHolder(View itemView) {
            super(itemView);
            horizontalModuleNameText = itemView.findViewById(R.id.horizontalModuleNameText);
            imageView = itemView.findViewById(R.id.imageView);
            horizontalTitleText = itemView.findViewById(R.id.horizontalTitleText);
        }

        void bind(List<MusicInfo> musicList, String moduleName) {
            Log.d(TAG, "[横滑大卡] 绑定数据: " + moduleName);

            // 设置模块标题
            horizontalModuleNameText.setText(moduleName);

            // 显示第一首音乐的信息
            if (musicList != null && !musicList.isEmpty()) {
                MusicInfo firstMusic = musicList.get(0);
                horizontalTitleText.setText(firstMusic.getMusicName() + " - " + firstMusic.getAuthor());

                // 加载封面图片
                loadImageWithDebug(firstMusic.getCoverUrl(), imageView, "横滑大卡");
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
        TextView musicNameText;
        TextView artistText;

        SingleColumnViewHolder(View itemView) {
            super(itemView);
            singleModuleNameText = itemView.findViewById(R.id.singleModuleNameText);
            coverImage = itemView.findViewById(R.id.coverImage);
            musicNameText = itemView.findViewById(R.id.musicNameText);
            artistText = itemView.findViewById(R.id.artistText);
        }

        void bind(List<MusicInfo> musicList, String moduleName) {
            Log.d(TAG, "[单列] 绑定数据: " + moduleName);

            // 设置模块标题
            singleModuleNameText.setText(moduleName);

            // 显示第一首音乐的信息
            if (musicList != null && !musicList.isEmpty()) {
                MusicInfo firstMusic = musicList.get(0);
                musicNameText.setText(firstMusic.getMusicName());
                artistText.setText(firstMusic.getAuthor());

                // 加载封面图片
                loadImageWithDebug(firstMusic.getCoverUrl(), coverImage, "单列");
            }
        }
    }

    /**
     * 双列布局 ViewHolder
     * 对应 item_two_column.xml
     */
    class TwoColumnViewHolder extends RecyclerView.ViewHolder {
        TextView twoColumnModuleNameText;
        ImageView coverImage1;
        ImageView coverImage2;
        TextView twoColumnMusicName1;
        TextView twoColumnMusicName2;
        TextView twoColumnArtist1;
        TextView twoColumnArtist2;

        TwoColumnViewHolder(View itemView) {
            super(itemView);
            twoColumnModuleNameText = itemView.findViewById(R.id.twoColumnModuleNameText);
            coverImage1 = itemView.findViewById(R.id.coverImage1);
            coverImage2 = itemView.findViewById(R.id.coverImage2);
            twoColumnMusicName1 = itemView.findViewById(R.id.twoColumnMusicName1);
            twoColumnMusicName2 = itemView.findViewById(R.id.twoColumnMusicName2);
            twoColumnArtist1 = itemView.findViewById(R.id.twoColumnArtist1);
            twoColumnArtist2 = itemView.findViewById(R.id.twoColumnArtist2);
        }

        void bind(List<MusicInfo> musicList, String moduleName) {
            Log.d(TAG, "[双列] 绑定数据: " + moduleName + ", 音乐数量: " + musicList.size());

            // 设置模块标题
            twoColumnModuleNameText.setText(moduleName);

            // 左侧音乐信息 (第一首)
            if (musicList.size() > 0) {
                MusicInfo leftMusic = musicList.get(0);
                twoColumnMusicName1.setText(leftMusic.getMusicName());
                twoColumnArtist1.setText(leftMusic.getAuthor());
                loadImageWithDebug(leftMusic.getCoverUrl(), coverImage1, "双列-左");
            } else {
                // 如果没有数据，设置默认值
                twoColumnMusicName1.setText("");
                twoColumnArtist1.setText("");
                coverImage1.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // 右侧音乐信息 (第二首)
            if (musicList.size() > 1) {
                MusicInfo rightMusic = musicList.get(1);
                twoColumnMusicName2.setText(rightMusic.getMusicName());
                twoColumnArtist2.setText(rightMusic.getAuthor());
                loadImageWithDebug(rightMusic.getCoverUrl(), coverImage2, "双列-右");
            } else {
                // 如果没有第二首音乐，设置默认值
                twoColumnMusicName2.setText("");
                twoColumnArtist2.setText("");
                coverImage2.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }
    }
}