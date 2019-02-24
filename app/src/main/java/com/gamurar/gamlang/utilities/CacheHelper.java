package com.gamurar.gamlang.utilities;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class CacheHelper {

    private static final String TAG = "Cache";


    private void clearCache(Context context) {
        Log.d(TAG, "Deleting all cache files...");
        File[] files = context.getCacheDir().listFiles();
        for (File file : files) {
            if (file.delete()) {
                Log.d(TAG, file.getName() + " deleted.");
            }
        }
    }

    @TargetApi(26)
    private void showCacheSize(Context context) throws IOException {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        long cacheQuota = storageManager.getCacheQuotaBytes(storageManager.getUuidForPath(context.getCacheDir()));
        long cacheSize = storageManager.getCacheSizeBytes(storageManager.getUuidForPath(context.getCacheDir()));
        Log.d(TAG, "cache quota: " + cacheQuota);
        Log.d(TAG, "cache size: " + cacheSize);
    }

    private void showCacheFiles(Context context) {
        String[] files = context.getCacheDir().list();
        Log.d(TAG, "-- cache files -- ");
        for (String file : files) {
            Log.d(TAG, "| cache file: " + file);
        }
    }

    public static File writeCacheFile(Context context, String content) {
        try {
            File file = new File(context.getCacheDir(), "main-cache");
            FileOutputStream out = new FileOutputStream(file);
            out.write(content.getBytes());
            out.flush();
            out.close();
            Log.d(TAG, "cache file path: " + file.getAbsolutePath());
            return file;
        } catch (IOException e) {
            // Error while creating file
            return null;
        }
    }

    public static void readCacheFile(File file) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line);
                content.append('\n');
            }
            br.close();

            Log.d(TAG, "cache file data: " + content.toString());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
