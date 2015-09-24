package com.meiyebang.meiyebang.pad.view.calendar;

import com.meiyebang.meiyebang.pad.model.UserSchedule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuzhen on 15/6/3.
 */
public class Column {

    private int id;
    private int sort;
    private String name;

    private List<UserSchedule> eventList;

    public Column(int id, int sort, String name) {
        this.id = id;
        this.sort = sort;
        this.name = name;
        this.eventList = new ArrayList<UserSchedule>();
    }

    public Column(int id, int sort, String name, List<UserSchedule> eventList) {
        this.id = id;
        this.sort = sort;
        this.name = name;
        this.eventList = eventList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserSchedule> getEventList() {
        return eventList;
    }

    public void setEventList(List<UserSchedule> eventList) {
        this.eventList = eventList;
    }
}
