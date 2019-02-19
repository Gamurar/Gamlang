package com.gamurar.gamlang.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.ExploreViewModel;
import com.gamurar.gamlang.utilities.SuggestionAdapter;
import com.gamurar.gamlang.utilities.SystemUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExploreFragment extends Fragment implements SuggestionAdapter.ExploreCardClickListener {

    private static final String TAG = "ExploreFragment";
    public static final String KEY_IS_REVERSED = "reverse_language";

    private ExploreViewModel mViewModel;
    private SuggestionAdapter mAdapter;
    private SearchView mSearchView;
    private boolean isReversed = false;
    private ExploreActivity parentActivity;
    private RecyclerView mRecyclerView;

    public static boolean sIsSearching = false;
    public static String sLastTyped;
    public static String sLastSearched;

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
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.clear();
                sLastTyped = query;
                mViewModel.queryOpenSearch(query);
                SystemUtils.closeKeyboard(getActivity());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.clear();
                sLastTyped = newText;
                mViewModel.queryOpenSearch(newText);
                return true;
            }
        });
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


}
