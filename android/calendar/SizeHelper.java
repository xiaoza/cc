package com.meiyebang.meiyebang.pad.view.calendar;

import com.meiyebang.meiyebang.pad.util.LocalUtil;

/**
 * Created by yuzhen on 15/7/24.
 */
public class SizeHelper {

    private int rowHeight = LocalUtil.dip2px(76);

    private float mTimeTextWidth;
    private float mTimeTextHeight;
    private float mHeaderTextHeight;
    private float mHeaderMarginBottom;
    private float mColumnWidth;

    public SizeHelper() {
    }

    public int getScheduleVerticalMargin() {
        return 1;
    }

    public int getSchedulePadding() {
        return 8;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    public int getRowDividerHeight() {
        return 2;
    }

    public int getColumnGap() {
        return LocalUtil.dip2px(1);
    }

    public float getHeaderFullHeight() {
        return mHeaderTextHeight + 2 * getHeaderPadding();
    }

    public float getHeaderFullHeightWidhMargin() {
        return getHeaderFullHeight() + getHeaderMarginBottom();
    }

    public int getHeaderPadding() {
        return LocalUtil.dip2px(12);
    }

    public float getTimeTextWidth() {
        return mTimeTextWidth;
    }

    public void setTimeTextWidth(float timeTextWidth) {
        mTimeTextWidth = timeTextWidth;
    }

    public float getTimeTextHeight() {
        return mTimeTextHeight;
    }

    public void setTimeTextHeight(float timeTextHeight) {
        mTimeTextHeight = timeTextHeight;
    }

    public float getHeaderTextHeight() {
        return mHeaderTextHeight;
    }

    public void setHeaderTextHeight(float headerTextHeight) {
        mHeaderTextHeight = headerTextHeight;
    }

    public float getHeaderMarginBottom() {
        return mHeaderMarginBottom;
    }

    public void setHeaderMarginBottom(float headerMarginBottom) {
        mHeaderMarginBottom = headerMarginBottom;
    }

    public float getColumnWidth() {
        return mColumnWidth;
    }

    public void setColumnWidth(float columnWidth) {
        mColumnWidth = columnWidth;
    }

    private float contentHeight = 0;
    public float getContentHeight(int containerHeight) {
        if (contentHeight <= 0) {
            contentHeight = containerHeight - getConstTopHeight() - getRowDividerHeight() * 0.5f;
        }
        return contentHeight;
    }

    private float constTopHeight = 0;
    public float getConstTopHeight() {
        if (constTopHeight <= 0) {
            constTopHeight = getHeaderHeight() + mHeaderMarginBottom + mHeaderTextHeight / 2;
        }
        return constTopHeight;
    }

    private float headerFullHeight = 0;
    public float getHeaderHeight() {
        if (headerFullHeight <= 0) {
            headerFullHeight = mHeaderTextHeight + getHeaderPadding() * 2;
        }
        return headerFullHeight;
    }

    private float columnFullWidth = 0;
    public float getColumnFullWidth() {
        if (columnFullWidth <= 0) {
            columnFullWidth = mColumnWidth + getColumnGap();
        }
        return columnFullWidth;
    }

}
