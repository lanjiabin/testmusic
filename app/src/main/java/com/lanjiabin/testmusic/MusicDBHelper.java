package com.lanjiabin.testmusic;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MusicDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "musicPlaylist.db";
    private static final int DATABASE_VERSION = 1 ;

    public MusicDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS allsongslist"+
                    "(musicid VARCHAR PRIMARY KEY," +
                    "playlistname VARCHAR," +
                    "musictitle VARCHAR," +
                    "musicartist VARCHAR," +
                    "musicduration VARCHAR," +
                    "musicsize VARCHAR," +
                    "musicurl VARCHAR," +
                    "playlistcreatetime VARCHAR," +
                    "playlistupdatetime VARCHAR," +
                    "musicchannel VARCHAR," +
                    "musicquality VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS playlist"+
                "(playlistname VARCHAR PRIMARY KEY," +
                "playlistcreatetime VARCHAR," +
                "playlistupdatetime VARCHAR)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
