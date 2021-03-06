package com.gamurar.gamlang.utilities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.views.ImageViewBitmap;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardViewHolder> {
    private static final String TAG = "CardsAdapter";
    private ArrayList<Card> mCards;


    public CardsAdapter() {

    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.card_stackitem;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder viewHolder, int i) {
        if (i < mCards.size()) {
            viewHolder.answerContainer.setVisibility(LinearLayout.INVISIBLE);
            Card card = mCards.get(i);

            //viewHolder.stage.setText(getStageData(card));

            viewHolder.question.setText(card.getQuestion());
            viewHolder.answer.setText(card.getAnswer());
            viewHolder.adapter.setImages(card.getPictures());

            if (!card.hasSound()) {
                viewHolder.playWord.setVisibility(ImageButton.GONE);
            } else {
                viewHolder.playWord.setVisibility(ImageButton.VISIBLE);
            }
        }
    }

    private String getStageData(Card card) {
        StringBuilder stageStr = new StringBuilder("Stage: ");
        stageStr.append(card.getStage());
        stageStr.append('\n');
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss", Locale.getDefault());
        stageStr.append("Last review: ");
        if (card.getLastReview() == null) {
            stageStr.append("null");
        } else {
            stageStr.append(dateFormat.format(card.getLastReview()));
        }
        stageStr.append("\nNext review: ");
        if (card.getNextReview() == null) {
            stageStr.append("null");
        } else {
            stageStr.append(dateFormat.format(card.getNextReview()));
        }
        return stageStr.toString();
    }

    @Override
    public int getItemCount() {
        if (mCards == null) {
            return 0;
        } else {
            return mCards.size();
        }
    }

    public void setCards(ArrayList<Card> cards) {
        //Log.d(TAG, "setCards: first word in the card stack: " + cards.get(0).getQuestion());
        mCards = cards;
        notifyDataSetChanged();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        TextView stage;
        TextView question;
        TextView answer;
        ImageButton playWord;
        LinearLayout answerContainer;
        RecyclerView imagesRV;
        CardImagesAdapter adapter;

        private CardViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.card_question);
            playWord = itemView.findViewById(R.id.play_word);
            answer = itemView.findViewById(R.id.card_answer);
            answerContainer = itemView.findViewById(R.id.answer_container);
            stage = itemView.findViewById(R.id.stage);
            imagesRV = itemView.findViewById(R.id.rv_images);
            playWord.setOnClickListener(v -> mCards.get(getAdapterPosition()).pronounce());

            adapter = new CardImagesAdapter();
            GridLayoutManager layoutManager = new GridLayoutManager(itemView.getContext(), 2);
//            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    int itemCount = adapter.getItemCount() - 1;
//                    Log.d("CardImages", "item count: " + itemCount);
//                    Log.d("CardImages", "position: " + position);
//
//                    if (itemCount == position && (itemCount + 1) % 2 != 0) {
//                        if (adapter.getCurrentImageViewHolder() != null) {
//                        }
//                        return 2;
//                    } else {
//                        return 1;
//                    }
//                }
//            });
            imagesRV.setLayoutManager(layoutManager);
            imagesRV.setAdapter(adapter);

        }

    }
}
