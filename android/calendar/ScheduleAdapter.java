package com.meiyebang.meiyebang.pad.view.calendar;

import android.util.SparseArray;
import android.view.MotionEvent;

import com.meiyebang.meiyebang.pad.model.User;
import com.meiyebang.meiyebang.pad.model.UserSchedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by yuzhen on 15/6/5.
 */
public class ScheduleAdapter extends SparseArray<SparseArray<List<ScheduleRect>>> {

    // 日历header展示的数据，
    // 天视图时，key代表美容师id，
    // 美容师视图时key代表的是int类型的时间，
    // 周视图时key代表int时间
    private SparseArray<String> columnNames = new SparseArray<String>();

    // 只有周视图时可用
    // key代表的是美容师id
    private SparseArray<String> rowNames = new SparseArray<String>();

    public void putScheduleRect(int columnKey, ScheduleRect rect) {
        fillScheduleRect(columnKey, rect);
        computeSchedulesPosition();
    }

    public boolean removeScheduleRect(int id) {
        boolean removed = false;
        done:
        for (int idx = 0; idx < size(); idx++) {
            for (int i = 0; i < valueAt(idx).size(); i++) {
                Iterator<ScheduleRect> it = valueAt(idx).valueAt(i).iterator();
                while (it.hasNext()) {
                    if (it.next().event.getId() == id) {
                        it.remove();
                        removed = true;
                        break done;
                    }
                }
            }
        }

        if (removed) {
            computeSchedulesPosition();
        }
        return removed;
    }

    private void fillScheduleRect(int columnKey, ScheduleRect rect) {
        if (get(columnKey) == null) {
            put(columnKey, new SparseArray<List<ScheduleRect>>());
        }
        if (get(columnKey).get(rect.event.getUserScheduleType()) == null) {
            get(columnKey).put(rect.event.getUserScheduleType(), new ArrayList<ScheduleRect>());
        }
        get(columnKey).get(rect.event.getUserScheduleType()).add(rect);
    }

