package com.sagarnileshshah.nytsearchapp.models;

import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by sshah on 2/12/16.
 */

@Parcel
public class Filter {

    private String startDate;
    private ArrayList<String> newsDesk;
    private String sortBy;

    public Filter() {
        startDate = "";
        newsDesk = new ArrayList<>();
        sortBy = "";
    }

    public String getStartDate() {
        return startDate;
    }

    public String getFormattedStartDate() throws ParseException {
        if (!startDate.equals("")) {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat targetFormat = new SimpleDateFormat("MMM dd, yyyy");
            Date date = originalFormat.parse(startDate);
            return targetFormat.format(date);
        } else {
            return startDate;
        }
    }

    public int[] getStartDateArray() throws ParseException {
        if (!startDate.equals("")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            Date date = format.parse(startDate);
            gregorianCalendar.setTime(date);
            int year = gregorianCalendar.get(Calendar.YEAR);
            int month = gregorianCalendar.get(Calendar.MONTH);
            int day = gregorianCalendar.get(Calendar.DAY_OF_MONTH);
            int[] array = {year, month, day};
            return array;
        } else {
            Calendar currDate = Calendar.getInstance();
            int[] array = {currDate.get(Calendar.YEAR), currDate.get(Calendar.MONTH), currDate.get(Calendar.DAY_OF_MONTH)};
            return array;
        }
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(int year, int month, int day) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, day);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        this.startDate = format.format(gregorianCalendar.getTime());
    }

    public ArrayList<String> getNewsDesk() {
        return newsDesk;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

}
