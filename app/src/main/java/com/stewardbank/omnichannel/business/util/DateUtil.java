package com.stewardbank.omnichannel.business.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @uthor Tasu Muzinda
 */
public class DateUtil {

    public static final Integer INITIAL_YEAR = 2014;
    public static final Integer CURRENT_YEAR = getCurrentYear();
    public static final Integer CURRENT_MONTH = getCurrentMonth();
    public static final Integer CURRENT_DATE = getCurrentDate();
    public static final List<Integer> YEAR_RANGE = getInitialDateRange(INITIAL_YEAR);
    public static final SimpleDateFormat restFmt = new SimpleDateFormat("yyyy/MM/dd");
    private static final Integer startDate = getMonthStartDate();
    private static final Integer startMonth = 0;
    private static final Integer endDate = getMonthEndDate();
    private static final Integer endMonth = 11;
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

    private DateUtil() {
        throw new IllegalStateException("Class not intended to be instantiated");
    }

    public static String formatDate(Date date) {
        return fmt.format(date);
    }

    public static String formatDateRest(Date date) {
        return restFmt.format(date);
    }

    public static Date getDateFromString(String date) {
        try {
            Date d = restFmt.parse(date);
            String newDate = formatDate(d);
            return fmt.parse(newDate);
        } catch (ParseException ex) {
            System.out.println("Error occurred");
        }
        throw new IllegalArgumentException("Un expected parameter provided :" + date);
    }
    public static Date getFromString(String date) {
        try {
            return fmt.parse(date);
        } catch (ParseException ex) {
            System.out.println("Error occurred");
        }
        throw new IllegalArgumentException("Un expected parameter provided :" + date);
    }

    public static Date getDateDiffDate(int factor) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, factor);
        return cal.getTime();
    }

    private static Integer getMonthEndDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Integer getMonthStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    }

    private static Integer getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.YEAR);
    }

    private static Integer getCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.MONTH);
    }

    private static Integer getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.DATE);
    }

    public static List<Integer> getInitialDateRange(Integer initialYear) {
        List<Integer> items = new ArrayList<>();
        for (int x = initialYear; x <= CURRENT_YEAR + 1; x++) {
            if (x <= CURRENT_YEAR) {
                items.add(x);
            } else {
                /**
                 * TODO make comparizon month editable
                 *
                 * @param comparizon month
                 */
                if (CURRENT_MONTH >= 8) {
                    items.add(x);
                }

            }
        }
        return items;
    }

    public static Date getPeriodStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, getMonthStartDate(date));
        return cal.getTime();
    }

    public static Date getPeriodEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, getMonthEndDate(date));
        return cal.getTime();
    }

    public static Integer getMonthStartDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    }

    private static Integer getMonthEndDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Date getDate(Integer year, Boolean start) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, start ? startMonth : endMonth, start ? startDate : endDate);
        return cal.getTime();
    }

    public static Integer getYearFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static Integer getMonthFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static String getFriendlyFileName(String name) {
        return name + "_" + getCurrentDate() + "-" + getCurrentMonth() + "-" + getCurrentYear();
    }

    public static String getPrintName(String name) {
        final String searchExp = "_";
        StringBuilder builder = new StringBuilder();
        String[] args = name.split(searchExp);
        int pos = 0;
        for (String s : args) {
            if (pos < args.length - 1) {
                builder.append(s);
                builder.append(" ");
                pos++;
            }
        }
        return builder.toString();
    }

    public static String getYearMonthName(Date date) {
        return getMonthNameFromDate(date) + " " + getYearFromDate(date);
    }

    private static String getMonthNameFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }

    public static String getFooterText(String name) {
        final String searchExp = "_";
        String[] args = name.split(searchExp);
        return args[args.length - 1];
    }

    public static Integer[] hex2Rgb(String colorStr) {
        return new Integer[]{Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16)};
    }

    public static String getStringFromDate(Date date) {
        return restFmt.format(date);
    }

    public static Date getDateFromAge(Integer age) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, -age);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 11);
        cal.set(Calendar.DAY_OF_MONTH, getMonthEndDate(cal.getTime()) - 1);
        return cal.getTime();
    }

    public static Date getEndDate(Integer age) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, -age);
        return cal.getTime();
    }

    public static Date getQuarter(Date date, int fact, Boolean start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (fact) {
            case 1:
                if (start) {
                    cal.set(cal.get(Calendar.YEAR), Calendar.JANUARY, 01);
                } else {
                    cal.set(cal.get(Calendar.YEAR), Calendar.MARCH, 31);
                }
            case 2:
                if (start) {
                    cal.set(cal.get(Calendar.YEAR), Calendar.APRIL, 01);
                } else {
                    cal.set(cal.get(Calendar.YEAR), Calendar.JUNE, 30);
                }
            case 3:
                if (start) {
                    cal.set(cal.get(Calendar.YEAR), Calendar.JULY, 01);
                } else {
                    cal.set(cal.get(Calendar.YEAR), Calendar.SEPTEMBER, 30);
                }
            case 4:
                if (start) {
                    cal.set(cal.get(Calendar.YEAR), Calendar.OCTOBER, 01);
                } else {
                    cal.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 31);
                }
        }
        return cal.getTime();
    }

    public static Date getHalfYear(Date date, int fact, Boolean start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (fact) {
            case 1:
                if (start) {
                    cal.set(cal.get(Calendar.YEAR), Calendar.JANUARY, 01);
                } else {
                    cal.set(cal.get(Calendar.YEAR), Calendar.JUNE, 30);
                }
            case 2:
                if (start) {
                    cal.set(cal.get(Calendar.YEAR), Calendar.JULY, 01);
                } else {
                    cal.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 31);
                }
        }
        return cal.getTime();
    }

    public static Date getYearPeriod(Date date, Boolean start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (start) {
            cal.set(cal.get(Calendar.YEAR), Calendar.JANUARY, 01);
        } else {
            cal.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 31);
        }
        return cal.getTime();
    }

    public static Date getDateDiffMonth(Date date, int factor) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, factor);
        return cal.getTime();
    }

}
