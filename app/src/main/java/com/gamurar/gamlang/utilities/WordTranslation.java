package com.gamurar.gamlang.utilities;

public interface WordTranslation {
    void onLoadTranslation();
    void onLoadTranslationFinished();
    void setTranslation(String translation);
    void showTranslationErrorMessage();
}
