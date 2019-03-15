package com.gamurar.gamlang.ViewModel;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.gamurar.gamlang.Model.CardRepository;
import com.gamurar.gamlang.Model.database.CardEntry;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.AppExecutors;
import com.gamurar.gamlang.utilities.ImagesLoadable;
import com.gamurar.gamlang.utilities.WordInfoLoader;
import com.gamurar.gamlang.views.ImageViewBitmap;

import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class CardCreationViewModel extends AndroidViewModel {
    private static final String TAG = "CardCreationViewModel";

    private CardRepository mRepository;
    private Word mWord;
    private String[] images;
    private String sounds;

    public CardCreationViewModel(@NonNull Application application) {
        super(application);
        mRepository = CardRepository.getInstance(application);
        mRepository.initRemote();
    }

    public void setWord(Word word) {
        mWord = word;
    }

    public Word getWord() {
        return mWord;
    }

    public void gatherWordInfo(WordInfoLoader updatable, ImagesLoadable imagesLoadable) {
        Log.d(TAG, "Word object: " + mWord);
        mRepository.gatherWordInfo(mWord, updatable);
        mRepository.fetchImages(mWord.getName(), imagesLoadable);
    }

    public void saveSound(String url) {
        sounds = mRepository.saveSound(url);
    }

    public void insert(CardEntry cardEntry) {
        mRepository.insertCard(cardEntry, images, sounds);
    }


    public void insert(HashSet<Pair<String, Bitmap>> selectedImages) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            CardEntry newCardEntry = new CardEntry(mWord.getName(), mWord.getTranslation());
            if (selectedImages != null && !selectedImages.isEmpty()) {
                images = mRepository.savePictures(selectedImages);
            }
            if (mWord.hasSoundURL()) {
                sounds = mRepository.saveSound(mWord.getSoundURL());
            }

            mRepository.insertCard(newCardEntry, images, sounds);
            Log.d(TAG, "The word " + newCardEntry.getQuestion() + " has been inserted to the Database");
        });
    }
}
