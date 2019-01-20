package com.hfad.gamlang.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.hfad.gamlang.views.ImageViewBitmap;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageHelper {

    private static final String TAG = "StorageHelper";

    private Context mContext;
    private File mPicturesDirectory;

    public StorageHelper(Context context) {
        mContext = context.getApplicationContext();

        File[] myDirs = mContext.getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        mPicturesDirectory = myDirs.length > 1 ? myDirs[1] : myDirs[0];
    }

    /**
     * @param imageView to save on the storage
     * @return the file name
     */
    public String saveImage(ImageViewBitmap imageView) {
        Bitmap finalBitmap = imageView.getBitmap();

        if (!mPicturesDirectory.exists()) {
            if (!mPicturesDirectory.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }
        }
        String fname = imageView.getId() + ".jpg";
        File file = new File(mPicturesDirectory, fname);
        Log.d(TAG, "saveImage: file path: " + file.getAbsolutePath());
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fname;
    }

    public void getImages(String imgsString) {
        if (imgsString == null) {
            Log.e(TAG, "getImages: There is no pictures");
            return;
        }
        if (!mPicturesDirectory.exists()) {
            Log.e(TAG, "getImages: There is no pictures directory");
            return;
        }

        String[] imgsName = imgsString.split(" ");

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                for (String fileName : imgsName) {
                    try {
                        File file = new File(mPicturesDirectory, fileName);
                        Bitmap bitmap = Picasso.get()
                                .load(file)
                                .get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });



    }
}
