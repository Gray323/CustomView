package com.rxd.customview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.rxd.customview.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/10/20.
 */

public class GestureLockViewGroup extends RelativeLayout{

    private GestureLockView[] mGestureLockViews;//保存所有的GestureLockView
    private int mCount = 4;//每个边上的GestureLockView个数
    private int[] mAnswer = {1};//存储答案
    private List<Integer> mChoose = new ArrayList<>();//保存用户所选的Id
    private Paint mPaint;
    private int mMarginBetweenLockView = 30;//每个GestureLockView中间的间距，设置为mGestureLockViewWidth * 25%
    private int mGestureLockViewWidth;//GestureLockView的边长， 4 * mWidth / (5 * Count + 1)
    private int mNoFingerInnerCircleColor = 0xFF939090;//无手指触摸时内圆颜色
    private int mNoFingerOuterCircleColor = 0xFFE0DBDB;//无手指触摸时外圆的颜色
    private int mFingerOnColor = 0xFF378FC9;//手指触摸时内圆和外圆的颜色
    private int mFingerUpColor = 0xFFFF0000;//手指抬起时内圆和外圆的颜色

    private int mWidth;//宽度
    private int mHeight;//高度
    private Path mPath;
    private int mLastPathX;//指引线的开始位置X
    private int mLastPathY;//指引线的开始位置Y
    private Point mTmpTarget = new Point();//指引下的结束位置
    private int mTryTimes;//最大尝试次数

    private OnGestureLockViewListener listener;//回调接口

    public GestureLockViewGroup(Context context) {
        this(context, null);
    }

