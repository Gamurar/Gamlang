package com.hfad.gamlang.utilities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfad.gamlang.Card;
import com.hfad.gamlang.R;
import com.hfad.gamlang.views.ImageViewBitmap;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardViewHolder> {
    private static final String TAG = "CardsAdapter";
    private ArrayList<Card> mCards;
    private Context mContext;



    public CardsAdapter(Context context) {
        mContext = context;
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = mContext;
        int layoutIdForListItem = R.layout.card_stackitem;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder viewHolder, int i) {
        if (i < mCards.size()) {
            Card card = mCards.get(i);
            viewHolder.question.setText(card.getQuestion());
            viewHolder.answer.setText(card.getAnswer());
            if (card.getPictures() != null) {
                viewHolder.imageView.setImageBitmap(card.getPictures().get(0));
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

        private CardViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.card_question);
            answer = itemView.findViewById(R.id.card_answer);
            imageView = itemView.findViewById(R.id.card_image);
        }

    }
}