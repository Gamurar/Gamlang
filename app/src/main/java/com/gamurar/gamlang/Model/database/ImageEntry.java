package com.gamurar.gamlang.Model.database;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Images")
public class ImageEntry {

    @PrimaryKey
    @ColumnInfo(name="file_name")
    @NonNull
    private String fileName;

    public ImageEntry(@NonNull String fileName) {
        this.fileName = fileName;
    }

    @NonNull
    public String getFileName() {
        return fileName;
    }

    public void setFileName(@NonNull String fileName) {
        this.fileName = fileName;
    }

}
