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
import com.hfad.gamlang.tasks.ImagesFromLocalStorageQueryTask;
import com.hfad.gamlang.utilities.AppExecutors;
import com.hfad.gamlang.utilities.CardsAdapter;
import com.hfad.gamlang.utilities.LearnWordsViewModel;
import com.hfad.gamlang.utilities.StorageHelper;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LearnWordsFragment extends Fragment implements LifecycleOwner {

    private static final String TAG = "LearnWordsFragment";

    private TextView mQuestion;
    private TextView mAnswer;
    private Button mShowAnswer;
    private ImageView mPicture;
    private static boolean isAnswerShown = false;
    private List<CardEntry> mCardEntries;
    private int mCardCount;
    private static int mCurrentCardId = 0;
    private CardEntry mCurrentWord;
    private CardsAdapter mAdapter;
    private CardStackView mCardStack;
    // private StorageHelper storageHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_learn_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        setupViewModel();

//        mShowAnswer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(isAnswerShown) {
//                    nextWord();
//                } else {
//                    showAnswer();
//                }
//            }
//        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(@NonNull View view) {
//        mQuestion = view.findViewById(R.id.question);
//        mAnswer = view.findViewById(R.id.answer);
//        mPicture = view.findViewById(R.id.card_picture);
        mShowAnswer = view.findViewById(R.id.show_answer);
        mCardStack = view.findViewById(R.id.card_stack);
        mAdapter = new CardsAdapter(getContext());
        CardStackLayoutManager manager = new CardStackLayoutManager(getContext());
        mCardStack.setLayoutManager(manager);
        mCardStack.setAdapter(mAdapter);
    }

    private void setupViewModel() {
        LearnWordsViewModel viewModel = ViewModelProviders.of(getActivity()).get(LearnWordsViewModel.class);
        viewModel.getCards().observe(this, (cardEntries) -> {
            Log.d(TAG, "setupViewModel: receive data from ViewModel to 'Learn words'");
            if (cardEntries.isEmpty()) {
                Log.d(TAG, "There is no cards retrieved from the DataBase");
                return;
            }
//            mCardEntries = cardEntries;
//            mCurrentCardId = 0;
//            mCurrentWord = mCardEntries.get(mCurrentCardId);
//            mQuestion.setText(mCurrentWord.getWord());
//            mAnswer.setText(mCurrentWord.getTranslation());
//            mCardCount = mCardEntries.size();
            try {
                CardEntry[] entries = new CardEntry[cardEntries.size()];
                ArrayList<Card> cards
                        = new ImagesFromLocalStorageQueryTask()
                        .execute(cardEntries.toArray(entries)).get();
                mAdapter.setCards(cards);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
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
