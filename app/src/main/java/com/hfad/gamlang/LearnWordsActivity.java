package com.hfad.gamlang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hfad.gamlang.database.CardEntry;

import java.util.List;

public class LearnWordsActivity extends AppCompatActivity implements LifecycleOwner {

    private static final String TAG = "LearnWordsActivity";

    private TextView mQuestion;
    private TextView mAnswer;
    private Button mShowAnswer;
    private static boolean isAnswerShown = false;
    private List<CardEntry> mCards;
    private int mCardCount;
    private static int mCurrentCardId = 0;
    private CardEntry mCurrentWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_words);
        setupViewModel();
        initViews();
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
        mCurrentWord = mCards.get(mCurrentCardId);

        mAnswer.setVisibility(TextView.INVISIBLE);
        isAnswerShown = false;

        mQuestion.setText(mCurrentWord.getWord());
        mAnswer.setText(mCurrentWord.getTranslation());
        mShowAnswer.setText("Show answer");
        Log.d(TAG, "Card index: " + mCurrentCardId);

    }

    private void initViews() {
        mQuestion = findViewById(R.id.question);
        mAnswer = findViewById(R.id.answer);
        mShowAnswer = findViewById(R.id.show_answer);
        mShowAnswer.setOnClickListener((view) -> {
            if (isAnswerShown) {
                nextWord();
            } else {
                showAnswer();
            }
        });
    }

    private void setupViewModel() {
        LearnWordsViewModel viewModel = ViewModelProviders.of(this).get(LearnWordsViewModel.class);
        viewModel.getCards().observe(this, (cardEntries) -> {
            if (cardEntries.isEmpty()) {
                Log.d(TAG, "There is no cards retrieved from the DataBase");
                return;
            }
            mCards = cardEntries;
            mCurrentCardId = 0;
            Log.d(TAG, "Card set to review was updated");
            mCurrentWord = mCards.get(mCurrentCardId);
            mQuestion.setText(mCurrentWord.getWord());
            mAnswer.setText(mCurrentWord.getTranslation());
            mCardCount = mCards.size();
            Log.d(TAG, "Card count: " + mCardCount);
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