    public ScheduleRect isDropIn(MotionEvent event) {
        ScheduleRect result = null;
        for (int idx = 0; idx < size(); idx++) {
            if (result != null) {
                break;
            }

            SparseArray<List<ScheduleRect>> rowRects = valueAt(idx);
            for (int inner = 0; inner < rowRects.size(); inner++) {
                if (result != null) {
                    break;
                }

                List<ScheduleRect> rects = rowRects.valueAt(inner);
                if (rects.isEmpty()) {
                    continue;
                }

                for (ScheduleRect rect : rects) {
                    if (rect.rectF != null &&
                            event.getX() > rect.rectF.left &&
                            event.getX() < rect.rectF.right &&
                            event.getY() > rect.rectF.top &&
                            event.getY() < rect.rectF.bottom) {
                        result = rect;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public void clearScheduleRects() {
        for (int idx = 0; idx < size(); idx++) {
            SparseArray<List<ScheduleRect>> rowRects = valueAt(idx);
            for (int inner = 0; inner < rowRects.size(); inner++) {
                List<ScheduleRect> rects = rowRects.valueAt(inner);
                if (rects.isEmpty()) {
                    continue;
                }

                for (ScheduleRect rect : rects) {
                    rect.rectF = null;
                    rect.fullRectF = null;
                }
            }
        }
    }

    @Override
    public void clear() {
        for (int idx = 0; idx < size(); idx++) {
            valueAt(idx).clear();
        }
        columnNames.clear();
        rowNames.clear();
        super.clear();
    }

    public String getColumnNameByIndex(int index) {
        if (index < 0 || index >= size()) {
            return "";
        }
        return columnNames.valueAt(index);
    }

    public String getColumnNameByKey(int key) {
        return columnNames.get(key, "");
    }

    public String getRowNameByIndex(int index) {
        if (index < 0 || index >= rowNames.size()) {
            return "";
        }
        return rowNames.valueAt(index);
    }

    public int getRowKeyByIndex(int index) {
        if (index < 0 || index >= rowNames.size()) {
            return Integer.MIN_VALUE;
        }
        return rowNames.keyAt(index);
    }

    // week view 中，根据第一个展示的日期来判断是否加载新数据，如果不需要返回null，需要则返回需要加载的开始日期
    public Calendar getStartDayForMoreSchedules(Calendar firstVisibleDay) {
        int curMin = Integer.MAX_VALUE;
        int curMax = Integer.MIN_VALUE;
        for (int i = 0; i < rowNames.size(); i++) {
            int row = rowNames.keyAt(i);
            if (row < curMin) {
                curMin = row;
            }
            if (row > curMax) {
                curMax = row;
            }
        }
        int curVisibleInt = CalendarHelper.getIntDate(firstVisibleDay);
        if (curVisibleInt >= curMin && curVisibleInt <= curMax - 2) {
            return null;
        }

        int firstVisibleInt = CalendarHelper.getIntDate(firstVisibleDay);
        if (firstVisibleInt < curMin) { //下滚动
            firstVisibleDay.add(Calendar.DATE, 5);
            firstVisibleInt = CalendarHelper.getIntDate(firstVisibleDay);
            if (firstVisibleInt <= curMin) {
                Calendar result = CalendarHelper.getCalendar(curMin);
                if (result != null) {
                    result.add(Calendar.DATE, -ScheduleView.DEFAULT_COLUMN_NUM);
                    return result;
                }
            }
        } else {
            firstVisibleDay.add(Calendar.DATE, 2);
            firstVisibleInt = CalendarHelper.getIntDate(firstVisibleDay);
            if (firstVisibleInt >= curMax) {
                Calendar result = CalendarHelper.getCalendar(curMax);
                if (result != null) {
                    result.add(Calendar.DATE, 1);
                    return result;
                }
            }
        }
        return null;
    }

    public void updateDataForOneWeek(Calendar startDay, List<User> userSchedules) {
        clear();
        for (User user : userSchedules) {
            int columnKey = user.getId();
            if (columnNames.get(columnKey) == null) {
                columnNames.put(columnKey, user.getName());
            }
            if (get(columnKey) == null) {
                put(columnKey, new SparseArray<List<ScheduleRect>>());
            }
//            clearFarSchedules(columnKey, startDay);

            Calendar day = (Calendar)startDay.clone();
            for (int rowIndex = 0; rowIndex < 7; rowIndex++) {
                int rowKey = CalendarHelper.getIntDate(day);
                String rowName = CalendarHelper.getFriendlyDate(day);
                if (rowNames.get(rowKey) == null) {
                    rowNames.put(rowKey, rowName);
                }
                if (get(columnKey).get(rowKey) == null) {
                    get(columnKey).put(rowKey, new ArrayList<ScheduleRect>());
                } else {
                    get(columnKey).get(rowKey).clear();
                }

                if (user.getUserSchedules() == null || user.getUserSchedules().isEmpty()) {
                    day.add(Calendar.DATE, 1);
                    continue;
                }
                for (UserSchedule schedule : user.getUserSchedules()) {
                    if (CalendarHelper.getIntDate(schedule.getStartTime()) != rowKey) {
                        continue;
                    }

                    // 留三个即可，多了展示不下
                    if (get(columnKey).get(rowKey).size() >= 3) {
                        continue;
                    }
                    get(columnKey).get(rowKey).add(new ScheduleRect(schedule, null));
                }

                day.add(Calendar.DATE, 1);
            }
        }
    }

    // in week view, remove faraway schedules
    private void clearFarSchedules(int columnKey, Calendar currentDay) {
        SparseArray<List<ScheduleRect>> columnData = get(columnKey);
        if (columnData == null || columnData.size() <= 0) {
            return;
        }

        Calendar startDay = (Calendar)currentDay.clone();
        startDay.add(Calendar.DATE, -7);
        Calendar endDay = (Calendar)currentDay.clone();
        endDay.add(Calendar.DATE, 14);

        int startDayInt = CalendarHelper.getIntDate(startDay);
        int endDayInt = CalendarHelper.getIntDate(endDay);
        for (int dayIndex = 0; dayIndex < columnData.size(); dayIndex++) {
            int dayInt = columnData.keyAt(dayIndex);
            if (dayInt < startDayInt || dayInt > endDayInt) {
                columnData.valueAt(dayIndex).clear();
                columnData.remove(dayInt);
                rowNames.remove(dayInt);
            }
        }
    }

    public void updateDataForOneDay(List<Column> columns) {
        clear();
        sortColumns(columns);

        ScheduleRect rect;
        for (Column column : columns) {
            if (column.getEventList().isEmpty()) {
                put(column.getId(), new SparseArray<List<ScheduleRect>>());
            } else {
                for (UserSchedule event : column.getEventList()) {
                    rect = new ScheduleRect(event, null);
                    fillScheduleRect(column.getId(), rect);
                }
            }
            columnNames.put(column.getId(), column.getName());
        }
        computeSchedulesPosition();
    }

    /** useful functions **/
    private static void sortColumns(List<Column> columns) {
        Collections.sort(columns, new Comparator<Column>() {
            @Override
            public int compare(Column c1, Column c2) {
                long s1 = c1.getSort();
                long s2 = c2.getSort();
                return s1 > s2 ? 1 : (s1 < s2 ? -1 : 0);
            }
        });
    }

    private void computeSchedulesPosition() {
        for (int idx = 0; idx < size(); idx++) {
            SparseArray<List<ScheduleRect>> rowRects = valueAt(idx);
            for (int inner = 0; inner < rowRects.size(); inner++) {
                computeSchedulesPositionInOneGroup(rowRects.valueAt(inner));
            }
        }
    }

    /**
     * 计算每列中每个事件的左右位置
     */
    private void computeSchedulesPositionInOneGroup(List<ScheduleRect> eventRects) {
        // 有重叠的事件集合
        List<List<ScheduleRect>> collisionGroups = new ArrayList<List<ScheduleRect>>();
        for (ScheduleRect eventRect : eventRects) {
            boolean isPlaced = false;
            outerLoop:
            for (List<ScheduleRect> collisionGroup : collisionGroups) {
                for (ScheduleRect groupEvent : collisionGroup) {
                    if (isTwoScheduleCollide(groupEvent.event, eventRect.event)) {
                        collisionGroup.add(eventRect);
                        isPlaced = true;
                        break outerLoop;
                    }
                }
            }

            // 没有重叠，自成一列
            if (!isPlaced) {
                List<ScheduleRect> newGroup = new ArrayList<ScheduleRect>();
                newGroup.add(eventRect);
                collisionGroups.add(newGroup);
            }
        }

        for (List<ScheduleRect> collisionGroup : collisionGroups) {
            expandSchedulesToMaxWidth(collisionGroup);
        }
    }

    private void expandSchedulesToMaxWidth(List<ScheduleRect> collisionGroup) {
        // 每组重叠的事件中，如果有两个事件没有重叠，这两个事件可以放在同一列
        List<List<ScheduleRect>> columns = new ArrayList<List<ScheduleRect>>();
        columns.add(new ArrayList<ScheduleRect>());

        for (ScheduleRect eventRect : collisionGroup) {
            boolean isPlaced = false;
            for (List<ScheduleRect> column : columns) {
                if (column.size() == 0) {
                    column.add(eventRect);
                    isPlaced = true;
                }
                // 两个事件没有重叠，放在一列
                else if (!isTwoScheduleCollide(eventRect.event, column.get(column.size() - 1).event)) {
                    column.add(eventRect);
                    isPlaced = true;
                    break;
                }
            }
            if (!isPlaced) {
                List<ScheduleRect> newColumn = new ArrayList<ScheduleRect>();
                newColumn.add(eventRect);
                columns.add(newColumn);
            }
        }

        // 最左侧是所有没有重叠的事件，他们放在第一列
        int maxRowCount = columns.get(0).size();
        for (int i = 0; i < maxRowCount; i++) {
            // Set the left and right values of the event.
            int j = 0;
            for (List<ScheduleRect> column : columns) {
                if (column.size() >= i+1) {
                    ScheduleRect eventRect = column.get(i);
                    eventRect.width = 1f / columns.size();
                    eventRect.left = j / columns.size();
                    eventRect.top = eventRect.event.getStartTime().get(Calendar.HOUR_OF_DAY) * 60 + eventRect.event.getStartTime().get(Calendar.MINUTE);
                    eventRect.bottom = eventRect.event.getEndTime().get(Calendar.HOUR_OF_DAY) * 60 + eventRect.event.getEndTime().get(Calendar.MINUTE);
                }
                j++;
            }
        }
    }

    private boolean isTwoScheduleCollide(UserSchedule event1, UserSchedule event2) {
//        long start1 = event1.getStartTime().getTimeInMillis() / 60000;
//        long end1 = event1.getEndTime().getTimeInMillis() / 60000;
//        long start2 = event2.getStartTime().getTimeInMillis() / 60000;
//        long end2 = event2.getEndTime().getTimeInMillis() / 60000;
//        return !((start1 > end2) || (end1 < start2));
        return false;
    }

}
