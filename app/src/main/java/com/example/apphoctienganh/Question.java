package com.example.apphoctienganh;

public class Question {
    private int id;
    private String question;
    private String answer;
    private String allchoice;

    public Question(int id, String question, String answer, String allchoice) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.allchoice = allchoice;
    }
    public Question( String question, String answer, String allchoice) {
        this.question = question;
        this.answer = answer;
        this.allchoice = allchoice;
    }
    public Question(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAllchoice() {
        return allchoice;
    }

    public void setAllchoice(String allchoice) {
        this.allchoice = allchoice;
    }
}
