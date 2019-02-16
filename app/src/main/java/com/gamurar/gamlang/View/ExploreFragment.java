package com.gamurar.gamlang.View;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ExploreFragment extends Fragment implements SuggestionAdapter.ExploreCardClickListener {

    private static final String TAG = "ExploreFragment";
    public static final String KEY_IS_REVERSED = "reverse_language";

    private ExploreViewModel mViewModel;
    private SuggestionAdapter mAdapter;
    private SearchView mSearchView;
    private boolean isReversed = false;
    private ExploreActivity parentActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            isReversed = true;
            Log.d(TAG, "onCreateView: Russian search selected");
        }
        parentActivity = (ExploreActivity) getActivity();
        mAdapter = parentActivity.suggestionAdapter;
        mAdapter.clear();
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(View view) {
        mViewModel = parentActivity
                .setExploreRecyclerView(view.findViewById(R.id.rv_suggestion_words));
        mSearchView = view.findViewById(R.id.sv_find_new_words);
        if (isReversed) {
            mSearchView.setQueryHint(getString(R.string.search_word_hint_ru));
        }
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.clear();
                mViewModel.queryOpenSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.clear();
                mViewModel.queryOpenSearch(newText);
                return true;
            }
        });
    }


    @Override
    public void onClick(String word, String translation) {
        Intent intent = new Intent(getActivity(), CardCreationActivity.class);
        intent.putExtra(CardCreationActivity.EXTRA_WORD_INFO, new String[] {word, translation});
        startActivity(intent);
    }

    public RecyclerView getRecyclerView() {
        return getView().findViewById(R.id.rv_suggestion_words);
    }
}
