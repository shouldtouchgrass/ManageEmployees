package com.ashstudios.safana.ui.mycalendar;

public class DayModel {
    private final String day;
    private final String month;
    private final String year;
    private int color;

    public DayModel(String day, String month, String year, int color) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.color = color;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public int getColor() {
        return color;
    }
}