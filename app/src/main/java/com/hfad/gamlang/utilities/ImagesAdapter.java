package com.hfad.gamlang.utilities;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hfad.gamlang.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {
    private static final String TAG = "ImagesAdapter";
    private static ArrayList<String> imgsURL;

    public ImagesAdapter(ArrayList<String> imgsURL) {
        this.imgsURL = imgsURL;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recycler_listitem;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        ImageViewHolder viewHolder = new ImageViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {
        if (i >= imgsURL.size()) {
            return;
        }
        Log.i(TAG, "Picture URI: " + imgsURL.get(i));
        Picasso.get().load(imgsURL.get(i))
                .into(imageViewHolder.imgView);
    }

    @Override
    public int getItemCount() {
        return NetworkUtils.imageCount;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.iv_word_picture);
        }
    }
}
