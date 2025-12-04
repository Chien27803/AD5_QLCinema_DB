package com.example.ad5;

import java.io.Serializable;

public class News implements Serializable {
    private int news_id;
    private String title;
    private String summary;
    private String content;
    private String image;
    private String created_at;

    // Getters and Setters
    public int getNews_id() { return news_id; }
    public void setNews_id(int news_id) { this.news_id = news_id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}