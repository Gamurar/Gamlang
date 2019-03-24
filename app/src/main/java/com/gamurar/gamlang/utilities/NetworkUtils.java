package com.gamurar.gamlang.utilities;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.gamurar.gamlang.Word;

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
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
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
    public static final String ABBYYsoundBaseUrl
            = "https://api.lingvolive.com/sounds?uri=LingvoUniversal%20(En-Ru)%2F";
    private static final String ABBYY_AUTH
            = "/api/v1.1/authenticate";
    public static final String ABBYY_TRANSLATE
            = "/api/v1/Translation";
    public static final String ABBYY_MINICARD
            = "/api/v1/Minicard";
    private static final String ABBYY_PARAM_WORD = "text";
    private static final String ABBYY_PARAM_SRCLANG = "srcLang";
    private static final String ABBYY_PARAM_DSTLANG = "dstLang";
    private static final int ENG = 1033;
    private static final int RUS = 1049;

    //ImageEntry search on Pixels.com
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
    private static final String GLOSBE_BASE_URL = "https://glosbe.com";
    private static final String GlOSBE_TRANSLATION_ACTION = "/translate";
    private static final String GLOSBE_CONTEXT_ACTION = "/tm";
    private static final String GlOSBE_BASE_URL_API = "https://glosbe.com/gapi";
    private static final String GLOSBE_PARAM_WORD = "phrase";
    private static final String GLOSBE_PARAM_ORIGIN_LANG = "from";
    private static final String GLOSBE_PARAM_TRANSLATION_LANG = "dest";

    //Wikitionary opensearch API
    public static final String WIKI_OPENSEARCH_ACTION = "https://en.wiktionary.org/w/api.php?action=opensearch";
    private static final String WIKI_PARAM_SEARCH = "search";
    private static final String WIKI_PARAM_LIMIT = "limit";
    private static final String WIKI_PARAM_FORMAT = "format";
    private static final String jsonFormat = "json";
    private static final String wikiResultsLimit = "10";

    //Wiktionary translation API
    private static final String WIKI_BASE_URL = "https://en.wiktionary.org/w/api.php";
    private static final String WIKI_PARAM_ACTION = "action";
    private static final String WIKI_PARAM_PAGE = "page";
    private static final String WIKI_PARAM_PROPERTIES = "prop";
    private static final String wikiActionParse = "parse";
    private static final String wikiPropIwlinks = "iwlinks";

    //Gamurar translation API
    private static final String GAMURAR_BASE_URL = "http://gamurar.ddns.net/api";
    private static final String GAMURAR_PARAM_WORD = "word";

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
            case GAMURAR_BASE_URL: {
                builtUri = Uri.parse(GAMURAR_BASE_URL).buildUpon()
                        .appendQueryParameter(GAMURAR_PARAM_WORD, word)
                        .build();
                break;
            }
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
            case WIKI_OPENSEARCH_ACTION: {
                builtUri = Uri.parse(WIKI_OPENSEARCH_ACTION).buildUpon()
                        .appendQueryParameter(WIKI_PARAM_SEARCH, word)
                        .appendQueryParameter(WIKI_PARAM_FORMAT, jsonFormat)
                        .appendQueryParameter(WIKI_PARAM_LIMIT, wikiResultsLimit)
                        .build();
                break;
            }
            case WIKI_BASE_URL: {
                builtUri = Uri.parse(WIKI_BASE_URL).buildUpon()
                        .appendQueryParameter(WIKI_PARAM_ACTION, wikiActionParse)
                        .appendQueryParameter(WIKI_PARAM_PROPERTIES, wikiPropIwlinks)
                        .appendQueryParameter(WIKI_PARAM_FORMAT, jsonFormat)
                        .appendQueryParameter(WIKI_PARAM_PAGE, word)
                        .build();
                break;
            }
            default: {
                builtUri = Uri.parse(ABBYY_BASE_URL + action).buildUpon()
                        .appendQueryParameter(ABBYY_PARAM_WORD, word)
                        .appendQueryParameter(ABBYY_PARAM_SRCLANG, Integer.toString(ENG))
                        .appendQueryParameter(ABBYY_PARAM_DSTLANG, Integer.toString(RUS))
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
     * @return response from the get request
     * */
    private static String GETRequest(URL url) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            Log.i(TAG, "\nSending 'GET' request to URL : " + url);
            Log.i(TAG, "Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.i(TAG, "Response: " + response);

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static String fetchGlosbeJSON(String word, String fromLang, String toLang, String action) throws IOException {
//        String fromLang = PreferencesUtils.getPrefFromLangCode(context);
//        String toLang = PreferencesUtils.getPrefToLangCode(context);
        word = word.toLowerCase();
        Uri builtUri = Uri.parse(GlOSBE_BASE_URL_API + action).buildUpon()
                .appendQueryParameter(GLOSBE_PARAM_WORD, word)
                .appendQueryParameter(GLOSBE_PARAM_ORIGIN_LANG, fromLang)
                .appendQueryParameter(GLOSBE_PARAM_TRANSLATION_LANG, toLang)
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
            Log.d(TAG, "Glosbe translation JSON: " + JSONString);
            translation = new JSONObject(JSONString)
                    .getJSONArray("tuc")
                    .getJSONObject(0)
                    .getJSONObject("phrase")
                    .getString("text");
        } catch (JSONException e) {
            String errorMessage = null;
            if (e.getMessage().equals("Index 0 out of range [0..0)")
                || e.getMessage().equals("No value for phrase")) {
                errorMessage = "There are no translations for the word";
            }
            if (errorMessage != null) {
                Log.d(TAG, errorMessage);
            } else {
                Log.e(TAG, "Exception message: " + e.getMessage(), e);
            }
        }

        return translation;
    }

    private static ArrayList<String[]> getGlosbeContextFromJSON(String JSONString) {
        if (JSONString == null) return null;
        ArrayList<String[]> result = new ArrayList<>();
        String contextOrigin = null;
        String contextTranslated = null;
        Log.d(TAG, "getGlosbeContextFromJSON: JSON: " + JSONString);
        try {
            contextOrigin = new JSONObject(JSONString)
                    .getJSONArray("examples")
                    .getJSONObject(0)
                    .getString("first");
            contextTranslated = new JSONObject(JSONString)
                    .getJSONArray("examples")
                    .getJSONObject(0)
                    .getString("second");
            String pair[] = new String[2];
            pair[0] = contextOrigin;
            pair[1] = contextTranslated;
            result.add(pair);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static String translateByGlosbe(String word, String fromLang, String toLang) {
        String json = null;
        try {
            json = fetchGlosbeJSON(word, fromLang, toLang, GlOSBE_TRANSLATION_ACTION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getGlosbeTranslationFromJSON(json);
    }

    public static ArrayList<String[]> contextByGlosbe(String word, String fromLang, String toLang) {
        String json = null;
        try {
            json = fetchGlosbeJSON(word, fromLang, toLang, GLOSBE_CONTEXT_ACTION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getGlosbeContextFromJSON(json);
    }

    /**
     * This method returns the ABBYY autho.
     *
     * @return Returns API token on successfull authentication.
     * Token must be used in Bearer Authorization header in every translation API request. <br>
     * In case of unsuccessfull authentication returns 401 Unauthorized error.<br>
     * Token TTL is 24 hours. After 24 hours or when your receive 401 Unauthorized response for translation methods you have to call this method again and get new token.
     * @throws IOException Related to network and stream reading
     */
    public static String getABBYYAuthToken() throws IOException {
        URL url = new URL(ABBYY_BASE_URL + ABBYY_AUTH);
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

    public static String fetchABBYYMinicardJSON(String authToken, String word) throws IOException {
//        String srcLang = PreferencesUtils.getPrefFromLang(context);
//        String destLang = PreferencesUtils.getPrefToLangCode(context);
        Uri builtUri = Uri.parse(ABBYY_BASE_URL + ABBYY_MINICARD)
                .buildUpon()
                .appendQueryParameter(ABBYY_PARAM_WORD, word)
                .appendQueryParameter(ABBYY_PARAM_SRCLANG, String.valueOf(ENG))
                .appendQueryParameter(ABBYY_PARAM_DSTLANG, String.valueOf(RUS))
                .build();

        URL url = new URL(builtUri.toString());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        String token = "Bearer " + authToken;

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

    public static String getSoundFromJSON(String jsonString) throws JSONException {
        return new JSONObject(jsonString)
                .getJSONObject("Translation")
                .getString("SoundName");
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
        Log.i(TAG, "ImageEntry search api key: " + IMAGE_SEARCH_API_KEY);

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
        if (siteDomain.equals("en")) {
            url = "https://www.google.com"
                    + "/search?q=" + word
                    + "&sout=2&tbm=isch&gs_l=img";
        } else {
            url = "https://www.google." + siteDomain
                    + "/search?q=" + word + "+site%3A" + siteDomain
                    + "&sout=2&tbm=isch&gs_l=img";
        }

        try {
            Log.d(TAG, "fetchRelatedImagesUrl: " + url);
            Document doc = Jsoup.connect(url).get();
            Log.d(TAG, doc.title());
            Log.d(TAG, "jsoupTest: " + doc.title());
            Elements images = doc.getElementsByClass("THL2l").next();
            for (Element image : images) {
                Log.d(TAG, "image url: " + image.toString());
                imgsUrl.add(image.attr("data-src"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgsUrl;
    }

    public static Document getGlosbePage(Word word, String fromLang, String toLang) {
        String url = "https://glosbe.com/" + fromLang + "/" + toLang + "/" + word.getName();
        try {
            Log.d(TAG, "parse Glosbe url: " + url);

            return Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String extractGlosbeIPA(Document glosbePage) {
        Elements defs = glosbePage.getElementsContainingOwnText("IPA:");
        if (defs.hasText()) {
            String IPAs = defs.get(0).nextElementSibling().text();
            String pattern = "(.*[/\\[])([^/]+)([/\\]].*)";
            return IPAs.replaceFirst(pattern, "$2");
        }
        return null;
    }

    public static String extractGlosbeSound(Document glosbePage) {
        try {
            Elements oggs = glosbePage.getElementById("add-translation-container")
                    .nextElementSibling().getElementsByAttribute("data-url-ogg");
            if (oggs.isEmpty()) return null;
            return GLOSBE_BASE_URL + oggs.get(0).attr("data-url-ogg");
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static String extractForvoSound(String word, String langCode) {
        String url = "https://forvo.com/word/" + word + "/#" + langCode;
        Log.d(TAG, "Forvo page url: " + url);
        try {
            Log.d(TAG, "parse Forvo url: " + url);
            Document page = Jsoup.connect(url).get();
            String func = page.getElementById("language-container-" + langCode)
                    .getElementsByAttribute("onclick").get(0).attr("onclick");
            String pattern = "(^Play\\(\\d*,'.*','.*',(?:false|true),')(.+)(','.*','h'\\);return false;$)";
            String cipher = func.replaceFirst(pattern, "$2");
            String baseUrl = "https://audio00.forvo.com/audios";
            String decoded = new String(android.util.Base64.decode(cipher, android.util.Base64.DEFAULT));
            String soundUrl = baseUrl + "/mp3/" + decoded;
            Log.d(TAG, "Forvo sound url: " + soundUrl);
            return soundUrl;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static String[] wikiOpenSearchRequest(String word) {
        URL url = buildUrl(word, WIKI_OPENSEARCH_ACTION);
        String response = GETRequest(url);
        try {
            if (response == null) return null;
            JSONArray jsonWords = new JSONArray(response).getJSONArray(1);
            String[] words = new String[jsonWords.length()];

            for (int i = 0; i < jsonWords.length(); i++) {
                words[i] = jsonWords.getString(i);
            }
            return words;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void requestWikiOpenSearchAgain() {
        String query = LiveSearchHelper.lastTyped;
        LiveSearchHelper.lastSearched = query;
        wikiOpenSearchRequest(query);
    }

    public static boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }

    public static String wikiTranslate(String word) {
        URL url = buildUrl(word, WIKI_BASE_URL);
        String strJson  = GETRequest(url);
        if (strJson == null) return null;
        try {
            JSONArray json = new JSONObject(strJson)
                    .getJSONObject("parse")
                    .getJSONArray("iwlinks");
            for (int i = 0; i < json.length(); i++) {
                if (json.getJSONObject(i).getString("prefix").equals("ru")) {
                    Log.d(TAG, "wiki translation: " + json.getJSONObject(i).getString("*").replace("ru:", ""));
                    return json.getJSONObject(i).getString("*").replace("ru:", "");
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static String gamurarTranslate(String word) {
        URL url = buildUrl(word, GAMURAR_BASE_URL);
        String strJson  = GETRequest(url);
        if (strJson == null) return null;
        try {
            JSONArray json = new JSONArray(GETRequest(url));
            return json.getJSONObject(0)
                    .getJSONArray("translations")
                    .getJSONObject(0)
                    .getString("translation");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }
}