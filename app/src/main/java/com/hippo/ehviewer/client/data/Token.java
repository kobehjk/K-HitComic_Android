package com.hippo.ehviewer.client.data;

import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by sadasd on 2017/12/19.
 */

public class Token extends BmobObject {
    private String token;
    private String start_time;
    private String available_period;
    private String device_id;

    public String getToken() {
        return token;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getAvailable_period() {
        return available_period;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setAvailable_period(String available_period) {
        this.available_period = available_period;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
