package com.hippo.ehviewer.client.data;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hejinkun on 2017/12/19.
 */

public class DateTools {

    public static Date getEndDateBy(Date startDate,String type){
        if (type.equals("m")){
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, +1);
            return cal.getTime();
        }else {
            return null;
        }
    }

    public static String dateToString(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }
}
