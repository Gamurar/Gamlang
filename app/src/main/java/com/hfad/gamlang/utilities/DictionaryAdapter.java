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
    private DictWordSelectListener mDictWordSelectListener;
    private Context mContext;

    public static boolean haveSelection = false;

    public interface DictWordSelectListener {
        void onFirstSelect(View view, int wordId);

        boolean onNextSelect(View view, int wordId);

        boolean onUnselect(View view, int wordId);
    }

    public DictionaryAdapter(Context context, DictWordSelectListener dictWordSelectListener) {
        mDictWordSelectListener = dictWordSelectListener;
        mContext = context;
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
        holder.itemView.setBackgroundResource(android.R.color.white);
        holder.word.setText(mCards.get(position).getWord());
        holder.setCardId(mCards.get(position).getId());
    }

    @Override
    public int getItemCount() {
        if (mCards == null) {
            return 0;
        } else {
            return mCards.size();
        }
    }

    public void setCards(List<CardEntry> cards) {
        mCards = cards;
        notifyDataSetChanged();
    }

    class CardViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {
        private static final String TAG = "CardViewHolder";
        TextView word;
        CardView cardView;
        View listView;
        int cardId;

        private CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.dict_cardview);
            word = itemView.findViewById(R.id.word_listitem);
            word.setOnLongClickListener(this);
            word.setOnClickListener(this);
            listView = itemView;
        }

        public void setCardId(int id) {
            cardId = id;
        }

        @Override
        public boolean onLongClick(View view) {
            if (!haveSelection) {
                mDictWordSelectListener.onFirstSelect(cardView, cardId);
                haveSelection = true;
            } else {
                haveSelection = mDictWordSelectListener.onNextSelect(cardView, cardId);
            }
            return true;
        }

        @Override
        public void onClick(View view) {
            if (haveSelection) {
                haveSelection = mDictWordSelectListener.onNextSelect(cardView, cardId);
            }
        }
    }
}
