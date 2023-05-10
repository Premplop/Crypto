package com.example.crypto;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface API {

    @GET("coins/markets")
    Call<ResponseBody> getCoinMarket(@QueryMap Map<String,Object> Params);
    @GET("coins/{id}/ohlc")
    Call<ResponseBody> getCandelStickData(@Path("id") String Coin_Id,@QueryMap Map<String,Object> Params);
}
