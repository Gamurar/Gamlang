package com.hfad.gamlang.utilities;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.hfad.gamlang.Card;
import com.hfad.gamlang.LearnWordsFragment;
import com.hfad.gamlang.database.AppDatabase;
import com.hfad.gamlang.database.CardEntry;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class LearnWordsViewModel extends AndroidViewModel {
    private static final String TAG = "LearnWordsViewModel";
    private Context mContext;

    private LiveData<List<CardEntry>> cards;
    private static int mCurrentCardId = 0;
    private int cardsCount;

    public LearnWordsViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the cards from the DataBase");
        cards = db.cardDao().loadAllCards();
        mContext = application.getApplicationContext();
    }

    public LiveData<List<CardEntry>> getCards() {
        return cards;
    }

//    public ArrayList<Card> getCardsFromEntries(List<CardEntry> cardEntries) {
//        ArrayList<Card> cards = new ArrayList<>();
//        File[] myDirs = mContext.getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
//        File root = myDirs.length > 1 ? myDirs[1] : myDirs[0];
//
//
//        for (CardEntry entry : cardEntries) {
//            Card card;
//            if (entry.getImage() != null) {
//                ArrayList<Bitmap> images = new ArrayList<>();
//                try {
//                    //get images for the card
//                    String[] fileNames = entry.getImage().split(" ");
//
//                    for (String fileName : fileNames) {
//                        File file = new File(root, fileName);
//                        Bitmap bitmap = Picasso.get().load(file).get();
//                        images.add(bitmap);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (!images.isEmpty()) {
//                    card = new Card(entry.getWord(), entry.getTranslation(), images);
//                } else {
//                    card = new Card(entry.getWord(), entry.getTranslation());
//                }
//            } else {
//                card = new Card(entry.getWord(), entry.getTranslation());
//            }
//
//            cards.add(card);
//        }
//
//
//        return cards;
//    }
}
