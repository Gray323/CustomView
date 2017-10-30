package com.rxd.customview.widget.tick;

/**
 * Created by Administrator on 2017/10/27.
 */

public interface TickCheckable {
    /**
     * 改变状态
     * @param checked 选中还是未选中
     */
    void setChecked(boolean checked);

    /**
     * 当前状态是否选中
     * @return
     */
    boolean isChecked();

    /**
     * 改变当前状态
     */
    void toggle();

}
