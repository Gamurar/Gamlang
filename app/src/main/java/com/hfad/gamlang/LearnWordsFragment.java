package com.hfad.gamlang;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfad.gamlang.database.CardEntry;
import com.hfad.gamlang.utilities.LearnWordsViewModel;
import com.hfad.gamlang.utilities.StorageHelper;

import java.util.ArrayList;
import java.util.List;

public class LearnWordsFragment extends Fragment implements LifecycleOwner {

    private static final String TAG = "LearnWordsFragment";

    private TextView mQuestion;
    private TextView mAnswer;
    private Button mShowAnswer;
    private ImageView mPicture;
    private static boolean isAnswerShown = false;
    private List<CardEntry> mCardEntries;
    private List<Card> mCards;
    private int mCardCount;
    private static int mCurrentCardId = 0;
    private CardEntry mCurrentWord;
    private StorageHelper storageHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_learn_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        setupViewModel();

        mShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAnswerShown) {
                    nextWord();
                } else {
                    showAnswer();
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(@NonNull View view) {
        mQuestion = view.findViewById(R.id.question);
        mAnswer = view.findViewById(R.id.answer);
        mShowAnswer = view.findViewById(R.id.show_answer);
        mPicture = view.findViewById(R.id.card_picture);
        storageHelper = new StorageHelper(getContext());
    }

    private void setupViewModel() {
        LearnWordsViewModel viewModel = ViewModelProviders.of(this).get(LearnWordsViewModel.class);
        viewModel.getCards().observe(this, (cardEntries) -> {
            if (cardEntries.isEmpty()) {
                Log.d(TAG, "There is no cards retrieved from the DataBase");
                return;
            }
            mCardEntries = cardEntries;
            mCards = new ArrayList<>();
            for (CardEntry entry : cardEntries) {
                Card card = new Card(entry.getWord(), entry.getTranslation());
                storageHelper.getImages(entry.getImage());
            }
            mCurrentCardId = 0;
            Log.d(TAG, "Card set to review was updated");
            mCurrentWord = mCardEntries.get(mCurrentCardId);
            mQuestion.setText(mCurrentWord.getWord());
            mAnswer.setText(mCurrentWord.getTranslation());
            mCardCount = mCardEntries.size();
            Log.d(TAG, "Card count: " + mCardCount);

        });
    }

    private void showAnswer() {
        mAnswer.setVisibility(TextView.VISIBLE);
        isAnswerShown = true;
        mShowAnswer.setText("Next word");
    }

    private void nextWord() {
        mCurrentCardId++;
        if (mCurrentCardId == mCardCount) {
            mCurrentCardId = 0;
        }
        mCurrentWord = mCardEntries.get(mCurrentCardId);

        mAnswer.setVisibility(TextView.INVISIBLE);
        isAnswerShown = false;

        mQuestion.setText(mCurrentWord.getWord());
        mAnswer.setText(mCurrentWord.getTranslation());
        mShowAnswer.setText("Show answer");
        Log.d(TAG, "Card index: " + mCurrentCardId);
    }
}
