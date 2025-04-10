package com.example.apphoctienganh;

public class VocabularyItem {
    private String englishWord;
    private String vietnameseMeaning;
    private String imageUrl;

    public VocabularyItem(String englishWord, String vietnameseMeaning, String imageUrl) {
        this.englishWord = englishWord;
        this.vietnameseMeaning = vietnameseMeaning;
        this.imageUrl = imageUrl;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public String getVietnameseMeaning() {
        return vietnameseMeaning;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
