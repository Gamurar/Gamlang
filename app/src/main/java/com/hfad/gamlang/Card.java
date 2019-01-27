package com.hfad.gamlang;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Card {
    private int id;
    private String question;
    private String answer;
    private ArrayList<Bitmap> pictures;
    private String[] pictureFileNames;

    public Card(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public Card(String question, String answer, ArrayList<Bitmap> pictures) {
        this.question = question;
        this.answer = answer;
        this.pictures = pictures;
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
        return pictureFileNames;
    }

    public void setPictureFileNames(String[] pictureFileNames) {
        this.pictureFileNames = pictureFileNames;
    }
}
