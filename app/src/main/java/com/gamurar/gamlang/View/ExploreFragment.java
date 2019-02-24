package com.gamurar.gamlang.View;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Interpolator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;

import com.airbnb.lottie.LottieAnimationView;
import com.gamurar.gamlang.Model.Tasks;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.ExploreViewModel;
import com.gamurar.gamlang.utilities.AppExecutors;
import com.gamurar.gamlang.utilities.InternetCheckable;
import com.gamurar.gamlang.utilities.LiveSearchHelper;
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
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.disposables.DisposableContainer;
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
    private static int wordsFromGlosbe = 0;
    private static boolean isOnline;

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
        subject.debounce(300, TimeUnit.MILLISECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String text) {
                        return !text.isEmpty() && isOnline;
                    }
                })
                .distinct()
                .switchMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String query) throws Exception {
                        if (query == null) return Observable.just("");
                        Log.d(TAG, "WikiOpenSearch api call: " + query);
                        String words[] = NetworkUtils.wikiOpenSearchRequest(query);
                        if (words == null || words.length < 1) return Observable.just("");
                        isSearching = true;
                        wordsFromWiki = words.length;
                        wordsFromGlosbe = 0;
                        return Observable.fromArray(words);
                    }
                })
                .flatMap(new Function<String, ObservableSource<Pair<String,String>>>() {
                    @Override
                    public ObservableSource<Pair<String,String>> apply(String word) throws Exception {
                        if (word == null) return Observable.just(new Pair<>(word, ""));
                        Log.d(TAG, "Glosbe translation api call: " + word);
                        String translation = mViewModel.translateByGlosbe(word);
                        wordsFromGlosbe++;
                        if (translation == null) return Observable.just(new Pair<>(word, ""));
                        return Observable.just(new Pair<>(word, translation));
//                        return new Pair<>(query, translation);
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<String, String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe()");
                    }

                    @Override
                    public void onNext(Pair<String, String> pair) {
                        if (!pair.first.isEmpty() && !pair.second.isEmpty()) {
                            mAdapter.insert(pair);
                        }
                        if (wordsFromWiki <= wordsFromGlosbe) {
                            progressBar.setVisibility(View.INVISIBLE);
                            isSearching = false;
                            wordsFromWiki = 0;
                            wordsFromGlosbe = 0;
                        }
                        else if (progressBar.getVisibility() == View.INVISIBLE) {
                            progressBar.setVisibility(View.VISIBLE);
                            isSearching = true;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete()");
                    }
                });
    }


    public boolean isOnline() {
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
