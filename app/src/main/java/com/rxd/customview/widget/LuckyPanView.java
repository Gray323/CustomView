package com.rxd.customview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.rxd.customview.R;

/**
 * Created by Administrator on 2017/10/27.
 * 抽奖轮盘控件
 */

public class LuckyPanView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    private SurfaceHolder mHolder;
    private Canvas mCanvas;//与surfaceholder绑定的canvas
    private Thread thread;//用于绘制的线程
    private boolean isRunning;//线程的控制开关
    private String[] mStrs = new String[] { "单反相机", "IPAD", "恭喜发财", "IPHONE",
            "妹子一只", "恭喜发财" };//抽奖的文字
    private int[] mColors = new int[] { 0xFFFFC300, 0xFFF17E01, 0xFFFFC300,
            0xFFF17E01, 0xFFFFC300, 0xFFF17E01 };//每个盘块的颜色
    private int[] mImgs = new int[] { R.mipmap.danfan, R.mipmap.ipad,
            R.mipmap.f040, R.mipmap.iphone, R.mipmap.meizi,
            R.mipmap.f040 };//与文字对应的图片
    private Bitmap[] mImgsBitmap;//与文字对应图片的bitmap数组
    private int mItemCount = 6;//盘块的个数
    private RectF mRange = new RectF();//绘制盘块的范围
    private int mRadius;//圆的直径
    private Paint mArcPaint;//绘制盘块的画笔
    private Paint mTextPaint;//绘制文字的画笔
    private double mSpeed;//滚动的速度
    private volatile float mStartAngle = 0;
    private boolean isShouldEnd;//是否点击了停止
    private int mCenter;//控件的中心位置
    private int mPadding;//控件的padding;
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(),
            R.mipmap.bg2);//背景图
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            20, getResources().getDisplayMetrics());//文字的大小


    public LuckyPanView(Context context) {
        this(context, null);
    }

    public LuckyPanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckyPanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mHolder = getHolder();
        mHolder.addCallback(this);

        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(getMeasuredWidth(), getMeasuredWidth());
        //获取圆形的直径
        mRadius = width - getPaddingLeft() - getPaddingRight();
        //padding值
        mPadding = getPaddingLeft();
        mCenter = width / 2;
        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //初始化绘制圆弧的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);//防抖动
        //初始化绘制文字的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(0xFFffffff);
        mTextPaint.setTextSize(mTextSize);
        //圆弧的绘制范围
        mRange = new RectF(getPaddingLeft(), getPaddingLeft(),
                mRadius + getPaddingLeft(), mRadius + getPaddingLeft());

        //初始化图片
        mImgsBitmap = new Bitmap[mItemCount];
        for(int i = 0; i < mItemCount; i++){
            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(), mImgs[i]);
        }

        //开启线程
        isRunning = true;
        thread = new Thread(thread);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while(isRunning){
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            try{
                if(end - start < 50){
                    Thread.sleep(50 - (end -  start));
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void draw(){
        try {
            mCanvas = mHolder.lockCanvas();
            if(mCanvas != null){
                //绘制背景图
                drawBg();

                //绘制每个盘块，以及上面的文本和图片
                float tmpAngle = mStartAngle;
                float sweepAngle =(float)(360 / mItemCount);
                for(int i = 0; i < mItemCount; i++){
                    //绘制块块
                    mArcPaint.setColor(mColors[i]);
                    mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);

                    //绘制文本
                    drawText(tmpAngle, sweepAngle, mStrs[i]);
                    drawIcon(tmpAngle, i);

                    tmpAngle += sweepAngle;
                }

                //如果speed不等于0，相当于在滚动
                mStartAngle += mSpeed;

                if(isShouldEnd){
                    mSpeed -= 1;
                }
                if(mSpeed <= 0){
                    mSpeed = 0;
                    isShouldEnd = false;
                }

                //计算所在区域
                calInExactArea(mStartAngle);

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(mCanvas != null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }

    }

    private void calInExactArea(float mStartAngle) {
        //让指针从水平向右开始计算
        float rotate = mStartAngle + 90;
        rotate %= 360.0;
        for(int i = 0; i < mItemCount; i++){
            float from = 360 - (i + 1) * (360 / mItemCount);
            float to = from + 360 - (i) * (360 / mItemCount);
            if((rotate > from) && (rotate < to)){
                Log.d("sc", mStrs[i]);
                return;
            }
        }
    }

    private void drawIcon(float tmpAngle, int i) {
        //设置图片的宽度为直径的1/8
        int imgWidth = mRadius / 8;
        float angle = (float)((30 + mStartAngle) * (Math.PI / 180));
        int x = (int)(mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int)(mCenter + mRadius / 2 / 2 * Math.sin(angle));
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        mCanvas.drawBitmap(mImgsBitmap[i], null, rect, null);
    }

    /**
     * 绘制文字
     * @param tmpAngle
     * @param sweepAngle
     * @param string
     */
    private void drawText(float tmpAngle, float sweepAngle, String string) {
        Path path = new Path();
        path.addArc(mRange, mStartAngle, sweepAngle);
        float textWidth = mTextPaint.measureText(string);
        float hOffset = (float)(mRadius * Math.PI / mItemCount / 2 - textWidth / 2);//水平偏移\
        float vOffset = mRadius / 2 / 6;
        mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
    }

    /**
     * 绘制背景
     */
    private void drawBg() {
        mCanvas.drawColor(0xFFFFFFFF);
        mCanvas.drawBitmap(mBgBitmap, null, new Rect(mPadding / 2, mPadding / 2,
                getMeasuredWidth() - mPadding / 2, getMeasuredWidth() - mPadding / 2), null);
    }

    /**
     * 点击开始旋转
     * @param luckyIndex
     */
    public void luckyStart(int luckyIndex){
        //每项角度大小
        float angle = (float)(360 / mItemCount);
        //中奖角度范围
        float from = 270 - (luckyIndex + 1) * angle;
        float to = from + angle;
        //停下来时旋转的距离
        float targetFrom = 4 * 360 + from;

        float v1 = (float) (Math.sqrt(1 * 1 + 8 * 1 * targetFrom) - 1) / 2;
        float targetTo = 4 * 360 + to;
        float v2 = (float) (Math.sqrt(1 * 1 + 8 * 1 * targetTo) - 1) / 2;

        mSpeed = (float) (v1 + Math.random() * (v2 - v1));
        isShouldEnd = false;

    }

    public void luckyEnd()
    {
        mStartAngle = 0;
        isShouldEnd = true;
    }

}
