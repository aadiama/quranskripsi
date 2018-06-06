package com.example.adiama.quranskripsi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class AyahDataSource {
    public final static String AYAH_TABLE_NAME = "quran";
    public final static String AYAH_ID = "_id";
    public final static String SURAH_ID = "surah_id";
    public final static String AYAH_NO = "verse_id";
    public final static String AYAH_ARABIC = "arabic";
    private static Cursor cursor;
    private DatabaseHelper databaseHelper;

    public AyahDataSource(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public ArrayList<Ayah> getAyahArrayList(String surah_id_param) {
        String[] params = new String[]{ surah_id_param };

        ArrayList<Ayah> ayahArrayList = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT quran._id,quran.surah_id," +
                "quran.verse_id,quran.arabic FROM quran WHERE surah_id = ?", params);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Ayah ayah = new Ayah();
            ayah.setId(cursor.getLong(cursor.getColumnIndex(AYAH_ID)));
            ayah.setSurahId(cursor.getLong(cursor.getColumnIndex(SURAH_ID)));
            ayah.setAyahNo(cursor.getLong(cursor.getColumnIndex(AYAH_NO)));
            ayah.setAyahArabic(cursor.getString(cursor.getColumnIndex(AYAH_ARABIC)));
            ayahArrayList.add(ayah);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return ayahArrayList;
    }
}
