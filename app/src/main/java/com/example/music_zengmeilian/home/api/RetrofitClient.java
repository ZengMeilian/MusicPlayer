package com.example.music_zengmelian.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit客户端单例
 * 负责创建和配置Retrofit实例
 */
public class RetrofitClient {
    private static final String BASE_URL = "https://hotfix-service-prod.g.mi.com/";
    private static Retrofit retrofit;

    /**
     * 获取Retrofit实例
     * @return 配置好的Retrofit实例
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}