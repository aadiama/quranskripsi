package com.example.adiama.quranskripsi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class TableDialog1 extends AppCompatActivity {
    ProgressBar geseran1,geseran2,geseran3;
    Button ton1, ton2, ton3;
    TextView text1,text2,text3;
    int val1 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_dialog1);

        geseran1 = (ProgressBar) findViewById(R.id.progressbarTL1);
        geseran2 = (ProgressBar) findViewById(R.id.progressbarTL2);
        geseran3 = (ProgressBar) findViewById(R.id.progressbarTL3);

        text1 = (TextView) findViewById(R.id.textPenandaTL1);
        text2 = (TextView) findViewById(R.id.textPenandaTL2);
        text3 = (TextView) findViewById(R.id.textPenandaTL3);

        ton1 = (Button) findViewById(R.id.buttonTL1);
        ton2 = (Button) findViewById(R.id.buttonTL2);
        ton3 = (Button) findViewById(R.id.buttonTL3);



        ton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                val1++;
                geseran1.setProgress(val1);
                text1.setText(val1);
            }
        });
    }
}