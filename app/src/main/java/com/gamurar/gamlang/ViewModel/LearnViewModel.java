package com.gamurar.gamlang.ViewModel;

import android.app.Application;

import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.Model.CardRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class LearnViewModel extends AndroidViewModel {
    private CardRepository mRepository;
    private LiveData<List<Card>> mCards;


    public LearnViewModel(@NonNull Application application) {
        super(application);
        mRepository = CardRepository.getInstance(application);
        mRepository.initLocal();
        mCards = mRepository.getLiveCards();
    }

    public LiveData<List<Card>> getCards() {
        return mCards;
    }


    public void updateCardReview(Card card) {
        mRepository.updateCardReview(card.getId(), card.getLastReview(), card.getNextReview());
    }
}
