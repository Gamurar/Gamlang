package com.gamurar.gamlang.utilities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gamurar.gamlang.R;
import com.gamurar.gamlang.View.AddWordsActivity;
import com.gamurar.gamlang.View.PickImageFragment;

import java.util.ArrayList;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.CardViewHolder>
    implements ProgressableAdapter {
    private static final String TAG = "ImagesAdapter";
    private ArrayList<Pair<String, String>> mCards;
    private ExploreCardClickListener mClickListener;
    private Context mContext;

    public interface ExploreCardClickListener {
        void onClick(String word, String translation);
    }

    public SuggestionAdapter(Context context, ExploreCardClickListener listener) {
        mCards = new ArrayList<>();
        mClickListener = listener;
        mContext = context;
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutIdForListItem = R.layout.suggestion_wordcard;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder imageViewHolder, int i) {
        Pair<String, String> card = mCards.get(i);
        imageViewHolder.mWord.setText(card.first);
        imageViewHolder.mTranslation.setText(card.second);
    }



    @Override
    public int getItemCount() {
        if (mCards == null) {
            return 0;
        } else {
            return mCards.size();
        }
    }

    public void clear() {
        mCards = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCards(ArrayList<Pair<String, String>> cards) {
        mCards = cards;
        notifyDataSetChanged();
    }

    public void addCard(Pair<String, String> card) {
        mCards.add(card);
        notifyItemInserted(mCards.size()-1);
    }

    @Override
    public void insert(Object item) {
        if (item instanceof Pair) {
            Pair<String, String> pair = (Pair<String, String>) item;
            addCard(pair);
        }
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        TextView mWord;
        TextView mTranslation;
        LinearLayout mContainer;


        private CardViewHolder(@NonNull View itemView) {
            super(itemView);
            mWord = itemView.findViewById(R.id.suggestion_word);
            mTranslation = itemView.findViewById(R.id.suggestion_translation);
            mContainer = itemView.findViewById(R.id.suggestion_card_container);
            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onClick(mWord.getText().toString(), mTranslation.getText().toString());
                }
            });
        }

    }
}
