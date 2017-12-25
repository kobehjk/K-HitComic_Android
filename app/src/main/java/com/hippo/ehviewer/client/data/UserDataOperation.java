package com.hippo.ehviewer.client.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hippo.ehviewer.EhApplication;
import com.hippo.ehviewer.Settings;
import com.hippo.ehviewer.client.APPConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by hejinkun on 2017/12/19.
 */

public class UserDataOperation {
    private static UserDataOperation userShared;
    public static synchronized UserDataOperation instance(){
        if (userShared == null){
            userShared = new UserDataOperation();
        }
        return userShared;
    }

    public interface CheckInterFace{
        void checkCallBack(Token token);
    }

    public CheckInterFace checkInterFace;
    public void setCheckInterFace(CheckInterFace checkInterFace) {
        this.checkInterFace = checkInterFace;
    }

    public interface CheckDeviceInterFace{
        //是VIP才返回true
        void checkCallBack(boolean isVip);
    }

    public CheckDeviceInterFace checkdeviceInterFace;
    public void setCheckDeciceInterFace(CheckDeviceInterFace checkdeviceInterFace) {
        this.checkdeviceInterFace = checkdeviceInterFace;
    }

    public interface CheckAvailable{
        //是VIP才返回true
        void isAvailable(boolean isavailable);
    }

