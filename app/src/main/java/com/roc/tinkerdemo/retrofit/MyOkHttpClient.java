package com.roc.tinkerdemo.retrofit;

import android.support.annotation.NonNull;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/6/6.
 */
public class MyOkHttpClient {

    public static OkHttpClient mOkHttpClient;

    @NonNull
    public static OkHttpClient getOkHttpClient() {
        if (mOkHttpClient != null) {
            return mOkHttpClient;
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();



        //设置cookie
//        CookieManager cookieManager = new CookieManager();
//        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//        builder.cookieJar(new JavaNetCookieJar(cookieManager));
//
        //公共参数
//        builder.addInterceptor(new QueryParameterInterceptor());

        //Gzip压缩
//        builder.addInterceptor(new GzipRequestInterceptor());

        //开启缓存
//        File cacheFile = new File(RockContextUtils.getApplicationContext().getExternalCacheDir(), "appCache");
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 30);
//        builder.cache(cache).addInterceptor(new CacheInterceptor());

        //开启facebook sDebug
//        builder.addNetworkInterceptor(new StethoInterceptor());

        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
//        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);

        mOkHttpClient = builder.build();
        return mOkHttpClient;
    }
}
