package com.rxd.customview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.rxd.customview.R;

/**
 * Created by Administrator on 2017/10/19.
 */

public class CustomVolumControlBar extends View{

    private int mFirstColor;//第一圈的颜色
    private int mSecondColor;//第二圈的颜色
    private int mCircleWidth;//圈的宽度
    private Paint mPaint;//画笔
    private int mCurrentState = 1;//当前状态
    private Bitmap mImage;//中间的图片
    private int mSplitSize;//每个块块之间的间距
    private int mCount;//个数
    private Rect mRect;

    public CustomVolumControlBar(Context context) {
        this(context, null);
    }

    public CustomVolumControlBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomVolumControlBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomVolumControlBar, defStyleAttr, 0);
        mFirstColor = a.getColor(R.styleable.CustomVolumControlBar_mfirstColor, Color.GREEN);
        mSecondColor = a.getColor(R.styleable.CustomVolumControlBar_msecondColor, Color.CYAN);
        mImage = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.CustomVolumControlBar_bg, 0));
        mCircleWidth = a.getDimensionPixelSize(R.styleable.CustomVolumControlBar_mcircleWidth, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
        mCount = a.getInteger(R.styleable.CustomVolumControlBar_dotCount, 20);
        mSplitSize = a.getInteger(R.styleable.CustomVolumControlBar_splitSize, 20);
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);//定义线段断电形状为圆头
        mRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = getWidth() / 2;
        int radius = center - mCircleWidth / 2;

        drawOval(canvas, center, radius);

        int relRadius = radius - mCircleWidth / 2;
        mRect.left = (int)(relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;
        mRect.top = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;
        mRect.bottom = (int) (mRect.left + Math.sqrt(2) * relRadius);
        mRect.right = (int) (mRect.left + Math.sqrt(2) * relRadius);

        /**
         * 如果图片比较小，那么根据图片的尺寸放置到正中心
         */
        if (mImage != null){
            if (mImage.getWidth() < Math.sqrt(2) * relRadius){
                mRect.left = (int) (mRect.left + Math.sqrt(2) * relRadius * 1.0f / 2 - mImage.getWidth() * 1.0f / 2);
                mRect.top = (int) (mRect.top + Math.sqrt(2) * relRadius * 1.0f / 2 - mImage.getHeight() * 1.0f / 2);
                mRect.right = (int) (mRect.left + mImage.getWidth());
                mRect.bottom = (int) (mRect.top + mImage.getHeight());
            }
            canvas.drawBitmap(mImage, null, mRect, mPaint);
        }

    }

    /**
     * 根据参数画出每个小块
     * @param canvas
     * @param center
     * @param radius
     */
    private void drawOval(Canvas canvas, int center, int radius){
        float itemSize = (360 * 1.0f - mCount * mSplitSize) / mCount;
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius);

        mPaint.setColor(mFirstColor);
        for (int i = 0; i < mCount; i++){
            //根据进度画圆弧
            canvas.drawArc(oval, i * (itemSize + mSplitSize), itemSize, false, mPaint);
        }

        mPaint.setColor(mSecondColor);
        for (int i = 0; i < mCurrentState; i++){
            canvas.drawArc(oval, i * (itemSize + mSplitSize), itemSize, false, mPaint);
        }
    }

    public void up(){
        if (mCurrentState < mCount && mCurrentState >= 0){
            mCurrentState++;
        }
        postInvalidate();
    }

    public void down(){
        if (mCurrentState < mCount && mCurrentState >= 0){
            mCurrentState--;
        }
        postInvalidate();
    }

    private int xDown, xUp;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xDown = (int)event.getY();
                break;
            case MotionEvent.ACTION_UP:
                xUp = (int)event.getY();
                if (xUp > xDown){
                    down();
                }else{
                    up();
                }
                break;
        }
        return true;
    }
}
