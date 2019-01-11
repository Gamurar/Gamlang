package com.hfad.selectionmenu;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.util.Log;

import com.hfad.selectionmenu.utilities.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Word {
    private static final String TAG = "Word";
    public String name;
    public String soundURL;
    private final String soundBaseURL
            = "https://api.lingvolive.com/sounds?uri=LingvoUniversal%20(En-Ru)%2F";
    public ArrayList<Translation> translations
            = new ArrayList<>();

    public Word(String name) {
        this.name = name;
    }

    void setName(String name) {
        this.name = name;
    }

    public void setSound(String fileName) {
        this.soundURL = soundBaseURL + fileName;
    }

    public void setTranslation(String translation) {
        this.translations.add(new Translation(translation));
    }

    String getName() {
        return this.name;
    }

    String getTranslation() {
        if (this.translations != null) {
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

    void playPronunc() {
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
