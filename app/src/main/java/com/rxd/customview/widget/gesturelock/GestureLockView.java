package com.rxd.customview.widget.gesturelock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * Created by Administrator on 2017/10/20.
 */

public class GestureLockView extends View{

    /**
     * 三种状态
     */
    enum Mode{
        STATUS_NO_FINGER, STATUS_FINGER_ON, STATUS_FINGER_UP
    }

    /**
     * 当前状态
     */
    private Mode mCurrentState = Mode.STATUS_NO_FINGER;

    /**
     * 宽度
     */
    private int mWidth;

    /**
     * 高度
     */
    private int mHeight;

    /**
     * 外圆半径
     */
    private int mRadius;

    /**
     * 画笔的宽度
     */
    private int mStrokeWidth = 2;

    /**
     * 圆心坐标
     */
    private int mCenterX;
    private int mCenterY;

    private Paint mPaint;

    /**
     * 箭头(小三角最长边的一半长度 = mArrawRate * mWith / 2)
     */
    private float mArrowRate = 0.333f;
    private int mArrowDegree = -1;
    private Path mArrowPath;

    /**
     * 内圆的半径
     */
    private float mInnerCircleRadiusRate = 0.3f;

    private int mColorNoFingerInner;
    private int mColorNoFingerOutter;
    private int mColorFingerOn;
    private int mColorFinerUp;

    public GestureLockView(Context context, int colorNoFingerInner, int colorNoFingerOutter, int colorFingerOn, int colorFingerUp) {
        super(context);
        this.mColorNoFingerInner = colorNoFingerInner;
        this.mColorNoFingerOutter = colorNoFingerOutter;
        this.mColorFingerOn = colorFingerOn;
        this.mColorFinerUp = colorFingerUp;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArrowPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        //取长和宽中的小值
        mWidth = mWidth < mHeight ? mWidth : mHeight;
        mRadius = mCenterX = mCenterY = mWidth / 2;
        mRadius -= mStrokeWidth / 2;

        //绘制三角形，初始时是个默认箭头朝上的一个等腰三角形，用户绘制结束后，根据由两个GestureLockView决定需要旋转多少度
        float mArrowLength = mWidth / 2 * mArrowRate;
        mArrowPath.moveTo(mWidth / 2, mStrokeWidth + 2);
        mArrowPath.lineTo(mWidth / 2 - mArrowLength, mStrokeWidth + 2 + mArrowLength);
        mArrowPath.lineTo(mWidth / 2 + mArrowLength, mStrokeWidth + 2 + mArrowLength);
        mArrowPath.close();
        mArrowPath.setFillType(Path.FillType.WINDING);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mCurrentState){
            //当有手指放下时
            case STATUS_FINGER_ON:
                //绘制外圆
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(mColorFingerOn);
                mPaint.setStrokeWidth(2);
                canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);

                //绘制内圆
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mCenterX, mCenterY, mRadius * mInnerCircleRadiusRate, mPaint);
                break;
            //当有手指离开时
            case STATUS_FINGER_UP:
                //绘制外圆
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(mColorFinerUp);
                mPaint.setStrokeWidth(2);
                canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);

                //绘制内圆
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mCenterX, mCenterY, mRadius * mInnerCircleRadiusRate, mPaint);

                drawArrow(canvas);

                break;
            //当没有手指操作时
            case STATUS_NO_FINGER:
                //绘制外圆
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mColorNoFingerOutter);
                canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);

                //绘制内圆
                mPaint.setColor(mColorNoFingerInner);
                canvas.drawCircle(mCenterX, mCenterY, mRadius * mInnerCircleRadiusRate, mPaint);
                break;
        }
    }

    /**
     * 绘制箭头
     * @param canvas
     */
    private void drawArrow(Canvas canvas){
        if (mArrowDegree != - 1){
            mPaint.setStyle(Paint.Style.FILL);

            canvas.save();
            canvas.rotate(mArrowDegree, mCenterX, mCenterY);
            canvas.drawPath(mArrowPath, mPaint);

            canvas.restore();
        }
    }

    /**
     * 设置当前模式并重绘界面
     * @param mode
     */
    public void setMode(Mode mode){
        this.mCurrentState = mode;
        invalidate();
    }

    public void setmArrowDegree(int degree){
        this.mArrowDegree = degree;
    }

    public int getArrowDegree(){
        return this.mArrowDegree;
    }

}
