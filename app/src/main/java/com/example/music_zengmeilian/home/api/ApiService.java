package com.example.music_zengmelian.api;

import com.example.music_zengmelian.model.HomePageResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 网络API接口定义
 * 使用Retrofit注解声明网络请求
 */
public interface ApiService {
    /**
     * 获取首页数据
     * @param current 当前页码
     * @param size 每页数据量
     * @return 首页数据响应
     */
    @GET("music/homePage")
    Call<HomePageResponse> getHomePageData(
            @Query("current") int current,
            @Query("size") int size
    );
}