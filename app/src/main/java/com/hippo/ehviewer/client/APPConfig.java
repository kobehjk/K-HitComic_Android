package com.hippo.ehviewer.client;

import com.hippo.ehviewer.client.data.Token;
import com.hippo.ehviewer.client.data.UserInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sadas on 2017/12/17.
 */

public class APPConfig {
    public static boolean isValible;
    public static int globalFreeTime;
    public static String deviceId;
    public static Date startDate;
    public static Date endDate;
    public static String localToken;
    public static boolean isExpire = true;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //BMOB
    public static String bmobApplicationId = "c1c77286821661cdc9673e73ec97a6eb";

    public static UserInfo currentUser = new UserInfo();
    public static Token currentToken = new Token();
}
