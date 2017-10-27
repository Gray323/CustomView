package com.rxd.customview.widget;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.rxd.customview.R;

/**
 * Created by Administrator on 2017/10/27.
 */

public class TickView extends View implements TickCheckable{

    private Context mContext;
    //内圆的画笔
    private Paint mPaintCircle;
    //外层圆环的画笔
    private Paint mPaintRing;
    //打钩的画笔
    private Paint mPaintTick;

    //整个圆外切的矩形
    private RectF mRectF = new RectF();
    //记录打钩路径的三个点坐标
    private float[] mPoints = new float[8];

    //控件中心的X，Y坐标
    private int centerX;
    private int centerY;

    //计数器
    private int circleRadius = -1;
    private int ringProgress = 0;

    //是否被点亮
    private boolean isChecked = false;
    private boolean clickable = true;
    //是否处于动画中
    private boolean isAnimationRunning = false;

    //自定义属性
    private int unCheckedBaseColor;
    private int checkBaseColor;
    private int checkTickColor;
    private int radius;

    //勾的半径
    private float tickRadius;
    //勾的偏移
    private float tickRadiusOffset;

    private AnimatorSet mFinalAnimatorSet;

    //动画持续时间
    private int mRingAnimatiorDuration;
    private int mCircleAnimatiorDuration;
    private int mScaleAnimatiorDuration;

    private OnCheckedChangeListener mOnCheckedChangeListener;

    public TickView(Context context) {
        this(context, null);
    }

    public TickView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(attrs);
        initPaint();
        initAnimatorCounter();
        setUpEvent();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.TickView);
        unCheckedBaseColor  = a.getColor(R.styleable.TickView_uncheck_base_color, getResources().getColor(R.color.tick_gray));
        checkBaseColor  = a.getColor(R.styleable.TickView_check_base_color, getResources().getColor(R.color.tick_yellow));
        checkTickColor = a.getColor(R.styleable.TickView_check_tick_color, getResources().getColor(R.color.tick_white));
    }

    private void initPaint() {
    }

    private void initAnimatorCounter() {
    }

    private void setUpEvent() {
    }

    @Override
    public void setChecked(boolean checked) {

    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void toggle() {

    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(TickView tickView, boolean isCheck);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }

}
