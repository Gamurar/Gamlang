package com.gamurar.gamlang.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gamurar.gamlang.R;
import com.gamurar.gamlang.views.ImageViewBitmap;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardImagesAdapter extends RecyclerView.Adapter<CardImagesAdapter.ImageViewHolder> {
    private static final String TAG = "ImagesAdapter";
    private ArrayList<Bitmap> mImages;
    private ImagesAdapter.ImageClickListener mImageClickListener;
    private ImageViewHolder currentImageViewHolder;

    //For future implementations
    public interface ImageClickListener {
        void onImageClick(ImageViewBitmap imgView);
    }

    public CardImagesAdapter() {
        mImages = new ArrayList<>();
    }


    @NonNull
    @Override
    public CardImagesAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.image_griditem;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new CardImagesAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardImagesAdapter.ImageViewHolder imageViewHolder, int i) {
        imageViewHolder.imgView.setImageBitmap(mImages.get(i));
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

    public void setImages(ArrayList<Bitmap> imgs) {
        mImages = imgs;
        notifyDataSetChanged();
    }

    public void addImage(Bitmap image) {
        mImages.add(image);
        notifyItemInserted(mImages.size()-1);
    }

    public ImageViewHolder getCurrentImageViewHolder() {
        return currentImageViewHolder;
    }


    class ImageViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ImageViewHolder: itemView width: " + itemView.getWidth());
            imgView = itemView.findViewById(R.id.picture);
            currentImageViewHolder = this;
        }

    }
}
