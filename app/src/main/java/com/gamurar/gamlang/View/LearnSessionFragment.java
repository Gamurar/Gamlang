package com.gamurar.gamlang.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
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
import androidx.constraintlayout.widget.Group;
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
    private LottieAnimationView mNotFoundAnim;
    private Group mNoCardsViews;
    private Group mRememberBtns;
    private Button mCreateNewCardBtn;

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
        mNotFoundAnim = view.findViewById(R.id.not_found_anim);
        mNoCardsViews = view.findViewById(R.id.no_cards_screen);
        mRememberBtns = view.findViewById(R.id.remember_btns);
        mCreateNewCardBtn = view.findViewById(R.id.create_new_btn);
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

        mCreateNewCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExploreActivity.class);
                startActivity(intent);
                getActivity().finish();
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
            if (!mIsSessionBegan) {
                if (cards != null) {
                    mCards = (ArrayList<Card>) LearnUtils.getTodayCards(cards);
                    if (mCards != null && !mCards.isEmpty())
                        mAdapter.setCards(mCards);
                    else
                        showNoCardsScreen();
                }
            }
        });
    }

    private void showNoCardsScreen() {
        mRememberBtns.setVisibility(View.GONE);
        mNoCardsViews.setVisibility(View.VISIBLE);
        mNotFoundAnim.playAnimation();
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
