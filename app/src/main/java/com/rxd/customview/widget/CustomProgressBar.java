package com.rxd.customview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.rxd.customview.R;

/**
 * Created by Administrator on 2017/10/19.
 */

public class CustomProgressBar extends View{

    private int mFirstColor;//第一个圈的颜色
    private int mSecondColor;//第二个圈的颜色
    private int mCircleWidth;//圆的宽度
    private Paint mPaint;//画笔
    private float mAngle = 0;//当前进度对应的圆弧角度
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                invalidate();
            }
        }
    };

    public CustomProgressBar(Context context) {
        this(context, null);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, defStyleAttr, 0);
        mFirstColor = a.getColor(R.styleable.CustomProgressBar_fistColor, Color.GREEN);
        mSecondColor = a.getColor(R.styleable.CustomProgressBar_secondColor, Color.RED);
        mCircleWidth = a.getDimensionPixelSize(R.styleable.CustomProgressBar_circleWidth, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = getWidth() / 2;
        int radius = center - mCircleWidth;//半径
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius);
        mPaint.setColor(mFirstColor);
        canvas.drawCircle(center, center, radius, mPaint);
        mPaint.setColor(mSecondColor);
        canvas.drawArc(oval, -90, mAngle, false, mPaint);
    }

    public void setPer(float per){
        mAngle = per / 100 * 360;
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);
    }

}
