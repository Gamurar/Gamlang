package com.hfad.gamlang.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hfad.gamlang.Card;
import com.hfad.gamlang.R;
import com.hfad.gamlang.ViewModel.CardViewModel;
import com.hfad.gamlang.utilities.DictionaryAdapter;

import java.util.HashSet;

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
    private HashSet<Card> selectedCards;
    private CardViewModel mViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dictionary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Recycler View
        mWordsList = view.findViewById(R.id.rv_my_dictionary);
        mWordsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mWordsList.setHasFixedSize(true);
        mAdapter = new DictionaryAdapter(this);
        mWordsList.setAdapter(mAdapter);
        //
        setupViewModel();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        mViewModel.getAllCards().observe(this, (cardEntries) -> {
            mAdapter.setCards(cardEntries);

        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dictionary_selected_words_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_delete_words: {
                deleteSelectedCards();
            }
        }
        return true;
    }

    private void deleteSelectedCards() {
        mViewModel.delete(selectedCards);
        Log.d(TAG, "deleteSelectedCards: The selected cards deleted");
        selectedCards.clear();

        setHasOptionsMenu(false);
        mAdapter.haveSelection = false;
    }


    @Override
    public void onFirstSelect(View view, Card card) {
//        view.setBackgroundResource(R.color.colorAccentLight);
        view.setBackgroundColor(R.attr.colorAccent);
        selectedCards = new HashSet<>();
        selectedCards.add(card);
        setHasOptionsMenu(true);
    }

    //return true if there are still selected items in the list and false otherwise
    @Override
    public boolean onNextSelect(View view, Card card) {
        if (selectedCards.contains(card)) {
            return onUnselect(view, card);
        } else {
            //view.setBackgroundResource(R.color.colorAccentLight);
            view.setBackgroundColor(R.attr.colorAccent);
            selectedCards.add(card);
            return true;
        }
    }

    //return true if there are still selected items in the list and false otherwise
    @Override
    public boolean onUnselect(View view, Card card) {
        view.setBackgroundResource(android.R.color.white);
        selectedCards.remove(card);
        boolean isSelectedItem = !selectedCards.isEmpty();
        if (!isSelectedItem) {
            setHasOptionsMenu(false);
        }

        return isSelectedItem;
    }
}


