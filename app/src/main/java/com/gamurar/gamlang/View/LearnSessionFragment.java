package com.gamurar.gamlang.View;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.ViewModel.CardViewModel;
import com.gamurar.gamlang.ViewModel.LearnViewModel;
import com.gamurar.gamlang.utilities.CardsAdapter;
import com.gamurar.gamlang.utilities.LearnUtils;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

public class LearnSessionFragment extends Fragment implements LifecycleOwner, CardStackListener {

    private static final String TAG = "LearnSeddionFragment";
    private ArrayList<Card> mCards;
    private CardsAdapter mAdapter;
    private ActionBar mActionBar;
    private TextView mRememberBtn;
    private TextView mDontRememberBtn;
    private CardStackLayoutManager mStackManager;
    private LearnViewModel mViewModel;
    private boolean isSessionBegan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_learn_session, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        isSessionBegan = false;
        mRememberBtn = view.findViewById(R.id.remember_btn);
        mDontRememberBtn = view.findViewById(R.id.dont_remember_btn);
        CardStackView mCardStack = view.findViewById(R.id.card_stack);
        mAdapter = new CardsAdapter();
        mStackManager = new CardStackLayoutManager(getContext(), this);
        mCardStack.setLayoutManager(mStackManager);
        mCardStack.setAdapter(mAdapter);
        setupViewModel();

        mRememberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer();
                Card card = mCards.get(
                        mStackManager.getTopPosition());
                LearnUtils.remember(card);
                mViewModel.updateCardReview(card);
            }
        });
        mDontRememberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer();
                Card card = mCards.get(
                        mStackManager.getTopPosition());
                LearnUtils.dontRemember(card);
                mViewModel.updateCardReview(card);
            }
        });
    }

    private void showAnswer() {
        mStackManager.getTopView()
                .findViewById(R.id.answer_container)
                .setVisibility(View.VISIBLE);
        if (!isSessionBegan) isSessionBegan = true;
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this).get(LearnViewModel.class);
        mViewModel.getCards().observe(this, (cards) -> {
            if (cards != null && !isSessionBegan) {
                mCards = (ArrayList<Card>) cards;
                mAdapter.setCards(mCards);
            }
        });
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (mStackManager.getTopPosition() == mCards.size()) {
            isSessionBegan = false;
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LearnSessionFinishFragment())
                    .commit();
        }
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
}
