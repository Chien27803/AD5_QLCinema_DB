package com.example.ad5;

public class DateItem {
    private String dayName;
    private String dayNumber;
    private String month;
    private String fullDate; // Format: dd/MM/yyyy

    public DateItem(String dayName, String dayNumber, String month, String fullDate) {
        this.dayName = dayName;
        this.dayNumber = dayNumber;
        this.month = month;
        this.fullDate = fullDate;
    }

    public String getDayName() {
        return dayName;
    }

    public String getDayNumber() {
        return dayNumber;
    }

    public String getMonth() {
        return month;
    }

    public String getFullDate() {
        return fullDate;
    }
}