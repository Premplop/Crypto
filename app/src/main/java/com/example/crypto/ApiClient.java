package com.example.crypto;

import androidx.activity.result.contract.ActivityResultContracts;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
   private static Retrofit retrofit = null;
   public static String BASEURL = "https://api.coingecko.com/api/v3/";


public static Retrofit getclient(){
    OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
    okHttpClient.writeTimeout(2,TimeUnit.MINUTES);
    okHttpClient.readTimeout(2, TimeUnit.MINUTES);
    okHttpClient.connectTimeout(2,TimeUnit.MINUTES);

    OkHttpClient client = okHttpClient.build();

    retrofit = new Retrofit.Builder().
            client(client).
            addConverterFactory(GsonConverterFactory.create()).
            baseUrl(BASEURL).
            build();

    return retrofit;
}
}
