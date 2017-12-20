package com.hippo.ehviewer.client.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hippo.ehviewer.EhApplication;
import com.hippo.ehviewer.Settings;
import com.hippo.ehviewer.client.APPConfig;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

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

    public void checkByDeviceId(String deviceId){

        BmobQuery<UserInfo> query =new BmobQuery<UserInfo>();
        query.addWhereEqualTo("device_id", APPConfig.deviceId);
        query.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> list, BmobException e) {
                if(e==null){
                    Log.i("bmob","查询成功：");
                    if (list.isEmpty()){
                        APPConfig.globalFreeTime = 3;
                        APPConfig.isValible = true;
                        addUserDeviceId(APPConfig.deviceId);
                    }else {
                        UserInfo user = list.get(0);
                        if (user.getToken()==null){
                            APPConfig.globalFreeTime = user.getFree_times();
                            if (APPConfig.globalFreeTime > 0){
                                APPConfig.isValible = true;
                            }else {
                                APPConfig.isValible = false;
                            }
                        }else {
                            APPConfig.isValible = true;
                            Settings.putString(Settings.TOKEN,user.getToken());
                            //更新激活时间

                        }

                    }

                }else{
                    APPConfig.globalFreeTime = 0;
                    APPConfig.isValible = false;
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
                Settings.putBoolean("isValible",APPConfig.isValible);
                Settings.putInt(APPConfig.deviceId,APPConfig.globalFreeTime);
            }
        });
    }

    public void addUserDeviceId(String deivceId){
        UserInfo user = new UserInfo();
        user.setDevice_id(deivceId);
        user.setFree_times(3);
        user.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Log.i("bmob","创建数据成功：" + s);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    public boolean checkToken(final String token){

        BmobQuery<Token> query =new BmobQuery<Token>();
        query.addWhereEqualTo("token", token);
        query.findObjects(new FindListener<Token>() {
            @Override
            public void done(List<Token> list, BmobException e) {
                if (e == null){
                    //一起同步user
                    if (list.isEmpty()){
                        APPConfig.isValible = false;
                        APPConfig.globalFreeTime = 0;
                        APPConfig.currentToken = new Token();
                        toast(EhApplication.getContext(),"激活码无效");
                    }else {
                        Token tokenInfo = list.get(0);
                        String type = tokenInfo.getAvailable_period();
                        Date startDate = tokenInfo.getStart_time();
                        if (startDate == null){
                            tokenInfo.setStart_time(new Date());
                        }
                        APPConfig.currentToken.setStart_time(startDate);
                        APPConfig.currentToken.setToken(token);
                        APPConfig.currentToken.setDevice_id(APPConfig.deviceId);
                        APPConfig.currentToken.setAvailable_period(type);
                    }

                }else {
                    APPConfig.globalFreeTime = 0;
                    APPConfig.isValible = false;
                    APPConfig.globalFreeTime = 0;
                    APPConfig.currentToken = new Token();
                    toast(EhApplication.getContext(),"激活码无效");
                    toast(EhApplication.getContext(),"激活码无效");
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
        return false;
    }

    public void updateToken(Token tokenInfo){
        tokenInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Log.i("bmob","更新数据成功：" + s);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    public void toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
