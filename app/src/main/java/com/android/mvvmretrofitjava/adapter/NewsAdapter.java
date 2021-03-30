package com.android.mvvmretrofitjava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mvvmretrofitjava.R;
import com.android.mvvmretrofitjava.model.Article;
import com.bumptech.glide.Glide;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ArticleViewHolder> {
    Context context;
    private OnItemClickListener listner;
    public static final DiffUtil.ItemCallback<Article> differCallback = new DiffUtil.ItemCallback<Article>() {
        @Override
        public boolean areItemsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getUrl().equals(newItem.getUrl());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.equals(newItem);
        }
    };


    public NewsAdapter(Context context, OnItemClickListener listner) {
        this.context = context;
        this.listner = listner;
    }

    public AsyncListDiffer<Article> getDiffer() {
        return differ;
    }

    private final AsyncListDiffer<Article> differ = new AsyncListDiffer<>(this, differCallback);


    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = differ.getCurrentList().get(position);

        holder.tvTitle.setText(article.getTitle());
        String src = article.getAuthor();
        if (src != null) {
            holder.tvScource.setText(src);
        } else {
            holder.tvScource.setText("Author");

        }

        holder.tvDescription.setText(article.getContent());

        Glide.with(context).load(article.getUrlToImage())
                .into(holder.ivArticleImage);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        private TextView tvScource, tvTitle, tvDescription;
        private ImageView ivArticleImage;
        Button btn_seeall;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            btn_seeall = itemView.findViewById(R.id.btn_seeall);
            tvScource = itemView.findViewById(R.id.tvSource);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivArticleImage = itemView.findViewById(R.id.ivArticleImage);

            btn_seeall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (listner != null && position != RecyclerView.NO_POSITION) {
                        listner.onItemClicked(differ.getCurrentList().get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(Article article);
    }

    public void setOnItemClickListner(OnItemClickListener listner) {
        this.listner = listner;
    }


}

