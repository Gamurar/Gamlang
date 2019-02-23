package com.gamurar.gamlang.Model.database;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "card")
public class CardEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String question;
    private String answer;
    private Date created;

    @ColumnInfo(name="last_review")
    private Date lastReview;

    @ColumnInfo(name="next_review")
    private Date nextReview;

    @Ignore
    public CardEntry(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.created = new Date();
        this.lastReview = new Date();
        this.nextReview = new Date();
    }

    public CardEntry(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.created = new Date();
        this.lastReview = new Date();
        this.nextReview = new Date();
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

    public Date getLastReview() {
        return lastReview;
    }

    public void setLastReview(Date lastReview) {
        this.lastReview = lastReview;
    }

    public Date getNextReview() {
        return nextReview;
    }

    public void setNextReview(Date nextReview) {
        this.nextReview = nextReview;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}