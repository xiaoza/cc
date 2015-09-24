package com.meiyebang.meiyebang.pad.view.calendar;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meiyebang.meiyebang.pad.model.Product;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yuzhen on 15/6/8.
 */
public class ScheduleCalendar extends RelativeLayout {

    public static final int MODE_FIXED_DAY = 1;  //fix模式是数据是固定的
//    public static final int MODE_SCROLL_DAY = 2;  //slide模式是可以无限滚动，动态加载的
//    public static final int MODE_FIXED_WEEK = 3; //按周展示的fixed
    public static final int MODE_SCROLL_WEEK = 4; // 无限滚动的周预约

    private ViewDragHelper mViewDragHelper;
    private DragRect mDragView;
    private ScheduleView mScheduleView;

    private float mInitialMotionX;
    private float mInitialMotionY;

    private boolean isDragging = false;

    private OnScheduleRePlacedListener scheduleRePlacedListener;

    public ScheduleCalendar(Context context) {
        super(context);
        init(context);
    }

    public ScheduleCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScheduleCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ScheduleDragCallback());
    }

    public void initDrag(ScheduleRect scheduleRect) {
        if (mDragView != null) {
            mDragView.setScheduleRect(scheduleRect);
            mDragView.requestFocus();
            isDragging = true;
        }
    }

    public DragRect getDragView() {
        return mDragView;
    }

    public ScheduleView getScheduleView() {
        return mScheduleView;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mScheduleView = new ScheduleView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        // 添加拖拽的view
        mDragView = new DragRect(getContext());

        addView(mScheduleView, params);
        addView(mDragView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if(action != MotionEvent.ACTION_DOWN) {
            mViewDragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        }
//        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
//            mViewDragHelper.cancel();
//            return false;
//        }

        final float x = ev.getX();
        final float y = ev.getY();
        boolean interceptTap = false;

        switch(action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mViewDragHelper.cancel();
                return false;
            case MotionEvent.ACTION_DOWN:
                mInitialMotionX = x;
                mInitialMotionY = y;
                interceptTap = mViewDragHelper.isViewUnder(mDragView, (int) x, (int) y);
                if (isDragging && !interceptTap) {
                    // 点击了空白区域，取消拖拽
                    cancelDrag();
                }
                break;
        }
        return isDragging && interceptTap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);

        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        boolean isDragViewUnder = mViewDragHelper.isViewUnder(mDragView, (int)x, (int)y);

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionX = x;
                mInitialMotionY = y;
                break;
            case MotionEvent.ACTION_UP:
                final float dx = x - mInitialMotionX;
                final float dy = y - mInitialMotionY;
                final float slop = mViewDragHelper.getTouchSlop();

                if(dx * dx + dy * dy < slop * slop && isDragViewUnder) {
                    int centerX = mDragView.getLeft() + mDragView.getWidth() / 2;
                    int centerY = mDragView.getTop() + mScheduleView.getRowHeight() / 2;

                    Cell cell = mScheduleView.getTapCell(centerX, centerY);
                    if (cell != null) {
                        scrollDragViewTo(cell.getRect().left, cell.getRect().top);
                    }
                }
                break;
        }
        return isDragViewUnder && isViewHit(mDragView, (int)x, (int)y);
    }

    public void cancelDrag() {
        isDragging = false;
        mDragView.cancel();
        mViewDragHelper.cancel();
        mScheduleView.notifyDataSetChanged();
    }

    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] &&
                screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] &&
                screenY < viewLocation[1] + view.getHeight();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private class ScheduleDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) { //返回true则表示启动拖拽
            return child.getId() == mDragView.getId();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int contentLeft = getPaddingLeft() + (int)mScheduleView.getHeaderColumnWidth();
            if (left < contentLeft){
                return contentLeft;
            }

            if (left > getWidth() - child.getMeasuredWidth()){
                return getWidth() - child.getMeasuredWidth();
            }
            return  left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int contentTop = getPaddingTop() + (int)mScheduleView.getHeaderHeight();
            if (top < contentTop){
                return contentTop;
            }

            if (top > getHeight() - child.getMeasuredHeight()) {
                return getHeight() - child.getMeasuredHeight();
            }
            return top;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int centerX = releasedChild.getLeft() + releasedChild.getWidth() / 2;
            int centerY = releasedChild.getTop() + mScheduleView.getRowHeight() / 2;

            Cell cell = mScheduleView.getTapCell(centerX, centerY);
            if (cell != null) {
                mViewDragHelper.settleCapturedViewAt(cell.getRect().left, cell.getRect().top);
                invalidate();
                notifyScheduleRePlaced(mDragView.getScheduleRect(), cell);
            }
        }
    }

    private void notifyScheduleRePlaced(ScheduleRect scheduleRect, Cell newCell) {
        long start = scheduleRect.event.getStartTime().getTimeInMillis();
        long end = scheduleRect.event.getEndTime().getTimeInMillis();
        int steps = (int)((end - start) / 1000 / Product.DURATION_STEP / 60);
        int height = mScheduleView.getRowHeight() * steps;
        newCell.getRectF().bottom += height;
        if (scheduleRePlacedListener != null) {
            scheduleRePlacedListener.onScheduleRePlaced(scheduleRect, newCell);
        }
    }

    public boolean scrollDragViewTo(int finalLeft, int finalTop) {
        if(mViewDragHelper.smoothSlideViewTo(mDragView, finalLeft, finalTop)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    public interface OnScheduleRePlacedListener {
        void onScheduleRePlaced(ScheduleRect scheduleRect, Cell tobePlaced);
    }

    public OnScheduleRePlacedListener getScheduleRePlacedListener() {
        return scheduleRePlacedListener;
    }

    public void setScheduleRePlacedListener(OnScheduleRePlacedListener scheduleRePlacedListener) {
        this.scheduleRePlacedListener = scheduleRePlacedListener;
    }

    /** operate schedule view */
    public void setDrawTimeline(boolean isDraw) {
        if (mScheduleView != null) {
            mScheduleView.setDrawTimeline(isDraw);
        }
    }

    public void notifyDataSetChanged() {
        mScheduleView.notifyDataSetChanged();
    }

    public void changeMode(int newMode) {
        mScheduleView.changeMode(newMode);
    }

    public int getMode() {
        return mScheduleView.getMode();
    }

    public void goToOpentime() {
        mScheduleView.goToOpentime();
    }

    public void goToDayOfWeekView(Calendar target) {
        mScheduleView.goToDayOfWeekview(target);
    }

    public boolean hasScheduleInCell(Cell cell) {
        return mScheduleView.hasScheduleInCell(cell);
    }

    public ScheduleRect getScheduleInCell(Cell cell) {
        return mScheduleView.getScheduleInCell(cell);
    }

    public void setBusinessTime(Date start, Date end) {
        if (mScheduleView != null && start != null && end != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            int startMinutes = CalendarHelper.getTimeInOneDay(calendar);
            calendar.setTime(end);
            int endMinutes = CalendarHelper.getTimeInOneDay(calendar);
            mScheduleView.setBusinessTime(startMinutes, endMinutes);
        }
    }

    public float getPosition() {
        if (mScheduleView != null) {
            return mScheduleView.getPosition();
        }
        return 0;
    }

    public void setPosition(float position) {
        if (mScheduleView != null) {
            mScheduleView.setPosition(position);
        }
    }

    public void setScheduleAdapter(ScheduleAdapter adapter) {
        if (mScheduleView != null) {
            mScheduleView.setScheduleAdapter(adapter);
        }
    }

    public void setHeaderClickListener(ScheduleView.HeaderClickListener mHeaderClickListener) {
        if (mScheduleView != null) {
            mScheduleView.setHeaderClickListener(mHeaderClickListener);
        }
    }

    public void setEventClickListener(ScheduleView.ScheduleClickListener mScheduleClickListener) {
        if (mScheduleView != null) {
            mScheduleView.setScheduleClickListener(mScheduleClickListener);
        }
    }

    public void setEventLongPressListener(ScheduleView.ScheduleLongPressListener mScheduleLongPressListener) {
        if (mScheduleView != null) {
            mScheduleView.setScheduleLongPressListener(mScheduleLongPressListener);
        }
    }

    public void setEmptyViewClickListener(ScheduleView.EmptyViewClickListener mEmptyViewClickListener) {
        if (mScheduleView != null) {
            mScheduleView.setEmptyViewClickListener(mEmptyViewClickListener);
        }
    }

    public void setEmptyViewLongPressListener(ScheduleView.EmptyViewLongPressListener mEmptyViewLongPressListener) {
        if (mScheduleView != null) {
            mScheduleView.setEmptyViewLongPressListener(mEmptyViewLongPressListener);
        }
    }

    public void setWeekChangeListener(ScheduleView.WeekChangeListener mWeekChangeListener) {
        if (mScheduleView != null) {
            mScheduleView.setWeekChangeListener(mWeekChangeListener);
        }
    }
}
