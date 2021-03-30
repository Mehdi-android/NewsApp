package com.android.mvvmretrofitjava.network;

import com.android.mvvmretrofitjava.model.NewsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    @GET("v2/top-headlines")
    Call<NewsResponse> getBreakingNews(
            @Query("country") String country,
            @Query("page") int pageNumber,
            @Query("apiKey") String apiKey);
}
