package com.android.mvvmretrofitjava;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mvvmretrofitjava.adapter.NewsAdapter;
import com.android.mvvmretrofitjava.model.Article;
import com.android.mvvmretrofitjava.model.NewsResponse;
import com.android.mvvmretrofitjava.util.Resource;
import com.android.mvvmretrofitjava.viewmodel.NewsViewModel;
import com.android.mvvmretrofitjava.viewmodel.NewsViewModelFactory;

import java.util.List;

public class NewsActivity extends AppCompatActivity implements NewsAdapter.OnItemClickListener {
    private NewsAdapter newsAdapter;
    public NewsViewModel newsViewModel;
    private RecyclerView rvBreaking;
    LinearLayout layout_top;
    ImageView iv_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        rvBreaking = findViewById(R.id.rvBreakingNews);
        iv_cancel = findViewById(R.id.iv_cancel);
        layout_top = findViewById(R.id.layout_top);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_top.setVisibility(View.GONE);
            }
        });
        newsViewModel = new ViewModelProvider(this, new NewsViewModelFactory(getApplication())).get(NewsViewModel.class);
        newsViewModel.init();

        setUpRecyclerView();
        if (newsViewModel.isConnected()) {
            newsViewModel.breakingNews.observe(this, new Observer<Resource<NewsResponse>>() {
                @Override
                public void onChanged(Resource<NewsResponse> newsResponseResource) {
                    if (newsResponseResource.isLoading()) {
//                    progressBar.setVisibility(View.VISIBLE);
                    } else if (newsResponseResource.isSuccess()) {

//                    progressBar.setVisibility(View.INVISIBLE);

                        newsAdapter.getDiffer().submitList(newsResponseResource.getResource().getArticles());
                        newsViewModel.getSavedArticles().observe(NewsActivity.this, new Observer<List<Article>>() {
                            @Override
                            public void onChanged(List<Article> articles) {
                                new ArticleAsync(newsResponseResource.getResource().getArticles(), articles).execute();
                            }
                        });
                        for (int i = 0; i < newsResponseResource.getResource().getArticles().size(); i++) {
                            newsViewModel.saveArticle(newsResponseResource.getResource().getArticles().get(i));
                        }

//                    newsAdapter.notifyDataSetChanged();


                    } else if (newsResponseResource.getError() != null) {
                        Toast.makeText(NewsActivity.this, newsResponseResource.getError(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            newsViewModel.getSavedArticles().observe(this, new Observer<List<Article>>() {
                @Override
                public void onChanged(List<Article> articles) {
                    if (articles.size() == 0) {
                        Toast.makeText(NewsActivity.this, "No saved News!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    newsAdapter.getDiffer().submitList(articles);
                }
            });
        }

    }

    @Override
    public void onItemClicked(Article article) {
        if (!newsViewModel.isConnected()) {
            Toast.makeText(this, "Internet is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("article_url", article.getUrl());
        intent.putExtra("title", article.getTitle());
        startActivity(intent);
    }


    private void setUpRecyclerView() {
        newsAdapter = new NewsAdapter(NewsActivity.this, this);
        rvBreaking.setNestedScrollingEnabled(false);
        rvBreaking.setLayoutManager(new LinearLayoutManager(NewsActivity.this));
        rvBreaking.setAdapter(newsAdapter);

    }

    private class ArticleAsync extends AsyncTask<Void, Void, Void> {

        List<Article> articles, savedList;

        public ArticleAsync(List<Article> articles, List<Article> savedList) {
            this.articles = articles;
            this.savedList = savedList;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < savedList.size(); i++) {
                boolean isArticleExist = false;
                for (int j = 0; j < articles.size(); j++) {
                    if (savedList.get(i).getPublishedAt().equalsIgnoreCase(articles.get(j).getPublishedAt())) {
                        isArticleExist = true;
                    }
                }
                if (!isArticleExist)
                    newsViewModel.deleteArticle(savedList.get(i));
            }

            return null;
        }
    }


}