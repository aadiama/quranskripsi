package com.example.adiama.quranskripsi;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AyahWordActivity extends AppCompatActivity {
    private ArrayList<Ayah> ayahArrayList;
    private ArrayList<QuranMaqtha> maqthaArrayList;

    Dialog dia;
    int val1 = 0;
    Long surah_id;
    DatabaseHelper dbHelper;
    protected Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayah_activity);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("surah_name");
        surah_id = bundle.getLong("surah_id");
        setTitle(title);

        ayahArrayList = getAyahArrayList(String.valueOf(surah_id));
        maqthaArrayList = getMaqthaArrayList(String.valueOf(surah_id));

        String htmlText = "<html><body style=\"text-align:right; font-size: 24px\"> %s </body></Html>";
        String myData = "";

        Integer tikrarNo = 1;
        for(Ayah currentX : ayahArrayList) {
            Long curSurahId = currentX.getSurahId();
            Long curAyahNo = currentX.getAyahNo();
            String strAyahNo = String.valueOf(curAyahNo);
            strAyahNo = arabicNumber(strAyahNo);
            strAyahNo = "("+String.valueOf(strAyahNo)+")";
            String theAyah = currentX.getAyahArabic();

            Long[] blueArray = new Long[50];
            Integer tIndex = 0;
            Integer lastTikrarAyah = 1;

            for(QuranMaqtha tMaqtha : maqthaArrayList){
                Long mSurahId = tMaqtha.getSurahId();
                Long mAyahNo = tMaqtha.getAyahNo();
                String theMaqtha = tMaqtha.getMaqthaArabic();
                Integer mTikrar = (int)(long)tMaqtha.getTikrar();

                if(curSurahId == mSurahId && curAyahNo == mAyahNo && theAyah.startsWith(theMaqtha)){
                    String textReplace = "<span style='color: blue'>"+theMaqtha+"</span>";
                    theAyah = theAyah.replace(theMaqtha,textReplace);

                    if(tIndex > 0 && tikrarNo != mTikrar){
                        lastTikrarAyah = ((int)(long)mAyahNo) - 1;
                        tikrarNo++;
                    }

                    break;
                }

                tIndex++;
            }

            if(tikrarNo == 1 || tikrarNo == 3){
                strAyahNo = "<span style='background-color: rgb(200, 225, 255, 0.5)'>"+strAyahNo+"</span>";
                theAyah = "<span style='background-color: rgb(200, 225, 255, 0.5)'>"+theAyah+"</span>";
            }

            myData += theAyah+strAyahNo;
        }

        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.loadData(String.format(htmlText, myData), "text/html", "utf-8");
    }

    public String arabicNumber(String stdNumber){
        String val = stdNumber;
        char[] arabicChars = {'٠','١','٢','٣','٤','٥','٦','٧','٨','٩'};
        StringBuilder builder = new StringBuilder();
        for(int i =0;i<val.length();i++){
            if(Character.isDigit(val.charAt(i))){
                builder.append(arabicChars[(int)(val.charAt(i))-48]);
            }
            else{
                builder.append(val.charAt(i));
            }
        }

        return builder.toString();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.a) {
            showDialogA();
        }else if(id == R.id.b){
            showDialogB();
        }else if(id == R.id.c){
            showDialogC();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialogA() {
        dia = new Dialog(AyahWordActivity.this);
        dia.setContentView(R.layout.activity_table_dialog1);
        dia.setTitle("Penanda Tilawah");
        dia.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dia.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dia.show();
        dia.getWindow().setAttributes(lp);
// surat no, tipe
        Button ton1 = (Button) dia.findViewById(R.id.buttonTL1);
        final ProgressBar geseran1 = (ProgressBar) dia.findViewById(R.id.progressbarTL1);
        final TextView text1 = (TextView) dia.findViewById(R.id.textPenandaTL1);


        Button tonsave = (Button) dia.findViewById(R.id.butonsave);
        dbHelper = new DatabaseHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        cursor = db.rawQuery("SELECT * FROM kebutuhannutrisi WHERE nama = '" + jenis + "'", null);
        for (int i = 0; i < cursor.getCount(); i++) {

        }
            val1 = 21;
            geseran1.setProgress(val1);
            text1.setText(String.valueOf(val1));

            ton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    increaseloop(val1, geseran1, text1);
                }
            });

            tonsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("insert into penanda_hafalan(surah_no, penanda_type, row_index, value) values('" +
                            surah_id + "','" +//surah_no
                            "TL" + "','" +//penanda_type (TL,TM,MR)
                            1 + "','" +//row_index
                            text1.getText().toString() + "')");//value
                    Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();
                }
            });
        }


    public void increaseloop(int val, ProgressBar  geseran, TextView text){
        if(val1 < 40){
            val1++;
            Log.d("Loop per click :", String.valueOf(val1));
            geseran.setProgress(val1);
            text.setText(String.valueOf(val1));
        }
    }

    public void showDialogB() {
        dia = new Dialog(AyahWordActivity.this);
        dia.setContentView(R.layout.activity_table_dialog2);
        dia.setTitle("Penanda Tikrar");
        dia.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dia.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dia.show();
        dia.getWindow().setAttributes(lp);
    }

    public void showDialogC() {
        dia = new Dialog(AyahWordActivity.this);
        dia.setContentView(R.layout.activity_table_dialog3);
        dia.setTitle("Penanda Muraja'ah");
        dia.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dia.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dia.show();
        dia.getWindow().setAttributes(lp);
    }

    private ArrayList<Ayah> getAyahArrayList(String surah_id) {
        ArrayList<Ayah> ayahArrayList = new ArrayList<Ayah>();
        AyahDataSource ayahDataSource = new AyahDataSource(this);
        ayahArrayList = ayahDataSource.getAyahArrayList(surah_id);
        return ayahArrayList;
    }

    private ArrayList<QuranMaqtha> getMaqthaArrayList(String surah_id) {
        ArrayList<QuranMaqtha> maqthaArrayList = new ArrayList<QuranMaqtha>();
        QuranMaqthaDataSource maqthaDataSource = new QuranMaqthaDataSource(this);
        maqthaArrayList = maqthaDataSource.getMaqthaArrayList(surah_id);
        return maqthaArrayList;
    }
}
