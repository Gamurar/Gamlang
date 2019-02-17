package com.gamurar.gamlang.ViewModel;

import android.app.Application;
import android.util.Log;

import com.gamurar.gamlang.Model.CardRepository;
import com.gamurar.gamlang.Model.database.CardEntry;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.ImagesLoadable;
import com.gamurar.gamlang.utilities.Updatable;
import com.gamurar.gamlang.views.ImageViewBitmap;

import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class CardCreationViewModel extends AndroidViewModel {
    private static final String TAG = "CardCreationViewModel";

    CardRepository mRepository;
    Word mWord;

    public CardCreationViewModel(@NonNull Application application) {
        super(application);
        mRepository = new CardRepository(application);
        mRepository.initRemote();
        mRepository.initLocal();
    }

    public void setWord(Word word) {
        mWord = word;
    }

    public Word getWord() {
        return mWord;
    }

    public void gatherWordInfo(Updatable updatable, ImagesLoadable imagesLoadable) {
        Log.d(TAG, "Word object: " + mWord);
        mRepository.gatherWordInfo(mWord, updatable);
        mRepository.fetchImages(mWord.getName(), imagesLoadable);
    }

    public void saveSound(String url) {
        mRepository.saveSound(url);
    }

    public void insert(CardEntry cardEntry) {
        mRepository.insert(cardEntry);
    }

    public String savePictures(HashSet<ImageViewBitmap> imageViews) {
        return mRepository.savePictures(imageViews);
    }
}
