package com.hfad.gamlang;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hfad.gamlang.ViewModel.CardViewModel;
import com.hfad.gamlang.utilities.CardsAdapter;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

public class LearnWordsFragment extends Fragment implements LifecycleOwner {

    private static final String TAG = "LearnWordsFragment";

//    private TextView mQuestion;
//    private TextView mAnswer;
//    private ImageView mPicture;
//    private static boolean isAnswerShown = false;
//    private int mCardCount;
//    private static int mCurrentCardId = 0;
//    private CardEntry mCurrentWord;
    private CardsAdapter mAdapter;
    private CardStackView mCardStack;

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
        mCardStack = view.findViewById(R.id.card_stack);
        mAdapter = new CardsAdapter();
        CardStackLayoutManager manager = new CardStackLayoutManager(getContext());
        mCardStack.setLayoutManager(manager);
        mCardStack.setAdapter(mAdapter);
    }

    private void setupViewModel() {
        CardViewModel viewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        viewModel.getAllCards().observe(this, (cards) -> {
            Log.d(TAG, "setupViewModel: receive data from ViewModel to 'Learn words'");
            mAdapter.setCards((ArrayList<Card>) cards);
        });
    }

//    private void showAnswer() {
//        mAnswer.setVisibility(TextView.VISIBLE);
//        isAnswerShown = true;
//    }
//
//    private void nextWord() {
//        mCurrentCardId++;
//        if (mCurrentCardId == mCardCount) {
//            mCurrentCardId = 0;
//        }
//        //mCurrentWord = mCardEntries.get(mCurrentCardId);
//
//        mAnswer.setVisibility(TextView.INVISIBLE);
//        isAnswerShown = false;
//
//        mQuestion.setText(mCurrentWord.getWord());
//        mAnswer.setText(mCurrentWord.getTranslation());
//        Log.d(TAG, "Card index: " + mCurrentCardId);
//    }
}
