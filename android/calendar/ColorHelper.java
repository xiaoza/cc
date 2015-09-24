package com.meiyebang.meiyebang.pad.view.calendar;

import android.graphics.Color;
import android.util.SparseIntArray;

import com.meiyebang.meiyebang.pad.model.Order;
import com.meiyebang.meiyebang.pad.model.UserSchedule;

/**
 * Created by yuzhen on 15/7/24.
 */
public class ColorHelper {
    private final int DEFAULT_SCHEDULE_COLOR_KEY = Order.STATUS_NONE;
    private SparseIntArray mScheduleBgStatusColor = new SparseIntArray();
    private SparseIntArray mScheduleTextStatusColor = new SparseIntArray();

    public ColorHelper() {
        mScheduleBgStatusColor.put(DEFAULT_SCHEDULE_COLOR_KEY, Color.parseColor("#ffd6dd"));
        mScheduleBgStatusColor.put(Order.STATUS_NEW, Color.parseColor("#ffd6dd"));
        mScheduleBgStatusColor.put(Order.STATUS_ARRIVAL, Color.parseColor("#e6f8c4"));
        mScheduleBgStatusColor.put(Order.STATUS_FINISHED, Color.parseColor("#e8e8e8"));

        mScheduleTextStatusColor.put(DEFAULT_SCHEDULE_COLOR_KEY, Color.parseColor("#e3546e"));
        mScheduleTextStatusColor.put(Order.STATUS_NEW, Color.parseColor("#e3546e"));
        mScheduleTextStatusColor.put(Order.STATUS_ARRIVAL, Color.parseColor("#5f9b00"));
        mScheduleTextStatusColor.put(Order.STATUS_FINISHED, Color.parseColor("#666666"));
    }

    public int getScheduleBgColor(UserSchedule schedule) {
        if (schedule == null) {
            return mScheduleBgStatusColor.get(DEFAULT_SCHEDULE_COLOR_KEY);
        }
        int color = mScheduleBgStatusColor.get(schedule.getStatus(), Integer.MIN_VALUE);
        if (color == Integer.MIN_VALUE) {
            color = mScheduleBgStatusColor.get(DEFAULT_SCHEDULE_COLOR_KEY);
        }
        return color;
    }

    public int getScheduleTextColor(UserSchedule schedule) {
        if (schedule == null) {
            return mScheduleTextStatusColor.get(DEFAULT_SCHEDULE_COLOR_KEY);
        }
        int color = mScheduleTextStatusColor.get(schedule.getStatus(), Integer.MIN_VALUE);
        if (color == Integer.MIN_VALUE) {
            color = mScheduleTextStatusColor.get(DEFAULT_SCHEDULE_COLOR_KEY);
        }
        return color;
    }

    public int getDefaultScheduleBgColor() {
        return mScheduleBgStatusColor.get(DEFAULT_SCHEDULE_COLOR_KEY);
    }

    public int getDefaultScheduleTextColor() {
        return mScheduleTextStatusColor.get(DEFAULT_SCHEDULE_COLOR_KEY);
    }

    public int getTimelineColor() {
        return Color.parseColor("#e2546f");
    }

    public int getColumnBgColor() {
        return Color.parseColor("#FCFCFC");
    }

    public int getCalendarDividerColor() {
        return Color.rgb(230, 230, 230);
    }

    public int getRowHeaderBgColor() {
        return Color.parseColor("#F5F5F5");
    }

    public int getColumnHeaderBgColor() {
        return Color.WHITE;
    }

    public int getHeaderTextColor() {
        return Color.parseColor("#666666");
    }
}
