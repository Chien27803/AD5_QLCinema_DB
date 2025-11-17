package com.example.ad5;

public class TicketItem {
    private int ticketId;
    private String movieName;
    private String movieImage;
    private String showDate;
    private String showTime;
    private String seats;
    private String paymentMethod;
    private int totalMoney;
    private String bookingTime;
    private String status;

    // Getters and Setters
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public String getMovieName() { return movieName; }
    public void setMovieName(String movieName) { this.movieName = movieName; }

    public String getMovieImage() { return movieImage; }
    public void setMovieImage(String movieImage) { this.movieImage = movieImage; }

    public String getShowDate() { return showDate; }
    public void setShowDate(String showDate) { this.showDate = showDate; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public String getSeats() { return seats; }
    public void setSeats(String seats) { this.seats = seats; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public int getTotalMoney() { return totalMoney; }
    public void setTotalMoney(int totalMoney) { this.totalMoney = totalMoney; }

    public String getBookingTime() { return bookingTime; }
    public void setBookingTime(String bookingTime) { this.bookingTime = bookingTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}