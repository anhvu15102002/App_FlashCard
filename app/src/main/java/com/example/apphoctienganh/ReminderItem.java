package com.example.apphoctienganh;

public class ReminderItem {
    private int id;
    private String time;
    private boolean active;

    public ReminderItem(int id, String time, boolean active) {
        this.id = id;
        this.time = time;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
