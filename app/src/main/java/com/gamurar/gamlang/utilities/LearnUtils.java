package com.gamurar.gamlang.utilities;

import android.util.Log;

import com.gamurar.gamlang.Card;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LearnUtils {

    public final static long[] INTERVALS = new long[] {
            TimeUnit.SECONDS.toMillis(5),
            TimeUnit.SECONDS.toMillis(25),
            TimeUnit.MINUTES.toMillis(2),
            TimeUnit.MINUTES.toMillis(10),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.HOURS.toMillis(5),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.DAYS.toMillis(5),
            TimeUnit.DAYS.toMillis(25),
            TimeUnit.DAYS.toMillis(120)
    };

    public static List<Card> reverseCards(List<Card> cards) {
        Date date = new Date();


        List<Card> reversedCards = new ArrayList<>();
        for (Card card : cards) {
            Card newCard = new Card(card);
            newCard.setQuestion(card.getAnswer());
            newCard.setAnswer(card.getQuestion());
            reversedCards.add(newCard);
        }

        return reversedCards;
    }

    public static void remember(Card card) {
        int stage = getReviewStage(card.getLastReview(), card.getNextReview());
        Date now = new Date();
        long nextReview = INTERVALS[++stage] + now.getTime();
        card.setLastReview(now);
        card.setNextReview(new Date(nextReview));
    }

    public static void dontRemember(Card card) {
        int stage = getReviewStage(card.getLastReview(), card.getNextReview());
        if (stage == 0) {
            return;
        }
        Date now = new Date();
        long nextReview = INTERVALS[--stage] + now.getTime();
        card.setLastReview(now);
        card.setNextReview(new Date(nextReview));
    }

    public static int getReviewStage(Date lastReview, Date nextReview) {
        if (lastReview == null && nextReview == null) return 0;
        long last = lastReview.getTime();
        long next = nextReview.getTime();
        long diff = next - last;
        for (int i = 0; i < INTERVALS.length; i++) {
            if (INTERVALS[i] == diff) return i;
        }

        return -1;
    }


}
