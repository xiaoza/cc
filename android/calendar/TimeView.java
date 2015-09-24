package com.meiyebang.meiyebang.pad.view.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meiyebang.meiyebang.pad.model.Order;
import com.meiyebang.meiyebang.pad.model.Product;
import com.meiyebang.meiyebang.pad.model.UserSchedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yuzhen on 15/6/27.
 */
public class TimeView extends TextView {

    private static final String HEADER_EXAMPLE = "今天（闲）";
    private static final String TIME_EXAMPLE = "00:00";
    private static final String CAN_SCHEDULE = "可预约";
    private static final String HAS_SCHEDULED = "已预约";
    private static final String MARK_SCHEDULE = "当前预约";
    private static final String OVERDUE_SCHEDULE = "不可预约";
    private static final String DEFAULT_TIP = "美容师该时间段无法预约，请选择其他时间段";

    private int mHeaderTextSize = 14;
    private int mHeaderTextColor = Color.parseColor("#333333");
    private int mHeaderBackgroundColor = Color.parseColor("#e8e8e8");
    private int mHeaderFocusedBackgroundColor = Color.parseColor("#ffffff");

    private int mTimeTextSize = 18;
    private int mContentTextSize = 16;

    private int mTextColorNormal = Color.parseColor("#666666");
    private int mTextColorHighlight = Color.parseColor("#ffffff");
    private int mTextColorInvalid = Color.parseColor("#cccccc");

    private int mContentBackgroundColorNormal = Color.parseColor("#ffffff");
    private int mContentBackgroundColorHighlight = Color.parseColor("#e9768b");

    private int mDividerColor = Color.parseColor("#cccccc");
    private int mDividerSize = 1;

    private int mHeaderTextHeight;
    private int mHeaderRowPadding = 15;
    private int mHeaderFullHeight;

    private int mTimeTextHeight;

    private final Context mContext;

    private Paint mHeaderTextPaint;
    private Paint mHeaderBackgroundPaint;
    private Paint mHeaderFocusedBackgroundPaint;

    private Paint mTimeTextPaint;
    private Paint mContentTextPaint;
    private Paint mContentBackgroundPaint;

    private Paint mDividerPaint;

    private int shopOpenTime = 6 * 60;
    private int shopCloseTime = 22 * 60;
    private int mRowCount = 8;
    private int mColumnCount = 6;
    private int mCellWidth;
    private int mCellHeight;

    private SparseArray<SparseBooleanArray> occupiedMinutes = new SparseArray<SparseBooleanArray>();
    private SparseArray<String> headerTitles = new SparseArray<String>();

    private int currentDay = -1;
    private List<Integer> currentMinutes = new ArrayList<Integer>();

    private int mCurrentColumn = 0;
    private String invalidTip = DEFAULT_TIP;

    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mPaddingBottom;

