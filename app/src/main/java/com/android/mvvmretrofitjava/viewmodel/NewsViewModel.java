package com.android.mvvmretrofitjava.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.android.mvvmretrofitjava.model.Article;
import com.android.mvvmretrofitjava.model.NewsResponse;
import com.android.mvvmretrofitjava.repository.NewsRepository;
import com.android.mvvmretrofitjava.util.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;
import static android.net.NetworkCapabilities.TRANSPORT_ETHERNET;
import static android.net.NetworkCapabilities.TRANSPORT_WIFI;

public class NewsViewModel extends ViewModel {

    public static final String API_KEY = "b8d1a7cde66e448dbef8fe927ef8ac07";
    private static final String TAG = "NewsViewModel";
    public MutableLiveData<Resource<NewsResponse>> breakingNews = new MutableLiveData();
    public NewsResponse breakingNewsResponse = null;
    public MutableLiveData<Resource<NewsResponse>> searchNews = new MutableLiveData<>();
    public NewsResponse searchNewsResponse = null;
    Application app;
    //search news...
    String oldQuery = null;
    private NewsRepository newsRepository;
    private int BreakingNewsPageNo = 1;
    private int searchNewsPage = 1;

    public NewsViewModel(@NonNull Application application, NewsRepository newsRepository) {
        this.app = application;
        this.newsRepository = newsRepository;
    }


    public void init() {
        newsRepository = NewsRepository.getInstance(app);
        safeBreakingNewsCall();
    }

    //saved articles........
    public void saveArticle(Article article) {
        newsRepository.upsert(article);
    }


    public LiveData<List<Article>> getSavedArticles() {
        return newsRepository.getSavedArticles();
    }

    public void deleteArticle(Article article) {
        newsRepository.deleteArticle(article);
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) return false;
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (capabilities == null) return false;

            if (capabilities.hasTransport(TRANSPORT_WIFI)) return true;
            if (capabilities.hasTransport(TRANSPORT_CELLULAR)) return true;
            return capabilities.hasTransport(TRANSPORT_ETHERNET);

        } else {

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null) return false;
            if (networkInfo.isConnectedOrConnecting()) return true;
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) return true;
            if (type == ConnectivityManager.TYPE_MOBILE) return true;
            return type == ConnectivityManager.TYPE_ETHERNET;

        }
    }

    ///breaking News......
    public void safeBreakingNewsCall() {
        breakingNews.postValue(Resource.loading(new NewsResponse()));

        if (isConnected()) {
            Call<NewsResponse> call = newsRepository.getBreakingNews("in", BreakingNewsPageNo, API_KEY);
            call.enqueue(new Callback<NewsResponse>() {
                @Override
                public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                    if (response.body() == null) return;
                    if (breakingNewsResponse == null) {
                        breakingNewsResponse = response.body();
                    } else {
                        List<Article> newArticles = response.body().getArticles();
                        if (breakingNewsResponse.getArticles() != null) {
                            breakingNewsResponse.getArticles().addAll(newArticles);
                        }

                    }
                    breakingNews.postValue(Resource.success(breakingNewsResponse));
                }

                @Override
                public void onFailure(Call<NewsResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    breakingNews.postValue(Resource.error("Failed to load News"));
                }
            });
        } else {
            breakingNews.postValue(Resource.error("No internet connection"));
        }


    }


}









