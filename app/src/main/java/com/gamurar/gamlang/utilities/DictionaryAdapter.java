package com.gamurar.gamlang.utilities;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.abdularis.civ.AvatarImageView;
import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.CardViewHolder> {
    private static final String TAG = "DictionaryAdapter";

    private static List<Card> mCards;
    private DictWordSelectListener mDictWordSelectListener;

    public boolean haveSelection = false;

    public interface DictWordSelectListener {
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
            if (!haveSelection) {
                mDictWordSelectListener.onFirstSelect(cardView, mCards.get(getAdapterPosition()));
                haveSelection = true;
            } else {
                haveSelection = mDictWordSelectListener.onNextSelect(cardView, mCards.get(getAdapterPosition()));
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
