package com.gamurar.gamlang.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.gamurar.gamlang.Model.CardRepository;
import com.gamurar.gamlang.View.CardCreationActivity;
import com.gamurar.gamlang.View.ExploreFragment;
import com.gamurar.gamlang.utilities.ProgressableAdapter;
import com.gamurar.gamlang.utilities.SuggestionAdapter;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ExploreViewModel extends AndroidViewModel {
    private CardRepository mRepository;

    public ExploreViewModel(Application application) {
        super(application);
        mRepository = new CardRepository(application);
        mRepository.initRemote();
    }

    public void queryOpenSearch(String query) {
        if (!query.isEmpty()) {
            CardRepository.openSearch(query);
        }
    }

    public void initOpenSearch(SuggestionAdapter adapter, boolean isReversed) {
        mRepository.initOpenSearch(adapter);
        mRepository.setReversed(isReversed);
    }

    public void loadSearchCards(String[] words) {
        CardRepository.loadSuggestionCards(words);
    }

    public void reverseSearchLang() { mRepository.reverseSearchLang(); }

    public boolean isReversed() {
        return mRepository.isReversed();
    }

}