    public GestureLockViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLockViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GestureLockViewGroup, defStyleAttr, 0);
        mNoFingerInnerCircleColor = a.getColor(R.styleable.GestureLockViewGroup_color_no_finger_inner_circle, mNoFingerInnerCircleColor);
        mNoFingerOuterCircleColor = a.getColor(R.styleable.GestureLockViewGroup_color_no_finger_outer_circle, mNoFingerOuterCircleColor);
        mFingerOnColor = a.getColor(R.styleable.GestureLockViewGroup_color_finger_on, mFingerOnColor);
        mFingerUpColor = a.getColor(R.styleable.GestureLockViewGroup_color_finger_up, mFingerUpColor);
        mCount = a.getInteger(R.styleable.GestureLockViewGroup_count, 3);
        mTryTimes = a.getInteger(R.styleable.GestureLockViewGroup_tryTimes, 3);
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        mHeight = mWidth = mWidth < mHeight ? mWidth : mHeight;

        //初始化GestureLockViews
        if (mGestureLockViews == null) {
            mGestureLockViews = new GestureLockView[mCount * mCount];
            //计算每个GestureLockView的宽度
            mGestureLockViewWidth = (int) (4 * mWidth * 1.0f / (5 * mCount + 1));
            //计算每个GestureLockView的间距
            mMarginBetweenLockView = (int) (mGestureLockViewWidth * 0.25);
            //设置画笔的宽度为GestureLockView的内圆直径稍微小点
            mPaint.setStrokeWidth(mGestureLockViewWidth * 0.29f);

            for (int i = 0; i < mGestureLockViews.length; i++) {
                //初始化GestureLockView
                mGestureLockViews[i] = new GestureLockView(getContext(), mNoFingerInnerCircleColor, mNoFingerOuterCircleColor,
                        mFingerOnColor, mFingerUpColor);
                mGestureLockViews[i].setId(i + 1);

                //设置参数，主要是定位GestureLockView间的位置
                RelativeLayout.LayoutParams lockParams = new RelativeLayout.LayoutParams(mGestureLockViewWidth, mGestureLockViewWidth);

                //不是每行的第一个，则设置位置为前一个的右边
                if (i % mCount != 0) {
                    lockParams.addRule(RelativeLayout.RIGHT_OF, mGestureLockViews[i - 1].getId());
                }
                //从第二行开始，设置为上一行同一位置view的下面
                if (i > mCount - 1) {
                    lockParams.addRule(RelativeLayout.BELOW, mGestureLockViews[i - mCount].getId());
                }

                //设置右下左上的边距
                int rightMargin = mMarginBetweenLockView;
                int bottomMargin = mMarginBetweenLockView;
                int leftMargin = 0;
                int topMargin = 0;

                //每个view都有右外边距和底外边距，第一行的有上外边距，第一列的有左外边距
                if (i >= 0 && i < mCount) {//第一行
                    topMargin = mMarginBetweenLockView;
                }
                if (i % mCount == 0) {//第一列
                    leftMargin = mMarginBetweenLockView;
                }

                lockParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                mGestureLockViews[i].setMode(GestureLockView.Mode.STATUS_NO_FINGER);
                addView(mGestureLockViews[i], lockParams);
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                mPaint.setColor(mFingerOnColor);
                mPaint.setAlpha(50);
                GestureLockView child = getChildByPos(x, y);
                if(child != null){
                    int cId = child.getId();
                    if (!mChoose.contains(cId)){
                        mChoose.add(cId);
                        child.setMode(GestureLockView.Mode.STATUS_FINGER_ON);
                        if (listener != null){
                            listener.onBlockSelected(cId);
                        }
                        //设置指引线的起点
                        mLastPathX = child.getLeft() / 2 + child.getRight() / 2;
                        mLastPathY = child.getTop() / 2 + child.getBottom() / 2;

                        if (mChoose.size() == 1){//当前添加为第一个
                            mPath.moveTo(mLastPathX, mLastPathY);
                        }else{//否则，用线将两者连上
                            mPath.lineTo(mLastPathX, mLastPathY);
                        }
                    }
                }

                //指引线的终点
                mTmpTarget.x = x;
                mTmpTarget.y = y;
                break;

            case MotionEvent.ACTION_UP:
                mPaint.setColor(mFingerUpColor);
                mPaint.setAlpha(50);
                this.mTryTimes--;

                //回调是否成功
                if (listener != null && mChoose.size() > 0){
                    listener.onGestureEvent(checkAnswer());
                    if (this.mTryTimes == 0){
                        listener.onUnmatchedExceedBoundary();
                    }
                }

                //将终点设置位置为起点，即取现指引线
                mTmpTarget.x = mLastPathX;
                mTmpTarget.y = mLastPathY;

                //更改子元素的状态
                changeItemMode();

                //计算每个元素中箭头需要旋转的角度
                for (int i = 0; i + 1 < mChoose.size(); i++){
                    int childId = mChoose.get(i);
                    int nextChildId = mChoose.get(i + 1);

                    GestureLockView startChild = findViewById(childId);
                    GestureLockView nextChild = findViewById(nextChildId);

                    int dx = nextChild.getLeft() - startChild.getLeft();
                    int dy = nextChild.getTop() - startChild.getTop();

                    //计算角度
                    int angle = (int) Math.toDegrees(Math.atan2(dy, dx)) + 90;
                    startChild.setmArrowDegree(angle);
                }
                break;
        }

        invalidate();

        return true;

    }

    /**
     * 更改子元素的状态为up
     */
    private void changeItemMode(){
        for (GestureLockView view : mGestureLockViews){
            if (mChoose.contains(view.getId())){
                view.setMode(GestureLockView.Mode.STATUS_FINGER_UP);
            }
        }
    }

    /**
     * 检验手势是否正确
     * @return
     */
    private boolean checkAnswer(){
        Log.d("mAnswer", Arrays.toString(mAnswer));
        Log.d("mAnswer", mChoose.toString());
        if (mAnswer.length != mChoose.size()){
            return false;
        }

        for (int i = 0; i < mAnswer.length; i++){
            if (mAnswer[i] != mChoose.get(i)){
                return false;
            }
        }
        return true;

    }

    /**
     * 判断当前左边是否在child中
     * @param child
     * @param x
     * @param y
     * @return
     */
    private boolean checkPositionInChild(View child, int x, int y){
        //设置了内边距，即x,y必须落入GestureLockView的内部中间的小区域中，可以通过调整padding使得
        //x,y落入范围不变大，或者不设置padding
        int padding = (int) (mGestureLockViewWidth * 0.15);
        if (x >= child.getLeft() + padding && x <= child.getRight() - padding
                && y >= child.getTop() && y <= child.getBottom() - padding){
            return true;
        }
        return false;
    }

    /**
     * 根据x, y获取落入的GestureLockView
     * @param x
     * @param y
     * @return
     */
    private GestureLockView getChildByPos(int x, int y) {
        for (GestureLockView view : mGestureLockViews){
            if (checkPositionInChild(view, x, y)){
                return view;
            }
        }
        return null;
    }

    public void reset() {
        mChoose.clear();
        mPath.reset();
        for (GestureLockView view : mGestureLockViews){
            view.setMode(GestureLockView.Mode.STATUS_NO_FINGER);
            view.setmArrowDegree(-1);
        }
        invalidate();
    }

    /**
     * 设置回调接口
     * @param listener
     */
    public void setOnGestureLockViewListener(OnGestureLockViewListener listener){
        this.listener = listener;
    }

    public interface OnGestureLockViewListener{
        /**
         * 单独选中元素的id
         * @param cId
         */
        void onBlockSelected(int cId);

        /**
         * 是否匹配
         * @param isMatched
         */
        void onGestureEvent(boolean isMatched);

        /**
         * 超过尝试次数
         */
        void onUnmatchedExceedBoundary();

    }

    /**
     * 设置正确答案
     * @param answer
     */
    public void setAnswer(int[] answer){
        try {
            boolean isCorrect = true;

            if (answer.length > 9){
                throw new AnswerNotLegalException("答案长度不能超过9位,使用了默认密码{1}");
            }

            for(int i = 0; i < answer.length; i++){
                if (answer[i] != 1 && answer[i] != 2 && answer[i] != 3 && answer[i] != 4 && answer[i] != 5
                        && answer[i] != 6 && answer[i] != 7 && answer[i] != 8 && answer[i] != 9){
                    isCorrect = false;
                }
            }

            if (isCorrect){
                this.mAnswer = answer;
                invalidate();
            }else{
                throw new AnswerNotLegalException("答案中只能包含1-9,使用了默认密码{1}");
            }
        }catch (AnswerNotLegalException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制GestureLockView之间的连线
        if (mPath != null){
            canvas.drawPath(mPath, mPaint);
        }
        if(mChoose.size() > 0){
            if(mLastPathX != 0 && mLastPathY != 0){
                canvas.drawLine(mLastPathX, mLastPathY, mTmpTarget.x, mTmpTarget.y, mPaint);
            }
        }
    }

    public void setTryTimes(int mTryTimes){
        this.mTryTimes = mTryTimes;
    }

}
