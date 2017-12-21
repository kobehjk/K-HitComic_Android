package com.hippo.ehviewer.client.data;

import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by hejinkun on 2017/12/19.
 */

public class UserInfo extends BmobObject {
    private String token;
//    private BmobDate start_time;
    private int free_times;
    private String device_id;
//    private String available_period;
//    private Date end_time;

    public void setToken(String token) {
        this.token = token;
    }

//    public void setStart_time(BmobDate start_time) {
//        this.start_time = start_time;
//    }

    public void setFree_times(int free_times) {
        this.free_times = free_times;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

//    public void setAvailable_period(String available_period) {
////        this.available_period = available_period;
//    }

    public String getToken() {
        return token;
    }

//    public BmobDate getStart_time() {
//        return start_time;
//    }

    public int getFree_times() {
        return free_times;
    }

    public String getDevice_id() {
        return device_id;
    }

//    public String getAvailable_period() {
//        return available_period;
//    }
}
