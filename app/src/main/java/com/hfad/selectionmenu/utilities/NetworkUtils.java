package com.hfad.selectionmenu.utilities;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.hfad.selectionmenu.MainActivity;
import com.hfad.selectionmenu.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    private static final String USER_AGENT = "Mozilla/5.0";
    //ABBYY
    private static final String ABBYY_API_KEY
            = "ZTNhMDAwMzUtZmQ3OC00ZjYwLTk3NWQtNzY4YzM0ZGRkM2ExOmQwMDgwMDQ3ZGE4ZjRjZGZiNTJiZjZhNThhOTg3NzM2";
    public static String authABBYYToken = null;
    private static final String ABBYY_BASE_URL
            = "https://developers.lingvolive.com";
    public static final String ABBYY_AUTH
            = "/api/v1/authenticate";
    public static final String ABBYY_TRANSLATE
            = "/api/v1/Translation";
    public static final String ABBYY_SHORT_TRANSLATE
            = "/api/v1/Minicard";
    private static final String PARAM_TEXT = "text";
    private static final String PARAM_SRCLANG = "srcLang";
    private static final String PARAM_DSTLANG = "dstLang";
    private static final int ENG = 1033;
    private static final int RUS = 1049;

    //Image search with ContextualWeb search engine
    private static final String IMAGE_SEARCH_API_KEY
            = "gOeKuubQgomshwJ2dgCdKvAR5iqip1nsrmLjsnayGsW4F5JJhe";
    private static final String IMAGE_SEARCH_BASE_URL
            = "https://contextualwebsearch-websearch-v1.p.rapidapi.com/api/Search/ImageSearchAPI";
    public static final String IMAGE_SEARCH_ACTION = "/api/Search/ImageSearchAPI";
    private static final String PARAM_IMAGE_COUNT = "count";
    private static final String PARAM_IMAGE_QUERY = "q";
    private static final String PARAM_IMAGE_AUTOCORRECT = "autoCorrect";
    static int imageCount = 50;
    private static String imageAutoCorrect = "false";

    /**
     * Builds the URL used to query GitHub.
     *
     * @param word The word that will be translated.
     * @param action The part of the url that define an action.
     * @return The URL to use to query the translation in ABBYY.
     */
    public static URL buildUrl(String word, String action) {
        Uri builtUri;

        switch (action) {
            case ABBYY_AUTH: {
                builtUri = Uri.parse(ABBYY_BASE_URL + action)
                        .buildUpon()
                        .build();
                break;
            }
            case IMAGE_SEARCH_ACTION: {
                builtUri = Uri.parse(IMAGE_SEARCH_BASE_URL).buildUpon()
                        .appendQueryParameter(PARAM_IMAGE_QUERY, word)
                        .appendQueryParameter(PARAM_IMAGE_COUNT, Integer.toString(imageCount))
                        .appendQueryParameter(PARAM_IMAGE_AUTOCORRECT, imageAutoCorrect)
                        .build();
                break;
            }
            default: {
                builtUri = Uri.parse(ABBYY_BASE_URL + action).buildUpon()
                        .appendQueryParameter(PARAM_TEXT, word)
                        .appendQueryParameter(PARAM_SRCLANG, Integer.toString(ENG))
                        .appendQueryParameter(PARAM_DSTLANG, Integer.toString(RUS))
                        .build();
            }
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getABBYYAuthToken(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        String basicAuth = "Basic " + ABBYY_API_KEY;

        urlConnection.setRequestProperty ("Authorization", basicAuth);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("Content-Length", "");
        urlConnection.setRequestProperty("Content-Language", "en-US");
        urlConnection.setUseCaches(false);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String getABBYYTranslation(URL url) throws IOException {
        Log.i(TAG, "getABBYYTranslation() ABBYY token: " + authABBYYToken);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        String token = "Bearer " + authABBYYToken;

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("Authorization", token);
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        Log.i(TAG, "\nSending 'GET' request to URL : " + url);
        Log.i(TAG, "Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    //Get JSON Images
    public static String getImagesJSON(URL url) throws IOException {
        Log.i(TAG, "Image search api key: " + IMAGE_SEARCH_API_KEY);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("X-RapidAPI-Key", IMAGE_SEARCH_API_KEY);
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        Log.i(TAG, "\nSending 'GET' request to URL : " + url);
        Log.i(TAG, "Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Log.i(TAG, "Images JSON: " + response);

        return response.toString();
    }

    public static Word getWordFromShortTranslation(String JSONString) {
        try {
            JSONObject json = new JSONObject(JSONString)
                    .getJSONObject("Translation");
            Log.i(TAG, "json: " + json);
            Log.i(TAG, "word: " + json.getString("Heading"));
            Log.i(TAG, "translation: " + json.getString("Translation"));
            Word word = new Word(json.getString("Heading"));
            word.setTranslation(json.getString("Translation"));
            word.setSound(json.getString("SoundName"));
            return word;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<String> getImagesURLFromJSON(String JSON) {
        ArrayList<String> imgURLs = new ArrayList<>();
        try {
            JSONArray jsonArr = new JSONObject(JSON)
                    .getJSONArray("value");
            for (int i = 0; i < jsonArr.length(); i++) {
                String picURL = jsonArr.getJSONObject(i)
                        .getString("url");
                imgURLs.add(picURL);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imgURLs;
    }

    public static void playPronunc(String url) {
        Log.i(TAG, "Pronunciation url: " + url);
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }
}