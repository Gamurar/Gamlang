package com.hfad.gamlang.utilities;

import com.hfad.gamlang.Card;

import java.util.ArrayList;
import java.util.List;

public class LearnUtils {
    public static List<Card> reverseCards(List<Card> cards) {
        List<Card> reversedCards = new ArrayList<>();
        for (Card card : cards) {
            Card newCard = new Card(card);
            newCard.setQuestion(card.getAnswer());
            newCard.setAnswer(card.getQuestion());
            reversedCards.add(newCard);
        }

        return reversedCards;
    }
}
