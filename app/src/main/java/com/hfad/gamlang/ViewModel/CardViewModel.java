package com.hfad.gamlang.ViewModel;

import android.app.Application;

import com.hfad.gamlang.Card;
import com.hfad.gamlang.Model.CardRepository;
import com.hfad.gamlang.Model.database.CardEntry;

import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class CardViewModel extends AndroidViewModel {
    private CardRepository repository;
    private LiveData<List<Card>> cards;

    public CardViewModel(@NonNull Application application) {
        super(application);
        repository = new CardRepository(application);
        cards = repository.getAllCards();
    }

    public void insert(CardEntry cardEntry) {
        repository.insert(cardEntry);
    }

    public void delete(CardEntry cardEntry) {
        repository.delete(cardEntry);
    }

    public void delete(HashSet<Card> cards) { repository.delete(cards); }

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
        return cards;
    }
}
