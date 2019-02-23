package com.gamurar.gamlang.utilities;

import android.util.Log;

import com.gamurar.gamlang.Card;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LearnUtils {

    private static final String TAG = "LearnUtils";

    private final static long[] INTERVALS = new long[] {
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

    public static void remember(Card card) {
        int stage = getReviewStage(card.getLastReview(), card.getNextReview());
        Date now = new Date();
        long interval = stage < 9 ? INTERVALS[++stage] : INTERVALS[stage];
        long nextReview = now.getTime() + interval;
        card.setLastReview(now);
        card.setNextReview(new Date(nextReview));
    }

    public static void dontRemember(Card card) {
        int stage = getReviewStage(card.getLastReview(), card.getNextReview());
        Date now = new Date();
        long interval = stage > 0 ? INTERVALS[--stage] : INTERVALS[0];
        long nextReview = now.getTime() + interval;
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

    /**
     * @param allCards all cards from the app database
     * @return cards for review today
     * */
    public static List<Card> getTodayCards(List<Card> allCards) {
        List<Card> todayCards = new ArrayList<>();
        for (Card card : allCards) {
            if (card.getNextReview().compareTo(new Date()) < 0) {
                todayCards.add(card);
            }
        }
        return todayCards;
    }


}
