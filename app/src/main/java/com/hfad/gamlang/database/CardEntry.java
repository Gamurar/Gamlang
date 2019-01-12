package com.hfad.gamlang.database;

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
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @Ignore
    public CardEntry(String word, String translation) {
        this.word = word;
        this.translation = translation;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}