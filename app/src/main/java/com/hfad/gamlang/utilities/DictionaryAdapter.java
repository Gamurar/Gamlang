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
import androidx.recyclerview.widget.RecyclerView;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.CardViewHolder> {
    private static final String TAG = "DictionaryAdapter";

    private static List<CardEntry> mCards;

    public DictionaryAdapter(List<CardEntry> cards) {
        mCards = cards;
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int dictItemLayoutId = R.layout.word_listitem;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(dictItemLayoutId, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        if(position >= mCards.size()) {
            return;
        }
        holder.word.setText(mCards.get(position).getWord());
        //holder.itemView.setOnCreateContextMenuListener(mContextMenuListener);
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "CardViewHolder";
        TextView word;

        private CardViewHolder(@NonNull View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.word_listitem);
            //itemView.setOnCreateContextMenuListener(mContextMenuListener);
        }
    }
}
