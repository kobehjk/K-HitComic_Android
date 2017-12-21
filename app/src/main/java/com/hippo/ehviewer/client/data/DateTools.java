package com.hippo.ehviewer.client.data;

import android.text.format.DateUtils;
import android.util.Log;

import com.hippo.yorozuya.StringUtils;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.R.attr.format;

/**
 * Created by hejinkun on 2017/12/19.
 */

public class DateTools {

    public static boolean ifExpire(Date startDate,String type){
        Date endDate = getEndDateBy(startDate,type);
        Date nowDate = getWebsiteDatetime();
        if (endDate != null && nowDate != null){
            return (nowDate.getTime() > endDate.getTime());
        }else {
            return false;
        }

    }

    public static Date getEndDateBy(Date startDate,String type){
        if (type.equals("m")){
            Calendar cal = Calendar.getInstance();
            cal.setTime(getWebsiteDatetime());
            cal.add(Calendar.MONTH, +1);
//            Log.d("kobehjk", "getEndDateBy: "+ date);
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


        public static Date getWebsiteDatetime() {
            try {

                    URL url = new URL("http://www.baidu.com");// 获取url对象
                    URLConnection uc = url.openConnection();// 获取生成连接对象
                    uc.connect();// 发出连接请求
                    long ld = uc.getDate();// 读取网站日期时间
                    Date date = new Date(ld);// 转化为时间对象

                    return date;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }



}
