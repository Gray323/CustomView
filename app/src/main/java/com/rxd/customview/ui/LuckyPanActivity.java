package com.rxd.customview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.rxd.customview.R;
import com.rxd.customview.widget.LuckyPanView;

public class LuckyPanActivity extends AppCompatActivity {

    private LuckyPanView luckyPanView;
    private ImageView mStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_pan);

        luckyPanView = (LuckyPanView) findViewById(R.id.id_luckypan);
        mStartBtn = (ImageView) findViewById(R.id.id_start_btn);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!luckyPanView.isStart()){
                    mStartBtn.setImageResource(R.mipmap.stop);
                    luckyPanView.luckyStart(1);
                }else{
                    if(!luckyPanView.isShouldEnd()){
                        mStartBtn.setImageResource(R.mipmap.start);
                        luckyPanView.luckyEnd();
                    }
                }
            }
        });
    }
}
