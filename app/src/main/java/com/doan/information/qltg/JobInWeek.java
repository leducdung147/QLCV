package com.doan.information.qltg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class được định nghĩa công việc phải hoành thành
 * trong một tuần
 */
public class JobInWeek {
    private String idKey;
    private String title;
    private String description;
    private Date dateFinish;
    private Date hourFinish;

    public Date getDateFinish() {
        return dateFinish;
    }

    public Date getHourFinish() {
        return hourFinish;
    }

    public String getIdKey() {
        return idKey;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDesciption(String desciption) {
        this.description = desciption;
    }

    public void setDateFinish(Date dateFinish) {
        this.dateFinish = dateFinish;
    }

    public void setHourFinish(Date hourFinish) {
        this.hourFinish = hourFinish;
    }

    public JobInWeek(String title, String description, Date dateFinish,
                     Date hourFinish, String idKey) {
        super();
        this.idKey = idKey;
        this.title = title;
        this.description = description;
        this.dateFinish = dateFinish;
        this.hourFinish = hourFinish;
    }

    public JobInWeek() {
        super();
    }

    /**
     * lấy định dạng ngày
     */
    public String getDateFormat(Date d) {
        SimpleDateFormat dft = new
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dft.format(d);
    }

    /**
     * lấy định dạng giờ phút
     */
    public String getHourFormat(Date d) {
        SimpleDateFormat dft = new
                SimpleDateFormat("hh:mm a", Locale.getDefault());
        return dft.format(d);
    }

    @Override
    public String toString() {
        return this.title + " --Bắt đầu: " +
                getDateFormat(this.dateFinish) + " --Vào lúc: " +
                getHourFormat(this.hourFinish);
    }
}