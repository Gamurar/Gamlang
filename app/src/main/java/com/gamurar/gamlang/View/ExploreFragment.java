package com.gamurar.gamlang.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import com.gamurar.gamlang.Model.Tasks;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.ExploreViewModel;
import com.gamurar.gamlang.utilities.AppExecutors;
import com.gamurar.gamlang.utilities.LiveSearchHelper;
import com.gamurar.gamlang.utilities.NetworkUtils;
import com.gamurar.gamlang.utilities.SuggestionAdapter;
import com.gamurar.gamlang.utilities.SystemUtils;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class ExploreFragment extends Fragment implements SuggestionAdapter.ExploreCardClickListener {

    private static final String TAG = "ExploreFragment";
    public static final String KEY_IS_REVERSED = "reverse_language";

    private ExploreViewModel mViewModel;
    private SuggestionAdapter mAdapter;
    private SearchView mSearchView;
    private boolean isReversed = false;
    private ExploreActivity parentActivity;
    private RecyclerView mRecyclerView;

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
        Log.d(TAG, "onViewCreated() called");
        init(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(View view) {
        mRecyclerView = view.findViewById(R.id.rv_suggestion_words);
        mRecyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 2));
        mAdapter = new SuggestionAdapter(getContext(), this);
        Log.d(TAG, "init: ExploreFragment adapter: " + mAdapter);
        mRecyclerView.setAdapter(mAdapter);

        mSearchView = view.findViewById(R.id.sv_find_new_words);
        if (getArguments() != null && getArguments().getBoolean(KEY_IS_REVERSED)) {
            mSearchView.setQueryHint(getString(R.string.search_word_hint_ru));
        }
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                mAdapter.clear();
//                return true;
//            }
//        });
        setUpSearchObservable();
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

    private void setUpSearchObservable() {
        LiveSearchHelper.RxSearchObservable.fromView(mSearchView)
                .debounce(300, TimeUnit.MILLISECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String text) {
                        return !text.isEmpty();
                    }
                })
                .distinct()
                .switchMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String query) throws Exception {
                        AppExecutors.getInstance().mainThread().execute(() -> mAdapter.clear());
                        if (query == null) return Observable.just("");
                        Log.d(TAG, "WikiOpenSearch api call: " + query);
                        String words[] = NetworkUtils.wikiOpenSearchRequest(query);
                        if (words == null || words.length < 1) return Observable.just("");
                        return Observable.fromArray(words);
                    }
                })
                .flatMap(new Function<String, ObservableSource<Pair<String,String>>>() {
                    @Override
                    public ObservableSource<Pair<String,String>> apply(String word) throws Exception {
                        if (word == null) return Observable.just(new Pair<>(word, ""));
                        Log.d(TAG, "Glosbe translation api call: " + word);
                        String translation = mViewModel.translateByGlosbe(word);
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


}
