package com.lanjiabin.testmusic;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class MusicDBManager {
    MusicDBHelper mMusicDBHelper;
    SQLiteDatabase mSQLiteDatabase;

    public MusicDBManager(Context context) {
        mMusicDBHelper = new MusicDBHelper(context);
        mSQLiteDatabase = mMusicDBHelper.getReadableDatabase();
    }

    public boolean updateSQLite(String sql, Object[] bindArgs) {
        boolean isSuccess = false;
        try {
            mSQLiteDatabase.execSQL(sql, bindArgs);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mSQLiteDatabase != null) {
                mSQLiteDatabase.close();
            }
        }
        return isSuccess;
    }

    public ArrayList<HashMap<String, String>> querySQLite(String sql, String[] bindArgs) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, bindArgs);
        if (cursor==null){
            return null;
        }
        int columnCount = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i < columnCount; i++) {
                String columnName = cursor.getColumnName(i);
                String columnValue = cursor.getString(cursor.getColumnIndex(columnName));
                if (columnValue == null) {
                    columnValue = "";
                }
                map.put(columnName, columnValue);
            }
            list.add(map);
        }
        return list;
    }
}
