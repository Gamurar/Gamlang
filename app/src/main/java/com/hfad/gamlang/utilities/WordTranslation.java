package com.hfad.gamlang.utilities;

public interface WordTranslation {
    void onLoadTranslation();
    void onLoadTranslationFinished();
    void setTranslation(String translation);
    void showTranslationErrorMessage();
}