    public void checkByDeviceId(final String deviceId, final CheckDeviceInterFace callBack){

        checkdeviceInterFace = callBack;
        BmobQuery<UserInfo> query =new BmobQuery<UserInfo>();
        query.addWhereEqualTo("device_id", deviceId);
        query.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> list, BmobException e) {
                if(e==null){
                    UserInfo newUser = new UserInfo();
                    Log.i("bmob","查询成功：");
                    if (list.isEmpty()){
                        APPConfig.globalFreeTime = 3;
                        APPConfig.isValible = true;
                        newUser.setDevice_id(deviceId);
                        newUser.setFree_times(3);
                        addUserInfo(newUser);
                    }else {
                        final UserInfo user = list.get(0);
                        if (user.getToken()==null){
                            APPConfig.globalFreeTime = user.getFree_times();
                            if (APPConfig.globalFreeTime > 0){
                                APPConfig.isValible = true;
                            }else {
                                APPConfig.isValible = false;
                            }
                            APPConfig.deviceId = deviceId;
                            Settings.putString(Settings.DEVICEID,deviceId);
                            callBack.checkCallBack(false);
//                            updateUser(APPConfig.currentUser,callBack);
                        }else {
//                            Settings.putString(Settings.TOKEN,user.getToken());
                            checkToken(user.getToken(), new CheckInterFace() {
                                @Override
                                public void checkCallBack(Token token) {
                                    if (token.getToken() == null || token.getToken().equals("")){
                                        APPConfig.globalFreeTime = user.getFree_times();
                                        if (APPConfig.globalFreeTime > 0){
                                            APPConfig.isValible = true;
                                        }else {
                                            APPConfig.isValible = false;
                                        }
                                        APPConfig.deviceId = deviceId;
                                        Settings.putString(Settings.DEVICEID,deviceId);
                                        callBack.checkCallBack(false);
                                    }else {
                                        APPConfig.isValible = true;
                                        APPConfig.isExpire = false;
                                        APPConfig.localToken = token.getToken();
                                        Settings.putString(Settings.TOKEN,token.getToken());
                                        callBack.checkCallBack(true);
                                    }
//                                    updateUser(APPConfig.currentUser,callBack);
                                }
                            });


                        }

                    }

                }else{
                    APPConfig.globalFreeTime = 0;
                    APPConfig.isValible = false;
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    callBack.checkCallBack(false);
                }
                Settings.putBoolean("isValible",APPConfig.isValible);
                Settings.putInt(APPConfig.deviceId,APPConfig.globalFreeTime);

            }
        });
    }

    public void addUserInfo(UserInfo user){
        user.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Log.i("bmob","创建数据成功：" + s);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
                checkdeviceInterFace.checkCallBack(false);
            }
        });
    }

    public void updateUser(final UserInfo userInfo, final CheckDeviceInterFace callBack){

        BmobQuery<UserInfo> query =new BmobQuery<UserInfo>();
        query.addWhereEqualTo("device_id", userInfo.getDevice_id());
        query.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> list, BmobException e) {
                if (e == null){
                    if (!list.isEmpty()){
                        UserInfo findInfo = list.get(0);
                        findInfo.setDevice_id(userInfo.getDevice_id());
//                        findInfo.setStart_time(userInfo.getStart_time());
                        findInfo.setToken(userInfo.getToken());
                        findInfo.setFree_times(userInfo.getFree_times());
                        findInfo.update(findInfo.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    toast(EhApplication.getContext(),"更新数据成功");
                                    Log.i("bmob","更新数据成功：" );
                                }else{
                                    toast(EhApplication.getContext(),"更新数据失败，请重新激活");
                                    APPConfig.currentToken = new Token();
                                    APPConfig.isValible = false;
                                    APPConfig.globalFreeTime = 0;
                                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                }
                                if (callBack != null){
                                    callBack.checkCallBack(false);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void updateUserFreeTimes(final UserInfo userInfo, final CheckDeviceInterFace callBack){

        BmobQuery<UserInfo> query =new BmobQuery<UserInfo>();
        query.addWhereEqualTo("device_id", userInfo.getDevice_id());
        query.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> list, BmobException e) {
                if (e == null){
                    if (!list.isEmpty()){
                        UserInfo findInfo = list.get(0);
                        findInfo.setFree_times(userInfo.getFree_times());
                        findInfo.update(findInfo.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    toast(EhApplication.getContext(),"更新数据成功");
                                    Log.i("bmob","更新数据成功：" );
                                }else{
                                    toast(EhApplication.getContext(),"更新数据失败，请重新激活");
                                    APPConfig.currentToken = new Token();
                                    APPConfig.isValible = false;
                                    APPConfig.globalFreeTime = 0;
                                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                }
                                if (callBack != null){
                                    callBack.checkCallBack(false);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void updateUserToken(final UserInfo userInfo, final CheckDeviceInterFace callBack){

        BmobQuery<UserInfo> query =new BmobQuery<UserInfo>();
        query.addWhereEqualTo("device_id", userInfo.getDevice_id());
        query.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> list, BmobException e) {
                if (e == null){
                    if (!list.isEmpty()){
                        UserInfo findInfo = list.get(0);
                        findInfo.setToken(userInfo.getToken());
                        findInfo.update(findInfo.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    toast(EhApplication.getContext(),"更新数据成功");
                                    Log.i("bmob","更新数据成功：" );
                                }else{
                                    toast(EhApplication.getContext(),"更新数据失败，请重新激活");
                                    APPConfig.currentToken = new Token();
                                    APPConfig.isValible = false;
                                    APPConfig.globalFreeTime = 0;
                                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                }
                                if (callBack != null){
                                    callBack.checkCallBack(false);
                                }
                            }
                        });
                    }
                }
            }
        });
    }


    public void checkToken(final String token,final CheckInterFace callback){

//        setCheckInterFace(callback);

        BmobQuery<Token> query =new BmobQuery<Token>();
        query.addWhereEqualTo("token", token);
        query.findObjects(new FindListener<Token>() {
            @Override
            public void done(List<Token> list, BmobException e) {
                if (e == null){
                    Token token1 = new Token();
                    if (list.isEmpty()){
                        APPConfig.isValible = false;
                        APPConfig.globalFreeTime = 0;
                        toast(EhApplication.getContext(),"激活码无效");
                    }else {
                        Token tokenInfo = list.get(0);
                        String type = tokenInfo.getAvailable_period();

                        String startDate = tokenInfo.getStart_time();
                        if (startDate == null || startDate.equals("")){
                            token1.setStart_time(DateTools.dateToString(DateTools.getWebsiteDatetime()));
                        }else {
                            token1.setStart_time(startDate);
                        }

                        token1.setToken(token);
                        token1.setAvailable_period(type);

                        String device = tokenInfo.getDevice_id();
                        if (device == null || device.equals("")){
                            //新激活
                            APPConfig.isExpire = false;
                            APPConfig.isValible = true;
                            token1.setDevice_id(APPConfig.deviceId);
                            updateTokens(token1);
                            UserInfo updateUser = new UserInfo();
//                            updateUser.setAvailable_period(token1.getAvailable_period());
                            updateUser.setDevice_id(APPConfig.deviceId);
//                            updateUser.setStart_time(new BmobDate(DateTools.getWebsiteDatetime()));
                            updateUser.setToken(token1.getToken());
                            updateUser(updateUser,null);
                        }else {
                            if (device.equals(APPConfig.deviceId)){
                                token1.setDevice_id(device);

                                APPConfig.isExpire = DateTools.ifExpire(DateTools.stringToDate(token1.getStart_time()),token1.getAvailable_period());
                                if (APPConfig.isExpire){
                                    APPConfig.isValible = false;
                                    APPConfig.globalFreeTime = 0;
                                    token1 = new Token();
                                }else {
                                    APPConfig.isValible = true;
                                    APPConfig.globalFreeTime = 0;
                                    updateTokens(token1);
//                                    APPConfig.endDate = DateTools.getEndDateBy(tempDate,tokenInfo.getAvailable_period());
                                }
                            }else {
                                APPConfig.isValible = false;
                                APPConfig.globalFreeTime = 0;
                                token1 = new Token();
                                toast(EhApplication.getContext(),"激活码已激活但不与设备匹配");
                            }
                        }
                    }
                    callback.checkCallBack(token1);
                }else {
                    APPConfig.globalFreeTime = 0;
                    APPConfig.isValible = false;
                    APPConfig.currentToken = new Token();
                    toast(EhApplication.getContext(),"网络错误，请重新验证");
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    callback.checkCallBack(null);
                }
            }
        });

    }

    public void updateTokens(final Token tokenInfo){

        BmobQuery<Token> query =new BmobQuery<Token>();
        query.addWhereEqualTo("token", tokenInfo.getToken());
        query.findObjects(new FindListener<Token>() {
            @Override
            public void done(List<Token> list, BmobException e) {
                if (e == null){
                    if (!list.isEmpty()){
                        Token findInfo = list.get(0);
                        findInfo.setDevice_id(tokenInfo.getDevice_id());
                        findInfo.setStart_time(tokenInfo.getStart_time());
                        findInfo.update(findInfo.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    toast(EhApplication.getContext(),"更新数据成功");
                                    Log.i("bmob","更新数据成功：" );
                                }else{
                                    toast(EhApplication.getContext(),"更新数据失败，请重新激活");
                                    APPConfig.isValible = false;
                                    APPConfig.globalFreeTime = 0;
                                    APPConfig.isExpire = true;
                                    APPConfig.localToken = null;
                                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                }

                            }
                        });
                    }
                }else {
                    toast(EhApplication.getContext(),"更新数据失败，请重新激活");
                    APPConfig.isValible = false;
                    APPConfig.globalFreeTime = 0;
                    APPConfig.isExpire = true;
                    APPConfig.localToken = null;
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


    public void checkAviailable(final CheckAvailable callBack){
//        esXpQQQY
        BmobQuery<NetSwitch> query =new BmobQuery<NetSwitch>();
        query.findObjects(new FindListener<NetSwitch>() {
            @Override
            public void done(List<NetSwitch> list, BmobException e) {
                if(e==null){
                    NetSwitch newUser = new NetSwitch();
                    Log.i("bmob","查询成功：");
                    if (list.isEmpty()){
                        callBack.isAvailable(false);
                    }else {
                        final NetSwitch user = list.get(0);
                        callBack.isAvailable(user.available);
                    }
                }else{
                    callBack.isAvailable(false);
                }

            }
        });
    }

    public void toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
