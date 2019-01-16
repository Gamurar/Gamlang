package com.hfad.gamlang.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hfad.gamlang.R;
import com.hfad.gamlang.database.CardEntry;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.CardViewHolder> {
    private static final String TAG = "DictionaryAdapter";

    private static List<CardEntry> mCards;
    private View.OnLongClickListener mLongClickListener;

    public DictionaryAdapter(List<CardEntry> cards, View.OnLongClickListener longClickListener) {
        mCards = cards;
        mLongClickListener = longClickListener;
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int dictItemLayoutId = R.layout.word_listitem;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(dictItemLayoutId, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        if (position >= mCards.size()) {
            return;
        }
        holder.word.setText(mCards.get(position).getWord());
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "CardViewHolder";
        TextView word;
        CardView cardView;
        View view;

        private CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.dict_cardview);
            word = itemView.findViewById(R.id.word_listitem);
            //word.setOnLongClickListener(mLongClickListener);
            view = itemView;
            view.setOnLongClickListener(mLongClickListener);
        }
    }
}
