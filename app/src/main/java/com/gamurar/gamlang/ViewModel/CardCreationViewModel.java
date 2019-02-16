package com.gamurar.gamlang.ViewModel;

import android.app.Application;
import android.util.Log;

import com.gamurar.gamlang.Model.CardRepository;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.Updatable;

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

    public void gatherWordInfo(Updatable updatable) {
        Log.d(TAG, "Word object: " + mWord);
        mRepository.gatherWordInfo(mWord, updatable);
    }
}
