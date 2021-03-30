package com.android.mvvmretrofitjava.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "articles")
public class Article implements Serializable {
    private int pk;

    @SerializedName("source")
    @Expose
    private Source SourceObject;

    @Nullable
    private String author;

    private String title;

    @Nullable
    private String description;

    private String url;

    @Nullable
    private String urlToImage;

    @NonNull
    @PrimaryKey
    private String publishedAt;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public Source getSourceObject() {
        return SourceObject;
    }

    public void setSourceObject(Source sourceObject) {
        SourceObject = sourceObject;
    }

    private String content;


    // Getter Methods

    public Source getSource() {
        return SourceObject;
    }

    @Nullable
    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    @Nullable
    public String getUrlToImage() {
        return urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getContent() {
        return content;
    }

    // Setter Methods

    public void setSource(Source sourceObject) {
        this.SourceObject = sourceObject;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setContent(String content) {
        this.content = content;
    }



}
