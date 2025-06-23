package com.example.music_zengmeilian.home.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.music_zengmeilian.home.model.HomePageResponse;
import com.example.music_zengmeilian.home.repository.MusicRepository;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final MutableLiveData<List<HomePageResponse.HomePageInfo>> homeData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MusicRepository repository;
    private int currentPage = 1;
    private final int pageSize = 5;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.repository = new MusicRepository(application);
    }

    public void loadHomeData(boolean isRefresh) {
        if (isRefresh) {
            currentPage = 1;
        }

        repository.getHomePageData(currentPage, pageSize,
                new MusicRepository.DataCallback<HomePageResponse>() {
                    @Override
                    public void onSuccess(HomePageResponse response) {
                        if (response == null || response.getData() == null) {
                            errorMessage.postValue("服务器返回空数据");
                            return;
                        }

                        List<HomePageResponse.HomePageInfo> validData = new ArrayList<>();
                        for (HomePageResponse.HomePageInfo item : response.getData().getRecords()) {
                            if (item != null && item.getMusicInfoList() != null) {
                                item.setModuleName(getLocalModuleName(item.getStyle()));
                                validData.add(item);
                            }
                        }

                        if (validData.isEmpty()) {
                            errorMessage.postValue("没有有效数据");
                        } else {
                            homeData.postValue(validData);
                            if (!isRefresh) {
                                currentPage++;
                            }
                        }
                    }

                    @Override
                    public void onError(String message) {
                        errorMessage.postValue(message);
                    }
                });
    }

    private String getLocalModuleName(int style) {
        switch (style) {
            case 1: return "推荐";
            case 2: return "热门单曲";
            case 3: return "每日推荐";
            case 4: return "热门歌单";
            default: return "";
        }
    }

    public MutableLiveData<List<HomePageResponse.HomePageInfo>> getHomeData() {
        return homeData;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
}