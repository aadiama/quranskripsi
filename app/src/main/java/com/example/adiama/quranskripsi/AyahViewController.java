package com.example.adiama.quranskripsi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import java.util.ArrayList;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class AyahViewController extends AppCompatActivity {
    private ArrayList<Ayah> ayahArrayList;
    private ArrayList<QuranMaqtha> maqthaArrayList;

    public Long surah_no;

    Dialog dia;
    DatabaseHelper dbHelper;
    MediaPlayer m;
    SeekBar mSeekBar;
    Context c;

    Button btnPlay;
    Boolean isPlay = false;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayah_activity);


        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("surah_name");
        title = title.replace("Surah ", "");

        Long surah_id = bundle.getLong("surah_id");
        surah_no = surah_id;
        setTitle(title);

        ayahArrayList = getAyahArrayList(String.valueOf(surah_id));
        maqthaArrayList = getMaqthaArrayList(String.valueOf(surah_id));

        String myData = "";

        Integer tikrarNo = 1;
        for (Ayah currentX : ayahArrayList) {
            Long curSurahId = currentX.getSurahId();
            Long curAyahNo = currentX.getAyahNo();
            String strAyahNo;

            strAyahNo = "&nbsp;<img src='b" + curAyahNo + ".png' width='30' height='30'/>&nbsp;";
            String theAyah = currentX.getAyahArabic();
            Integer tIndex = 0;

            for (QuranMaqtha tMaqtha : maqthaArrayList) {
                Long mSurahId = tMaqtha.getSurahId();
                Long mAyahNo = tMaqtha.getAyahNo();
                String theMaqtha = tMaqtha.getMaqthaArabic();
                Integer mTikrar = (int) (long) tMaqtha.getTikrar();

                if (curSurahId == mSurahId && curAyahNo == mAyahNo && theAyah.startsWith(theMaqtha)) {
                    String textReplace = "<span style='color: blue'>" + theMaqtha + "</span>";
                    theAyah = theAyah.replace(theMaqtha, textReplace);

                    if (tIndex > 0 && tikrarNo != mTikrar) {
                        tikrarNo++;
                    }

                    break;
                }

                tIndex++;
            }

            if (tikrarNo == 1 || tikrarNo == 3) {
                strAyahNo = "<span style='background-color: #e8edff'>" + strAyahNo + "</span>";
                theAyah = "<span style='background-color: #e8edff'>" + theAyah + "</span>";
            }

            myData += theAyah + strAyahNo;
        }

        WebView webView = findViewById(R.id.webView1);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadDataWithBaseURL("file:///android_asset/", htmlFormat(myData),
                "text/html", "UTF-8", null);


        btnPlay = findViewById(R.id.play);
        Button btnStop = findViewById(R.id.stop);
        mSeekBar = findViewById(R.id.seekBar);

        c = getApplicationContext();
        m = MediaPlayer.create(getApplicationContext(), getResources().getIdentifier(
                "surah" + surah_no, "raw", getPackageName()));
        mSeekBar.setMax(m.getDuration());

        //Register button click listener
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(btnPlay);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop(btnPlay);
            }
        });

        mSeekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);
                return false;
            }
        });
    }



    @Override
    public void onBackPressed(){
        if(m != null){
            mSeekBar.setProgress(0);
            m.stop();
            m.release();
            m = null;
        }

        finish();
        return;
    }

    // This is event handler thumb moving event
    private void seekChange(View v){
        if(m != null){
            SeekBar sb = (SeekBar)v;
            m.seekTo(sb.getProgress());
        }
    }

    public void startPlayProgressUpdater() {
        if(m != null){
            mSeekBar.setProgress(m.getCurrentPosition());
        }

        if (m != null && m.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,1000);
        }
        else{
            isPlay = false;

            if(m != null){
                m.pause();
            }

            Drawable top = getResources().getDrawable(R.drawable.play);
            btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
        }
    }

    //Play/Pause music
    public void play(Button but){
        if(m == null){
            m = MediaPlayer.create(getApplicationContext(), getResources().getIdentifier(
                    "surah"+surah_no,"raw",getPackageName()));
        }

        if (isPlay == false) {
            Drawable top = getResources().getDrawable(R.drawable.pause);
            btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);

            try{
                isPlay = true;

                if(m != null){
                    m.start();
                }

                startPlayProgressUpdater();
            }catch (IllegalStateException e) {
                isPlay = false;

                if(m != null){
                    m.pause();
                }
            }
        }else {
            Drawable top = getResources().getDrawable(R.drawable.play);
            btnPlay.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
            isPlay = false;

            if(m != null){
                m.pause();
            }
        }
    }

    //Stop music play
    public void stop(Button but1){
        try {
            if(m != null){
                Drawable top = getResources().getDrawable(R.drawable.play);
                but1.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
                mSeekBar.setProgress(0);
                m.stop();
                m.release();
                m = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static String htmlFormat(String mainBody) {
        String html = "";

        String bimillahDiv = "<div style=\"text-align: center;\"> بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ </div><br/>";

        html = "<!DOCTYPE html>" +
                "<html dir='rtl' lang='ar'>" +
                "<head>" +
                "<style>@font-face {font-family: 'Roboto'; src: url('file:///android_asset/Roboto-Regular.ttf');}</style>" +
                "<title>" +
                "</title>" +
                "<link href='penanda.css' rel='stylesheet' type='text/css' />" +
                "</head>" +
                "<body>" +
                bimillahDiv +
                mainBody +
                "</body>" +
                "</html>";

        return html;
    }

    public static String htmlInformasi(String mainBody) {
        String html = "";

        html = "<!DOCTYPE html>" +
                "<html dir='rtl' lang='ar'>" +
                "<head>" +
                "<style>@font-face {font-family: 'Roboto'; src: url('file:///android_asset/Roboto-Regular.ttf');}</style>" +
                "<title>" +
                "</title>" +
                "<link href='penanda.css' rel='stylesheet' type='text/css' />" +
                "</head>" +
                "<body style='font-size: 20px'>" +
                mainBody +
                "</body>" +
                "</html>";

        return html;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.penanda, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.a) {
            showDialogTilawah();
        } else if (id == R.id.b) {
            showDialogTikrar();
        } else if (id == R.id.c) {
            showDialogMurajaah();
        } else if (id == R.id.d) {
            showDialogInformation();
        }

        return super.onOptionsItemSelected(item);
    }

//    fungsi untuk mengisi text ayat tikrar pada penanda kolom tikrar
    public void setTextTikrar(Cursor cursor, int textTikrarId, int theTM, int rowIndex){
        String theText = "";

        if(cursor.getCount() > 0){
            int totalPartTM = 0;
            int firstAyat = 0;
            int lastAyat = 0;

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int theTikrar = Integer.parseInt(cursor.getString(4).toString());

                if(theTM == theTikrar){
                    totalPartTM++;
                }
            }

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int ayahNo = Integer.parseInt(cursor.getString(2).toString());
                int theTikrar = Integer.parseInt(cursor.getString(4).toString());

                if(totalPartTM == 1 && theTM == theTikrar && ((rowIndex == 1 || rowIndex == 4 ||
                        rowIndex == 8 || rowIndex == 13))){
                    if(i < cursor.getCount()-1){
                        cursor.moveToPosition(i+1);
                    }

                    int ayahNoNext = Integer.parseInt(cursor.getString(2).toString());

                    firstAyat = ayahNo;
                    lastAyat = (ayahNoNext-1);

                    if(i == cursor.getCount()-1){
                        lastAyat = ayahArrayList.size();
                    }
                }
                else if(totalPartTM > 1 && theTM == theTikrar){
                    if(rowIndex == 1 || rowIndex == 4 || rowIndex == 8 || rowIndex == 13){
                        if(i < cursor.getCount()-1){
                            cursor.moveToPosition(i+1);
                        }

                        int ayahNoNext = Integer.parseInt(cursor.getString(2).toString());

                        firstAyat = ayahNo;
                        lastAyat = (ayahNoNext-1);

                        if(i == cursor.getCount()-1){
                            lastAyat = ayahArrayList.size();
                        }

                        break;
                    }
                    else if(rowIndex == 2 || rowIndex == 5 || rowIndex == 9 || rowIndex == 14){
                        if(i < cursor.getCount()-1){
                            cursor.moveToPosition(i+1);
                        }

                        int ayahNoNext = Integer.parseInt(cursor.getString(2).toString());

                        firstAyat = ayahNo;
                        lastAyat = (ayahNoNext-1);

                        if(i == cursor.getCount()-1){
                            lastAyat = ayahArrayList.size();
                        }
                    }
                }
            }

            if(firstAyat > 0 && lastAyat > 0){
                theText = firstAyat + "-" + lastAyat;

                if(firstAyat == lastAyat){
                    theText = ""+firstAyat;
                }
            }
        }

        TextView textTikrar = dia.findViewById(textTikrarId);
        textTikrar.setText(theText);
    }

