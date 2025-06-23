package com.example.music_zengmelian.repository;

import com.example.music_zengmelian.api.ApiService;
import com.example.music_zengmelian.api.RetrofitClient;
import com.example.music_zengmelian.model.HomePageResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 数据仓库类
 * 统一管理数据获取逻辑
 */
public class MusicRepository {
    private ApiService apiService;

    public MusicRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    /**
     * 获取首页数据
     * @param page 页码
     * @param size 每页大小
     * @param callback 回调接口
     */
    public void getHomePageData(int page, int size,
                                final DataCallback<HomePageResponse> callback) {
        apiService.getHomePageData(page, size).enqueue(new Callback<HomePageResponse>() {
            @Override
            public void onResponse(Call<HomePageResponse> call,
                                   Response<HomePageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Response error");
                }
            }

            @Override
            public void onFailure(Call<HomePageResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * 数据回调接口
     * @param <T> 数据类型
     */
    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }
}