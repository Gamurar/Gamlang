package com.hfad.gamlang.utilities;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.hfad.gamlang.Card;
import com.hfad.gamlang.MainActivity;
import com.hfad.gamlang.MyDictionaryFragment;
import com.hfad.gamlang.database.CardEntry;
import com.hfad.gamlang.views.ImageViewBitmap;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageHelper {

    private static final String TAG = "StorageHelper";

    private Context mContext;
    public static File mPicturesDirectory;

    public StorageHelper(Context context) {
        mContext = context;

        File[] myDirs = mContext.getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        mPicturesDirectory = myDirs.length > 1 ? myDirs[1] : myDirs[0];
    }

    /**
     * @param imageView to save on the storage
     * @return the file name
     */
    public String saveImage(ImageViewBitmap imageView) {
        Bitmap finalBitmap = imageView.getBitmap();

        if (!mPicturesDirectory.exists()) {
            if (!mPicturesDirectory.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }
        }
        String fname = imageView.getCode() + ".jpg";
        File file = new File(mPicturesDirectory, fname);
        Log.d(TAG, "saveImage: file path: " + file.getAbsolutePath());
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fname;
    }

//    public ArrayList<Card> getCardsFromEntries(List<CardEntry> cardEntries) {
//        ArrayList<Card> cards = new ArrayList<>();
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
//                        File file = new File(mPicturesDirectory, fileName);
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
