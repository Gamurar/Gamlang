package com.hfad.gamlang.Model.database;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "card")
public class CardEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String word;
    private String translation;
    private String image;
    private String pronunciation;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @Ignore
    public CardEntry(String word, String translation) {
        this.word = word;
        this.translation = translation;
        this.updatedAt = new Date();
    }

    @Ignore
    public CardEntry(String word, String translation, String image) {
        this.word = word;
        this.translation = translation;
        this.image = image;
        this.updatedAt = new Date();
    }

    @Ignore
    public CardEntry(String word, String translation, String image, String pronunciation) {
        this.word = word;
        this.translation = translation;
        this.image = image;
        this.pronunciation = pronunciation;
        this.updatedAt = new Date();
    }

    public CardEntry(int id, String word, String translation) {
        this.id = id;
        this.word = word;
        this.translation = translation;
        this.updatedAt = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}