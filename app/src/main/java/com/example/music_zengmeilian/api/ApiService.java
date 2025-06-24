package com.example.music_zengmeilian.api;

import com.example.music_zengmeilian.model.HomePageResponse;

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