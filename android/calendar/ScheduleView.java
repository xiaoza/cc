package com.meiyebang.meiyebang.pad.view.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Scroller;

import com.meiyebang.meiyebang.pad.model.Product;
import com.meiyebang.meiyebang.pad.model.UserSchedule;
import com.meiyebang.meiyebang.pad.util.LocalUtil;

import java.util.Calendar;
import java.util.List;

/**
 * Created by yuzhen on 15/6/3.
 */
public class ScheduleView extends View {

    private int mCurMode = ScheduleCalendar.MODE_FIXED_DAY;

    private static final String TIME_FORMAT = "00:00";
    public static final int DEFAULT_COLUMN_NUM = 7;

    /**
     * 最左边一列和最上边一行统称为header
     */

    private final Context mContext;
    private Paint mTimeTextPaint;
    private Paint mHeaderColumnBackgroundPaint;
    private Paint mHeaderBackgroundPaint;
    private Paint mHeaderTextPaint;
    private float mHeaderColumnWidth;

    private Paint mColumnBackgroundPaint;
    private Paint mHourSeparatorPaint;
    private Paint mEventBackgroundPaint;
    private TextPaint mEventTextPaint;

    private Paint mTimelinePaint;
    private Paint mTimelineTextPaint;

    private float mColumnWidth;
    private GestureDetectorCompat mGestureDetector;
    private OverScroller mScroller;
    private Scroller mStickyScroller;
    private PointF mCurrentOrigin = new PointF(0f, 0f);
    private Direction mCurrentScrollDirection = Direction.NONE;
    private Direction mCurrentFlingDirection = Direction.NONE;

    private ScheduleAdapter mScheduleAdapter;

    private float mDistanceY = 0;
    private float mDistanceX = 0;

    private int firstTime = 480; // 天视图中时间开始
    private int lastTime = 1320; // 天视图中时间结束

    // Attributes and their default values.
    private int mRowCount = (lastTime - firstTime) / Product.DURATION_STEP;
    private int mRowHeight = LocalUtil.dip2px(56);

    private int mTextSize = 12;
    private int mNumberOfVisibleColumns = DEFAULT_COLUMN_NUM;

    private ColorHelper mColorHelper = new ColorHelper();
    private SizeHelper mSizeHelper = new SizeHelper();

    private int mVerticalScrollTo = firstTime;

    private boolean mAreDimensionsInvalid = true;
    private boolean mIsDrawTimeline = true;
    private boolean isLoadingMoreSchedules = false;

