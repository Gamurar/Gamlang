package com.gamurar.gamlang.ViewModel;

import android.app.Application;

import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.View.ExploreActivity;
import com.gamurar.gamlang.Model.CardRepository;
import com.gamurar.gamlang.Model.database.CardEntry;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.WordTranslation;

import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class CardViewModel extends AndroidViewModel {
    private CardRepository repository;
    private LiveData<List<Card>> mCards;
    private Word mWord;

    public CardViewModel(@NonNull Application application) {
        super(application);
        repository = CardRepository.getInstance(application);
        repository.initLocal();
        mCards = repository.getLiveCards();
    }

    public void delete(CardEntry cardEntry) {
        repository.deleteCard(cardEntry);
    }

    public void delete(HashSet<com.gamurar.gamlang.Card> cards) { repository.deleteCard(cards); }

    public void deleteById(Integer[] cardIds) {
        repository.deleteById(cardIds);
    }

    public void deleteById(int cardId) {
        repository.deleteById(cardId);
    }

    public void deleteAllCards() {
        repository.deleteAllCards();
    }

    public LiveData<List<Card>> getAllCards() {
        return mCards;
    }

    public void translateWord(ExploreActivity fragment, String word) {
        repository.translateWord(fragment, word);
    }

    public void translateWordOnly(WordTranslation fragment, String word) {
        repository.translateWordOnly(fragment, word);
    }

    public void setWord(Word word) {
        mWord = word;
    }

    public Word getWord() {
        return mWord;
    }
}
