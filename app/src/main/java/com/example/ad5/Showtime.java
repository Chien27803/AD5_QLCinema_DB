package com.example.ad5;

import java.io.Serializable;
public class Showtime implements Serializable {

    // ===== Thuộc tính TRỰC TIẾP TỪ BẢNG Showtime =====
    private int showtime_id;
    private int movie_id;
    private int room_id;
    private String start_time;
    private String end_time;
    private double price;
    private String show_date;

    // ===== Thuộc tính BỔ SUNG (Được lấy qua JOIN cho mục đích hiển thị UI) =====
    private String room_name;
    private String movie_name;
    private String movie_image; // URL hoặc đường dẫn ảnh

    // Constructor mặc định (cần thiết cho Firebase/Deserialization)
    public Showtime() {
    }

    // Constructor đầy đủ (Tùy chọn)
    public Showtime(int showtime_id, int movie_id, int room_id, String start_time, String end_time, double price, String show_date) {
        this.showtime_id = showtime_id;
        this.movie_id = movie_id;
        this.room_id = room_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.price = price;
        this.show_date = show_date;
    }

    // =================================================================
    // GETTERS VÀ SETTERS
    // =================================================================

    // Thuộc tính chính
    public int getShowtime_id() {
        return showtime_id;
    }

    public void setShowtime_id(int showtime_id) {
        this.showtime_id = showtime_id;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getShow_date() {
        return show_date;
    }

    public void setShow_date(String show_date) {
        this.show_date = show_date;
    }

    // Thuộc tính phụ (JOIN data)
    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public String getMovie_image() {
        return movie_image;
    }

    public void setMovie_image(String movie_image) {
        this.movie_image = movie_image;
    }
}