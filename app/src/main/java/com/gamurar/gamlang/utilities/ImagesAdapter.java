package com.gamurar.gamlang.utilities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamurar.gamlang.R;
import com.gamurar.gamlang.views.ImageViewBitmap;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {
    private static final String TAG = "ImagesAdapter";
    private ArrayList<Pair<String, Bitmap>> mImages;
    private ImageClickListener mImageClickListener;

    public interface ImageClickListener {
        void onImageClick(ImageViewBitmap imgView);
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
            imgView.setOnClickListener(v -> mImageClickListener.onImageClick(imgView));
        }

    }
}
