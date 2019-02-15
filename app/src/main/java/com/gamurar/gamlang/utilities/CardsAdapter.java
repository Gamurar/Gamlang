package com.gamurar.gamlang.utilities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.R;
import com.gamurar.gamlang.views.ImageViewBitmap;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
            viewHolder.question.setText(card.getQuestion());
            viewHolder.answer.setText(card.getAnswer());
            if (card.getPictures() != null) {
                viewHolder.imageView.setImageBitmap(card.getPictures().get(0));
            } else {
                viewHolder.imageView.setVisibility(View.INVISIBLE);
            }

            if (!card.hasSound()) {
                viewHolder.playWord.setVisibility(ImageButton.GONE);
            } else {
                viewHolder.playWord.setVisibility(ImageButton.VISIBLE);
            }
        }
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
        TextView question;
        TextView answer;
        ImageView imageView;
        ImageButton playWord;
        LinearLayout answerContainer;

        private CardViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.card_question);
            playWord = itemView.findViewById(R.id.play_word);
            answer = itemView.findViewById(R.id.card_answer);
            imageView = itemView.findViewById(R.id.card_image);
            answerContainer = itemView.findViewById(R.id.answer_container);
            playWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCards.get(getAdapterPosition()).pronounce();
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerContainer.setVisibility(LinearLayout.VISIBLE);
                    Log.d(TAG, "onClick: Answer container clicked");
                }
            });
        }

    }
}
