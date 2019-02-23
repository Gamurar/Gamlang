package com.gamurar.gamlang.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.R;
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
    private boolean mIsSessionBegan;
    private boolean mIsAnswered;
    private Card mCurrentCard;

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
        mIsSessionBegan = false;
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
                if (mIsAnswered) return;
                showAnswer();
                LearnUtils.remember(mCurrentCard);
                mViewModel.updateCardReview(mCurrentCard);
            }
        });
        mDontRememberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsAnswered) return;
                showAnswer();
                LearnUtils.dontRemember(mCurrentCard);
                mViewModel.updateCardReview(mCurrentCard);
            }
        });
    }

    private void showAnswer() {
        mIsAnswered = true;
        mStackManager.getTopView()
                .findViewById(R.id.answer_container)
                .setVisibility(View.VISIBLE);
        if (!mIsSessionBegan) mIsSessionBegan = true;
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this).get(LearnViewModel.class);
        mViewModel.getCards().observe(this, (cards) -> {
            if (cards != null && !mIsSessionBegan) {
                mCards = (ArrayList<Card>) LearnUtils.getTodayCards(cards);
                mAdapter.setCards(mCards);
            }
        });
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        mIsAnswered = false;
        if (mStackManager.getTopPosition() == mCards.size()) {
            mIsSessionBegan = false;
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
            mCurrentCard = mCards.get(position);
            if (mCurrentCard.hasSound()) mCurrentCard.pronounce();
        }
    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }
}
