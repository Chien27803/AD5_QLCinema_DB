package com.example.ad5;

import java.io.Serializable;

public class Movie implements Serializable {
    private int movie_id;
    private String movie_name;
    private String movie_type;
    private String description;
    private String image;
    private String language;
    private String release_date;
    private double point;
    private String status;

    // Constructor rỗng
    public Movie() {
    }

    // Constructor đầy đủ (không có movie_id - vì auto increment)
    public Movie(String movie_name, String movie_type, String description,
                 String image, String language, String release_date,
                 double point, String status) {
        this.movie_name = movie_name;
        this.movie_type = movie_type;
        this.description = description;
        this.image = image;
        this.language = language;
        this.release_date = release_date;
        this.point = point;
        this.status = status;
    }

    // Constructor đầy đủ (có movie_id)
    public Movie(int movie_id, String movie_name, String movie_type,
                 String description, String image, String language,
                 String release_date, double point, String status) {
        this.movie_id = movie_id;
        this.movie_name = movie_name;
        this.movie_type = movie_type;
        this.description = description;
        this.image = image;
        this.language = language;
        this.release_date = release_date;
        this.point = point;
        this.status = status;
    }

    // Getters
    public int getMovie_id() {
        return movie_id;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public String getMovie_type() {
        return movie_type;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getLanguage() {
        return language;
    }

    public String getRelease_date() {
        return release_date;
    }

    public double getPoint() {
        return point;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public void setMovie_type(String movie_type) {
        this.movie_type = movie_type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // toString() để debug
    @Override
    public String toString() {
        return "Movie{" +
                "movie_id=" + movie_id +
                ", movie_name='" + movie_name + '\'' +
                ", movie_type='" + movie_type + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", language='" + language + '\'' +
                ", release_date='" + release_date + '\'' +
                ", point=" + point +
                ", status='" + status + '\'' +
                '}';
    }
}
