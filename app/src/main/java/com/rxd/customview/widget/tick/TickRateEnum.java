package com.rxd.customview.widget.tick;

/**
 * Created by Administrator on 2017/10/27.
 */

public enum TickRateEnum {
    SLOW(600, 300, 450),
    NORMAL(400, 200, 300),
    FAST(200, 100, 150);

    public static final int RATE_MODE_SLOW = 0;
    public static final int RATE_MODE_NORMAL = 1;
    public static final int RATE_MODE_FAST = 2;

    private int mRingAnimatiorDuration;
    private int mCircleAnimatiorDuration;
    private int mScaleAnimatiorDuration;

    TickRateEnum(int mRingAnimatiorDuration, int mCircleAnimatiorDuration, int mScaleAnimatiorDuration) {
        this.mRingAnimatiorDuration = mRingAnimatiorDuration;
        this.mCircleAnimatiorDuration = mCircleAnimatiorDuration;
        this.mScaleAnimatiorDuration = mScaleAnimatiorDuration;
    }

    public int getmRingAnimatiorDuration(){
        return mRingAnimatiorDuration;
    }

    public TickRateEnum setmRingAnimatorDuration(int mRingAnimatiorDuration){
        this.mRingAnimatiorDuration = mRingAnimatiorDuration;
        return this;
    }

    public int getmCircleAnimatiorDuration() {
        return mCircleAnimatiorDuration;
    }

    public TickRateEnum setmCircleAnimatiorDuration(int mCircleAnimatiorDuration) {
        this.mCircleAnimatiorDuration = mCircleAnimatiorDuration;
        return this;
    }

    public int getmScaleAnimatiorDuration() {
        return mScaleAnimatiorDuration;
    }

    public TickRateEnum setmScaleAnimatiorDuration(int mScaleAnimatiorDuration) {
        this.mScaleAnimatiorDuration = mScaleAnimatiorDuration;
        return this;
    }

    public static TickRateEnum getRateEnum(int rateMode){
        TickRateEnum tickRateEnum;
        switch (rateMode){
            case RATE_MODE_SLOW:
                tickRateEnum = TickRateEnum.SLOW;
                break;
            case RATE_MODE_NORMAL:
                tickRateEnum = TickRateEnum.NORMAL;
                break;
            case RATE_MODE_FAST:
                tickRateEnum = TickRateEnum.FAST;
                break;
            default:
                tickRateEnum = TickRateEnum.NORMAL;
                break;
        }
        return tickRateEnum;
    }
}
