package com.hfad.gamlang;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.hfad.gamlang.ViewModel.CardViewModel;
import com.hfad.gamlang.utilities.CardsAdapter;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

public class LearnWordsFragment extends Fragment implements LifecycleOwner, CardStackListener {

    private static final String TAG = "LearnWordsFragment";
    private ArrayList<Card> mCards;
    private CardsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_learn_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        setupViewModel();
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(@NonNull View view) {
        setHasOptionsMenu(true);
        CardStackView mCardStack = view.findViewById(R.id.card_stack);
        mAdapter = new CardsAdapter();
        CardStackLayoutManager manager = new CardStackLayoutManager(getContext(), this);
        mCardStack.setLayoutManager(manager);
        mCardStack.setAdapter(mAdapter);
    }

    private void setupViewModel() {
        CardViewModel viewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        viewModel.getAllCards().observe(this, (cards) -> {
            Log.d(TAG, "setupViewModel: receive data from ViewModel to 'Learn words'");
            mCards = (ArrayList<Card>) cards;
            mAdapter.setCards(mCards);
        });
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {

    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {
        if (mCards != null && mCards.size() > position) {
            Card card = mCards.get(position);
            if (card.hasSound()) card.pronounce();
        }
    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.learn_words_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_refresh: {
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
        return true;
    }
}
