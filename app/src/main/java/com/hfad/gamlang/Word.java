package com.hfad.gamlang;

import android.media.MediaPlayer;

import com.hfad.gamlang.utilities.Pair;

import java.util.ArrayList;
import java.util.List;

public class Word {
    private static final String TAG = "Word";
    private String name;
    private ArrayList<Translation> translations = new ArrayList<>();
    public ArrayList<String> context = new ArrayList<>();
    private MediaPlayer pronunciation;
    private String soundURL;

    public Word(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTranslation(String translation) {
        if (this.translations == null) this.translations = new ArrayList<>();
        this.translations.add(new Translation(translation));
    }

    public String getName() {
        return this.name;
    }

    public String getTranslation() {
        if (this.translations != null && !this.translations.isEmpty()) {
            return this.translations.get(0).transVariants.get(0);
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


    public static class Translation {
        public ArrayList<String> transVariants
                = new ArrayList<>();
        public String partOfSpeech;
        public String fullPartOfSpeech;
        public List<Pair<String, String>> contexts
                = new ArrayList<Pair<String, String>>();

        public Translation(String translation) {
            this.transVariants.add(translation);
        }

        public void addContextPair(String origContext, String transContext) {
            Pair<String, String> pair = new Pair(origContext, transContext);
            contexts.add(pair);
        }
    }
}
