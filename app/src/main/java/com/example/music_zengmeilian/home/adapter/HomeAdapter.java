package com.example.music_zengmeilian.home.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.music_zengmeilian.R;
import com.example.music_zengmelian.model.HomePageResponse;
import com.example.music_zengmelian.model.MusicInfo;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_BANNER = 1;
    private static final int TYPE_HORIZONTAL = 2;
    private static final int TYPE_SINGLE_COLUMN = 3;
    private static final int TYPE_TWO_COLUMN = 4;

    private Context context;
    private List<HomePageResponse.HomePageInfo> dataList;

    public HomeAdapter(Context context) {
        this.context = context;
        this.dataList = new ArrayList<>();
    }

    public void setData(List<HomePageResponse.HomePageInfo> data) {
        this.dataList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getStyle();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_BANNER:
                return new BannerViewHolder(inflater.inflate(R.layout.item_banner, parent, false));
            case TYPE_HORIZONTAL:
                return new HorizontalViewHolder(inflater.inflate(R.layout.item_horizontal, parent, false));
            case TYPE_SINGLE_COLUMN:
                return new SingleColumnViewHolder(inflater.inflate(R.layout.item_single_column, parent, false));
            case TYPE_TWO_COLUMN:
                return new TwoColumnViewHolder(inflater.inflate(R.layout.item_two_column, parent, false));
            default:
                throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomePageResponse.HomePageInfo item = dataList.get(position);
        List<MusicInfo> musicList = item.getMusicInfoList();

        if (musicList == null || musicList.isEmpty()) return;

        switch (holder.getItemViewType()) {
            case TYPE_BANNER:
                ((BannerViewHolder) holder).bind(musicList);
                break;
            case TYPE_HORIZONTAL:
                ((HorizontalViewHolder) holder).bind(musicList.get(0));
                break;
            case TYPE_SINGLE_COLUMN:
                ((SingleColumnViewHolder) holder).bind(musicList.get(0));
                break;
            case TYPE_TWO_COLUMN:
                ((TwoColumnViewHolder) holder).bind(musicList);
                break;
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof BannerViewHolder) {
            ((BannerViewHolder) holder).cleanup();
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        private ViewPager2 viewPager;
        private DotsIndicator indicator;
        private Handler autoScrollHandler;
        private Runnable autoScrollRunnable;
        private ViewPager2.OnPageChangeCallback pageChangeCallback;

        BannerViewHolder(View itemView) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.viewPager);
            indicator = itemView.findViewById(R.id.indicator);
        }

        void bind(List<MusicInfo> musicList) {
            BannerAdapter adapter = new BannerAdapter(context, musicList);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(Math.min(3, musicList.size()));

            if (musicList.size() > 1) {
                indicator.setVisibility(View.VISIBLE);
                indicator.setViewPager2(viewPager);

                setupAutoScroll();

                pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        autoScrollHandler.removeCallbacks(autoScrollRunnable);
                        autoScrollHandler.postDelayed(autoScrollRunnable, 3000);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                            autoScrollHandler.removeCallbacks(autoScrollRunnable);
                        } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                            autoScrollHandler.postDelayed(autoScrollRunnable, 3000);
                        }
                    }
                };
                viewPager.registerOnPageChangeCallback(pageChangeCallback);
            } else {
                indicator.setVisibility(View.GONE);
            }
        }

        private void setupAutoScroll() {
            autoScrollHandler = new Handler(Looper.getMainLooper());
            autoScrollRunnable = new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getAdapter() != null && viewPager.getAdapter().getItemCount() > 0) {
                        int nextItem = (viewPager.getCurrentItem() + 1) % viewPager.getAdapter().getItemCount();
                        viewPager.setCurrentItem(nextItem, true);
                    }
                    autoScrollHandler.postDelayed(this, 3000);
                }
            };
            autoScrollHandler.postDelayed(autoScrollRunnable, 3000);
        }

        public void cleanup() {
            if (autoScrollHandler != null && autoScrollRunnable != null) {
                autoScrollHandler.removeCallbacks(autoScrollRunnable);
            }
            if (pageChangeCallback != null) {
                viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
            }
        }
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleText = itemView.findViewById(R.id.titleText);
        }

        void bind(MusicInfo music) {
            Glide.with(context).load(music.getCoverUrl()).into(imageView);
            titleText.setText(music.getMusicName());
        }
    }

    class SingleColumnViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView musicNameText;
        TextView artistText;

        public SingleColumnViewHolder(View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.coverImage);
            musicNameText = itemView.findViewById(R.id.musicNameText);
            artistText = itemView.findViewById(R.id.artistText);
        }

        void bind(MusicInfo music) {
            Glide.with(context).load(music.getCoverUrl()).into(coverImage);
            musicNameText.setText(music.getMusicName());
            artistText.setText(music.getAuthor());
        }
    }

    class TwoColumnViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage1;
        ImageView coverImage2;
        TextView musicNameText1;
        TextView musicNameText2;
        TextView artistText1;
        TextView artistText2;

        public TwoColumnViewHolder(View itemView) {
            super(itemView);
            coverImage1 = itemView.findViewById(R.id.coverImage1);
            coverImage2 = itemView.findViewById(R.id.coverImage2);
            musicNameText1 = itemView.findViewById(R.id.musicNameText1);
            musicNameText2 = itemView.findViewById(R.id.musicNameText2);
            artistText1 = itemView.findViewById(R.id.artistText1);
            artistText2 = itemView.findViewById(R.id.artistText2);
        }

        void bind(List<MusicInfo> musicList) {
            if (musicList.size() > 0) {
                MusicInfo music1 = musicList.get(0);
                Glide.with(context).load(music1.getCoverUrl()).into(coverImage1);
                musicNameText1.setText(music1.getMusicName());
                artistText1.setText(music1.getAuthor());
            }

            if (musicList.size() > 1) {
                MusicInfo music2 = musicList.get(1);
                Glide.with(context).load(music2.getCoverUrl()).into(coverImage2);
                musicNameText2.setText(music2.getMusicName());
                artistText2.setText(music2.getAuthor());
            }
        }
    }
}