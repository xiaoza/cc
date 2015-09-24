package com.meiyebang.meiyebang.pad.view.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by yuzhen on 15/6/7.
 */
public class DragRect extends TextView {

    private static final int mScheduleTextSize = 12;
    private static final int mScheduleTextColor = Color.WHITE;
    private static final int mSchedulePadding = 8;

    private ScheduleRect draggingRect;  //正在拖动的

    public DragRect(Context context) {
        super(context);
        init();
    }

    public DragRect(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragRect(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.parseColor("#ffe2546f"));
        setTextColor(mScheduleTextColor);
        setTextSize(mScheduleTextSize);
        setPadding(mSchedulePadding, mSchedulePadding, mSchedulePadding, mSchedulePadding);

        setVisibility(GONE);
    }

    public ScheduleRect getScheduleRect() {
        return draggingRect;
    }

    public void setScheduleRect(ScheduleRect mScheduleRect) {
        this.draggingRect = mScheduleRect;
        draggingRect.color = Color.parseColor("#f8b552");
        setText(draggingRect.event.getContent());

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)getLayoutParams();
        params.leftMargin = (int)mScheduleRect.fullRectF.left;
        params.topMargin = (int)mScheduleRect.fullRectF.top;
        setLayoutParams(params);

        setVisibility(VISIBLE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (int)(draggingRect.fullRectF.right - draggingRect.fullRectF.left);
        int height = (int)(draggingRect.fullRectF.bottom - draggingRect.fullRectF.top);
        setMeasuredDimension(width, height);
    }

    public void cancel() {
        setVisibility(GONE);
    }
}
