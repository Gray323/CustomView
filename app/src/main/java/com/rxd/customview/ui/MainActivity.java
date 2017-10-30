package com.rxd.customview.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.rxd.customview.R;
import com.rxd.customview.widget.gesturelock.GestureLockViewGroup;

public class MainActivity extends AppCompatActivity {

    //private CustomProgressBar customProgressBar;

    private GestureLockViewGroup gestureLockViewGroup;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 0){
                gestureLockViewGroup.reset();
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //手势锁
        gestureLockViewGroup = (GestureLockViewGroup) findViewById(R.id.gestureLockViewGroup);
        gestureLockViewGroup.setAnswer(new int[]{1,2,3,4,5,6,7,8,9});
        gestureLockViewGroup.setTryTimes(1);
        gestureLockViewGroup.setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {
            @Override
            public void onBlockSelected(int cId) {

            }

            @Override
            public void onGestureEvent(boolean isMatched) {
                if (isMatched){
                    Toast.makeText(MainActivity.this, "答案正确", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "答案错误", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessageDelayed(0, 2000);

                }
            }

            @Override
            public void onUnmatchedExceedBoundary() {
                Log.d("sc", "超过尝试次数");
            }
        });

/*      //进度条
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
