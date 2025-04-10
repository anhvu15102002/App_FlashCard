package com.example.apphoctienganh;

import android.widget.ImageView;

public class TopicModel {
    private int imageView;
    private String topic;

    public TopicModel(int imageView, String topic) {
        this.imageView = imageView;
        this.topic = topic;
    }

    public int getImageView() {
        return imageView;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
