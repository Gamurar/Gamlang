package com.gamurar.gamlang.utilities;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.abdularis.civ.AvatarImageView;
import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.R;

import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.CardViewHolder> {
    private static final String TAG = "DictionaryAdapter";

    private static List<Card> mCards;
    private DictWordSelectListener mDictWordSelectListener;
    private HashSet<Integer> selectedCardIds = new HashSet<>();

    public boolean haveSelection = false;

    public interface DictWordSelectListener {
        void onWordClick(int position, Card card, AvatarImageView picture, TextView word);

        void onFirstSelect(View view, Card card);

        boolean onNextSelect(View view, Card card);

        boolean onUnselect(View view, Card card);
    }

    public DictionaryAdapter(DictWordSelectListener dictWordSelectListener) {
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
        Card card = mCards.get(position);
        holder.itemView.setBackgroundResource(android.R.color.white);
        holder.word.setText(card.getQuestion());
        holder.setCardId(card.getId());
        holder.picture.setText(card.getQuestion().toUpperCase());
        if (card.hasPictures()) {
            holder.picture.setImageBitmap(card.getPictures().get(0));
            holder.picture.setState(AvatarImageView.SHOW_IMAGE);
        }

        ViewCompat.setTransitionName(holder.word, card.getQuestion());

        if (card.hasPictures()) {
            ViewCompat.setTransitionName(holder.picture, card.getPictureFileNames()[0]);
            holder.itemView.setOnClickListener(v -> mDictWordSelectListener
                    .onWordClick(holder.getAdapterPosition(), card, holder.picture, holder.word));
        } else {
            holder.itemView.setOnClickListener(v -> mDictWordSelectListener
                    .onWordClick(holder.getAdapterPosition(), card, null, holder.word));
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

    public void setCards(List<Card> cards) {
        mCards = cards;
        notifyDataSetChanged();
    }

    public void removeSelectedCards() {
        int removed = 0;
        for (int i : selectedCardIds) {
            if (i > mCards.size() - 1) {
                i -= removed;
            }
            mCards.remove(i);
            notifyItemRemoved(i);
            removed++;
        }
    }

    class CardViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {
        private static final String TAG = "CardViewHolder";
        TextView word;
        CardView cardView;
        View listView;
        AvatarImageView picture;
        int cardId;

        private CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.dict_cardview);
            word = itemView.findViewById(R.id.word_listitem);
            picture = itemView.findViewById(R.id.dict_listitem_picture);
            cardView.setOnLongClickListener(this);
            cardView.setOnClickListener(this);
            listView = itemView;
        }

        public void setCardId(int id) {
            cardId = id;
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            if (!haveSelection) {
                mDictWordSelectListener.onFirstSelect(cardView, mCards.get(position));
                selectedCardIds.add(position);
                haveSelection = true;
            } else {
//                haveSelection = mDictWordSelectListener.onNextSelect(cardView, mCards.get(getAdapterPosition()));
                if (selectedCardIds.contains(position)) {
                    mDictWordSelectListener.onUnselect(cardView, mCards.get(position));
                    selectedCardIds.remove(position);
                } else {
                    mDictWordSelectListener.onNextSelect(cardView, mCards.get(position));
                    selectedCardIds.add(position);
                }
                haveSelection = !selectedCardIds.isEmpty();
            }
            return true;
        }

        @Override
        public void onClick(View view) {
            if (haveSelection) {
                haveSelection = mDictWordSelectListener.onNextSelect(cardView, mCards.get(getAdapterPosition()));
            }
        }
    }
}
