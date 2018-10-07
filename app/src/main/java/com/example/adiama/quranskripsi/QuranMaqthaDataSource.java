package com.example.adiama.quranskripsi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

public class QuranMaqthaDataSource {
    public final static String MAQTHA_ID = "_id";
    public final static String SURAH_ID = "surah_id";
    public final static String AYAH_NO = "ayah_no";
    public final static String MAQTHA_ARABIC = "arabic";
    public final static String TIKRAR_NO = "tikrar";
    private static Cursor cursor;
    private DatabaseHelper databaseHelper;

    public QuranMaqthaDataSource(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public ArrayList<QuranMaqtha> getMaqthaArrayList(String surah_id_param) {
        String[] params = new String[]{ surah_id_param };

        ArrayList<QuranMaqtha> maqthaArrayList = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT quran_maqtha._id,quran_maqtha.surah_id," +
                "quran_maqtha.ayah_no,quran_maqtha.arabic,quran_maqtha.tikrar FROM quran_maqtha WHERE surah_id = ?", params);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            QuranMaqtha maqtha = new QuranMaqtha();
            maqtha.setId(cursor.getLong(cursor.getColumnIndex(MAQTHA_ID)));
            maqtha.setSurahId(cursor.getLong(cursor.getColumnIndex(SURAH_ID)));
            maqtha.setAyahNo(cursor.getLong(cursor.getColumnIndex(AYAH_NO)));
            maqtha.setMaqthaArabic(cursor.getString(cursor.getColumnIndex(MAQTHA_ARABIC)));
            maqtha.setTikrar(cursor.getLong(cursor.getColumnIndex(TIKRAR_NO)));
            maqthaArrayList.add(maqtha);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return maqthaArrayList;
    }
}
