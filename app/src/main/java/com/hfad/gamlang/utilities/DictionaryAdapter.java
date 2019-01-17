package com.hfad.gamlang.utilities;

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
    private DictWordSelectListener mDictWordSelectListener;

    public static boolean haveSelection = false;

    public interface DictWordSelectListener {
        void onFirstSelect(View view, int wordId);

        boolean onNextSelect(View view, int wordId);

        boolean onUnselect(View view, int wordId);
    }

    public DictionaryAdapter(List<CardEntry> cards, DictWordSelectListener dictWordSelectListener) {
        mCards = cards;
        mDictWordSelectListener = dictWordSelectListener;
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

    class CardViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {
        private static final String TAG = "CardViewHolder";
        TextView word;
        CardView cardView;
        View listView;

        private CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.dict_cardview);
            word = itemView.findViewById(R.id.word_listitem);
            word.setOnLongClickListener(this);
            word.setOnClickListener(this);
            listView = itemView;
        }

        @Override
        public boolean onLongClick(View view) {
            if (!haveSelection) {
                mDictWordSelectListener.onFirstSelect(itemView, this.getAdapterPosition());
                haveSelection = true;
            } else {
                haveSelection = mDictWordSelectListener.onNextSelect(itemView, this.getAdapterPosition());
            }
            return true;
        }

        @Override
        public void onClick(View view) {
            if (haveSelection) {
                haveSelection = mDictWordSelectListener.onNextSelect(itemView, this.getAdapterPosition());
            }
        }
    }
}
