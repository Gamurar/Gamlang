package com.gamurar.gamlang.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.gamurar.gamlang.View.AddWordsActivity;
import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.Model.database.CardDao;
import com.gamurar.gamlang.Model.database.CardEntry;
import com.gamurar.gamlang.utilities.AppExecutors;
import com.gamurar.gamlang.utilities.NetworkUtils;
import com.gamurar.gamlang.utilities.PreferencesUtils;
import com.gamurar.gamlang.utilities.ProgressableAdapter;
import com.gamurar.gamlang.utilities.WordContext;
import com.gamurar.gamlang.utilities.WordTranslation;
import com.gamurar.gamlang.views.ImageViewBitmap;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.Executor;

public class Tasks {

    /**
     * Fetch images from local storage and bind them with data from the local database
     */
    public static class createCardsAsyncTask extends AsyncTask<CardEntry, Void, ArrayList<Card>> {

        private static final String TAG = "createCardsAsyncTask";

        /**
         * @param cardEntries array of entries from the database
         * @return ArrayList of created cards
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
                            File file = new File(CardRepository.picturesDirectory, fileName);
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

                if (entry.getPronunciation() != null) {
                    try {
                        String fileName = entry.getPronunciation()
                                .substring(NetworkUtils.ABBYYsoundBaseUrl.length());
                        String filePath = CardRepository.musicDirectory + "/" + fileName;
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(filePath);
                        mediaPlayer.prepare();
                        card.setPronunciation(mediaPlayer, filePath);
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
    public static class translateQueryTask extends AsyncTask<String, Void, Pair<String, ArrayList<String[]>>> {

        private static final String TAG = "TranslateQueryTask";

        private WordTranslation fragment;
        private String fromLang;
        private String toLang;

        public translateQueryTask(WordTranslation addWordsFragment, String fromLang, String toLang) {
            fragment = addWordsFragment;
            this.fromLang = fromLang;
            this.toLang = toLang;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragment.onLoadTranslation();
        }

        @Override
        protected Pair<String, ArrayList<String[]>> doInBackground(String... words) {
            String fromLang = "en";
            String toLang = "ru";
            String word = words[0];

            String translation = NetworkUtils.translateByGlosbe(
                    word,
                    fromLang, toLang);

            ArrayList<String[]> wordContext = NetworkUtils.contextByGlosbe(
                    word,
                    fromLang, toLang);

            Pair<String, ArrayList<String[]>> result = new Pair<>(translation, wordContext);

            return result;
        }

        @Override
        protected void onPostExecute(Pair<String, ArrayList<String[]>> result) {
            String translation = result.first;
            ArrayList<String[]> context = result.second;
            fragment.onLoadTranslationFinished();
            if (translation != null && !translation.isEmpty()) {
                fragment.setTranslation(translation);
            } else {
                fragment.showTranslationErrorMessage();
            }

            if (context != null && !context.isEmpty() && fragment instanceof WordContext) {
                ((WordContext) fragment).setContext(context);
            }
        }
    }

    public static class soundQueryAsyncTask extends AsyncTask<String, Void, String> {
        private static final String TAG = "soundQueryAsyncTask";

        private final String soundBaseURL
                = "https://api.lingvolive.com/sounds?uri=LingvoUniversal%20(En-Ru)%2F";
        private AddWordsActivity addWordsActivity;

        public soundQueryAsyncTask(AddWordsActivity addWordsActivity) {
            this.addWordsActivity = addWordsActivity;
        }

        @Override
        protected String doInBackground(String... strings) {
            String word = strings[0];
            try {
                String authToken = NetworkUtils.getABBYYAuthToken();
                String JSON = NetworkUtils.fetchABBYYMinicardJSON(authToken, word);
                String fileName = NetworkUtils.getSoundFromJSON(JSON);

                return soundBaseURL + fileName;

            } catch (IOException | JSONException e) {
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String soundUrl) {
            if (soundUrl != null && !soundUrl.isEmpty()) {
                addWordsActivity.setSound(soundUrl);
            } else {
                addWordsActivity.hidePronunciation();
            }
        }
    }


    /**
     * Fetch related to the word images from Google Images service by parsing the html page.
     */
    public static class imagesQueryTask extends AsyncTask<String, Pair<String, Bitmap>, HashMap<String, Bitmap>> {

