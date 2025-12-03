// File: com.example.ad5.model.Movie.java (Hoặc package phù hợp)

package com.example.ad5;

import java.io.Serializable;

public class Movie implements Serializable {

    // Khai báo các trường dữ liệu ánh xạ TRỰC TIẾP từ tên cột DB
    private int movie_id;
    private String movie_name;
    private String movie_type;
    private String description;
    private String image;       // image URL
    private int duration;
    private String language;
    private String release_date;
    private double point;
    private String status;

    // Constructor rỗng
    public Movie() {
    }

    // Constructor đầy đủ
    public Movie(int movie_id, String movie_name, String movie_type, String description, String image,
                 int duration, String language, String release_date, double point, String status) {
        this.movie_id = movie_id;
        this.movie_name = movie_name;
        this.movie_type = movie_type;
        this.description = description;
        this.image = image;
        this.duration = duration;
        this.language = language;
        this.release_date = release_date;
        this.point = point;
        this.status = status;
    }

    // --- Getters và Setters ---
    // Tên phương thức vẫn tuân theo quy tắc camelCase chuẩn Java (getMovie_id không phải là chuẩn)
    // Tôi sẽ sử dụng getMovieId, setMovieName, v.v. để giữ chuẩn Java,
    // nhưng tên biến (field) bên trong vẫn là tên cột DB.

    public int getMovie_id() { return movie_id; }
    public void setMovie_id(int movie_id) { this.movie_id = movie_id; }

    public String getMovie_name() { return movie_name; }
    public void setMovie_name(String movie_name) { this.movie_name = movie_name; }

    public String getMovie_type() { return movie_type; }
    public void setMovie_type(String movie_type) { this.movie_type = movie_type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getRelease_date() { return release_date; }
    public void setRelease_date(String release_date) { this.release_date = release_date; }

    public double getPoint() { return point; }
    public void setPoint(double point) { this.point = point; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    @Override
    public String toString() {
        return "Movie{" +
                "movie_id=" + movie_id +
                ", movie_name='" + movie_name + '\'' +
                ", movie_type='" + movie_type + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", duration='" + duration + '\'' +
                ", language='" + language + '\'' +
                ", release_date='" + release_date + '\'' +
                ", point=" + point +
                ", status='" + status + '\'' +
                '}';
    }
}