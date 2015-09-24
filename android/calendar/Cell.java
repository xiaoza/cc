package com.meiyebang.meiyebang.pad.view.calendar;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by yuzhen on 15/6/10.
 */
public class Cell {
    private int columnKey;
    private String columnName;
    private int rowKey;
    private String rowName;
    private RectF rectF;

    public Cell(int columnKey, String columnName, int rowKey, String rowName) {
        this.columnKey = columnKey;
        this.columnName = columnName;
        this.rowKey = rowKey;
        this.rowName = rowName;
    }

    public int getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(int columnKey) {
        this.columnKey = columnKey;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getRowKey() {
        return rowKey;
    }

    public void setRowKey(int rowKey) {
        this.rowKey = rowKey;
    }

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public RectF getRectF() {
        return rectF;
    }

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
    }

    public Rect getRect() {
        if (rectF != null) {
            return new Rect((int)rectF.left, (int)rectF.top, (int)rectF.right, (int)rectF.bottom);
        }
        return null;
    }
}
