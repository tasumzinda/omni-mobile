package com.stewardbank.omnichannel.business.rest;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @uthor Tasu Muzinda
 */
public class RestClient {

    final static String BASE_URL = "";

    public static Retrofit getRetrofit(){
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(client.build()).build();
        return retrofit;
    }
}
