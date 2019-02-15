package com.gamurar.gamlang.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.gamurar.gamlang.R;

import androidx.preference.PreferenceManager;

public class PreferencesUtils {

    public static String getPrefFromLang(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(
                context.getString(R.string.pref_from_lang_key),
                context.getString(R.string.pref_lang_eng_value));
    }

    public static String getSiteDomain(Context context) {
        String origLang = getPrefFromLang(context);
        String domain = "com";
        if (origLang.equals(context.getString(R.string.pref_lang_eng_value))) {
            domain = "com";
        }
        if (origLang.equals(context.getString(R.string.pref_lang_ru_value))) {
            domain = "ru";
        }
        if (origLang.equals(context.getString(R.string.pref_lang_ro_value))) {
            domain = "ro";
        }

        return domain;
    }

    public static String getPrefFromLangCode(Context context) {
        String originLang = getPrefFromLang(context);
        return getLangCode(originLang, context);
    }

    public static String getPrefToLangCode(Context context) {
        String destLang = getPrefToLang(context);
        return getLangCode(destLang, context);
    }

    public static String getPrefToLang(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(
                context.getString(R.string.pref_to_lang_key),
                context.getString(R.string.pref_lang_ru_value));
    }

    public static void reversePrefLang(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String fromLang = getPrefFromLang(context);
        String toLang = getPrefToLang(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_from_lang_key), toLang);
        editor.putString(context.getString(R.string.pref_to_lang_key), fromLang);
        editor.commit();
    }

    public static String getLangCode(String prefLang, Context context) {
        String langCode = "en";
        if (prefLang.equals(context.getString(R.string.pref_lang_eng_value))) {
            langCode = "en";
        }
        if (prefLang.equals(context.getString(R.string.pref_lang_ru_value))) {
            langCode = "ru";
        }
        if (prefLang.equals(context.getString(R.string.pref_lang_ro_value))) {
            langCode = "ro";
        }

        return langCode;
    }
    
}
