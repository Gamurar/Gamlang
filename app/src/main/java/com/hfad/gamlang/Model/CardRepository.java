package com.hfad.gamlang.Model;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.hfad.gamlang.Card;
import com.hfad.gamlang.Model.database.AppDatabase;
import com.hfad.gamlang.Model.database.CardDao;
import com.hfad.gamlang.Model.database.CardEntry;
import com.hfad.gamlang.utilities.StorageHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class CardRepository {
    private static final String TAG = "CardRepository";

    private static final String INSERT_TASK = "insert_task";
    private static final String DELETE_TASK = "delete_task";
    private static final String DELETE_ALL_TASK = "delete_all_task";

    private static File mPicturesDirectory;


    private CardDao cardDao;
    private LiveData<List<CardEntry>> mCardEntries;
    private LiveData<List<Card>> cards;

    public CardRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        cardDao = db.cardDao();
        mCardEntries = cardDao.loadAllCards();
        cards = Transformations.map(mCardEntries, cardEntries -> {
            CardEntry[] entries = new CardEntry[cardEntries.size()];
            List<Card> cards = null;
            try {
                cards = new fetchImagesFromLocalStorageAsyncTask()
                        .execute(cardEntries.toArray(entries))
                        .get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return cards;
        });

        File[] myDirs = application.getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        mPicturesDirectory = myDirs.length > 1 ? myDirs[1] : myDirs[0];
    }

    public LiveData<List<CardEntry>> getCardEntries() {
        return mCardEntries;
    }

    public LiveData<List<Card>> getAllCards() {
        return cards;
    }

    public void insert(CardEntry cardEntry) {
        new DatabaseAsyncTask(cardDao, INSERT_TASK).execute(cardEntry);
    }

    public void delete(CardEntry cardEntry) {
        new DatabaseAsyncTask(cardDao, DELETE_TASK).execute(cardEntry);
    }

    public void deleteById(Integer[] cardIds) {
        new DatabaseDeleteByIdAsyncTask(cardDao).execute(cardIds);
    }

    public void deleteById(int cardId) {
        new DatabaseDeleteByIdAsyncTask(cardDao).execute(cardId);
    }

    public void deleteAllCards() {
        new DatabaseAsyncTask(cardDao, DELETE_ALL_TASK).execute();
    }

    public List<Card> getCards(List<CardEntry> cardEntries) {
        CardEntry[] entries = new CardEntry[cardEntries.size()];
        List<Card> cards = null;
        try {
            cards = new fetchImagesFromLocalStorageAsyncTask()
                    .execute(cardEntries.toArray(entries))
                    .get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return cards;
    }

    private static class DatabaseAsyncTask extends AsyncTask<CardEntry, Void, Void> {
        private CardDao cardDao;
        private String task;

        private DatabaseAsyncTask(CardDao cardDao, String task) {
            this.cardDao = cardDao;
            this.task = task;
        }

        @Override
        protected Void doInBackground(CardEntry... cardEntries) {
            if (INSERT_TASK.equals(task)) {
                cardDao.insertCard(cardEntries[0]);
            }
            if (DELETE_TASK.equals(task)) {
                cardDao.deleteCard(cardEntries[0]);
            }
            if (DELETE_ALL_TASK.equals(task)) {
                cardDao.deleteAllCards();
            }
            return null;
        }
    }

    private static class DatabaseDeleteByIdAsyncTask extends AsyncTask<Integer, Void, Void> {
        private CardDao cardDao;

        private DatabaseDeleteByIdAsyncTask(CardDao cardDao) {
            this.cardDao = cardDao;
        }

        @Override
        protected Void doInBackground(Integer... cardIds) {
            cardDao.deleteCardsById(cardIds);
            return null;
        }
    }

    private static class fetchImagesFromLocalStorageAsyncTask extends AsyncTask<CardEntry, ArrayList<Card>, ArrayList<Card>> {

        @Override
        protected ArrayList<Card> doInBackground(CardEntry... cardEntries) {
            ArrayList<Card> cards = new ArrayList<>();
            ArrayList<Bitmap> images = null;

            for (CardEntry entry : cardEntries) {
                if (entry.getImage() != null) {
                    images = new ArrayList<>();
                    try {
                        //get images for the card
                        String[] fileNames = entry.getImage().split(" ");

                        for (String fileName : fileNames) {
                            File file = new File(mPicturesDirectory, fileName);
                            Log.d(TAG, "doInBackground: path to the picture: \n"
                            + file.getAbsolutePath());
                            Bitmap bitmap = Picasso.get().load(file).get();
                            images.add(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Card card = new Card(entry.getWord(), entry.getTranslation());
                card.setId(entry.getId());
                if (images != null && !images.isEmpty()) card.setPictures(images);

                cards.add(card);
            }
            return cards;
        }
    }
}
