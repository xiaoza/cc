package com.meiyebang.meiyebang.pad.view.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yuzhen on 15/6/2.
 */
public class CalendarHelper {
    private static SimpleDateFormat DATE_INT_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    public static boolean isSameDay(Calendar dayOne, Calendar dayTwo) {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR);
    }

    public static String getFriendlyDate(Calendar calendar) {
//        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        try{
//            String dayName = sdf.format(calendar.getTime()).toUpperCase();
//            return String.format("%s %d/%02d", dayName, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            return String.format("%d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static int getIntDate(Calendar calendar) {
        if (calendar == null) {
            return -1;
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year * 10000 + month * 100 + day;
//        try {
//            String str = DATE_INT_FORMAT.format(calendar.getTime());
//            if (str != null) {
//                return Integer.parseInt(str);
//            }
//            return -1;
//        } catch (Exception e) {
//            return -1;
//        }
    }

    public static Calendar getCalendar(int yearMonthDay) {
        try {
            Date date = DATE_INT_FORMAT.parse(yearMonthDay + "");
            if (date != null) {
                Calendar result = Calendar.getInstance();
                result.setTime(date);
                return result;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getNormalDateStr(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return format.format(calendar.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    public static String getNormalTimeStr(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            return format.format(calendar.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    public static Calendar getMondayOfThisWeek() {
        Calendar today = Calendar.getInstance();
        int todayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        if (todayOfWeek == Calendar.MONDAY) {
            return today;
        }

        if (todayOfWeek == Calendar.SUNDAY) {
            today.add(Calendar.DATE, -6);
            return today;
        }

        today.add(Calendar.DATE, Calendar.MONDAY - todayOfWeek);
        return today;
    }

    // 比较两个日期的大小，忽略时分秒
    public static int compareDate(Calendar c1, Calendar c2) {
        int c1year = c1.get(Calendar.YEAR);
        int c1month = c1.get(Calendar.MONTH);
        int c1day = c1.get(Calendar.DAY_OF_MONTH);

        int c2year = c2.get(Calendar.YEAR);
        int c2month = c2.get(Calendar.MONTH);
        int c2day = c2.get(Calendar.DAY_OF_MONTH);

        if (c1year != c2year) {
            return c1year - c2year;
        } else {
            if (c1month != c2month) {
                return c1month - c2month;
            } else {
                if (c1day != c2day) {
                    return c1day - c2day;
                }
            }
        }
        return 0;
    }

    /**
     * 返回从零点开始的分钟数
     * @return
     */
    public static int getTimeInOneDay(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        return hour * 60 + min;
    }

    public static String getTimeStrByPassedMinutes(int minutes) {
        int hour = (int)Math.floor(minutes / 60);
        String res = "";
        if (hour <= 0) {
            res = "00:";
        } else if (hour < 10) {
            res = "0" + hour + ":";
        } else {
            res = hour + ":";
        }
        int last = minutes - hour * 60;
        if (last <= 0) {
            res += "00";
        } else if (last < 10) {
            res += "0" + last;
        } else {
            res += last;
        }
        return res;
    }

    public static String getTimeByMinutes(int minutes) {
        int hour = minutes / 60;
//        if (hour < 10) {
//            durationText = "0" + hour + ":";
//        } else {
//            durationText = hour + ":";
//        }
        int minute = minutes % 60;
//        if (minute < 10) {
//            durationText += "0" + minute;
//        } else {
//            durationText += minute;
//        }
        if (hour == 0 && minute == 0) {
            return "00:00";
        }
        if (minute != 0) {
            return "";
        }
        if (hour < 10) {
            return "0" + hour + ":00";
        } else {
            return hour + ":00";
        }
    }

    public static String getFriendlyDurationText(int minutes) {
        if (minutes < 60) {
            return minutes + "分钟";
        }

        String durationText = minutes / 60 + "小时 ";
        int last = minutes % 60;
        if (last != 0) {
            durationText += last + "分钟";
        }
        return durationText;
    }
}
