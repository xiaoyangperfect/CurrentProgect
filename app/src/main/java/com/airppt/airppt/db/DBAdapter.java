package com.airppt.airppt.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by user on 2015/9/22.
 */
public class DBAdapter {
    public static String DB_NAME = "airppt.db";

    public static String TAB_SHARE_CONTENT = "share_content";

    //the key
    public static String SHARE_CONTENT_ID = "_id";
    public static String SHARE_WORK_ID = "share_work_id";
    public static String SHARE_TITLE = "share_title";
    public static String SHARE_CONTENT = "share_content";
    //1:true;0:false
    public static String SHARE_ISMUSICMOD = "share_ismusicmodle";
    //1:true;0:false
    public static String SHARE_ISMUSICINUSE = "share_ismusicinuse";
    public static String SHARE_MUSICTILTE = "share_musictitle";
    public static String SHARE_MUSICAUTHOR = "share_musicauthor";


    private SQLiteDatabase db;
    public DBAdapter(Context context) {
        db = DBHelper.getInstance(context).getWritableDatabase();
    }

    public void closeDB() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    /**
     * insert share content to db
     * @return
     */
    public void insertShareContent(ShareDBEntry entry) {
        Cursor cursor = db.query(TAB_SHARE_CONTENT, new String[]{SHARE_WORK_ID, SHARE_TITLE, SHARE_CONTENT, SHARE_ISMUSICMOD, SHARE_ISMUSICINUSE, SHARE_MUSICTILTE, SHARE_MUSICAUTHOR},
                SHARE_WORK_ID + "=?", new String[]{entry.getId()}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(SHARE_TITLE, entry.getTitle());
            values.put(SHARE_WORK_ID, entry.getId());
            values.put(SHARE_CONTENT, entry.getContent());
            values.put(SHARE_ISMUSICMOD, entry.getIsMusicModle());
            values.put(SHARE_ISMUSICINUSE, entry.getIsMusicInuse());
            values.put(SHARE_MUSICTILTE, entry.getMusicTitle());
            values.put(SHARE_MUSICAUTHOR, entry.getMusicAuthor());
            db.insert(TAB_SHARE_CONTENT, null, values);
        } else {
            updateShareContent(entry);
        }
        cursor.close();
    }

    public void updateShareContent(ShareDBEntry entry) {
        ContentValues values = new ContentValues();
        values.put(SHARE_TITLE, entry.getTitle());
        values.put(SHARE_CONTENT, entry.getContent());
        values.put(SHARE_ISMUSICMOD, entry.getIsMusicModle());
        values.put(SHARE_ISMUSICINUSE, entry.getIsMusicInuse());
        values.put(SHARE_MUSICTILTE, entry.getMusicTitle());
        values.put(SHARE_MUSICAUTHOR, entry.getMusicAuthor());
        db.update(TAB_SHARE_CONTENT, values, SHARE_WORK_ID + "=?", new String[]{entry.getId()});
    }

    /**
     * 创建或更新分享音乐信息
     * @param entry
     */
    public void updateShareMusic(ShareDBEntry entry) {
        Cursor cursor = db.query(TAB_SHARE_CONTENT, new String[]{SHARE_WORK_ID, SHARE_TITLE, SHARE_CONTENT, SHARE_ISMUSICMOD, SHARE_ISMUSICINUSE, SHARE_MUSICTILTE, SHARE_MUSICAUTHOR},
                SHARE_WORK_ID + "=?", new String[]{entry.getId()}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(SHARE_TITLE, entry.getTitle());
            values.put(SHARE_WORK_ID, entry.getId());
            values.put(SHARE_CONTENT, entry.getContent());
            values.put(SHARE_ISMUSICMOD, entry.getIsMusicModle());
            values.put(SHARE_ISMUSICINUSE, entry.getIsMusicInuse());
            values.put(SHARE_MUSICTILTE, entry.getMusicTitle());
            values.put(SHARE_MUSICAUTHOR, entry.getMusicAuthor());
            db.insert(TAB_SHARE_CONTENT, null, values);
        } else {
            ContentValues values = new ContentValues();
            values.put(SHARE_ISMUSICMOD, entry.getIsMusicModle());
            values.put(SHARE_ISMUSICINUSE, entry.getIsMusicInuse());
            values.put(SHARE_MUSICTILTE, entry.getMusicTitle());
            values.put(SHARE_MUSICAUTHOR, entry.getMusicAuthor());
            db.update(TAB_SHARE_CONTENT, values, SHARE_WORK_ID + "=?", new String[]{entry.getId()});
        }
    }

    /**
     * 创建或更新分享内容信息
     * @param entry
     */
    public void updateShareText(ShareDBEntry entry) {
        Cursor cursor = db.query(TAB_SHARE_CONTENT, new String[]{SHARE_WORK_ID, SHARE_TITLE, SHARE_CONTENT, SHARE_ISMUSICMOD, SHARE_ISMUSICINUSE, SHARE_MUSICTILTE, SHARE_MUSICAUTHOR},
                SHARE_WORK_ID + "=?", new String[]{entry.getId()}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(SHARE_TITLE, entry.getTitle());
            values.put(SHARE_WORK_ID, entry.getId());
            values.put(SHARE_CONTENT, entry.getContent());
            values.put(SHARE_ISMUSICMOD, entry.getIsMusicModle());
            values.put(SHARE_ISMUSICINUSE, entry.getIsMusicInuse());
            values.put(SHARE_MUSICTILTE, entry.getMusicTitle());
            values.put(SHARE_MUSICAUTHOR, entry.getMusicAuthor());
            db.insert(TAB_SHARE_CONTENT, null, values);
        } else {
            ContentValues values = new ContentValues();
            values.put(SHARE_TITLE, entry.getTitle());
            values.put(SHARE_CONTENT, entry.getContent());
            db.update(TAB_SHARE_CONTENT, values, SHARE_WORK_ID + "=?", new String[]{entry.getId()});
        }
    }

    /**
     *
     * @param id
     */
    public ShareDBEntry getShareContent(String id) {
        Cursor cursor = db.query(TAB_SHARE_CONTENT, new String[]{SHARE_WORK_ID, SHARE_TITLE, SHARE_CONTENT, SHARE_ISMUSICMOD, SHARE_ISMUSICINUSE, SHARE_MUSICTILTE, SHARE_MUSICAUTHOR},
                SHARE_WORK_ID + "=?", new String[]{id}, null, null, null);
        if (cursor.getCount() == 0) {
            return null;
        } else {
            cursor.moveToFirst();
            ShareDBEntry entry = new ShareDBEntry();
            entry.setId(cursor.getString(cursor.getColumnIndex(SHARE_WORK_ID)));
            entry.setTitle(cursor.getString(cursor.getColumnIndex(SHARE_TITLE)));
            entry.setContent(cursor.getString(cursor.getColumnIndex(SHARE_CONTENT)));
            entry.setIsMusicModle(cursor.getString(cursor.getColumnIndex(SHARE_ISMUSICMOD)));
            entry.setIsMusicInuse(cursor.getString(cursor.getColumnIndex(SHARE_ISMUSICINUSE)));
            entry.setMusicTitle(cursor.getString(cursor.getColumnIndex(SHARE_MUSICTILTE)));
            entry.setMusicAuthor(cursor.getString(cursor.getColumnIndex(SHARE_MUSICAUTHOR)));
            cursor.close();
            return entry;
        }
    }
}
