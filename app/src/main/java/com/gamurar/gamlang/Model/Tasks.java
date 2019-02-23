package com.gamurar.gamlang.Model;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.Model.database.CardEntry;
import com.gamurar.gamlang.Model.database.ImageDao;
import com.gamurar.gamlang.Model.database.ImageEntry;
import com.gamurar.gamlang.Model.database.SoundDao;
import com.gamurar.gamlang.Model.database.SoundEntry;
import com.gamurar.gamlang.View.ExploreActivity;
import com.gamurar.gamlang.Model.database.CardDao;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.AppExecutors;
import com.gamurar.gamlang.utilities.ImagesLoadable;
import com.gamurar.gamlang.utilities.LiveSearchHelper;
import com.gamurar.gamlang.utilities.NetworkUtils;
import com.gamurar.gamlang.utilities.ProgressableAdapter;
import com.gamurar.gamlang.utilities.WordInfoLoader;
import com.gamurar.gamlang.utilities.WordContext;
import com.gamurar.gamlang.utilities.WordTranslation;
import com.gamurar.gamlang.views.ImageViewBitmap;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Tasks {

    /**
     * Fetch images from local storage and bind them with data from the local database
     */
    public static class createCardsAsyncTask extends AsyncTask<CardEntry, Void, ArrayList<Card>> {

        private static final String TAG = "createCardsAsyncTask";

        private CardDao cardDao;
        private ImageDao imageDao;
        private SoundDao soundDao;

        public createCardsAsyncTask(CardDao cardDao, ImageDao imageDao, SoundDao soundDao) {
            this.cardDao = cardDao;
            this.imageDao = imageDao;
            this.soundDao = soundDao;
        }

        /**
         * @param cardEntries array of entries from the database
         * @return ArrayList of created cards
         */
        @Override
        protected ArrayList<com.gamurar.gamlang.Card> doInBackground(CardEntry... cardEntries) {
            ArrayList<com.gamurar.gamlang.Card> cards = new ArrayList<>();
            ArrayList<Bitmap> images;

            for (CardEntry entry : cardEntries) {
                int cardId = entry.getId();
                Card card = new Card(cardId,
                        entry.getQuestion(), entry.getAnswer(),
                        entry.getLastReview(), entry.getNextReview());
                List<ImageEntry> imageEntries = imageDao.loadImagesByCardId(cardId);

                if (imageEntries != null) {
                    images = new ArrayList<>();
                    try {
                        //get images for the card
                        for (ImageEntry imageEntry : imageEntries) {
                            String fileName = imageEntry.getFileName();
                            File file = new File(CardRepository.picturesDirectory, fileName);
                            Log.d(TAG, "doInBackground: path to the picture: \n"
                                    + file.getAbsolutePath());
                            Bitmap bitmap = Picasso.get().load(file).get();
                            images.add(bitmap);
                            card.addPictureFileName(fileName);
                        }
                        card.setPictures(images);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                SoundEntry soundEntry = soundDao.loadSoundByCardId(cardId);
                if (soundEntry != null) {
                    try {
                        String fileURL = soundEntry.getFileName();
                        String fileName = getFileNameFromURL(fileURL);
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
        private ExploreActivity exploreActivity;

        public soundQueryAsyncTask(ExploreActivity exploreActivity) {
            this.exploreActivity = exploreActivity;
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
                exploreActivity.setSound(soundUrl);
            } else {
                exploreActivity.hidePronunciation();
            }
        }
    }


    /**
     * Fetch related to the word images from Google Images service by parsing the html page.
     */
    public static class imagesQueryTask extends AsyncTask<String, Pair<String, Bitmap>, HashMap<String, Bitmap>> {

        private static final String TAG = "imagesQueryTask";
        private ImagesLoadable imagesLoadable;
        private String mLang;

        public imagesQueryTask(ImagesLoadable imagesLoadable, String lang) {
            this.imagesLoadable = imagesLoadable;
            mLang = lang;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imagesLoadable.onLoadImagesStart();
        }

        @Override
        protected HashMap<String, Bitmap> doInBackground(String... queryWord) {
            String word = queryWord[0];
            if (word == null && TextUtils.isEmpty(word)) {
                return null;
            } else {
                HashMap<String, Bitmap> images = new LinkedHashMap<>();
                ArrayList<String> imgsURL = NetworkUtils.fetchRelatedImagesUrl(word, mLang);
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
                                publishProgress(image);
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
            imagesLoadable.addImage(images[0]);
        }

        @Override
        protected void onPostExecute(HashMap<String, Bitmap> images) {
            imagesLoadable.onLoadImagesFinished();
            if (images == null || images.isEmpty()) {
                imagesLoadable.showImagesErrorMessage();
            }
        }
    }

    /**
     * Saves pictures to the local storage.
     */
    public static class savePicturesAsyncTask extends AsyncTask<ImageViewBitmap, Void, String[]> {

        private static final String TAG = "savePicturesAsyncTask";

        private ImageDao imageDao;

        public savePicturesAsyncTask(ImageDao imageDao) {
            this.imageDao = imageDao;
        }

        /**
         * @param imageViews to save on the storage
         * @return the files names concatenated to String divided by a space
         */
        @Override
        protected String[] doInBackground(ImageViewBitmap... imageViews) {
            File mPicturesDirectory = CardRepository.picturesDirectory;
            ArrayList<String> fileNames = new ArrayList<>();
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

                    fileNames.add(fname);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            return fileNames.toArray(new String[0]);
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
                String fileName = getFileNameFromURL(fileUrl);

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

        private ImageDao imageDao;

        public deletePicturesAsyncTask(ImageDao imageDao) {
            this.imageDao = imageDao;
        }

        @Override
        protected Void doInBackground(String... fileNames) {
            for (String fileName : fileNames) {
                File image = new File(CardRepository.picturesDirectory, fileName);
                if (image.delete()) {
                    Log.d(TAG, "The image "
                            + image.getAbsolutePath()
                            + " deleted!");
                    imageDao.deleteImageByFileName(fileName);
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

        private SoundDao soundDao;

        public deleteSoundAsyncTask(SoundDao soundDao) {
            this.soundDao = soundDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String filePath = strings[0];
            File sound = new File(filePath);
            if (sound.delete()) {
                soundDao.deleteSoundByFileName(sound.getName());
                Log.d(TAG, "The sound "
                        + sound.getAbsolutePath()
                        + " deleted!");
            } else {
                Log.d(TAG, "The sound "
                        + sound.getAbsolutePath()
                        + " didn't deleteCard!");
            }

            return null;
        }
    }

    /**
     * Performs database queries.
     */
    public static class databaseAsyncTask extends AsyncTask<CardEntry, Void, Void> {
        private CardDao cardDao;
        private ImageDao imageDao;
        private SoundDao soundDao;
        private String task;
        private String[] images;
        private String sound;

        public databaseAsyncTask(CardDao cardDao, String task,
                                 String[] images, ImageDao imageDao,
                                 String sound, SoundDao soundDao) {
            this.cardDao = cardDao;
            this.task = task;
            this.images = images;
            this.sound = sound;
            this.imageDao = imageDao;
            this.soundDao = soundDao;
        }

        @Override
        protected Void doInBackground(CardEntry... cardEntries) {
            if (CardRepository.INSERT_TASK.equals(task)) {
                int cardId = (int)cardDao.insertCard(cardEntries[0]);
                insertImagesAndSounds(cardId);
            }
            if (CardRepository.DELETE_TASK.equals(task)) {
                cardDao.deleteCard(cardEntries[0]);
                int cardId = cardEntries[0].getId();
            }
            if (CardRepository.DELETE_ALL_TASK.equals(task)) {
                cardDao.deleteAllCards();
            }
            return null;
        }

        private void insertImagesAndSounds(int cardId) {
            if (images != null && imageDao != null) {
                for (String image : images) {
                    ImageEntry imageEntry = new ImageEntry(image, cardId);
                    imageDao.insertImage(imageEntry);
                }
            }
            if (sound != null && soundDao != null) {
                SoundEntry soundEntry = new SoundEntry(sound, cardId);
                soundDao.insertSound(soundEntry);
            }
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
        private final ProgressableAdapter mAdapter;
        private String fromLang;
        private String toLang ;

        public loadSuggestionCards(ProgressableAdapter adapter, String fromLang, String toLang) {
            mAdapter = adapter;
            this.fromLang = fromLang;
            this.toLang = toLang;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!LiveSearchHelper.lastTyped.equals(LiveSearchHelper.lastSearched)) {
                this.cancel(true);
            }
        }

        @Override
        protected Void doInBackground(String... words) {
            if (isCancelled()) return null;
            for (String word : words) {
                String translation = NetworkUtils.translateByGlosbe(word, fromLang, toLang);
                if (translation != null && !translation.isEmpty()) {
                    publishProgress(new Pair<>(word, translation));
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Pair... values) {
            super.onProgressUpdate(values);
                if (LiveSearchHelper.lastTyped.equals(LiveSearchHelper.lastSearched)) {
                    mAdapter.insert(values[0]);
                } else {
                    this.cancel(true);
                    mAdapter.clear();
                    if (!LiveSearchHelper.lastTyped.isEmpty())
                        NetworkUtils.requestWikiOpenSearchAgain();
                }
        }
    }

    public static class gatherWordInfo extends AsyncTask<Word, Void, Void> {
        private static final String TAG = "gatherWordInfo";

        private String mFromLang;
        private String mToLang;
        private WordInfoLoader mUpdatable;


        gatherWordInfo(String fromLang, String toLang, WordInfoLoader updatable) {
            mFromLang = fromLang;
            mToLang = toLang;
            mUpdatable = updatable;
        }

        @Override
        protected Void doInBackground(Word... words) {
            Word word = words[0];
            Document glosbePage = NetworkUtils.getGlosbePage(word, mFromLang, mToLang);
            if (glosbePage != null) {
                String IPA = NetworkUtils.extractGlosbeIPA(glosbePage);
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        word.setIPA(IPA);
                    }
                });
                try {
                    String soundURL = NetworkUtils.extractGlosbeSound(glosbePage);
                    if (soundURL == null || soundURL.isEmpty()) {
                        soundURL = NetworkUtils.extractForvoSound(word.getName(), mFromLang);
                        if (soundURL == null) return null;
                    }
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(soundURL);
                    mediaPlayer.prepare();
                    final String soundURLfinal = soundURL;
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            word.setPronunciation(mediaPlayer, soundURLfinal);
                        }
                    });
                } catch (IOException e) { e.printStackTrace(); }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public static class soundDBQuery extends AsyncTask<String, Void, Void> {

        private String task;
        private SoundDao soundDao;
        private int cardId;

        public soundDBQuery(String task, SoundDao soundDao, int cardId) {
            this.task = task;
            this.soundDao = soundDao;
            this.cardId = cardId;
        }

        @Override
        protected Void doInBackground(String... fileNames) {
            if (CardRepository.INSERT_TASK.equals(task)) {
                for (String fileName : fileNames) {
                    SoundEntry soundEntry = new SoundEntry(fileName, cardId);
                    soundDao.insertSound(soundEntry);
                }
            }

            return null;
        }
    }

    public static class ImageDBQuery extends AsyncTask<String, Void, Void> {

        private String task;
        private ImageDao imageDao;
        private int cardId;

        public ImageDBQuery(String task, ImageDao imageDao, int cardId) {
            this.task = task;
            this.imageDao = imageDao;
            this.cardId = cardId;
        }

        @Override
        protected Void doInBackground(String... fileNames) {
            if (CardRepository.INSERT_TASK.equals(task)) {
                for (String fileName : fileNames) {
                    ImageEntry imageEntry = new ImageEntry(fileName, cardId);
                    imageDao.insertImage(imageEntry);
                }
            }

            return null;
        }
    }

    private static String getFileNameFromURL(String url) {
        String pattern = "(.*/)(.*$)";
        return url.replaceAll(pattern, "$2");
    }


    public static class dbUpdateReview extends AsyncTask <Void, Void, Void> {
        private int cardId;
        private Date lastReview;
        private Date nextReview;
        private CardDao cardDao;

        public dbUpdateReview(int cardId, Date lastReview, Date nextReview, CardDao cardDao) {
            this.cardId = cardId;
            this.lastReview = lastReview;
            this.nextReview = nextReview;
            this.cardDao = cardDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            cardDao.updateReview(cardId, lastReview, nextReview);
            return null;
        }
    }
}
