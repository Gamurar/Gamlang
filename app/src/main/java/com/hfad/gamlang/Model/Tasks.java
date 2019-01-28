package com.hfad.gamlang.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.hfad.gamlang.AddWordsFragment;
import com.hfad.gamlang.Card;
import com.hfad.gamlang.Model.database.CardDao;
import com.hfad.gamlang.Model.database.CardEntry;
import com.hfad.gamlang.Word;
import com.hfad.gamlang.utilities.NetworkUtils;
import com.hfad.gamlang.utilities.PreferencesUtils;
import com.hfad.gamlang.views.ImageViewBitmap;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import androidx.lifecycle.MutableLiveData;

public class Tasks {

    /**
    * Fetch images from local storage and bind them with data from the local database
    */
    public static class createCardsAsyncTask extends AsyncTask<CardEntry, Void, ArrayList<Card>> {

        private static final String TAG = "createCardsAsyncTask";

        /**
         * @param cardEntries array of entries from the database
         * @return            ArrayList of created cards
         */
        @Override
        protected ArrayList<Card> doInBackground(CardEntry... cardEntries) {
            ArrayList<Card> cards = new ArrayList<>();
            ArrayList<Bitmap> images = null;
            String[] fileNames = null;

            for (CardEntry entry : cardEntries) {
                Card card = new Card(entry.getWord(), entry.getTranslation());
                card.setId(entry.getId());

                if (entry.getImage() != null) {
                    images = new ArrayList<>();
                    try {
                        //get images for the card
                        fileNames = entry.getImage().split(" ");

                        for (String fileName : fileNames) {
                            File file = new File(CardRepository.mPicturesDirectory, fileName);
                            Log.d(TAG, "doInBackground: path to the picture: \n"
                                    + file.getAbsolutePath());
                            Bitmap bitmap = Picasso.get().load(file).get();
                            images.add(bitmap);

                            card.setPictures(images);
                            card.setPictureFileNames(fileNames);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                cards.add(card);
            }
            return cards;
        }
    }

    /**
     * Gets the LiveData Word object and updates it to have translation.
     * Translates the word via Glosbe API.
     * <p>
     * While execution finished, call MutableLiveData's postValue method to update the word.
     */
    public static class translateQueryTask extends AsyncTask<MutableLiveData<Word>, Void, Void> {

        private static final String TAG = "TranslateQueryTask";

        private AddWordsFragment fragment;
        private Word word;

        public translateQueryTask(AddWordsFragment addWordsFragment) {
            fragment = addWordsFragment;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragment.onLoadTranslation();
        }

        @Override
        protected Void doInBackground(MutableLiveData<Word>... words) {
            Context context = fragment.getContext();
            word = words[0].getValue();

            String translation = NetworkUtils.translateByGlosbe(
                    word.getName(),
                    context);

            String wordContext = NetworkUtils.contextByGlosbe(
                    word.getName(),
                    context);

            if (translation != null && !translation.isEmpty()) word.setTranslation(translation);
            if (wordContext != null && !wordContext.isEmpty()) word.addContext(wordContext);

            words[0].postValue(word);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            fragment.onLoadTranslationFinished();

            if (!word.isTranslated()) {
                fragment.showTranslationErrorMessage();
                fragment.forbidAddToDict();
            }
        }
    }


    /**
     * Fetch related to the word images from Google Images service by parsing the html page.
     */
    public static class imagesQueryTask extends AsyncTask<String, HashMap<String, Bitmap>, HashMap<String, Bitmap>> {

        private static final String TAG = "imagesQueryTask";
        private final AddWordsFragment addWordsFragment;

        public imagesQueryTask(AddWordsFragment addWordsFragment) {
            this.addWordsFragment = addWordsFragment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            addWordsFragment.onLoadImages();
        }

        @Override
        protected HashMap<String, Bitmap> doInBackground(String... queryWord) {
            String word = queryWord[0];
            if (word == null && TextUtils.isEmpty(word)) {
                return null;
            } else {
                HashMap<String, Bitmap> images = new LinkedHashMap<>();
//            Executor mainThread = AppExecutors.getInstance().mainThread();

                String siteDomain = PreferencesUtils.getSiteDomain(addWordsFragment.getContext());
                ArrayList<String> imgsURL = NetworkUtils.fetchRelatedImagesUrl(word, siteDomain);
                if (imgsURL != null && !imgsURL.isEmpty()) {
                    for (String url : imgsURL) {
                        if (url != null && !TextUtils.isEmpty(url)) {
                            try {
                                Bitmap bitmap = Picasso.get()
                                        .load(url)
                                        .get();
                                String id = url.substring(url.lastIndexOf("tbn:") + 4);

                                images.put(id, bitmap);

                                //mainThread.execute(() -> onProgressUpdate(images));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                return images;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, Bitmap> images) {
            addWordsFragment.onLoadImagesFinished();
            if (images != null && !images.isEmpty()) {
                addWordsFragment.setImages(images);
            } else {
                addWordsFragment.showImagesErrorMessage();
            }
        }
    }

    /**
     * Saves pictures to the local storage.
     */
    public static class savePicturesAsyncTask extends AsyncTask<ImageViewBitmap, Void, String> {

        private static final String TAG = "savePicturesAsyncTask";

        /**
         * @param imageViews to save on the storage
         * @return the files names concatenated to String divided by a space
         */
        @Override
        protected String doInBackground(ImageViewBitmap... imageViews) {
            File mPicturesDirectory = CardRepository.mPicturesDirectory;
            StringBuilder strBuilder = new StringBuilder();
            for (ImageViewBitmap imageView : imageViews) {
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

                    strBuilder.append(fname);
                    strBuilder.append(" ");
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            return strBuilder.toString();
        }
    }


    /**
     * Deletes pictures from the local storage.
     */
    public static class deletePicturesAsyncTask extends AsyncTask<String, Void, Void> {
        private static final String TAG = "DeletePictureAsyncTask";

        @Override
        protected Void doInBackground(String... fileNames) {
            for (String fileName : fileNames) {
                File image = new File(CardRepository.mPicturesDirectory, fileName);
                if (image.delete()) {
                    Log.d(TAG, "The image "
                            + image.getAbsolutePath()
                            + " deleted!");
                } else {
                    Log.d(TAG, "The image "
                            + image.getAbsolutePath()
                            + " didn't delete!");
                }
            }
            return null;
        }
    }

    /**
     * Performs database queries.
     */
    public static class databaseAsyncTask extends AsyncTask<CardEntry, Void, Void> {
        private CardDao cardDao;
        private String task;

        public databaseAsyncTask(CardDao cardDao, String task) {
            this.cardDao = cardDao;
            this.task = task;
        }

        @Override
        protected Void doInBackground(CardEntry... cardEntries) {
            if (CardRepository.INSERT_TASK.equals(task)) {
                cardDao.insertCard(cardEntries[0]);
            }
            if (CardRepository.DELETE_TASK.equals(task)) {
                cardDao.deleteCard(cardEntries[0]);
            }
            if (CardRepository.DELETE_ALL_TASK.equals(task)) {
                cardDao.deleteAllCards();
            }
            return null;
        }
    }

    /**
     * Delete entries from the database by id.
     */
    public static class databaseDeleteByIdAsyncTask extends AsyncTask<Integer, Void, Void> {
        private CardDao cardDao;

        public databaseDeleteByIdAsyncTask(CardDao cardDao) {
            this.cardDao = cardDao;
        }

        @Override
        protected Void doInBackground(Integer... cardIds) {
            cardDao.deleteCardsById(cardIds);
            return null;
        }
    }




}