    private OnTimeClickListener onTimeClickListener;
    private GestureDetectorCompat mGestureDetector;
    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            int columnNum = (int)Math.floor(e.getX() / (mCellWidth + mDividerSize));
            if (e.getY() < mHeaderFullHeight) {
                if (mCurrentColumn != columnNum) {
                    mCurrentColumn = columnNum;
                    playSoundEffect(SoundEffectConstants.CLICK);
                    invalidate();
                }
            } else if (onTimeClickListener != null) {
                int rowNum = (int)Math.floor((e.getY() - mHeaderFullHeight) / (mCellHeight + mDividerSize));
                int selectedMinute = (rowNum * mColumnCount + columnNum) * Product.DURATION_STEP + shopOpenTime;
                playSoundEffect(SoundEffectConstants.CLICK);

                Calendar calendar = Calendar.getInstance();
                int today = CalendarHelper.getIntDate(calendar);
                int currentTime = CalendarHelper.getTimeInOneDay(calendar);

                boolean isAvailable = true;
                if (occupiedMinutes.valueAt(mCurrentColumn) != null && selectedMinute < shopCloseTime) {
                    int currentSelectedDay = occupiedMinutes.keyAt(mCurrentColumn);
                    if (currentSelectedDay < today ||
                            (today == currentSelectedDay && selectedMinute <= currentTime)) {
                        isAvailable = false;
                    } else {
                        // 新建的情况为空
                        if (currentMinutes.isEmpty()) {
                            if (occupiedMinutes.valueAt(mCurrentColumn).get(selectedMinute)) {
                                isAvailable = false;
                            }
                        } else {
                            // 修改的情况，看是否有冲突
                            for (int tmp = selectedMinute, i = 0; i < currentMinutes.size(); ++i, tmp += Product.DURATION_STEP) {
                                if (occupiedMinutes.valueAt(mCurrentColumn).get(tmp)) {
                                    isAvailable = false;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    isAvailable = false;
                }

                if (isAvailable) {
                    Calendar selected = CalendarHelper.getCalendar(occupiedMinutes.keyAt(mCurrentColumn));
                    if (selected != null) {
                        selected.set(Calendar.HOUR_OF_DAY, selectedMinute / 60);
                        selected.set(Calendar.MINUTE, selectedMinute % 60);
                    }
                    onTimeClickListener.onTimeClick(selected);
                } else {
                    Toast.makeText(mContext, invalidTip, Toast.LENGTH_LONG).show();
                }
            }
            return super.onSingleTapConfirmed(e);
        }
    };

    public void setShopTime(int start, int end) {
        int duration = end - start;
        int steps = duration / Product.DURATION_STEP;
        if (steps % mColumnCount == 0) {
            mRowCount = steps / mColumnCount;
        } else {
            mRowCount = steps / mColumnCount + 1;
        }
        shopOpenTime = start;
        shopCloseTime = end;
    }

    public void setCurrentSelect(Order order) {
        if (order != null && order.getStartTime() != null && order.getDuration() > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(order.getStartTime());

            currentDay = -1;
            currentMinutes.clear();

            currentDay = CalendarHelper.getIntDate(calendar);

            int start = CalendarHelper.getTimeInOneDay(calendar);
            int duration = (int)Math.ceil(order.getDuration() / Product.DURATION_STEP);
            for (int time = start, i = 0; i < duration; ++i, time += Product.DURATION_STEP) {
                currentMinutes.add(time);
            }
            invalidate();
        }
    }

    public void setData(Calendar startDay, List<UserSchedule> schedules) {
        if (schedules != null) {
            occupiedMinutes.clear();
            headerTitles.clear();
            Calendar day = (Calendar)startDay.clone();
            for (int i = 0; i < mColumnCount; i++) {
                int dayInt = CalendarHelper.getIntDate(day);
                occupiedMinutes.put(dayInt, new SparseBooleanArray());
                headerTitles.put(dayInt, CalendarHelper.getFriendlyDate(day));
                day.add(Calendar.DATE, 1);
            }

            for (UserSchedule schedule : schedules) {
                Calendar startTime = schedule.getStartTime();
                Calendar endTime = schedule.getEndTime();
                int dayInt = CalendarHelper.getIntDate(startTime);
                if (occupiedMinutes.get(dayInt) == null) {
                    continue;
                }

                int start = CalendarHelper.getTimeInOneDay(startTime);
                int end = CalendarHelper.getTimeInOneDay(endTime);
                for (int time = start; time < end; time += Product.DURATION_STEP) {
                    occupiedMinutes.get(dayInt).put(time, true);
                }
            }

            mCurrentColumn = 0;
            invalidate();
        }
    }

    public TimeView(Context context) {
        this(context, null);
    }

    public TimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mHeaderTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mHeaderTextSize, context.getResources().getDisplayMetrics());
        mTimeTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTimeTextSize, context.getResources().getDisplayMetrics());
        mContentTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mContentTextSize, context.getResources().getDisplayMetrics());
        mDividerSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mDividerSize, context.getResources().getDisplayMetrics());
        mHeaderRowPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mHeaderRowPadding, context.getResources().getDisplayMetrics());
        init();
    }

    private void init() {
        Calendar day = Calendar.getInstance();
        for (int i = 0; i < mColumnCount; i++) {
            String title = i == 0 ? "今天" : i == 1 ? "明天" : i == 2 ? "后天" : CalendarHelper.getFriendlyDate(day);
            headerTitles.put(i, title);
            day.add(Calendar.DATE, 1);
        }

        mGestureDetector = new GestureDetectorCompat(mContext, mGestureListener);

        mHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeaderTextPaint.setTextAlign(Paint.Align.CENTER);
        mHeaderTextPaint.setTextSize(mHeaderTextSize);
        mHeaderTextPaint.setColor(mHeaderTextColor);

        Rect rect = new Rect();
        mHeaderTextPaint.getTextBounds(HEADER_EXAMPLE, 0, HEADER_EXAMPLE.length(), rect);
        mHeaderTextHeight = rect.height();
        mHeaderFullHeight = mHeaderTextHeight + 2 * mHeaderRowPadding;

        mTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextPaint.setTextAlign(Paint.Align.CENTER);
        mTimeTextPaint.setTextSize(mTimeTextSize);
        mTimeTextPaint.setColor(mTextColorNormal);

        mTimeTextPaint.getTextBounds(TIME_EXAMPLE, 0, TIME_EXAMPLE.length(), rect);
        mTimeTextHeight = rect.height();

        mContentTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mContentTextPaint.setTextAlign(Paint.Align.CENTER);
        mContentTextPaint.setTextSize(mContentTextSize);
        mContentTextPaint.setColor(mTextColorNormal);

        mHeaderBackgroundPaint = new Paint();
        mHeaderBackgroundPaint.setColor(mHeaderBackgroundColor);
        mHeaderFocusedBackgroundPaint = new Paint();
        mHeaderFocusedBackgroundPaint.setColor(mHeaderFocusedBackgroundColor);

        mContentBackgroundPaint = new Paint();
        mContentBackgroundPaint.setColor(mContentBackgroundColorNormal);

        mDividerPaint = new Paint();
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setStrokeWidth(mDividerSize);
        mDividerPaint.setColor(mDividerColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaddingLeft = getCompoundPaddingLeft();
        mPaddingTop = getCompoundPaddingTop();
        mPaddingRight = getCompoundPaddingRight();
        mPaddingBottom = getCompoundPaddingBottom();

        calculateCellWidth();
        calculateCellHeight();
        resize();

        drawHeader(canvas);
        drawContent(canvas);
        drawDivider(canvas);

        canvas.restore();
    }

    private void drawDivider(Canvas canvas) {
        for (int row = 0; row < mRowCount; row++) {
            float top = mPaddingTop + mHeaderFullHeight + row * (mCellHeight + mDividerSize) + mDividerSize / 2;
            canvas.drawLine(0, top, getWidth(), top, mDividerPaint);
        }
        for (int column = 1; column < mColumnCount; column++) {
            float left = mPaddingLeft + column * (mCellWidth + mDividerSize) - mDividerSize / 2;
            canvas.drawLine(left, 0, left, getHeight(), mDividerPaint);
        }
    }

    private void drawHeader(Canvas canvas) {
        for (int column = 0; column < mColumnCount; column++) {
            float left = mPaddingLeft + column * (mCellWidth + mDividerSize);
            float top = mPaddingTop;
            float right = left + mCellWidth;
            float bottom = top + mHeaderFullHeight;
            if (column == mCurrentColumn) {
                canvas.drawRect(left, top, right, bottom, mHeaderFocusedBackgroundPaint);
            } else {
                canvas.drawRect(left, top, right, bottom, mHeaderBackgroundPaint);
            }

            // 绘制每列的title
            String title = headerTitles.valueAt(column);
            canvas.drawText(title, left + mCellWidth / 2, mHeaderTextHeight + mHeaderRowPadding, mHeaderTextPaint);
        }
    }

    private void drawContent(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        int startTime = shopOpenTime;
        boolean isSameDay = false;
        boolean isToday = false;
        if (headerTitles.valueAt(mCurrentColumn) != null) {
            isSameDay = currentDay == headerTitles.keyAt(mCurrentColumn);
            isToday = headerTitles.keyAt(mCurrentColumn) == CalendarHelper.getIntDate(calendar);
        }

        int currentTime = CalendarHelper.getTimeInOneDay(calendar);
        for (int row = 0; row < mRowCount; row++) {
            for (int column = 0; column < mColumnCount; column++) {
                float left = mPaddingLeft + column * (mCellWidth + mDividerSize);
                float top = mPaddingTop + row * (mCellHeight + mDividerSize) + mHeaderFullHeight + mDividerSize;
                float right = left + mCellWidth;
                float bottom = top + mCellHeight;

                boolean isMarked = isSameDay;
                if (isMarked) {
                    isMarked = currentMinutes.contains(startTime);
                }

                boolean isValid = true;
                if ((isToday && startTime <= currentTime) || startTime >= shopCloseTime) {
                    isValid = false;
                }
                if (isScheduled(startTime)) {
                    isValid = false;
                }

                prepareContentPaint(isValid, isMarked);

                //绘制背景
                canvas.drawRect(left, top, right, bottom, mContentBackgroundPaint);

                // 绘制内容
                String time = CalendarHelper.getTimeStrByPassedMinutes(startTime);
                canvas.drawText(time, left + mCellWidth / 2, top + mCellHeight / 2 + mTimeTextHeight / 2, mTimeTextPaint);
                startTime += Product.DURATION_STEP;
            }
        }
    }

    private boolean isScheduled(int hour) {
        boolean res = false;
        if (occupiedMinutes.valueAt(mCurrentColumn) != null && occupiedMinutes.valueAt(mCurrentColumn).get(hour)) {
            res = true;
        }
        return res;
    }

    private void prepareContentPaint(boolean isValid, boolean isSelected) {
        if (isSelected) {
            mContentBackgroundPaint.setColor(mContentBackgroundColorHighlight);
            mTimeTextPaint.setColor(mTextColorHighlight);
            mContentTextPaint.setColor(mTextColorHighlight);
            return;
        }

        if (!isValid) {
            mContentBackgroundPaint.setColor(mContentBackgroundColorNormal);
            mTimeTextPaint.setColor(mTextColorInvalid);
            mContentTextPaint.setColor(mTextColorInvalid);
        } else {
            mContentBackgroundPaint.setColor(mContentBackgroundColorNormal);
            mTimeTextPaint.setColor(mTextColorNormal);
            mContentTextPaint.setColor(mTextColorNormal);
        }
    }

    private void calculateCellWidth() {
        int dividerTotalWidth = (mColumnCount - 1) * mDividerSize;
        int availableWidth = getWidth() - dividerTotalWidth - mPaddingLeft - mPaddingRight;
        mCellWidth = availableWidth / mColumnCount;
    }

    private void calculateCellHeight() {
        int dividerTotalHeight = mRowCount * mDividerSize;
        int availableHeight = getHeight() - mHeaderFullHeight - dividerTotalHeight - mPaddingTop - mPaddingBottom;
        mCellHeight = availableHeight / mRowCount;
    }

    private void resize() {
        int realWidth = (mColumnCount - 1) * mDividerSize + mCellWidth * mColumnCount + mPaddingLeft + mPaddingRight;
        int realHeight = mRowCount * mDividerSize + mCellHeight * mRowCount + mHeaderFullHeight + mPaddingTop + mPaddingBottom;
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = realWidth;
        params.height = realHeight;
        setLayoutParams(params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public interface OnTimeClickListener {
        void onTimeClick(Calendar selectedTime);
    }

    public void setOnTimeClickListener(OnTimeClickListener onTimeClickListener) {
        this.onTimeClickListener = onTimeClickListener;
    }

}