        private static final String TAG = "imagesQueryTask";
        private final AddWordsActivity addWordsActivity;

        public imagesQueryTask(AddWordsActivity addWordsActivity) {
            this.addWordsActivity = addWordsActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            addWordsActivity.onLoadImages();
        }

        @Override
        protected HashMap<String, Bitmap> doInBackground(String... queryWord) {
            String word = queryWord[0];
            if (word == null && TextUtils.isEmpty(word)) {
                return null;
            } else {
                HashMap<String, Bitmap> images = new LinkedHashMap<>();
                Executor mainThread = AppExecutors.getInstance().mainThread();

                String siteDomain = PreferencesUtils.getSiteDomain(addWordsActivity);
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
                                Pair<String, Bitmap> image = new Pair<>(id, bitmap);

                                mainThread.execute(() -> onProgressUpdate(image));
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
        protected void onProgressUpdate(Pair<String, Bitmap>... images) {
            addWordsActivity.addImage(images[0]);
        }

        @Override
        protected void onPostExecute(HashMap<String, Bitmap> images) {
            addWordsActivity.onLoadImagesFinished();
            if (images != null && !images.isEmpty()) {
                //addWordsActivity.setImages(images);
            } else {
                addWordsActivity.showImagesErrorMessage();
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
            File mPicturesDirectory = CardRepository.picturesDirectory;
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

    public static class savePronunciationAsyncTask extends AsyncTask<String, Void, String> {
        private static final String TAG = "savePronunciationAsyncT";

        /**
         * @return saved sound file name
         */
        @Override
        protected String doInBackground(String... fileUrls) {
            try {
                String fileUrl = fileUrls[0];
                String fileName = fileUrl.substring(NetworkUtils.ABBYYsoundBaseUrl.length());

                URL sourceUrl = new URL(fileUrl);
                HttpURLConnection con = (HttpURLConnection) sourceUrl.openConnection();
                File newFile = new File(CardRepository.musicDirectory, fileName);
                Log.d(TAG, "sound file path: " + newFile.getAbsolutePath());

                FileOutputStream out = new FileOutputStream(newFile);//Get OutputStream for NewFile Location

                InputStream is = con.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len = 0;//init length
                while ((len = is.read(buffer)) != -1) {
                    out.write(buffer, 0, len);//Write new file
                }

                //Close all connection after doing task
                out.flush();
                out.close();
                is.close();
                return fileName;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

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
                File image = new File(CardRepository.picturesDirectory, fileName);
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

    public static class deleteSoundAsyncTask extends AsyncTask<String, Void, Void> {

        private static final String TAG = "deleteSoundAsyncTask";

        @Override
        protected Void doInBackground(String... strings) {
            String filePath = strings[0];
            File sound = new File(filePath);
            if (sound.delete()) {
                Log.d(TAG, "The sound "
                        + sound.getAbsolutePath()
                        + " deleted!");
            } else {
                Log.d(TAG, "The sound "
                        + sound.getAbsolutePath()
                        + " didn't delete!");
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

    public static class loadSuggestionCards extends AsyncTask<String, Pair, Void> {
        private ProgressableAdapter mAdapter;
        private String fromLang;
        private String toLang ;

        public loadSuggestionCards(ProgressableAdapter adapter, String fromLang, String toLang) {
            mAdapter = adapter;
            this.fromLang = fromLang;
            this.toLang = toLang;
        }

        @Override
        protected Void doInBackground(String... words) {
            for (String word : words) {
                String translation = NetworkUtils.translateByGlosbe(word, fromLang, toLang);
                if (translation != null && !translation.isEmpty()) {
                    Pair<String, String> pair = new Pair<>(word, translation);
                    publishProgress(pair);
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Pair... values) {
            super.onProgressUpdate(values);
            mAdapter.insert(values[0]);
        }
    }


}