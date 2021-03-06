package com.gamurar.gamlang;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;

import com.gamurar.gamlang.utilities.LearnUtils;

import java.util.ArrayList;
import java.util.Date;

public class Card implements Parcelable {
    private int id;
    private String question;
    private String answer;
    private ArrayList<Bitmap> pictures;
    private ArrayList<String> pictureFileNames;
    private MediaPlayer pronunciation;
    private String soundFileName;
    private Date lastReview;
    private Date nextReview;

    public Card(int id, String question, String answer, Date lastReview, Date nextReview) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.lastReview = lastReview;
        this.nextReview = nextReview;
    }

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

    protected Card(Parcel in) {
        id = in.readInt();
        question = in.readString();
        answer = in.readString();
        pictures = in.createTypedArrayList(Bitmap.CREATOR);
        pictureFileNames = in.createStringArrayList();
        soundFileName = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

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

    public void addPicture(Bitmap picture) {
        if (this.pictures == null) {
            this.pictures = new ArrayList<>();
        }
        this.pictures.add(picture);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getPictureFileNames() {
        if (!hasPictureFileNames()) return null;
        return pictureFileNames.toArray(new String[0]);
    }

    private boolean hasPictureFileNames() {
        return pictureFileNames != null && !pictureFileNames.isEmpty();
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

    public Date getNextReview() {
        return nextReview;
    }

    public void setNextReview(Date nextReview) {
        this.nextReview = nextReview;
    }

    public Date getLastReview() {
        return lastReview;
    }

    public void setLastReview(Date lastReview) {
        this.lastReview = lastReview;
    }

    public int getStage() {
        return LearnUtils.getReviewStage(lastReview, nextReview);
    }

    @Override
    protected void finalize() throws Throwable {
        if (pronunciation != null) {
            pronunciation.release();
            pronunciation = null;
        }
        super.finalize();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(question);
        dest.writeString(answer);
        if (pictures.size() > 1) {
            ArrayList<Bitmap> image = new ArrayList<>();
            image.add(pictures.get(0));
            dest.writeTypedList(image);
        } else {
            dest.writeTypedList(pictures);
        }

        dest.writeStringList(pictureFileNames);
        dest.writeString(soundFileName);
    }
}
