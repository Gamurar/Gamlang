package com.gamurar.gamlang;

import android.graphics.Bitmap;
import android.media.MediaPlayer;

import java.util.ArrayList;

public class Card {
    private int id;
    private String question;
    private String answer;
    private ArrayList<Bitmap> pictures;
    private ArrayList<String> pictureFileNames;
    private MediaPlayer pronunciation;
    private String soundFileName;

    public Card(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public Card(String question, String answer, ArrayList<Bitmap> pictures) {
        this.question = question;
        this.answer = answer;
        this.pictures = pictures;
    }

    public Card(Card card) {
        this.id = card.id;
        this.question = card.question;
        this.answer = card.answer;
        this.pictures = card.pictures;
        this.pictureFileNames = card.pictureFileNames;
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

    public ArrayList<Bitmap> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<Bitmap> pictures) {
        this.pictures = pictures;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getPictureFileNames() {
        return pictureFileNames.toArray(new String[0]);
    }

    public void setPictureFileNames(ArrayList<String> pictureFileNames) {
        this.pictureFileNames = pictureFileNames;
    }

    public boolean hasPictures() {
        return pictures != null && pictures.size() > 0;
    }

    public MediaPlayer getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(MediaPlayer pronunciation, String fileName) {
        this.pronunciation = pronunciation;
        this.soundFileName = fileName;
    }

    public void pronounce() {
        if (pronunciation != null) {
            pronunciation.start();
        }
    }

    public boolean hasSound() {
        return pronunciation != null;
    }

    public String getSoundFileName() {
        return this.soundFileName;
    }

    public void addPictureFileName(String fileName) {
        if (pictureFileNames == null) {
            pictureFileNames = new ArrayList<>();
        }
        pictureFileNames.add(fileName);
    }
}
