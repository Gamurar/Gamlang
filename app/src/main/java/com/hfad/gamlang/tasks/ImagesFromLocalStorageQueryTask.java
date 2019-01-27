package com.hfad.gamlang.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.hfad.gamlang.Card;
import com.hfad.gamlang.Model.database.CardEntry;
import com.hfad.gamlang.utilities.StorageHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImagesFromLocalStorageQueryTask extends AsyncTask<CardEntry, ArrayList<Card>, ArrayList<Card>> {

    @Override
    protected ArrayList<Card> doInBackground(CardEntry... cardEntries) {
        ArrayList<Card> cards = new ArrayList<>();
        File mPicturesDirectory = StorageHelper.mPicturesDirectory;

        for (CardEntry entry : cardEntries) {
            Card card;
            if (entry.getImage() != null) {
                ArrayList<Bitmap> images = new ArrayList<>();
                try {
                    //get images for the card
                    String[] fileNames = entry.getImage().split(" ");

                    for (String fileName : fileNames) {
                        File file = new File(mPicturesDirectory, fileName);
                        Bitmap bitmap = Picasso.get().load(file).get();
                        images.add(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!images.isEmpty()) {
                    card = new Card(entry.getWord(), entry.getTranslation(), images);
                } else {
                    card = new Card(entry.getWord(), entry.getTranslation());
                }
            } else {
                card = new Card(entry.getWord(), entry.getTranslation());
            }

            cards.add(card);
        }


        return cards;
    }
//

    @Override
    protected void onPostExecute(ArrayList<Card> cards) {
        super.onPostExecute(cards);
    }
}