    // Listeners.
    private HeaderClickListener mHeaderClickListener;
    private ScheduleClickListener mScheduleClickListener;
    private ScheduleLongPressListener mScheduleLongPressListener;
    private EmptyViewClickListener mEmptyViewClickListener;
    private EmptyViewLongPressListener mEmptyViewLongPressListener;
    private WeekChangeListener mWeekChangeListener;

    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            mScroller.forceFinished(true);
            mStickyScroller.forceFinished(true);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mCurrentScrollDirection == Direction.NONE) {
                if (Math.abs(distanceX) > Math.abs(distanceY)){
                    mCurrentScrollDirection = Direction.HORIZONTAL;
                    mCurrentFlingDirection = Direction.HORIZONTAL;
                } else {
                    mCurrentFlingDirection = Direction.VERTICAL;
                    mCurrentScrollDirection = Direction.VERTICAL;
                }
            }
            mDistanceX = distanceX;
            mDistanceY = distanceY;
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.forceFinished(true);
            mStickyScroller.forceFinished(true);

            if (mCurrentFlingDirection == Direction.HORIZONTAL){
                int minX = (int) -(getColumnFullWidth() * mScheduleAdapter.size() - mSizeHelper.getColumnGap() + mHeaderColumnWidth - getWidth());
                int maxX = 0;
                mScroller.fling((int) mCurrentOrigin.x, 0, (int) (velocityX), 0, minX, maxX, 0, 0);
            } else if (mCurrentFlingDirection == Direction.VERTICAL) {
                int minY = 0;
                int maxY = 0;
                if (mCurMode == ScheduleCalendar.MODE_FIXED_DAY) {
                    minY = getMinY();
                    maxY = getMaxY();
                } else if (mCurMode == ScheduleCalendar.MODE_SCROLL_WEEK) {
                    float availableScreenHeight = getContentHeight();
                    int screens = 0;
                    if (velocityY > 0 && velocityY > 750) { //下滑
                        screens = (int)Math.ceil(mCurrentOrigin.y / availableScreenHeight);
                    } else if (velocityY < 0 && velocityY < -750) {
                        screens = (int)Math.floor(mCurrentOrigin.y / availableScreenHeight);
                    } else {
                        screens = Math.round(mCurrentOrigin.y / availableScreenHeight);
                    }
                    maxY = minY = (int)(screens * availableScreenHeight);
                }
                mScroller.fling(0, (int) mCurrentOrigin.y, 0, (int) velocityY, 0, 0, minY, maxY);
            }

            ViewCompat.postInvalidateOnAnimation(ScheduleView.this);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // 如果是周视图，全认为是空得
            if (mCurMode == ScheduleCalendar.MODE_SCROLL_WEEK) {
                if (mEmptyViewClickListener != null &&
                        e.getX() > mHeaderColumnWidth &&
                        e.getY() > getConstTopHeight()) {
                    Cell cell = getTapCell(e.getX(), e.getY());
                    if (cell != null) {
                        playSoundEffect(SoundEffectConstants.CLICK);
                        mEmptyViewClickListener.onEmptyViewClicked(cell);
                        return super.onSingleTapConfirmed(e);
                    }
                }
            }

            // 如果点击了某个预约
            if (mScheduleAdapter != null && mScheduleClickListener != null) {
                ScheduleRect rect = mScheduleAdapter.isDropIn(e);
                if (rect != null) {
                    playSoundEffect(SoundEffectConstants.CLICK);
                    mScheduleClickListener.onScheduleClick(rect.event, rect.rectF);
                    return super.onSingleTapConfirmed(e);
                }
            }

            // 如果点击了header
            if (mHeaderClickListener != null &&
                    e.getX() > mHeaderColumnWidth &&
                    e.getY() < getConstTopHeight()) {
                Cell cell = getTapCell(e.getX(), e.getY());
                if (cell != null) {
                    playSoundEffect(SoundEffectConstants.CLICK);
                    mHeaderClickListener.onHeaderClick(cell.getColumnKey(), cell.getColumnName());
                    return super.onSingleTapConfirmed(e);
                }
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            if (mCurMode == ScheduleCalendar.MODE_FIXED_DAY) {
                // 如果长按了某个预约
                if (mScheduleAdapter != null && mScheduleClickListener != null) {
                    ScheduleRect rect = mScheduleAdapter.isDropIn(e);
                    if (rect != null) {
                        mScheduleLongPressListener.onScheduleLongPress(rect);
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        return;
                    }
                }

                // 长按了空白区域
                if (mEmptyViewLongPressListener != null &&
                        e.getX() > mHeaderColumnWidth &&
                        e.getY() > getConstTopHeight()) {
                    Cell cell = getTapCell(e.getX(), e.getY());
                    if (cell != null) {
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        mEmptyViewLongPressListener.onEmptyViewLongPress(cell);
                    }
                }
            } else if (mCurMode == ScheduleCalendar.MODE_SCROLL_WEEK) {
                if (mEmptyViewLongPressListener != null &&
                        e.getX() > mHeaderColumnWidth &&
                        e.getY() > getConstTopHeight()) {
                    Cell cell = getTapCell(e.getX(), e.getY());
                    if (cell != null) {
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        mEmptyViewLongPressListener.onEmptyViewLongPress(cell);
                    }
                }
            }
        }
    };

    private enum Direction {
        NONE, HORIZONTAL, VERTICAL
    }

    public ScheduleView(Context context) {
        this(context, null);
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, context.getResources().getDisplayMetrics());
        init();
    }

    private void init() {
        setBackgroundColor(mColorHelper.getCalendarDividerColor());

        // Scrolling initialization.
        mGestureDetector = new GestureDetectorCompat(mContext, mGestureListener);
        mScroller = new OverScroller(mContext);
        mStickyScroller = new Scroller(mContext);

        // Measure settings for time column.
        mTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextPaint.setTextAlign(Paint.Align.RIGHT);
        mTimeTextPaint.setTextSize(mTextSize);
        mTimeTextPaint.setColor(mColorHelper.getHeaderTextColor());
        Rect rect = new Rect();
        mTimeTextPaint.getTextBounds(TIME_FORMAT, 0, TIME_FORMAT.length(), rect);
        mSizeHelper.setTimeTextWidth(mTimeTextPaint.measureText(TIME_FORMAT));
        mSizeHelper.setTimeTextHeight(rect.height());
        mSizeHelper.setHeaderMarginBottom(rect.height() / 2);

        // Measure settings for header row.
        mHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeaderTextPaint.setColor(mColorHelper.getHeaderTextColor());
        mHeaderTextPaint.setTextAlign(Paint.Align.CENTER);
        mHeaderTextPaint.setTextSize(mTextSize);
        mHeaderTextPaint.getTextBounds(TIME_FORMAT, 0, TIME_FORMAT.length(), rect);
        mHeaderTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mSizeHelper.setHeaderTextHeight(rect.height());

        // Prepare header background paint.
        mHeaderBackgroundPaint = new Paint();
        mHeaderBackgroundPaint.setColor(mColorHelper.getRowHeaderBgColor());

        // Prepare day background color paint.
        mColumnBackgroundPaint = new Paint();
        mColumnBackgroundPaint.setColor(mColorHelper.getColumnBgColor());

        // Prepare hour separator color paint.
        mHourSeparatorPaint = new Paint();
        mHourSeparatorPaint.setStyle(Paint.Style.STROKE);
        mHourSeparatorPaint.setStrokeWidth(mSizeHelper.getRowDividerHeight());
        mHourSeparatorPaint.setColor(mColorHelper.getCalendarDividerColor());

        // Prepare header column background color.
        mHeaderColumnBackgroundPaint = new Paint();
        mHeaderColumnBackgroundPaint.setColor(mColorHelper.getColumnHeaderBgColor());

        // Prepare event background color.
        mEventBackgroundPaint = new Paint();
        mEventBackgroundPaint.setColor(mColorHelper.getDefaultScheduleBgColor());

        // Prepare event text size and color.
        mEventTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        mEventTextPaint.setStyle(Paint.Style.FILL);
        mEventTextPaint.setColor(mColorHelper.getDefaultScheduleTextColor());
        mEventTextPaint.setTextSize(mTextSize);

        mTimelinePaint = new Paint();
        mTimelinePaint.setColor(mColorHelper.getTimelineColor());
        mTimelinePaint.setStrokeWidth(2f);

        mTimelineTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimelineTextPaint.setTextAlign(Paint.Align.RIGHT);
        mTimelineTextPaint.setTextSize(mTextSize);
        mTimelineTextPaint.setColor(mColorHelper.getTimelineColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mScheduleAdapter == null) {
            throw new RuntimeException("data not initialized");
        }
        // 绘制主体和预约信息
        drawHeaderAndSchedules(canvas);

        // Hide everything in the first cell (top left corner).
        canvas.drawRect(0, 0, mSizeHelper.getTimeTextWidth() + mSizeHelper.getHeaderPadding() * 2, getHeaderHeight(), mHeaderBackgroundPaint);

        // 绘制标题下面的一小块空白
        canvas.drawRect(mHeaderColumnWidth, getHeaderHeight(), getWidth(), getConstTopHeight() - mSizeHelper.getRowDividerHeight() / 2, mHeaderColumnBackgroundPaint);
        if (mCurMode == ScheduleCalendar.MODE_FIXED_DAY) {
            canvas.drawRect(mHeaderColumnWidth, getHeight() - (mSizeHelper.getHeaderTextHeight() - mSizeHelper.getRowDividerHeight()) / 2, getWidth(), getHeight(), mHeaderColumnBackgroundPaint);
        }
    }

    private int getMinY() {
        return (int) -(lastTime / Product.DURATION_STEP * mRowHeight + getConstTopHeight() + mSizeHelper.getHeaderTextHeight() / 2 - getHeight());
    }

    private int getMaxY() {
        return -(firstTime / Product.DURATION_STEP * mRowHeight);
    }

    private void drawHeaderAndSchedules(Canvas canvas) {
        if (mCurrentScrollDirection == Direction.HORIZONTAL) {
            float minScrollLeft = -(getColumnFullWidth() * mScheduleAdapter.size() - mSizeHelper.getColumnGap() + mHeaderColumnWidth - getWidth());
            if (mCurrentOrigin.x > mDistanceX) {
                mCurrentOrigin.x = 0;
            }
            else if (mCurrentOrigin.x - mDistanceX < minScrollLeft) {
                mCurrentOrigin.x = minScrollLeft;
            }
            else {
                mCurrentOrigin.x -= mDistanceX;
            }
        } else if (mCurrentScrollDirection == Direction.VERTICAL && mCurMode == ScheduleCalendar.MODE_FIXED_DAY) {
            if (mCurrentOrigin.y - getMaxY() > mDistanceY) {
                mCurrentOrigin.y = getMaxY();
            } else if (mCurrentOrigin.y - mDistanceY < getMinY()) {
                mCurrentOrigin.y = getMinY();
            } else {
                mCurrentOrigin.y -= mDistanceY;
            }
        }
        if (mCurMode == ScheduleCalendar.MODE_FIXED_DAY) {
            drawFixedDayView(canvas);
        } else if (mCurMode == ScheduleCalendar.MODE_SCROLL_WEEK) {
            drawUpDownScrollWeekView(canvas);
        }

        // 绘制纵轴
        drawHeaderColumn(canvas);

        // 绘制横轴。先绘制事件，再绘制头部，为了覆盖事件的文字
        drawHeader(canvas);
    }

    // 计算每列的宽度
    private void calculateColumnWidth() {
        mHeaderColumnWidth = mSizeHelper.getTimeTextWidth() + mSizeHelper.getHeaderPadding() * 2;
        mColumnWidth = getWidth() - mHeaderColumnWidth - mSizeHelper.getColumnGap() * (mNumberOfVisibleColumns - 1);
        mColumnWidth = mColumnWidth / mNumberOfVisibleColumns;
    }

    // 准备要绘制的每个小时分割线的四条边
    private float[] prepareRowDivider() {
        int lineCount = (int) (getContentHeight() / mRowHeight) + 1;
        lineCount = (lineCount) * (mNumberOfVisibleColumns + 1); //加1是应为左右两侧可能都只显示半个column，这样一屏显示的是 +1个column
        return new float[lineCount * 4]; // 每个分割线的绘制需要四条边
    }

    private void drawRowDivider(Canvas canvas, float columnLeft, float columnRight, int startRow, int endRow) {
        float[] hourLines = prepareRowDivider();
        // 往下移二分之一字体的高度，让分割线看起来在字体的中间位置
        float constTop = getConstTopHeight();
        int i = 0;
        for (int row = startRow; row < endRow; ++row) {
            float hourSeparatorTop = constTop + mCurrentOrigin.y + mRowHeight * row;
            if (hourSeparatorTop + mSizeHelper.getRowDividerHeight() > constTop && hourSeparatorTop < getHeight()){
                hourLines[i * 4] = columnLeft;
                hourLines[i * 4 + 1] = hourSeparatorTop;
                hourLines[i * 4 + 2] = columnRight;
                hourLines[i * 4 + 3] = hourSeparatorTop;
                i++;
            }
        }
        canvas.drawLines(hourLines, mHourSeparatorPaint);
    }

    private void drawFixedDayView(Canvas canvas) {
        calculateColumnWidth();

        // 从指定行开始显示
        if (mAreDimensionsInvalid) {
            mAreDimensionsInvalid = false;
            if(mVerticalScrollTo >= firstTime) {
                goToMinutes(mVerticalScrollTo);
            }

            mVerticalScrollTo = Integer.MIN_VALUE;
            mAreDimensionsInvalid = false;
        }

        // 清空预约事件的位置信息
        mScheduleAdapter.clearScheduleRects();

        // 循环绘制每一列信息
        float constTop = getConstTopHeight() - mSizeHelper.getRowDividerHeight() / 2;
        for (int columnIndex = 0; columnIndex < mScheduleAdapter.size(); ++columnIndex) {
            float left = mHeaderColumnWidth + mCurrentOrigin.x + getColumnFullWidth() * columnIndex;
            if (left < -mColumnWidth || left > getWidth() + mColumnWidth) {
                continue;
            }

            // 绘制每一列的背景
            float columnRight = left + mColumnWidth;
            float columnLeft =  left < 0 ? 0 : left;
            columnRight = columnRight > getWidth() ? getWidth() : columnRight;
            canvas.drawRect(columnLeft, constTop, columnRight, getHeight(), mColumnBackgroundPaint);

            // 绘制每一列的时间分割线
            drawRowDivider(canvas, columnLeft, columnRight, 0, mRowCount);

            // 绘制事件
            drawDaySchedules(canvas, mScheduleAdapter.valueAt(columnIndex), left);
        }
    }

    private void drawUpDownScrollWeekView(Canvas canvas) {
        calculateColumnWidth();

        // 清空预约事件的位置信息
        mScheduleAdapter.clearScheduleRects();

        // Consider scroll offset.
        if (mCurrentScrollDirection == Direction.VERTICAL) {
            mCurrentOrigin.y -= mDistanceY;
        }

        float constTop = getConstTopHeight();

        // 横轴上面的天数
        int uponDays = (int) -(Math.ceil(mCurrentOrigin.y / mRowHeight));
        Calendar firstVisibleDay = Calendar.getInstance();
        firstVisibleDay.add(Calendar.DATE, uponDays);

        int uponDaysFloor = (int) -Math.floor(mCurrentOrigin.y / mRowHeight);
        if (uponDays != uponDaysFloor) {
            uponDays += 1;
        }

        if (!isLoadingMoreSchedules && mCurrentScrollDirection != Direction.HORIZONTAL) {
            Calendar startForMore = mScheduleAdapter.getStartDayForMoreSchedules(firstVisibleDay);
            if (startForMore != null && mWeekChangeListener != null) {
                mWeekChangeListener.onWeekChanged(startForMore);
                isLoadingMoreSchedules = true;
            }
        }

        // 循环绘制每一列信息
        for (int columnIndex = 0; columnIndex < mScheduleAdapter.size(); ++columnIndex) {
            float columnLeftStart = mHeaderColumnWidth + mCurrentOrigin.x + getColumnFullWidth() * columnIndex;
            // 如果该列已不可见，则不在绘制
            if (columnLeftStart < -mColumnWidth || columnLeftStart > getWidth() + mColumnWidth) {
                continue;
            }

            // 绘制每一列的背景
            float columnRightReal = columnLeftStart + mColumnWidth;
            float columnLeftReal =  columnLeftStart < 0 ? 0 : columnLeftStart;
            columnRightReal = columnRightReal > getWidth() ? getWidth() : columnRightReal;
            canvas.drawRect(columnLeftReal, constTop, columnRightReal, getHeight(), mColumnBackgroundPaint);

            // 绘制每一列的分割线
            drawRowDivider(canvas, columnLeftReal, columnRightReal, uponDays, DEFAULT_COLUMN_NUM + uponDays);

            // 绘制事件
            drawWeekSchedules(canvas, mScheduleAdapter.valueAt(columnIndex), columnLeftStart);
        }
    }

    private void drawTimeline(Canvas canvas, float top) {
        if (0 < top && top < getHeight()) {
            canvas.drawLine(mSizeHelper.getTimeTextWidth() + mSizeHelper.getHeaderPadding() * 2, top, getWidth(), top, mTimelinePaint);
        }
    }

    /**
     * 绘制横轴标题
     */
    private void drawHeader(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeaderHeight(), mHeaderBackgroundPaint);
        for (int columnIndex = 0; columnIndex < mScheduleAdapter.size(); ++columnIndex) {
            float left = mHeaderColumnWidth + mCurrentOrigin.x + getColumnFullWidth() * columnIndex;
            if (left < -mColumnWidth || left > getWidth() + mColumnWidth) {
                continue;
            }

            // 绘制每列的title
            String title = mScheduleAdapter.getColumnNameByIndex(columnIndex);
            canvas.drawText(title, left + mColumnWidth / 2, mSizeHelper.getHeaderTextHeight() + mSizeHelper.getHeaderPadding(), mHeaderTextPaint);
        }
    }

    /**
     * 绘制纵轴标题
     */
    private void drawHeaderColumn(Canvas canvas) {
        // 绘制列的背景
        canvas.drawRect(0, getHeaderHeight(), mHeaderColumnWidth, getHeight(), mHeaderColumnBackgroundPaint);

        if (mCurMode == ScheduleCalendar.MODE_FIXED_DAY) {
            Calendar calendar = Calendar.getInstance();
            int currentMinute = CalendarHelper.getTimeInOneDay(calendar);
            int innerHeight = currentMinute % Product.DURATION_STEP * mRowHeight / Product.DURATION_STEP;

            for (int minute = firstTime; minute <= lastTime; minute += Product.DURATION_STEP) {
                float timeHeight = mRowHeight * minute / Product.DURATION_STEP;
                float top = mSizeHelper.getHeaderFullHeightWidhMargin() + mCurrentOrigin.y + timeHeight;
                if (0 < top && top < getHeight()) {
                    String label = CalendarHelper.getTimeByMinutes(minute);
                    canvas.drawText(label, mSizeHelper.getTimeTextWidth() + mSizeHelper.getHeaderPadding(), top + mSizeHelper.getTimeTextHeight(), mTimeTextPaint);
                    if (mIsDrawTimeline && currentMinute >= minute && currentMinute < minute + Product.DURATION_STEP) {
                        canvas.drawText(CalendarHelper.getNormalTimeStr(calendar), mSizeHelper.getTimeTextWidth() + mSizeHelper.getHeaderPadding(), top + mSizeHelper.getTimeTextHeight() + innerHeight, mTimelineTextPaint);
                        drawTimeline(canvas, top + mSizeHelper.getTimeTextHeight() / 2 + innerHeight);
                    }
                }
            }
        } else if (mCurMode == ScheduleCalendar.MODE_SCROLL_WEEK) {
            int uponDays = (int) -Math.ceil(mCurrentOrigin.y / mRowHeight);
            Calendar visibleDay = Calendar.getInstance();
            visibleDay.add(Calendar.DATE, uponDays);
            for (int row = uponDays; row < DEFAULT_COLUMN_NUM + uponDays + 1; ++row) {
                float top = getHeaderHeight() + mSizeHelper.getHeaderMarginBottom() + mRowHeight / 2 + mCurrentOrigin.y + mRowHeight * row;
                String label = interpretDate(visibleDay);
                if (top > 0 && top < getHeight()) {
                    canvas.drawText(label, mSizeHelper.getTimeTextWidth() + mSizeHelper.getHeaderPadding(), top + mSizeHelper.getTimeTextHeight(), mTimeTextPaint);
                }
                visibleDay.add(Calendar.DATE, 1);
            }
        }
    }

    private void drawDaySchedules(Canvas canvas, SparseArray<List<ScheduleRect>> rowRects, float startLeftPix) {
        if (rowRects == null || rowRects.size() == 0) {
            return;
        }

        float constTop = getConstTopHeight();
        for (int index = 0; index < rowRects.size(); index++) {
            List<ScheduleRect> rects = rowRects.valueAt(index);
            for (ScheduleRect rect : rects) {
                // Calculate top.
                float top = mRowHeight * mRowCount * rect.top / 1440 + mCurrentOrigin.y + constTop + mSizeHelper.getScheduleVerticalMargin();
                float originalTop = top;
                if (top < constTop) {
                    top = constTop;
                }

                // Calculate bottom.
                float bottom = mRowHeight * mRowCount * rect.bottom / 1440 + mCurrentOrigin.y + constTop - mSizeHelper.getScheduleVerticalMargin();

                // Calculate left.
                float left = startLeftPix + rect.left * mColumnWidth;
                float originalLeft = left;
                if (left < 0) {
                    left = 0;
                }

                // Calculate right.
                float right = originalLeft + rect.width * mColumnWidth;
                if (right > getWidth()) {
                    right = getWidth();
                }

                // Draw the event and the event name on top of it.
                RectF eventRectF = new RectF(left, top, right, bottom);
                RectF fullRectF = new RectF(originalLeft, originalTop, right, bottom);
                if (bottom > constTop &&
                        left < right &&
                        eventRectF.right > 0 &&
                        eventRectF.left < getWidth() &&
                        eventRectF.bottom > constTop &&
                        eventRectF.top < getHeight()
                        ) {
                    rect.rectF = eventRectF;
                    rect.fullRectF = fullRectF;
                    drawOneSchedule(
                            canvas,
                            mColorHelper.getScheduleBgColor(rect.event),
                            mColorHelper.getScheduleTextColor(rect.event),
                            rect.rectF,
                            rect.event.getContent(),
                            originalTop, originalLeft
                    );
                }
                else {
                    rect.rectF = null;
                    rect.fullRectF = null;
                }
            }
        }
    }

    private void drawWeekSchedules(Canvas canvas, SparseArray<List<ScheduleRect>> columnRects, float originalLeft) {
        if (columnRects == null || columnRects.size() == 0) {
            return;
        }

        int padding = LocalUtil.dip2px(5);
        int eventRowMargin = LocalUtil.dip2px(3);
        float eventWidth = mColumnWidth - padding * 2;
        float eventHeight = (mRowHeight - padding * 2 - (eventRowMargin * 2)) / 3;  // 默认一个cell显示3个预约事件
        float constTop = getConstTopHeight();

        // 整个rect都在x轴上面才算
        int uponDays = (int) -Math.ceil(mCurrentOrigin.y / mRowHeight);
        Calendar firstVisibleDay = Calendar.getInstance();
        firstVisibleDay.add(Calendar.DATE, uponDays);

        for (int day = uponDays; day < DEFAULT_COLUMN_NUM + uponDays + 1; day++) {
            int curDayInt = CalendarHelper.getIntDate(firstVisibleDay);
            List<ScheduleRect> rectsForOneDay = columnRects.get(curDayInt);
            // 没有预约事件，直接跳过
            if (rectsForOneDay == null || rectsForOneDay.isEmpty()) {
                firstVisibleDay.add(Calendar.DATE, 1);
                continue;
            }

            // 遍历该用户在这一天的所有预约
            float originalTop = constTop + mCurrentOrigin.y + mRowHeight * day;
            for (int eventIndex = 0; eventIndex < rectsForOneDay.size(); eventIndex++) {
                float eventOriginLeft = originalLeft + padding;
                float eventOriginTop = originalTop + padding + eventIndex * (eventHeight + eventRowMargin);

                float eleft = eventOriginLeft;
                if (eleft < 0) {
                    eleft = 0;
                }
                float etop = eventOriginTop;
                if (etop < constTop) {
                    etop = constTop;
                }
                float ebottom = eventOriginTop + eventHeight;
                if (ebottom > getHeight()) {
                    ebottom = getHeight();
                }
                float eright = eventOriginLeft + eventWidth;
                if (eright > getWidth()) {
                    eright = getWidth();
                }

                if (eleft < eright && etop < ebottom && etop < getHeight() && ebottom > constTop) {
                    RectF rectF = new RectF(eleft, etop, eright, ebottom);
                    ScheduleRect rect = rectsForOneDay.get(eventIndex);
                    drawOneSchedule(
                            canvas,
                            mColorHelper.getScheduleBgColor(rect.event),
                            mColorHelper.getScheduleTextColor(rect.event),
                            rectF,
                            rect.event.getContent(),
                            eventOriginTop, eventOriginLeft
                    );
                }
            }

            firstVisibleDay.add(Calendar.DATE, 1);
        }
    }

    private void drawOneSchedule(Canvas canvas, int bgColor, int textColor, RectF rectF, String content, float originalTop, float originalLeft) {
        mEventBackgroundPaint.setColor(bgColor);
        canvas.drawRect(rectF, mEventBackgroundPaint);
        drawScheduleText(canvas, content, textColor, rectF, originalTop, originalLeft);
    }

    private void drawScheduleText(Canvas canvas, String text, int textColor, RectF rect, float originalTop, float originalLeft) {
        if (rect.right - rect.left - mSizeHelper.getSchedulePadding() * 2 < 0) {
            return;
        }
        mEventTextPaint.setColor(textColor);

        // Get text dimensions
        int availableWidth = (int) (rect.right - originalLeft - mSizeHelper.getSchedulePadding() * 2);
        int availableHeight = (int) (rect.bottom - originalTop - mSizeHelper.getSchedulePadding() * 2);

        StaticLayout textLayout = new StaticLayout(text, mEventTextPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int textFullHeight = textLayout.getHeight();
        int lineCount = textLayout.getLineCount();
        int lineHeight = textFullHeight / lineCount;
        int textFullWidth = lineCount > 1 ? availableWidth : (int)mEventTextPaint.measureText(text);

        // Crop height
        if (lineHeight < availableHeight && textLayout.getHeight() > rect.height() - mSizeHelper.getSchedulePadding() * 2) {
            int availableLineCount = (int) Math.floor(lineCount * availableHeight / textLayout.getHeight());
            float allLineWidth = (rect.right - originalLeft - mSizeHelper.getSchedulePadding() * 2) * availableLineCount;

            // 太多的内容用省略号代替
            CharSequence cutContent = TextUtils.ellipsize(text, mEventTextPaint, allLineWidth, TextUtils.TruncateAt.END);
            textLayout = new StaticLayout(cutContent, mEventTextPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            textFullHeight = textLayout.getHeight();
        }
        // 如果单行的高度都大于事件的可用高度，直接...代替
        else if (lineHeight >= availableHeight) {
            CharSequence newText = TextUtils.ellipsize(text, mEventTextPaint, availableWidth, TextUtils.TruncateAt.END);
            textLayout = new StaticLayout(newText, mEventTextPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        }

        // Draw text at vertical center
        canvas.save();
        canvas.translate(originalLeft + mSizeHelper.getSchedulePadding() + (availableWidth - textFullWidth) / 2, originalTop + mSizeHelper.getSchedulePadding() + (availableHeight - textFullHeight) / 2);
        textLayout.draw(canvas);
        canvas.restore();
    }

    public Cell getTapCell(float x, float y) {
        int leftColumnsWithGaps = (int) -(Math.ceil(mCurrentOrigin.x / getColumnFullWidth()));
        float startPixel = mCurrentOrigin.x + getColumnFullWidth() * leftColumnsWithGaps + mHeaderColumnWidth;
        float constHeight = getConstTopHeight();
        // 加1为了处理边缘的情况
        for (int columnNumber = leftColumnsWithGaps + 1;
             columnNumber <= mNumberOfVisibleColumns + leftColumnsWithGaps + 1;
             columnNumber++) {
            float start =  (startPixel < mHeaderColumnWidth ? mHeaderColumnWidth : startPixel);
            if (mColumnWidth + startPixel - start > 0 &&
                    x > start &&
                    x < startPixel + mColumnWidth) {
                Cell cell;
                int columnKey = mScheduleAdapter.keyAt(columnNumber - 1);
                String columnName = mScheduleAdapter.getColumnNameByKey(columnKey);

                float pixelsFromZero = y - mCurrentOrigin.y - constHeight;
                int oriRowNumber = (int)pixelsFromZero / mRowHeight;
                if (mCurMode == ScheduleCalendar.MODE_SCROLL_WEEK) {
                    int relRowNumber = oriRowNumber;
                    if (pixelsFromZero < 0) { // 时间往前滚动
                        relRowNumber = mRowCount - 1 - ((-relRowNumber) % mRowCount);
                    } else {
                        relRowNumber = relRowNumber % mRowCount;
                    }

                    int rowKey = mScheduleAdapter.getRowKeyByIndex(relRowNumber);
                    String rowName = mScheduleAdapter.getRowNameByIndex(relRowNumber);
                    cell = new Cell(columnKey, columnName, rowKey, rowName);
                } else {
                    cell = new Cell(columnKey, columnName, oriRowNumber * Product.DURATION_STEP, "");
                }
                if (pixelsFromZero < 0) {
                    oriRowNumber -= 1;
                }
                float cellTop = oriRowNumber * mRowHeight + constHeight + mCurrentOrigin.y;
                cell.setRectF(new RectF(start, cellTop, startPixel + mColumnWidth, cellTop + mRowHeight));
                return cell;
            }
            startPixel += mColumnWidth + mSizeHelper.getColumnGap();
        }
        return null;
    }

    public boolean hasScheduleInCell(Cell cell) {
        return getScheduleInCell(cell) != null;
    }

    public ScheduleRect getScheduleInCell(Cell cell) {
        SparseArray<List<ScheduleRect>> columnSchedules = mScheduleAdapter.get(cell.getColumnKey());
        if (columnSchedules != null) {
            for (int i = 0; i < columnSchedules.size(); i++) {
                List<ScheduleRect> scheduleRects = columnSchedules.valueAt(i);
                if (!scheduleRects.isEmpty()) {
                    for (ScheduleRect scheduleRect : scheduleRects) {
                        if (isTwoRectFCollide(scheduleRect.fullRectF, cell.getRectF())) {
                            return scheduleRect;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean isTwoRectFCollide(RectF r1, RectF r2) {
        if (r1 == null || r2 == null) {
            return false;
        }
        if(r1.left >= r2.right) {
            return false;
        }
        if (r1.right <= r2.left) {
            return false;
        }
        if (r1.top >= r2.bottom) {
            return false;
        }
        if (r1.bottom <= r2.top) {
            return false;
        }
        return true;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        mAreDimensionsInvalid = true;
    }

    /////////////////////////////////////////////////////////////////
    //
    //      Functions related to setting and getting the properties.
    //
    /////////////////////////////////////////////////////////////////

    public float getHeaderColumnWidth() {
        return mHeaderColumnWidth;
    }

    public float getColumnWidth() {
        return mColumnWidth;
    }

    public int getRowHeight() {
        return mRowHeight;
    }

    public void setRowHeight(int height) {
        this.mRowHeight = height;
    }

    public ScheduleAdapter getScheduleAdapter() {
        return mScheduleAdapter;
    }

    public boolean isDrawTimeline() {
        return mIsDrawTimeline;
    }

    public void setDrawTimeline(boolean mIsDrawTimeline) {
        this.mIsDrawTimeline = mIsDrawTimeline;
    }

    public void setScheduleAdapter(ScheduleAdapter mScheduleAdapter) {
        this.mScheduleAdapter = mScheduleAdapter;
    }

    public void setHeaderClickListener(HeaderClickListener mHeaderClickListener) {
        this.mHeaderClickListener = mHeaderClickListener;
    }

    public void setScheduleClickListener(ScheduleClickListener listener) {
        this.mScheduleClickListener = listener;
    }

    public void setScheduleLongPressListener(ScheduleLongPressListener scheduleLongPressListener) {
        this.mScheduleLongPressListener = scheduleLongPressListener;
    }

    public void setEmptyViewClickListener(EmptyViewClickListener emptyViewClickListener){
        this.mEmptyViewClickListener = emptyViewClickListener;
    }

    public void setEmptyViewLongPressListener(EmptyViewLongPressListener emptyViewLongPressListener){
        this.mEmptyViewLongPressListener = emptyViewLongPressListener;
    }

    public void setWeekChangeListener(WeekChangeListener mWeekChangeListener) {
        this.mWeekChangeListener = mWeekChangeListener;
    }

    public String interpretDate(Calendar date) {
        return CalendarHelper.getFriendlyDate(date);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mCurMode == ScheduleCalendar.MODE_SCROLL_WEEK && mCurrentScrollDirection == Direction.VERTICAL) {
                float availableScreenHeight = getContentHeight();
                int topScreens = Math.round(mCurrentOrigin.y / availableScreenHeight);
                int distance = (int) (mCurrentOrigin.y - topScreens * availableScreenHeight);
                mStickyScroller.startScroll(0, (int) mCurrentOrigin.y, 0, -distance);
                ViewCompat.postInvalidateOnAnimation(this);
            }
            mCurrentScrollDirection = Direction.NONE;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    /**
     * fling或startScroll的时候会调用
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mCurMode == ScheduleCalendar.MODE_SCROLL_WEEK) {
            if (mScroller.computeScrollOffset()) {
                if (mCurrentFlingDirection == Direction.HORIZONTAL) {
                    mCurrentOrigin.x = mScroller.getCurrX();
                } else {
                    float availableScreenHeight = getContentHeight();
                    if (Math.abs(mScroller.getFinalY() - mScroller.getCurrY()) < availableScreenHeight &&
                            Math.abs(mScroller.getFinalY() - mScroller.getStartY()) != 0) {
                        mScroller.forceFinished(true);

                        int screens;
                        if (mScroller.getFinalY() > mScroller.getCurrY()) {
                            screens = (int)Math.ceil(mCurrentOrigin.y / availableScreenHeight);
                        } else {
                            screens = (int)Math.floor(mCurrentOrigin.y / availableScreenHeight);
                        }
                        int distance = (int) (mCurrentOrigin.y - screens * availableScreenHeight);
                        mStickyScroller.startScroll(0, (int) mCurrentOrigin.y, 0, -distance);
                    } else {
                        mCurrentOrigin.y = mScroller.getCurrY();
                    }
                }
                ViewCompat.postInvalidateOnAnimation(this);
            }
            if (mStickyScroller.computeScrollOffset()) {
                mCurrentOrigin.y = mStickyScroller.getCurrY();
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else if (mCurMode == ScheduleCalendar.MODE_FIXED_DAY) {
            if (mScroller.computeScrollOffset()) {
                if (mCurrentFlingDirection == Direction.VERTICAL) {
                    mCurrentOrigin.y = mScroller.getCurrY();
                } else {
                    mCurrentOrigin.x = mScroller.getCurrX();
                }
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    private float contentHeight = 0;
    public float getContentHeight() {
        if (contentHeight <= 0) {
            contentHeight = getHeight() - getConstTopHeight() - mSizeHelper.getRowDividerHeight() * 0.5f;
        }
        return contentHeight;
    }

    private float constTopHeight = 0;
    public float getConstTopHeight() {
        if (constTopHeight <= 0) {
            constTopHeight = getHeaderHeight() + mSizeHelper.getHeaderMarginBottom() + mSizeHelper.getHeaderTextHeight() / 2;
        }
        return constTopHeight;
    }

    private float headerFullHeight = 0;
    public float getHeaderHeight() {
        if (headerFullHeight <= 0) {
            headerFullHeight = mSizeHelper.getHeaderTextHeight() + mSizeHelper.getHeaderPadding() * 2;
        }
        return headerFullHeight;
    }

    private float columnFullWidth = 0;
    public float getColumnFullWidth() {
        if (columnFullWidth <= 0) {
            columnFullWidth = mColumnWidth + mSizeHelper.getColumnGap();
        }
        return columnFullWidth;
    }

    /**
     * 转换日历展示模式
     */
    public void changeMode(int newMode) {
        if (mCurMode == newMode ||
                (newMode != ScheduleCalendar.MODE_FIXED_DAY &&
                        newMode != ScheduleCalendar.MODE_SCROLL_WEEK)) {
            return;
        }

        mCurMode = newMode;
        if (mScheduleAdapter != null) {
            mScheduleAdapter.clear();
        }

        mCurrentOrigin.x = 0f;
        mCurrentOrigin.y = 0f;
        mNumberOfVisibleColumns = DEFAULT_COLUMN_NUM;
        mAreDimensionsInvalid = true;
    }

    public int getMode() {
        return mCurMode;
    }

    public void notifyDataSetChanged(){
        if (mScheduleAdapter != null) {
            if (mScheduleAdapter.size() < mNumberOfVisibleColumns) {
                mNumberOfVisibleColumns = mScheduleAdapter.size();
            }
        }

        if (mCurMode == ScheduleCalendar.MODE_SCROLL_WEEK) {
            if (mScheduleAdapter != null) {
                mRowCount = DEFAULT_COLUMN_NUM; //一周7天
                float availableHeight = getContentHeight();
                mRowHeight = Math.round(availableHeight / mRowCount); // 一屏显示7天
            }
            if (isLoadingMoreSchedules) {
                isLoadingMoreSchedules = false;
            }
        } else if (mCurMode == ScheduleCalendar.MODE_FIXED_DAY) {
            mRowCount = 1440 / Product.DURATION_STEP;
            mRowHeight = LocalUtil.dip2px(56);
        }

        invalidate();
    }

    public void goToDayOfWeekview(Calendar targetDay) {
        float contentHeight = getContentHeight();
        Calendar now = Calendar.getInstance();
        int tYear = targetDay.get(Calendar.YEAR);
        int tMonth = targetDay.get(Calendar.MONTH);
        int tDay = targetDay.get(Calendar.DAY_OF_MONTH);

        targetDay.setTime(now.getTime());
        targetDay.set(Calendar.YEAR, tYear);
        targetDay.set(Calendar.MONTH, tMonth);
        targetDay.set(Calendar.DAY_OF_MONTH, tDay);

        long nowTime = now.getTimeInMillis();
        long targetTime = targetDay.getTimeInMillis();
        int disDays = (int)((targetTime - nowTime) / 1000 / 3600 / 24);
        int disScreens = disDays / 7;
        int verticalOffset = (int)(disScreens * contentHeight);
        setPosition(-verticalOffset);
    }

    public void goToOpentime() {
        goToMinutes(firstTime);
    }

    public void goToMinutes(int minutes){
        if (minutes < firstTime) {
            minutes = firstTime;
        }
        if (minutes > lastTime) {
            minutes = lastTime;
        }

        int steps = minutes / Product.DURATION_STEP;
        int verticalOffset = mRowHeight * steps;
        if (steps < 0) {
            verticalOffset = 0;
        } else if (steps > mRowCount) {
            verticalOffset = mRowHeight * mRowCount;
        }

        if (mAreDimensionsInvalid) {
            mVerticalScrollTo = minutes;
            return;
        } else if (verticalOffset > mRowHeight * mRowCount - getHeight() + mSizeHelper.getHeaderTextHeight() + mSizeHelper.getHeaderPadding() * 2 + mSizeHelper.getHeaderMarginBottom()) {
            verticalOffset = (int) (mRowHeight * mRowCount - getHeight() + mSizeHelper.getHeaderTextHeight() + mSizeHelper.getHeaderPadding() * 2 + mSizeHelper.getHeaderMarginBottom());
        }
        setPosition(-verticalOffset);
    }

    public float getPosition() {
        return mCurrentOrigin.y;
    }

    public void setPosition(float position) {
        mCurrentOrigin.y = position;
        invalidate();
    }

    public void setBusinessTime(int startMinutes, int endMinutes) {
        firstTime = startMinutes;
        lastTime = endMinutes;
        mVerticalScrollTo = firstTime;
        invalidate();
    }

    public interface HeaderClickListener {
        void onHeaderClick(int columnKey, String columnName);
    }

    public interface ScheduleClickListener {
        void onScheduleClick(UserSchedule schedule, RectF eventRect);
    }

    public interface ScheduleLongPressListener {
        void onScheduleLongPress(ScheduleRect scheduleRect);
    }

    public interface EmptyViewClickListener {
        void onEmptyViewClicked(Cell cell);
    }

    public interface EmptyViewLongPressListener {
        void onEmptyViewLongPress(Cell cell);
    }

    public interface WeekChangeListener {
        void onWeekChanged(Calendar startDay);
    }

}
