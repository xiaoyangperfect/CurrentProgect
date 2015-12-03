package com.airppt.airppt.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2015/9/22.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 3;

    private static DBHelper instance;

    private String CREATE_TAB_SHARE = "create table if not exists "
            + DBAdapter.TAB_SHARE_CONTENT + " ("
            + DBAdapter.SHARE_CONTENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DBAdapter.SHARE_WORK_ID + " INTEGER,"
            + DBAdapter.SHARE_TITLE + " TEXT,"
            + DBAdapter.SHARE_CONTENT + " TEXT,"
            + DBAdapter.SHARE_ISMUSICMOD + " TEXT,"
            + DBAdapter.SHARE_ISMUSICINUSE + " TEXT,"
            + DBAdapter.SHARE_MUSICTILTE + " TEXT,"
            + DBAdapter.SHARE_MUSICAUTHOR + " TEXT);";


    private DBHelper (Context context) {
        super(context, DBAdapter.DB_NAME, null, DB_VERSION);
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TAB_SHARE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("ALTER TABLE " + DBAdapter.TAB_SHARE_CONTENT + " ADD COLUMN other STRING");
        db.execSQL("DROP TABLE IF EXISTS " + DBAdapter.TAB_SHARE_CONTENT);
        onCreate(db);
    }
}
