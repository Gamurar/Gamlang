package com.gamurar.gamlang.utilities;

import android.os.AsyncTask;

import com.gamurar.gamlang.Model.Tasks;

import java.util.concurrent.Executor;

import androidx.appcompat.widget.SearchView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.subjects.PublishSubject;

public class LiveSearchHelper {
    public static boolean isSearching = false;
    public static boolean isTyping = true;
    public static boolean isNewQuery = false;
    public static String lastTyped;
    public static String lastSearched;
}