//    fungsi untu
    public void openDialogProcess(Dialog theDialog, String typePenanda){

        ArrayList<ProgressBar> progressBarArrayList = new ArrayList<>();

        LayoutInflater li = theDialog.getLayoutInflater();
        ViewGroup theLayout = (ViewGroup) li.inflate(R.layout.table_dialog_tilawah, null);
        ViewGroup tableLayout = theLayout.findViewById(R.id.tableLayoutTL);

        if(typePenanda == "TL"){
            theLayout = (ViewGroup) li.inflate(R.layout.table_dialog_tilawah, null);
            tableLayout = theLayout.findViewById(R.id.tableLayoutTL);
        }
        else if(typePenanda == "TM"){
            theLayout = (ViewGroup) li.inflate(R.layout.table_dialog_tikrar, null);
            tableLayout = theLayout.findViewById(R.id.tableLayoutTM);
        }
        else if(typePenanda == "MR"){
            theLayout = (ViewGroup) li.inflate(R.layout.table_dialog_murajaah, null);
            tableLayout = theLayout.findViewById(R.id.tableLayoutMR);
        }
        for (int i = 0; i < tableLayout.getChildCount(); i++){
            ViewGroup tableRow = (ViewGroup) tableLayout.getChildAt(i);

            for(int j = 0; j < tableRow.getChildCount(); j++){
                if (tableRow.getChildAt(j) instanceof ProgressBar){
                    ProgressBar theProgressBar = (ProgressBar) tableRow.getChildAt(j);

                    progressBarArrayList.add(theProgressBar);
                }
            }
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursorTikrar = db.rawQuery("SELECT * FROM quran_maqtha WHERE surah_id = '"
                +surah_no+"' order by ayah_no asc",null);

        int rowPBIndex = 1;
        int theTM = 1;
        for(ProgressBar pb : progressBarArrayList){
            if(rowPBIndex == 1 || rowPBIndex == 2){
                theTM = 1;
            }
            else if(rowPBIndex == 4 || rowPBIndex == 5){
                theTM = 2;
            }
            else if(rowPBIndex == 8 || rowPBIndex == 9){
                theTM = 3;
            }
            else if(rowPBIndex == 13 || rowPBIndex == 14){
                theTM = 4;
            }

            View thePBView = dia.findViewById(pb.getId());
            ViewGroup thePBParent = (ViewGroup) thePBView.getParent();

            if(typePenanda == "TM" && (rowPBIndex == 1 || rowPBIndex == 2 || rowPBIndex == 4 || rowPBIndex == 5
                    || rowPBIndex == 8 || rowPBIndex == 9 || rowPBIndex == 13 || rowPBIndex == 14)){

                View textTikrarView = thePBParent.getChildAt(thePBParent.indexOfChild(thePBView)-2);
                int textTikrarId = textTikrarView.getId();
                setTextTikrar(cursorTikrar,textTikrarId,theTM,rowPBIndex);
            }

            rowPBIndex++;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM penanda_hafalan WHERE surah_no = '"
                +surah_no+"' and penanda_type = '"+typePenanda+"' order by row_index asc",null);

        if(cursor.getCount() > 0){
            int rowIndex = 1;

            for(ProgressBar pb : progressBarArrayList){
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);

                    int cRowIndex = Integer.parseInt(cursor.getString(3).toString());
                    int cPenandaValue = Integer.parseInt(cursor.getString(4).toString());

                    if(rowIndex == cRowIndex){
                        ProgressBar thePB = dia.findViewById(pb.getId());
                        thePB.setProgress(cPenandaValue);

                        View thePBView = dia.findViewById(pb.getId());
                        ViewGroup thePBParent = (ViewGroup) thePBView.getParent();

                        View thePenandaView = thePBParent.getChildAt(thePBParent.indexOfChild(thePBView)-1);
                        int thePenandaId = thePenandaView.getId();
                        TextView thePenandaText = dia.findViewById(thePenandaId);
                        thePenandaText.setText(String.valueOf(cPenandaValue));

                        break;
                    }
                }

                rowIndex++;
            }
        }
    }

    public void showDialogTilawah() {
        dia = new Dialog(AyahViewController.this);
        dia.setContentView(R.layout.table_dialog_tilawah);
        dia.setTitle("Penanda Tilawah");
        dia.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dia.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dia.show();
        dia.getWindow().setAttributes(lp);

        openDialogProcess(dia,"TL");
    }

    public void showDialogTikrar() {
        dia = new Dialog(AyahViewController.this);
        dia.setContentView(R.layout.table_dialog_tikrar);
        dia.setTitle("Penanda Tikrar");
        dia.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dia.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dia.show();
        dia.getWindow().setAttributes(lp);

        openDialogProcess(dia,"TM");
    }

    public void showDialogMurajaah() {
        dia = new Dialog(AyahViewController.this);
        dia.setContentView(R.layout.table_dialog_murajaah);
        dia.setTitle("Penanda Muraja'ah");
        dia.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dia.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dia.show();
        dia.getWindow().setAttributes(lp);

        openDialogProcess(dia,"MR");
    }

    public void showDialogInformation() {
        String myData = "<table border='1' style='border-collapse: collapse; width: 100%'>"+
                "<thead><tr><th style='text-align: center;'>Kata-kata Kunci Hafalan</th></tr></thead>"+
                "<tbody>";

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM kata_kunci_hafalan WHERE surah_no = '"+surah_no+"'",null);
        for (int i = 0; i<cursor.getCount();i++) {
            cursor.moveToPosition(i);
            String kataPertama = "<span style='color: blue'>"+cursor.getString(2).toString()+"</span>";

            myData += "<tr>";
            myData += "<td>";
            myData += kataPertama + " ..... " + cursor.getString(3).toString();
            myData += "</td>";
            myData += "</tr>";
        }

        myData += "</tbody>";
        myData += "</table>";
        myData += "<br/>";

        String myData2 = "<table border='1' style='border-collapse: collapse; width: 100%'>"+
                "<thead><tr><th style='text-align: center;'>Ayat-ayat yang Mirip</th></tr></thead>"+
                "<tbody>";

        Cursor cursor2 = db.rawQuery("SELECT * FROM ayah_mirip WHERE surah_no = '"+surah_no+"'",null);
        for (int i = 0; i<cursor2.getCount();i++) {
            cursor2.moveToPosition(i);

            String ayahAsal = "<span style='color: blue'>" + cursor2.getString(5).toString() + "</span>";
            String ayahMirip = cursor2.getString(6).toString();
            String surahAyahAsal = "<span style='font-size: 14px;'><b>("
                    + cursor2.getString(1).toString() + ":" + cursor2.getString(2).toString() + ")</b></span>";
            String surahAyahMirip = "<span style='font-size: 14px'><b>("
                    + cursor2.getString(3).toString() + ":" + cursor2.getString(4).toString() + ")</b></span>";

            myData2 += "<tr>";
            myData2 += "<td>";
            myData2 += surahAyahAsal + " " + ayahAsal + "<br/>" + surahAyahMirip + " " + ayahMirip;
            myData2 += "</td>";
            myData2 += "</tr>";
        }

        myData2 += "</tbody>";
        myData2 += "</table>";

        myData += myData2;

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        WebView wv = new WebView(this);
        wv.loadDataWithBaseURL("file:///android_asset/",htmlInformasi(myData),
                "text/html", "UTF-8", null);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void minusPenandaTikrar(View v) {

        ViewGroup container = (ViewGroup) v.getParent();

        View textPenandaTL = container.getChildAt(container.indexOfChild(v)-2);
        int textPenandaTLId = textPenandaTL.getId();

        View progressPenandaTL = container.getChildAt(container.indexOfChild(v)-1);
        int progressPenandaTLId = progressPenandaTL.getId();

        final TextView penandaTL = dia.findViewById(textPenandaTLId);
        int penandaTLLastValue = Integer.parseInt(String.valueOf(penandaTL.getText()));
        penandaTLLastValue = penandaTLLastValue-1;

        if(penandaTLLastValue < 0){
            penandaTLLastValue = 0;
        }

        penandaTL.setText(String.valueOf(penandaTLLastValue));

        final ProgressBar progressTL = dia.findViewById(progressPenandaTLId);
        int progressTLLastValue = progressTL.getProgress();
        progressTLLastValue = progressTLLastValue-1;
        progressTL.setProgress(progressTLLastValue);
    }

    public void addPenandaTikrar(View v) {

        ViewGroup container = (ViewGroup) v.getParent();

        View textPenandaTL = container.getChildAt(container.indexOfChild(v)-3);
        int textPenandaTLId = textPenandaTL.getId();

        View progressPenandaTL = container.getChildAt(container.indexOfChild(v)-2);
        int progressPenandaTLId = progressPenandaTL.getId();

        final TextView penandaTL = dia.findViewById(textPenandaTLId);
        int penandaTLLastValue = Integer.parseInt(String.valueOf(penandaTL.getText()));
        penandaTLLastValue = (penandaTLLastValue < 40 ? penandaTLLastValue+1 : 40);
        penandaTL.setText(String.valueOf(penandaTLLastValue));

        final ProgressBar progressTL = dia.findViewById(progressPenandaTLId);
        int progressTLLastValue = progressTL.getProgress();
        progressTLLastValue = (progressTLLastValue < 40 ? progressTLLastValue+1 : 40);
        progressTL.setProgress(progressTLLastValue);
    }

    public void minusPenandaTilawah(View v) {
        ViewGroup container = (ViewGroup) v.getParent();

        View textPenandaTL = container.getChildAt(container.indexOfChild(v)-2);
        int textPenandaTLId = textPenandaTL.getId();

        View progressPenandaTL = container.getChildAt(container.indexOfChild(v)-1);
        int progressPenandaTLId = progressPenandaTL.getId();

        final TextView penandaTL = dia.findViewById(textPenandaTLId);
        int penandaTLLastValue = Integer.parseInt(String.valueOf(penandaTL.getText()));
        penandaTLLastValue = penandaTLLastValue-1;

        if(penandaTLLastValue < 0){
            penandaTLLastValue = 0;
        }

        penandaTL.setText(String.valueOf(penandaTLLastValue));

        final ProgressBar progressTL = dia.findViewById(progressPenandaTLId);
        int progressTLLastValue = progressTL.getProgress();
        progressTLLastValue = progressTLLastValue-1;
        progressTL.setProgress(progressTLLastValue);
    }

    public void addPenandaTilawah(View v) {

        ViewGroup container = (ViewGroup) v.getParent();

        View textPenandaTL = container.getChildAt(container.indexOfChild(v)-3);
        int textPenandaTLId = textPenandaTL.getId();

        View progressPenandaTL = container.getChildAt(container.indexOfChild(v)-2);
        int progressPenandaTLId = progressPenandaTL.getId();

        final TextView penandaTL = dia.findViewById(textPenandaTLId);
        int penandaTLLastValue = Integer.parseInt(String.valueOf(penandaTL.getText()));
        penandaTLLastValue = (penandaTLLastValue < 120 ? penandaTLLastValue+1 : 120);
        penandaTL.setText(String.valueOf(penandaTLLastValue));

        final ProgressBar progressTL = dia.findViewById(progressPenandaTLId);
        int progressTLLastValue = progressTL.getProgress();
        progressTLLastValue = (progressTLLastValue < 120 ? progressTLLastValue+1 : 120);
        progressTL.setProgress(progressTLLastValue);
    }


    public void minusPenandaMurajaah(View v) {
        ViewGroup container = (ViewGroup) v.getParent();

        View textPenandaTL = container.getChildAt(container.indexOfChild(v)-2);
        int textPenandaTLId = textPenandaTL.getId();

        View progressPenandaTL = container.getChildAt(container.indexOfChild(v)-1);
        int progressPenandaTLId = progressPenandaTL.getId();

        final TextView penandaTL = dia.findViewById(textPenandaTLId);
        int penandaTLLastValue = Integer.parseInt(String.valueOf(penandaTL.getText()));
        penandaTLLastValue = penandaTLLastValue-1;

        if(penandaTLLastValue < 0){
            penandaTLLastValue = 0;
        }

        penandaTL.setText(String.valueOf(penandaTLLastValue));

        final ProgressBar progressTL = dia.findViewById(progressPenandaTLId);
        int progressTLLastValue = progressTL.getProgress();
        progressTLLastValue = progressTLLastValue-1;
        progressTL.setProgress(progressTLLastValue);
    }

    public void addPenandaMurajaah(View v) {
        ViewGroup container = (ViewGroup) v.getParent();

        View textPenandaTL = container.getChildAt(container.indexOfChild(v)-3);
        int textPenandaTLId = textPenandaTL.getId();

        View progressPenandaTL = container.getChildAt(container.indexOfChild(v)-2);
        int progressPenandaTLId = progressPenandaTL.getId();

        final TextView penandaTL = dia.findViewById(textPenandaTLId);
        int penandaTLLastValue = Integer.parseInt(String.valueOf(penandaTL.getText()));
        penandaTLLastValue = (penandaTLLastValue < 160 ? penandaTLLastValue+1 : 160);
        penandaTL.setText(String.valueOf(penandaTLLastValue));

        final ProgressBar progressTL = dia.findViewById(progressPenandaTLId);
        int progressTLLastValue = progressTL.getProgress();
        progressTLLastValue = (progressTLLastValue < 160 ? progressTLLastValue+1 : 160);
        progressTL.setProgress(progressTLLastValue);
    }

//    fungsi untuk menyimpan penanda ke database
    public void saveFunction(View v) {
        String penandaType = "TL";

        int buttonId = v.getId();
        String buttonName = getResources().getResourceEntryName(buttonId);
        ViewGroup container = (ViewGroup) v.getParent();
        ViewGroup tableLayout = container.findViewById(R.id.tableLayoutTL);

        if(buttonName.contains("TM")){
            tableLayout = container.findViewById(R.id.tableLayoutTM);
            penandaType = "TM";
        }
        else if(buttonName.contains("MR")){
            tableLayout = container.findViewById(R.id.tableLayoutMR);
            penandaType = "MR";
        }

        ArrayList<ProgressBar> progressBarArrayList = new ArrayList();

        for (int i = 0; i < tableLayout.getChildCount(); i++){
            ViewGroup tableRow = (ViewGroup) tableLayout.getChildAt(i);

            for(int j = 0; j < tableRow.getChildCount(); j++){
                if (tableRow.getChildAt(j) instanceof ProgressBar){
                    ProgressBar theProgressBar = (ProgressBar) tableRow.getChildAt(j);
                    progressBarArrayList.add(theProgressBar);
                }
            }
        }

        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM penanda_hafalan WHERE surah_no = '"
                +surah_no+"' and penanda_type = '"+penandaType+"'",null);

        int rowIndex = 1;
        for(ProgressBar pb : progressBarArrayList){
            if(cursor.getCount() > 0){
                db.execSQL("update penanda_hafalan set " +
                        "value = '" + pb.getProgress() + "'" +
                        "where surah_no = '" + surah_no + "' " +
                        "and row_index = '" + rowIndex + "' " +
                        "and penanda_type = '" + penandaType + "'");
            }
            else{
                db.execSQL("insert into penanda_hafalan(surah_no, penanda_type, row_index, value) values('" +
                        surah_no + "','" +//surah_no
                        penandaType + "','" +//penanda_type (TL,TM,MR)
                        rowIndex + "','" +//row_index
                        pb.getProgress() + "')");//value
            }
            rowIndex++;
        }

        dia.dismiss();
        Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();
    }

    private ArrayList<Ayah> getAyahArrayList(String surah_id) {
        ArrayList<Ayah> ayahArrayList;
        AyahDataSource ayahDataSource = new AyahDataSource(this);
        ayahArrayList = ayahDataSource.getAyahArrayList(surah_id);
        return ayahArrayList;
    }

    private ArrayList<QuranMaqtha> getMaqthaArrayList(String surah_id) {
        ArrayList<QuranMaqtha> maqthaArrayList;
        QuranMaqthaDataSource maqthaDataSource = new QuranMaqthaDataSource(this);
        maqthaArrayList = maqthaDataSource.getMaqthaArrayList(surah_id);
        return maqthaArrayList;
    }
}
