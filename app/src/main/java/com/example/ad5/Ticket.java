package com.example.ad5;

import java.io.Serializable;

public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== Thuộc tính TRỰC TIẾP TỪ BẢNG Ticket =====
    private int ticket_id;
    private int user_id;
    private int showtime_id;
    private String booking_time; // TEXT DEFAULT CURRENT_TIMESTAMP
    private String status;      // TEXT (booked, pending, cancelled)
    private double total_money; // REAL

    // ===== Thuộc tính BỔ SUNG (Lấy qua JOIN để hiển thị) =====
    private String userName;        // Từ bảng Users
    private String movie_name;      // Từ bảng Movie (qua Showtime)
    private String room_name;       // Từ bảng Room (qua Showtime)
    private String showtimeDate;    // Ngày chiếu (S.show_date)
    private String showtimeStart;   // Giờ bắt đầu (S.start_time)

    // Lưu ý: Cần đảm bảo có lớp User, Movie, Room, Showtime trong project của bạn

    // Constructor mặc định
    public Ticket() {}

    // =================================================================
    // GETTERS VÀ SETTERS (Thuộc tính chính)
    // =================================================================

    public int getTicket_id() { return ticket_id; }
    public void setTicket_id(int ticket_id) { this.ticket_id = ticket_id; }

    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }

    public int getShowtime_id() { return showtime_id; }
    public void setShowtime_id(int showtime_id) { this.showtime_id = showtime_id; }

    public String getBooking_time() { return booking_time; }
    public void setBooking_time(String booking_time) { this.booking_time = booking_time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotal_money() { return total_money; }
    public void setTotal_money(double total_money) { this.total_money = total_money; }

    // =================================================================
    // GETTERS VÀ SETTERS (Thuộc tính JOIN)
    // =================================================================

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getMovie_name() { return movie_name; }
    public void setMovie_name(String movie_name) { this.movie_name = movie_name; }

    public String getRoom_name() { return room_name; }
    public void setRoom_name(String room_name) { this.room_name = room_name; }

    public String getShowtimeDate() { return showtimeDate; }
    public void setShowtimeDate(String showtimeDate) { this.showtimeDate = showtimeDate; }

    public String getShowtimeStart() { return showtimeStart; }
    public void setShowtimeStart(String showtimeStart) { this.showtimeStart = showtimeStart; }
}