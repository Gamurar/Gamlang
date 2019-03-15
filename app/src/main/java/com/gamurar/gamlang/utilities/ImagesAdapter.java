package com.gamurar.gamlang.utilities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamurar.gamlang.R;
import com.gamurar.gamlang.views.ImageViewBitmap;

import java.util.ArrayList;
import java.util.HashSet;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {
    private static final String TAG = "ImagesAdapter";
    private ArrayList<Pair<String, Bitmap>> mImages;
    private ImageClickListener mImageClickListener;
    private HashSet<Integer> selectedPositions = new HashSet<>();

    public interface ImageClickListener {
        void onImageClick(Pair<String, Bitmap> imgInfo);
    }

    public ImagesAdapter(ImageClickListener imageClickListener) {
        mImageClickListener = imageClickListener;
        mImages = new ArrayList<>();
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.image_listitem;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {
        Pair<String, Bitmap> image = mImages.get(i);
        imageViewHolder.imgView.setImageBitmap(image.second);
        imageViewHolder.imgView.setCode(image.first);

        if (selectedPositions.contains(i)) {
            imageViewHolder.imgView.setBorderColor(imageViewHolder.itemView.getResources().getColor(R.color.colorAccent));
        } else {
            imageViewHolder.imgView.setBorderColor(imageViewHolder.itemView.getResources().getColor(android.R.color.white));
        }
    }



    @Override
    public int getItemCount() {
        if (mImages == null) {
            return 0;
        } else {
            return mImages.size();
        }
    }

    public void clear() {
        mImages = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setImages(ArrayList<Pair<String, Bitmap>> imgs) {
        mImages = imgs;
        notifyDataSetChanged();
    }

    public void addImage(Pair<String, Bitmap> image) {
        mImages.add(image);
        notifyItemInserted(mImages.size()-1);
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageViewBitmap imgView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.iv_word_picture);
            imgView.setOnClickListener(v -> {
                mImageClickListener.onImageClick(new Pair<>(imgView.getCode(), imgView.getBitmap()));

                if (!selectedPositions.contains(getAdapterPosition())) {
                    selectedPositions.add(getAdapterPosition());
                    imgView.setBorderColor(itemView.getResources().getColor(R.color.colorAccent));
                } else {
                    selectedPositions.remove(getAdapterPosition());
                    imgView.setBorderColor(itemView.getResources().getColor(android.R.color.white));
                }
            });
        }

    }
}
