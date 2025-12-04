package com.example.ad5;

import java.io.Serializable;

public class Showtime implements Serializable {
    private int showtime_id;
    private int movie_id;
    private int room_id;
    private String start_time;
    private String end_time;
    private double price;
    private String show_date; // Format: dd/MM/yyyy

    // Constructor rỗng
    public Showtime() {}

    // Constructor đầy đủ
    public Showtime(int showtime_id, int movie_id, int room_id, String start_time,
                    String end_time, double price, String show_date) {
        this.showtime_id = showtime_id;
        this.movie_id = movie_id;
        this.room_id = room_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.price = price;
        this.show_date = show_date;
    }

    // Getters
    public int getShowtime_id() { return showtime_id; }
    public int getMovie_id() { return movie_id; }
    public int getRoom_id() { return room_id; }
    public String getStart_time() { return start_time; }
    public String getEnd_time() { return end_time; }
    public double getPrice() { return price; }
    public String getShow_date() { return show_date; }

    // Setters
    public void setShowtime_id(int showtime_id) { this.showtime_id = showtime_id; }
    public void setMovie_id(int movie_id) { this.movie_id = movie_id; }
    public void setRoom_id(int room_id) { this.room_id = room_id; }
    public void setStart_time(String start_time) { this.start_time = start_time; }
    public void setEnd_time(String end_time) { this.end_time = end_time; }
    public void setPrice(double price) { this.price = price; }
    public void setShow_date(String show_date) { this.show_date = show_date; }
}