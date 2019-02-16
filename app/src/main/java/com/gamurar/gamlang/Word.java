package com.gamurar.gamlang;

import android.media.MediaPlayer;
import android.util.Log;

import java.util.ArrayList;

public class Word {
    private static final String TAG = "Word";
    private String name;
    private String translation;
    private String IPA;
    private ArrayList<String> translations;
    public ArrayList<String> context;
    private MediaPlayer pronunciation;
    private String soundURL;

    public Word() {}

    public Word(String name) {
        this.name = name.toLowerCase();
    }

    public Word(String word, String translation) {
        this.name = word;
        this.translation = translation;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public void setTranslation(String translation) {
        if (translation != null && !translation.isEmpty()) {
            this.translation = translation;
        } else {
            Log.e(TAG, "setTranslation: Wrong translation!");
        }

    }

    public String getName() {
        return this.name;
    }

    public String getTranslation() {
        if (this.translation != null && !this.translation.isEmpty()) {
            return this.translation;
        } else {
            return "There is no translation.";
        }
    }

    public void addContext(String context) {
        if (this.context == null) this.context = new ArrayList<>();
        this.context.add(context);
    }

    public ArrayList<String> getContext() {
        if (context != null && !context.isEmpty()) {
            return context;
        } else {
            return null;
        }
    }

    public boolean isTranslated() {
        return getTranslation() != null && !getTranslation().isEmpty();
    }

    public MediaPlayer getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String soundURL) {
        this.soundURL = soundURL;
    }

    public void setPronunciation(MediaPlayer pronunciation, String soundURL) {
        if (this.pronunciation != null) {
            this.pronunciation.release();
            this.pronunciation = null;
        }
        this.pronunciation = pronunciation;
        this.soundURL = soundURL;
    }

    public String getSoundURL() {
        return soundURL;
    }

    public boolean hasSoundURL() {
        return soundURL != null && !soundURL.isEmpty();
    }

    public void pronounce() {
        if (pronunciation != null) {
            pronunciation.start();
        }
    }

    public String getIPA() {
        return IPA;
    }

    public void setIPA(String IPA) {
        this.IPA = IPA;
    }

    public void onUpdate() {

    }
}
