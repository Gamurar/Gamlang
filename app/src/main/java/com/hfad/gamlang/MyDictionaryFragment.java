package com.hfad.gamlang;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hfad.gamlang.database.CardEntry;
import com.hfad.gamlang.utilities.DictionaryAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyDictionaryFragment extends Fragment {
    private static final String TAG = "MyDictionaryFragment";
    private RecyclerView mWordsList;
    private DictionaryAdapter mAdapter;
    private static List<CardEntry> cards;

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
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        mWordsList.addItemDecoration(itemDecoration);
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
            mAdapter = new DictionaryAdapter(cards);
            mWordsList.setAdapter(mAdapter);
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d(TAG, "onCreateContextMenu: context menu created");
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.card_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "Context menu item selected");
        return super.onContextItemSelected(item);
    }
}
