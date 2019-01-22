package com.hfad.gamlang.utilities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hfad.gamlang.R;
import com.hfad.gamlang.views.ImageViewBitmap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {
    private static final String TAG = "ImagesAdapter";
    private static Map<String, Bitmap> mImages;
    private static Iterator<Map.Entry<String, Bitmap>> mImagesIterator;
    private ImageClickListener mImageClickListener;

    public interface ImageClickListener {
        void onImageClick(ImageViewBitmap imgView);
    }

    public ImagesAdapter(ImageClickListener imageClickListener) {
        mImageClickListener = imageClickListener;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.image_listitem;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        ImageViewHolder viewHolder = new ImageViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {
        if (mImagesIterator.hasNext()) {
            Map.Entry<String, Bitmap> pair = mImagesIterator.next();
            imageViewHolder.imgView.setImageBitmap(pair.getValue());
            imageViewHolder.imgView.setCode(pair.getKey());
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

    public void setImages(HashMap<String, Bitmap> imgsURL) {
        mImages = imgsURL;
        mImagesIterator = imgsURL.entrySet().iterator();

        notifyDataSetChanged();
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
