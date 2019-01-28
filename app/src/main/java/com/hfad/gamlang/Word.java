package com.hfad.gamlang;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.hfad.gamlang.utilities.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Word {
    private static final String TAG = "Word";
    private String name;
    private String soundURL;
    private final String soundBaseURL
            = "https://api.lingvolive.com/sounds?uri=LingvoUniversal%20(En-Ru)%2F";
    private ArrayList<Translation> translations = new ArrayList<>();
    public ArrayList<String> context = new ArrayList<>();

    public Word(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSound(String fileName) {
        this.soundURL = soundBaseURL + fileName;
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

    String getSoundURL() {
        if (this.soundURL != null) {
            return this.soundURL;
        } else {
            return "There is no pronunciation.";
        }
    }

    public void playPronunc() {
        if (soundURL == null) {
            Log.e(TAG, "There is no sound to play.");
            return;
        }
        Log.i(TAG, "Pronunciation url: " + soundURL);
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(soundURL);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
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
