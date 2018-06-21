package com.example.adiama.quranskripsi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class AyahWordActivity extends AppCompatActivity {
    private ArrayList<Ayah> ayahArrayList;
    private ArrayList<QuranMaqtha> maqthaArrayList;

    public Long surah_no;

    Dialog dia;

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

        String htmlText = "<html><body style=\"text-align:right; font-size: 24px\"> %s </body></Html>";
        String myData = "";

        Integer tikrarNo = 1;
        for (Ayah currentX : ayahArrayList) {
            Long curSurahId = currentX.getSurahId();
            Long curAyahNo = currentX.getAyahNo();
            String strAyahNo = String.valueOf(curAyahNo);

            strAyahNo = arabicNumber(strAyahNo);
            strAyahNo = "<img src='b"+curAyahNo+".png' width='30' height='30'/>";

            //strAyahNo = "(" + String.valueOf(strAyahNo) + ")";
            String theAyah = currentX.getAyahArabic();

            Long[] blueArray = new Long[50];
            Integer tIndex = 0;
            Integer lastTikrarAyah = 1;

            for (QuranMaqtha tMaqtha : maqthaArrayList) {
                Long mSurahId = tMaqtha.getSurahId();
                Long mAyahNo = tMaqtha.getAyahNo();
                String theMaqtha = tMaqtha.getMaqthaArabic();
                Integer mTikrar = (int) (long) tMaqtha.getTikrar();

                if (curSurahId == mSurahId && curAyahNo == mAyahNo && theAyah.startsWith(theMaqtha)) {
                    String textReplace = "<span style='color: blue'>" + theMaqtha + "</span>";
                    theAyah = theAyah.replace(theMaqtha, textReplace);

                    if (tIndex > 0 && tikrarNo != mTikrar) {
                        lastTikrarAyah = ((int) (long) mAyahNo) - 1;
                        tikrarNo++;
                    }

                    break;
                }

                tIndex++;
            }

            if (tikrarNo == 1 || tikrarNo == 3) {
                strAyahNo = "<span style='background-color: rgb(200, 225, 255, 0.5)'>" + strAyahNo + "</span>";
                theAyah = "<span style='background-color: rgb(200, 225, 255, 0.5)'>" + theAyah + "</span>";
            }

            myData += theAyah + strAyahNo;
        }

        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.loadDataWithBaseURL("file:///android_asset/", htmlFormat(myData), "text/html", "UTF-8", null);
        //webView.loadData(String.format(htmlText, myData), "text/html", "utf-8");
    }

    public static String htmlFormat(String mainBody) {
        String html = "";

        String bimillahDiv = "<div style=\"text-align: center;\"> بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ </div><br/>";

        html = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>@font-face {font-family: 'Roboto'; src: url('file:///android_asset/Roboto-Regular.ttf');}</style>" +
                "<title>" +
                "</title>" +
                "<link href='main.css' rel='stylesheet' type='text/css' />" +
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
                "<html>" +
                "<head>" +
                "<style>@font-face {font-family: 'Roboto'; src: url('file:///android_asset/Roboto-Regular.ttf');}</style>" +
                "<title>" +
                "</title>" +
                "<link href='main.css' rel='stylesheet' type='text/css' />" +
                "</head>" +
                "<body style='font-size: 20px'>" +
                mainBody +
                "</body>" +
                "</html>";

        return html;
    }

    public String arabicNumber(String stdNumber) {
        String val = stdNumber;
        char[] arabicChars = {'٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < val.length(); i++) {
            if (Character.isDigit(val.charAt(i))) {
                builder.append(arabicChars[(int) (val.charAt(i)) - 48]);
            } else {
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
        } else if (id == R.id.b) {
            showDialogB();
        } else if (id == R.id.c) {
            showDialogC();
        } else if (id == R.id.d) {
            showDialogD();
        }

        return super.onOptionsItemSelected(item);
    }

    public void openDialogProcess(Dialog theDialog, String typePenanda){
        LayoutInflater li = theDialog.getLayoutInflater();
        ViewGroup theLayout = (ViewGroup) li.inflate(R.layout.activity_table_dialog1, null);
        ViewGroup tableLayout = (ViewGroup) theLayout.findViewById(R.id.tableLayoutTL);

        if(typePenanda == "TL"){
            theLayout = (ViewGroup) li.inflate(R.layout.activity_table_dialog1, null);
            tableLayout = (ViewGroup) theLayout.findViewById(R.id.tableLayoutTL);
        }
        else if(typePenanda == "TM"){
            theLayout = (ViewGroup) li.inflate(R.layout.activity_table_dialog2, null);
            tableLayout = (ViewGroup) theLayout.findViewById(R.id.tableLayoutTM);
        }
        else if(typePenanda == "MR"){
            theLayout = (ViewGroup) li.inflate(R.layout.activity_table_dialog3, null);
            tableLayout = (ViewGroup) theLayout.findViewById(R.id.tableLayoutMR);
        }

        ArrayList<ProgressBar> progressBarArrayList = new ArrayList<ProgressBar>();

        for (int i = 0; i < tableLayout.getChildCount(); i++){
            ViewGroup tableRow = (ViewGroup) tableLayout.getChildAt(i);

            for(int j = 0; j < tableRow.getChildCount(); j++){
                if (tableRow.getChildAt(j) instanceof ProgressBar){
                    ProgressBar theProgressBar = (ProgressBar) tableRow.getChildAt(j);

                    progressBarArrayList.add(theProgressBar);
                }
            }
        }

        DatabaseHelper dbcenter = new DatabaseHelper(this);
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM penanda_hafalan WHERE surah_no = '"+surah_no+"' and penanda_type = '"+typePenanda+"'",null);

        if(cursor.getCount() > 0){
            int rowIndex = 1;
            for(ProgressBar pb : progressBarArrayList){
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String cSurahNo = cursor.getString(1).toString();
                    int cRowIndex = Integer.parseInt(cursor.getString(3).toString());
                    String cPenandaType = cursor.getString(2).toString();
                    int cPenandaValue = Integer.parseInt(cursor.getString(4).toString());

                    if(rowIndex == cRowIndex){
                        ProgressBar thePB = (ProgressBar) dia.findViewById(pb.getId());
                        thePB.setProgress(cPenandaValue);

                        View thePBView = (View) dia.findViewById(pb.getId());
                        ViewGroup thePBParent = (ViewGroup) thePBView.getParent();
                        View thePenandaView = thePBParent.getChildAt(thePBParent.indexOfChild(thePBView)-1);
                        int thePenandaId = thePenandaView.getId();

                        TextView thePenandaText = (TextView) dia.findViewById(thePenandaId);
                        thePenandaText.setText(String.valueOf(cPenandaValue));
                    }
                }

                rowIndex++;
            }
        }
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

        openDialogProcess(dia,"TL");
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

        openDialogProcess(dia,"TM");
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

        openDialogProcess(dia,"MR");
    }
    public void showDialogD() {
        String myData = "<table border='1' style='border-collapse: collapse; width: 100%'>"+
                "<thead><tr><th style='text-align: center;'>Kata-kata Kunci Hafalan</th></tr></thead>"+
                "<tbody>";

        DatabaseHelper dbcenter = new DatabaseHelper(this);
        SQLiteDatabase db = dbcenter.getReadableDatabase();
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
            String surahAyahAsal = "<span style='font-size: 14px;'><b>(" + cursor2.getString(1).toString() + ":" + cursor2.getString(2).toString() + ")</b></span>";
            String surahAyahMirip = "<span style='font-size: 14px'><b>(" + cursor2.getString(3).toString() + ":" + cursor2.getString(4).toString() + ")</b></span>";

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
        wv.loadDataWithBaseURL("file:///android_asset/",htmlInformasi(myData),"text/html", "UTF-8", null);
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

    public void addPenandaTL(View v) {
        int buttonId = v.getId();
        String buttonName = getResources().getResourceEntryName(buttonId);

        ViewGroup container = (ViewGroup) v.getParent();

        View textPenandaTL = container.getChildAt(container.indexOfChild(v)-2);
        int textPenandaTLId = textPenandaTL.getId();
        String textPenandaTLName = getResources().getResourceEntryName(textPenandaTLId);

        View progressPenandaTL = container.getChildAt(container.indexOfChild(v)-1);
        int progressPenandaTLId = progressPenandaTL.getId();
        String progressPenandaTLName = getResources().getResourceEntryName(progressPenandaTLId);

        final TextView penandaTL = (TextView) dia.findViewById(textPenandaTLId);
        int penandaTLLastValue = Integer.parseInt(String.valueOf(penandaTL.getText()));
        penandaTLLastValue = (penandaTLLastValue < 40 ? penandaTLLastValue+1 : 40);
        penandaTL.setText(String.valueOf(penandaTLLastValue));

        final ProgressBar progressTL = (ProgressBar) dia.findViewById(progressPenandaTLId);
        int progressTLLastValue = progressTL.getProgress();
        progressTLLastValue = (progressTLLastValue < 40 ? progressTLLastValue+1 : 40);
        progressTL.setProgress(progressTLLastValue);
    }

    public void saveFunction(View v) {
        String penandaType = "TL";

        int buttonId = v.getId();
        String buttonName = getResources().getResourceEntryName(buttonId);
        ViewGroup container = (ViewGroup) v.getParent();
        ViewGroup tableLayout = (ViewGroup) container.findViewById(R.id.tableLayoutTL);

        if(buttonName.contains("TM")){
            tableLayout = (ViewGroup) container.findViewById(R.id.tableLayoutTM);
            penandaType = "TM";
        }
        else if(buttonName.contains("MR")){
            tableLayout = (ViewGroup) container.findViewById(R.id.tableLayoutMR);
            penandaType = "MR";
        }

        ArrayList<ProgressBar> progressBarArrayList = new ArrayList<ProgressBar>();

        for (int i = 0; i < tableLayout.getChildCount(); i++){
            ViewGroup tableRow = (ViewGroup) tableLayout.getChildAt(i);

            for(int j = 0; j < tableRow.getChildCount(); j++){
                if (tableRow.getChildAt(j) instanceof ProgressBar){
                    ProgressBar theProgressBar = (ProgressBar) tableRow.getChildAt(j);
                    progressBarArrayList.add(theProgressBar);
                }
            }
        }

        DatabaseHelper dbcenter = new DatabaseHelper(this);
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM penanda_hafalan WHERE surah_no = '"+surah_no+"' and penanda_type = '"+penandaType+"'",null);

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