package com.gamurar.gamlang.View;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;

import com.airbnb.lottie.LottieAnimationView;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.ExploreViewModel;
import com.gamurar.gamlang.utilities.NetworkUtils;
import com.gamurar.gamlang.utilities.SuggestionAdapter;
import com.gamurar.gamlang.utilities.SystemUtils;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class ExploreFragment extends Fragment implements SuggestionAdapter.ExploreCardClickListener {

    private static final String TAG = "ExploreFragment";
    public static final String KEY_LANG = "language_key";

    private ExploreViewModel mViewModel;
    private SuggestionAdapter mAdapter;
    private SearchView mSearchView;
    private boolean isReversed = false;
    private ExploreActivity parentActivity;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private ConnectivityManager connectivityManager;
    private LottieAnimationView mOfflineAnim;

    private static boolean isSearching;
    private static int wordsFromWiki = 0;
    private static int translatedWords = 0;
    private static boolean isOnline;
    private Disposable mDisposable;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach() called");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated() called");
        init(view);
    }

    private void init(View view) {
        mRecyclerView = view.findViewById(R.id.rv_suggestion_words);
        mRecyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 2));
        mAdapter = new SuggestionAdapter(getContext(), this);
        Log.d(TAG, "init: ExploreFragment adapter: " + mAdapter);
        mRecyclerView.setAdapter(mAdapter);

        mSearchView = view.findViewById(R.id.sv_find_new_words);
        progressBar = view.findViewById(R.id.progress_bar);
        mOfflineAnim = view.findViewById(R.id.offline_anim);
        if (getArguments() != null) {
            String lang = getArguments().getString(KEY_LANG);
            mSearchView.setQueryHint(getString(R.string.search_word_hint, lang));
        }
        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        isOnline = isOnline();

        final PublishSubject<String> subject = PublishSubject.create();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SystemUtils.closeKeyboard(getActivity());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.clear();
                subject.onNext(newText.toLowerCase());
                if (newText.isEmpty() || !isOnline) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        setUpSearchObservable(subject);
        if (!isOnline) {
            mOfflineAnim.setVisibility(View.VISIBLE);
            mOfflineAnim.playAnimation();
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach() called");
        super.onDetach();
    }

    @Override
    public void onClick(String word, String translation) {
        Intent intent = new Intent(getActivity(), CardCreationActivity.class);
        String[] wordExtra;
        if (mViewModel.isReversed()) {
            wordExtra = new String[] {translation, word};
        } else {
            wordExtra = new String[] {word, translation};
        }
        intent.putExtra(CardCreationActivity.EXTRA_WORD_INFO, wordExtra);
        startActivity(intent);
        mSearchView.setQuery("", false);
    }

    @Override
    public void onItemInsert() {

    }

    public RecyclerView getRecyclerView() {
        return getView().findViewById(R.id.rv_suggestion_words);
    }

    public SuggestionAdapter getAdapter() {
        return mAdapter;
    }

    public void setViewModel(ExploreViewModel viewModel) {
        mViewModel = viewModel;
    }

    private void setUpSearchObservable(PublishSubject<String> subject) {
        mDisposable = subject.debounce(300, TimeUnit.MILLISECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String text) {
                        log("filter");
                        return !text.isEmpty() && isOnline;
                    }
                })
                .distinctUntilChanged()
                .switchMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String query) throws Exception {
                        log("switchMap");
                        if (query == null) return Observable.just("");
                        Log.d(TAG, "WikiOpenSearch api call: " + query);
                        String words[] = NetworkUtils.wikiOpenSearchRequest(query);
                        if (words == null || words.length < 1) return Observable.just("");
                        isSearching = true;
                        wordsFromWiki = words.length;
                        translatedWords = 0;
                        return Observable.fromArray(words);
                    }
                })
                .flatMap(new Function<String, ObservableSource<Pair<String,String>>>() {
                    @Override
                    public ObservableSource<Pair<String,String>> apply(String word) throws Exception {
                        log("flatMap");
                        if (word == null) return Observable.just(new Pair<>(word, ""));
//                        String translation = mViewModel.translateByWiki(word);
                        String translation = mViewModel.translateByGamurar(word);
                        translatedWords++;
                        if (translation == null) return Observable.just(new Pair<>(word, ""));
                        return Observable.just(new Pair<>(word, translation));
//                        return new Pair<>(query, translation);
                    }

                })
                .skipWhile(new Predicate<Pair<String, String>>() {
                    @Override
                    public boolean test(Pair<String, String> pair) throws Exception {
                        log("skipWhile");
                        return pair.second.isEmpty();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> {
                    log("subscribe");
                    if (!pair.first.isEmpty() && !pair.second.isEmpty()) {
                        mAdapter.insert(pair);
                    }
                    if (wordsFromWiki <= translatedWords) {
                        progressBar.setVisibility(View.INVISIBLE);
                        isSearching = false;
                        wordsFromWiki = 0;
                        translatedWords = 0;
                    } else if (progressBar.getVisibility() == View.INVISIBLE) {
                        progressBar.setVisibility(View.VISIBLE);
                        isSearching = true;
                    }
                }, throwable -> {
                    Log.e(TAG, throwable.getMessage(), throwable );
                });
    }


    private boolean isOnline() {
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDisposable.dispose();
        Log.d("rxFlow", "onDestroyView: search disposed");
    }

    private void log(String method) {
        Log.d("rxFlow", Thread.currentThread().getName() + ": " + method);
    }
}
