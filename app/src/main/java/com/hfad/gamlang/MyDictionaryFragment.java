package com.hfad.gamlang;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hfad.gamlang.database.CardEntry;
import com.hfad.gamlang.utilities.DictionaryAdapter;

import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyDictionaryFragment extends Fragment
    implements DictionaryAdapter.DictWordSelectListener {
    private static final String TAG = "MyDictionaryFragment";
    private RecyclerView mWordsList;
    private DictionaryAdapter mAdapter;
    private static List<CardEntry> cards;
    private static HashSet<Integer> selectedCardsId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dictionary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupViewModel();
        //Recycler View
        mWordsList = view.findViewById(R.id.rv_my_dictionary);
        mWordsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mWordsList.setHasFixedSize(true);

        //
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupViewModel() {
        LearnWordsViewModel viewModel = ViewModelProviders.of(this).get(LearnWordsViewModel.class);
        viewModel.getCards().observe(this, (cardEntries) -> {
            if (cardEntries.isEmpty()) {
                Log.d(TAG, "There is no cards retrieved from the DataBase");
                return;
            }
            cards = cardEntries;
            mAdapter = new DictionaryAdapter(cards, this);
            mWordsList.setAdapter(mAdapter);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dictionary_selected_words_menu, menu);
    }


    @Override
    public void onFirstSelect(View view, int wordId) {
        view.setBackgroundResource(R.color.colorSelected);
        selectedCardsId = new HashSet<Integer>();
        selectedCardsId.add(wordId);
        setHasOptionsMenu(true);
    }

    //return true if there are still selected items in the list and false otherwise
    @Override
    public boolean onNextSelect(View view, int wordId) {
        if (selectedCardsId.contains(wordId)) {
            return onUnselect(view, wordId);
        } else {
            view.setBackgroundResource(R.color.colorSelected);
            selectedCardsId.add(wordId);
            return true;
        }
    }

    //return true if there are still selected items in the list and false otherwise
    @Override
    public boolean onUnselect(View view, int wordId) {
        view.setBackgroundResource(android.R.color.white);
        selectedCardsId.remove(wordId);
        boolean isSelectedItem = !selectedCardsId.isEmpty();
        if (!isSelectedItem) {
            setHasOptionsMenu(false);
        }

        return isSelectedItem;
    }
}


