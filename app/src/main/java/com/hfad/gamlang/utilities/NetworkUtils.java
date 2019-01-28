package com.hfad.gamlang.utilities;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.hfad.gamlang.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    private static String authABBYYToken = null;
    private static final String ABBYY_BASE_URL
            = "https://developers.lingvolive.com";
    private static final String ABBYY_AUTH
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

    //Image search on Pixels.com
    private static final String IMAGE_SEARCH_API_KEY
            = "563492ad6f917000010000010b67db13fe80472ea18059085d7f7a45";
    private static final String IMAGE_SEARCH_BASE_URL
            = "https://api.pexels.com/v1/search";
    private static final String IMAGE_SEARCH_ACTION = "searchImages";
    private static final String PARAM_IMAGE_QUERY = "query";
    private static final String PARAM_IMAGE_PER_PAGE = "per_page";
    private static final String PARAM_IMAGE_PAGE = "page";
    static int imagesPerPage = 15;

    //Glosbe.com translation API
    private static final String GlOSBE_TRANSLATION_ACTION = "/translate";
    private static final String GLOSBE_CONTEXT_ACTION = "/tm";
    private static final String GlOSBE_BASE_URL = "https://glosbe.com/gapi";
    private static final String GLOSBE_PARAM_WORD = "phrase";
    private static final String GLOSBE_PARAM_ORIGIN_LANG = "from";
    private static final String GLOSBE_PARAM_TRANSLATION_LANG = "dest";

    /**
     * Builds the URL used to query GitHub.
     *
     * @param word   The word that will be translated.
     * @param action The part of the url that define an action.
     * @return The URL to use to query the translation in ABBYY.
     */
    public static URL buildUrl(String word, String action) {
        Uri builtUri;
        word = word.toLowerCase().trim();

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
                        .appendQueryParameter(PARAM_IMAGE_PER_PAGE, Integer.toString(imagesPerPage))
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

    private static String fetchGlosbeTranslationJSON(String word, Context context) throws IOException {
        String origLang = PreferencesUtils.getPreferedOriginLangCode(context);
        String destLang = PreferencesUtils.getPreferedDestLangCode(context);
        Uri builtUri = Uri.parse(GlOSBE_BASE_URL + GlOSBE_TRANSLATION_ACTION).buildUpon()
                .appendQueryParameter(GLOSBE_PARAM_WORD, word)
                .appendQueryParameter(GLOSBE_PARAM_ORIGIN_LANG, origLang)
                .appendQueryParameter(GLOSBE_PARAM_TRANSLATION_LANG, destLang)
                .appendQueryParameter("format", "json")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

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

    private static String getGlosbeTranslationFromJSON(String JSONString) {
        if (JSONString == null) return null;
        String translation = null;
        try {
            translation = new JSONObject(JSONString)
                    .getJSONArray("tuc")
                    .getJSONObject(0)
                    .getJSONObject("phrase")
                    .getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return translation;
    }

    private static String getGlosbeContextFromJSON(String JSONString) {
        if (JSONString == null) return null;
        String context = null;
        try {
            context = new JSONObject(JSONString)
                    .getJSONArray("examples")
                    .getJSONObject(0)
                    .getString("first");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return context;
    }



    public static String translateByGlosbe(String word, Context context) {
        String json = null;
        try {
            json = fetchGlosbeTranslationJSON(word, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getGlosbeTranslationFromJSON(json);
    }

    public static String contextByGlosbe(String word, Context context) {
        String json = null;
        try {
            json = fetchGlosbeTranslationJSON(word, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getGlosbeContextFromJSON(json);
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

        urlConnection.setRequestProperty("Authorization", basicAuth);
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
        con.setRequestProperty("Authorization", IMAGE_SEARCH_API_KEY);
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


    public static LinkedHashMap<Integer, String> getImagesURLFromJSON(String JSON) {
        LinkedHashMap<Integer, String> imgsURL = new LinkedHashMap<>();
        try {
            JSONArray jsonArr = new JSONObject(JSON)
                    .getJSONArray("photos");
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonPic = jsonArr.getJSONObject(i);
                String picURL = jsonPic
                        .getJSONObject("src")
                        .getString("tiny");
                int picId = jsonPic
                        .getInt("id");
                imgsURL.put(picId, picURL);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imgsURL;
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

    public static ArrayList<String> fetchRelatedImagesUrl(String word, String siteDomain) {
        ArrayList<String> imgsUrl = new ArrayList<>();
        word = word.replaceAll(" ", "+");
        String url;
        if (siteDomain.equals("com")) {
            url = "https://www.google.com"
                    + "/search?q=" + word
                    + "&sout=1&tbm=isch&gs_l=img";
        } else {
            url = "https://www.google." + siteDomain
                    + "/search?q=" + word + "+site%3A" + siteDomain
                    + "&sout=1&tbm=isch&gs_l=img";
        }

        try {
            Log.d(TAG, "fetchRelatedImagesUrl: " + url);
            Document doc = Jsoup.connect(url).get();
            Log.d(TAG, doc.title());
            Log.d(TAG, "jsoupTest: " + doc.title());
            Elements images = doc.select("#res #search #ires .images_table img");
            for (Element image : images) {
                Log.d(TAG, "jsoupTest: " + image.attr("src"));
//                                + image.attr("title") + "\n\t"
//                                + image.absUrl("href"));
                imgsUrl.add(image.attr("src"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgsUrl;
    }

}