package com.example.ad5;

import java.io.Serializable;

public class Seat implements Serializable {
    private int seat_id;
    private int room_id;
    private String seat_name;
    private int status; // 1: available, 0: disabled/out of service

    // Constructor rỗng
    public Seat() {}

    // Constructor đầy đủ
    public Seat(int seat_id, int room_id, String seat_name, int status) {
        this.seat_id = seat_id;
        this.room_id = room_id;
        this.seat_name = seat_name;
        this.status = status;
    }

    // Getters
    public int getSeat_id() { return seat_id; }
    public int getRoom_id() { return room_id; }
    public String getSeat_name() { return seat_name; }
    public int getStatus() { return status; }

    // Setters
    public void setSeat_id(int seat_id) { this.seat_id = seat_id; }
    public void setRoom_id(int room_id) { this.room_id = room_id; }
    public void setSeat_name(String seat_name) { this.seat_name = seat_name; }
    public void setStatus(int status) { this.status = status; }
}