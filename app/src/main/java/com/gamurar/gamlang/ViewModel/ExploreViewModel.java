package com.gamurar.gamlang.ViewModel;

import android.content.Context;

import com.gamurar.gamlang.Model.CardRepository;
import com.gamurar.gamlang.utilities.ProgressableAdapter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ExploreViewModel extends ViewModel {
    private CardRepository mRepository;
    private LiveData<String[]> mOpenSearchWords;
    private boolean mReversed;

    public ExploreViewModel(Context context) {
        mRepository = new CardRepository(context);
        mRepository.initRemote();
        mOpenSearchWords = mRepository.getOpenSearchLiveData();
    }

    public void queryOpenSearch(String query) {
        mRepository.openSearch(query);
    }

    public LiveData<String[]> getOpenSearchWords() {
        return mOpenSearchWords;
    }

    public void loadSearchCards(String[] words, ProgressableAdapter adapter) {
        mRepository.loadSuggestionCards(words, adapter);
    }

    public void reverseSearchLang() { mRepository.reverseSearchLang(); }

    public boolean isReversed() {
        return mRepository.isReversed();
    }

}
