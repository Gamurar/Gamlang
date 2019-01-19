package com.hfad.gamlang;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hfad.gamlang.database.AppDatabase;
import com.hfad.gamlang.utilities.DictionaryAdapter;
import com.hfad.gamlang.utilities.MyDictionaryViewModel;

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
    private static HashSet<Integer> selectedCardsId;
    private AppDatabase mDb;
    private MyDictionaryViewModel mViewModel;


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
        mAdapter = new DictionaryAdapter(getContext(), this);
        mWordsList.setAdapter(mAdapter);
        //
        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
        setupViewModel();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupViewModel() {
        MyDictionaryViewModelFactory factory = new MyDictionaryViewModelFactory(mDb);
        mViewModel = ViewModelProviders.of(this, factory).get(MyDictionaryViewModel.class);
        mViewModel.getCards().observe(this, (cardEntries) -> {
            if (cardEntries.isEmpty()) {
                Log.d(TAG, "There is no cards retrieved from the DataBase");
            } else {
                mAdapter.setCards(cardEntries);
            }
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
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Integer[] cardsIdToDelete = selectedCardsId.toArray(new Integer[selectedCardsId.size()]);
                mViewModel.deleteCardsById(cardsIdToDelete);
                Log.d(TAG, "run: The selected cards deleted");
                selectedCardsId.clear();
            }
        });
    }


    @Override
    public void onFirstSelect(View view, int wordId) {
        view.setBackgroundResource(R.color.colorSelected);
        selectedCardsId = new HashSet<>();
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


