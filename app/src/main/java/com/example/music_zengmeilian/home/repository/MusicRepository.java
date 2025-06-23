package com.example.music_zengmeilian.home.repository;

import android.content.Context;
import android.util.Log;

import com.example.music_zengmeilian.home.api.ApiService;
import com.example.music_zengmeilian.home.api.RetrofitClient;
import com.example.music_zengmeilian.home.model.HomePageResponse;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

public class MusicRepository {
    private static final String TAG = "MusicRepository";
    private static final String CACHE_FILE = "home_data_cache.json";
    private final ApiService apiService;
    private final Context context;
    private final Gson gson = new Gson();

    public MusicRepository(Context context) {
        this.context = context.getApplicationContext();
        this.apiService = RetrofitClient.getClient(context).create(ApiService.class);
    }

    public void getHomePageData(int page, int size, DataCallback<HomePageResponse> callback) {
        // 先尝试网络请求
        apiService.getHomePageData(page, size).enqueue(new Callback<HomePageResponse>() {
            @Override
            public void onResponse(Call<HomePageResponse> call, Response<HomePageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveToCache(response.body());
                    callback.onSuccess(response.body());
                } else {
                    Log.w(TAG, "网络响应异常: " + response.code());
                    loadFromCache(callback);
                }
            }

            @Override
            public void onFailure(Call<HomePageResponse> call, Throwable t) {
                Log.e(TAG, "网络请求失败", t);
                loadFromCache(callback);
            }
        });
    }

    private void saveToCache(HomePageResponse data) {
        try (FileOutputStream fos = context.openFileOutput(CACHE_FILE, Context.MODE_PRIVATE)) {
            fos.write(gson.toJson(data).getBytes());
            Log.d(TAG, "数据缓存成功");
        } catch (Exception e) {
            Log.e(TAG, "缓存保存失败", e);
        }
    }

    private void loadFromCache(DataCallback<HomePageResponse> callback) {
        File cacheFile = new File(context.getFilesDir(), CACHE_FILE);
        if (!cacheFile.exists()) {
            callback.onError("网络不可用且无缓存数据");
            return;
        }

        try (FileInputStream fis = context.openFileInput(CACHE_FILE);
             Scanner scanner = new Scanner(fis).useDelimiter("\\A")) {

            String json = scanner.hasNext() ? scanner.next() : "";
            HomePageResponse cachedData = gson.fromJson(json, HomePageResponse.class);

            if (cachedData != null && cachedData.getData() != null) {
                Log.d(TAG, "使用缓存数据");
                callback.onSuccess(cachedData);
            } else {
                callback.onError("缓存数据无效");
            }
        } catch (Exception e) {
            Log.e(TAG, "缓存读取失败", e);
            callback.onError("缓存解析失败");
        }
    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }
}