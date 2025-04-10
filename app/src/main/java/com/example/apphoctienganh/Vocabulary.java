package com.example.apphoctienganh;

public class Vocabulary {
    private int id;
    private String image;
    private String words;
    private String answer;
    private String topic;
    public Vocabulary() {
    }
    public Vocabulary(int id, String image, String words, String answer, String topic) {
        this.id = id;
        this.image = image;
        this.words = words;
        this.answer = answer;
        this.topic = topic;
    }
    public Vocabulary( String words, String image, String answer, String topic) {

        this.image = image;
        this.words = words;
        this.answer = answer;
        this.topic = topic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
