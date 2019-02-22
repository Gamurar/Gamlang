package com.gamurar.gamlang.Model.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ImageDao {

    @Insert
    void insertImage(ImageEntry imageEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateImage(ImageEntry imageEntry);

    @Delete
    void deleteImage(ImageEntry imageEntry);

    @Query("DELETE FROM image WHERE file_name IN(:fileNames)")
    void deleteImagesByFileName(String[] fileNames);

    @Query("DELETE FROM image WHERE file_name = :fileName")
    void deleteImageByFileName(String fileName);

    @Query("DELETE FROM image")
    void deleteAllImages();

    @Query("SELECT * FROM image WHERE card_id IN(:ids)")
    List<ImageEntry> loadImagesByCardId(Integer[] ids);

    @Query("SELECT * FROM image WHERE card_id = :id")
    List<ImageEntry> loadImagesByCardId(int id);

}