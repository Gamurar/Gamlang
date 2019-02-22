package com.gamurar.gamlang.Model.database;

import androidx.room.ColumnInfo;

public class ImageAndSound {

    @ColumnInfo(name="image")
    public String imageFileName;

    @ColumnInfo(name="sound")
    public String soundFileName;
}
