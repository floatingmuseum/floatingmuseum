package com.floatingmuseum.androidtest.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Floatingmuseum on 2017/3/7.
 */

public class FloatingMuseumRetrofit {
    final NetService service;

    final static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").serializeNulls().create();

    FloatingMuseumRetrofit(){
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
//                .addInterceptor(new HeaderIntercept())
//                .addInterceptor(new AuthInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                //baseUrl方法指定了请求地址的前半部分，即服务器地址
                .baseUrl("http://gank.io/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        service = retrofit.create(NetService.class);
    }

    public NetService getService(){
        return  service;
    }
}
