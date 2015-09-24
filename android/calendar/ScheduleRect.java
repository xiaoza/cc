package com.meiyebang.meiyebang.pad.view.calendar;

import android.graphics.RectF;

import com.meiyebang.meiyebang.pad.model.UserSchedule;

import java.util.Calendar;

public class ScheduleRect {
    public UserSchedule event;
    public RectF rectF;
    public RectF fullRectF; // 在边缘的时候，rectF是被截断的，当完全显示的时候，这两个值是一样的
    public float left;
    public float width;
    public float top;
    public float bottom;
    public int color = Integer.MAX_VALUE;  //背景颜色

    public ScheduleRect(UserSchedule event, RectF rectF) {
        this.event = event;
        this.rectF = rectF;
        this.fullRectF = rectF;
    }

    public ScheduleRect(UserSchedule event, RectF rectF, RectF fullRectF) {
        this.event = event;
        this.rectF = rectF;
        this.fullRectF = fullRectF;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isEditable() {
        if (event.isArrival() || event.isFinished()) {
            return false;
        }
        return true;
    }
}
