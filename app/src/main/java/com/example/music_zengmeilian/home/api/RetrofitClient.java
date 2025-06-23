package com.example.music_zengmeilian.home.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "https://hotfix-service-prod.g.mi.com/";
    private static final int CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static Retrofit retrofit;

    public static synchronized Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .cache(new Cache(new File(context.getCacheDir(), "httpCache"), CACHE_SIZE))
                    .addInterceptor(chain -> {
                        if (!isNetworkAvailable(context)) {
                            throw new NoNetworkException();
                        }
                        return chain.proceed(chain.request());
                    });

            if (isDebuggable(context)) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                clientBuilder.addInterceptor(logging);
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(clientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static class SafeDns implements Dns {
        @Override
        public List<InetAddress> lookup(String hostname) throws UnknownHostException {
            if (hostname.equals("hotfix-service-prod.g.mi.com")) {
                try {
                    return Arrays.asList(
                            InetAddress.getByName("36.155.115.36"),
                            InetAddress.getByName("139.196.235.147")
                    );
                } catch (UnknownHostException e) {
                    Log.w(TAG, "使用备用DNS失败，回退系统DNS");
                    return Dns.SYSTEM.lookup(hostname);
                }
            }
            return Dns.SYSTEM.lookup(hostname);
        }
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private static boolean isDebuggable(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static class NoNetworkException extends IOException {
        public NoNetworkException() {
            super("无可用网络连接");
        }
    }
}