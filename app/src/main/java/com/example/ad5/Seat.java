package com.example.ad5;

import java.io.Serializable;

// Triển khai Serializable để cho phép truyền đối tượng qua Intent
public class Seat implements Serializable {

    // SerialVersionUID được khuyến nghị cho Serializable
    private static final long serialVersionUID = 1L;

    private int seat_id;
    private int room_id;
    private String seat_name;
    // status: 0: Đã đặt, 1: Còn trống, 2: Không tồn tại (ví dụ: chỗ trống giữa các hàng)
    private int status;

    // isSelected: Dùng trong Adapter, KHÔNG lưu vào DB.
    private boolean isSelected = false;

    // Constructor mặc định (cần thiết cho một số thư viện và logic)
    public Seat() {}

    // Constructor đầy đủ (Tùy chọn)
    public Seat(int seat_id, int room_id, String seat_name, int status) {
        this.seat_id = seat_id;
        this.room_id = room_id;
        this.seat_name = seat_name;
        this.status = status;
    }

    // =================================================================
    // GETTERS VÀ SETTERS
    // =================================================================

    public int getSeat_id() {
        return seat_id;
    }

    public void setSeat_id(int seat_id) {
        this.seat_id = seat_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getSeat_name() {
        return seat_name;
    }

    public void setSeat_name(String seat_name) {
        this.seat_name = seat_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}