package com.hippo.ehviewer.client;

import com.hippo.ehviewer.client.data.Token;
import com.hippo.ehviewer.client.data.UserInfo;

/**
 * Created by hejinkun on 2017/12/17.
 */

public class APPConfig {
    public static boolean isValible;
    public static int globalFreeTime;
    public static String deviceId;

    //BMOB
    public static String bmobApplicationId = "c1c77286821661cdc9673e73ec97a6eb";

    public static UserInfo currentUser = new UserInfo();
    public static Token currentToken = new Token();
}
