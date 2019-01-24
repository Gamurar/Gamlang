package com.hfad.gamlang.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.hfad.gamlang.R;

import androidx.preference.PreferenceManager;

public class PreferencesUtils {

    public static String getPreferedOriginLang(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(
                context.getString(R.string.pref_from_lang_key),
                context.getString(R.string.pref_lang_eng_value));
    }

    public static String getSiteDomain(Context context) {
        String origLang = getPreferedOriginLang(context);
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

    public static String getPreferedOriginLangCode(Context context) {
        String originLang = getPreferedOriginLang(context);

        return getLangCode(originLang, context);
    }

    public static String getPreferedDestLangCode(Context context) {
        String destLang = getPreferedDestLang(context);

        return getLangCode(destLang, context);
    }

    public static String getPreferedDestLang(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(
                context.getString(R.string.pref_to_lang_key),
                context.getString(R.string.pref_lang_ru_value));
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
