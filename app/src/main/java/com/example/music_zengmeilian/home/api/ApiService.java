package com.example.music_zengmeilian.home.api;

import com.example.music_zengmeilian.home.model.HomePageResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("music/homePage")
    Call<HomePageResponse> getHomePageData(
            @Query("current") int current,
            @Query("size") int size
    );
}