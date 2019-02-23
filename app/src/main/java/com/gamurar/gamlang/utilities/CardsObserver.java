package com.gamurar.gamlang.utilities;
import com.gamurar.gamlang.Card;

import java.util.List;

import androidx.lifecycle.Observer;

public class CardsObserver implements Observer<List<Card>> {
    private List<Card> mCards;

    @Override
    public void onChanged(List<Card> cards) {
        mCards = cards;
    }

    public List<Card> getCards() {
        return mCards;
    }
}
