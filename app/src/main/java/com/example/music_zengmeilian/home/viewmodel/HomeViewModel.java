package com.example.music_zengmeilian.home.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.music_zengmelian.model.HomePageResponse;
import com.example.music_zengmelian.model.MusicInfo;
import com.example.music_zengmelian.repository.MusicRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页ViewModel
 * 负责管理首页数据的获取和状态
 */
public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<HomePageResponse.HomePageInfo>> homeData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MusicRepository repository;
    private int currentPage = 1;
    private final int pageSize = 5;

    public HomeViewModel() {
        repository = new MusicRepository();
    }

    /**
     * 加载首页数据
     * @param isRefresh 是否为刷新操作
     */
    public void loadHomeData(boolean isRefresh) {
        if (isRefresh) {
            currentPage = 1;
        }

        repository.getHomePageData(currentPage, pageSize,
                new MusicRepository.DataCallback<HomePageResponse>() {
                    @Override
                    public void onSuccess(HomePageResponse data) {
                        if (isRefresh) {
                            homeData.setValue(data.getData().getRecords());
                        } else {
                            List<HomePageResponse.HomePageInfo> current = homeData.getValue();
                            if (current == null) {
                                current = new ArrayList<>();
                            }
                            current.addAll(data.getData().getRecords());
                            homeData.setValue(current);
                            currentPage++;
                        }
                    }

                    @Override
                    public void onError(String message) {
                        errorMessage.setValue(message);
                    }
                });
    }

    public MutableLiveData<List<HomePageResponse.HomePageInfo>> getHomeData() {
        return homeData;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
}