package com.rxd.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //private CustomProgressBar customProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        customProgressBar = (CustomProgressBar) findViewById(R.id.custom_progress_bar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++){
                    customProgressBar.setPer(i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/



    }
}
