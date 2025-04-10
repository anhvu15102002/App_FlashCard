package com.example.apphoctienganh;
public class UserPoint {
    private String user;
    private int points;
    private String time;

    public UserPoint(String user, int points, String time) {
        this.user = user;
        this.points = points;
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
