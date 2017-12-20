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

    public interface CheckInterFace{
        public void checkCallBack(Token token);
    }

    public CheckInterFace checkInterFace;
    public void setCheckInterFace(CheckInterFace checkInterFace) {
        this.checkInterFace = checkInterFace;
    }

    public interface CheckDeviceInterFace{
        public void checkCallBack();
    }

    public CheckDeviceInterFace checkdeviceInterFace;
    public void setCheckDeciceInterFace(CheckDeviceInterFace checkdeviceInterFace) {
        this.checkdeviceInterFace = checkdeviceInterFace;
    }



    public void checkByDeviceId(final String deviceId, final CheckDeviceInterFace callBack){

        checkdeviceInterFace = callBack;
        BmobQuery<UserInfo> query =new BmobQuery<UserInfo>();
        query.addWhereEqualTo("device_id", deviceId);
        query.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> list, BmobException e) {
                if(e==null){
                    Log.i("bmob","查询成功：");
                    if (list.isEmpty()){
                        APPConfig.globalFreeTime = 3;
                        APPConfig.isValible = true;
                        APPConfig.currentUser.setDevice_id(deviceId);
                        APPConfig.currentUser.setFree_times(3);
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
                            APPConfig.currentUser.setDevice_id(deviceId);
                            APPConfig.currentUser.setFree_times(APPConfig.globalFreeTime);
                            updateUser(APPConfig.currentUser,callBack);
                        }else {
                            APPConfig.isValible = true;
                            Settings.putString(Settings.TOKEN,user.getToken());
                            checkToken(user.getToken(), new CheckInterFace() {
                                @Override
                                public void checkCallBack(Token token) {
                                    if (token.getToken().isEmpty()){

                                    }
                                    updateUser(APPConfig.currentUser,callBack);
                                }
                            });
                            //更新激活时间

                        }

                    }

                }else{
                    APPConfig.globalFreeTime = 0;
                    APPConfig.isValible = false;
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    callBack.checkCallBack();
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
                checkdeviceInterFace.checkCallBack();
            }
        });
    }

    public void updateUser(final UserInfo userInfo, final CheckDeviceInterFace callBack){
        userInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    toast(EhApplication.getContext(),"更新数据成功");
                    Log.i("bmob","更新数据成功：" + s);
                }else{
                    toast(EhApplication.getContext(),"更新数据失败，请重新激活");
                    APPConfig.currentToken = new Token();
                    APPConfig.isValible = false;
                    APPConfig.globalFreeTime = 0;
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
                callBack.checkCallBack();
            }
        });
    }

    public void checkToken(final String token,CheckInterFace callback){

        setCheckInterFace(callback);

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
                        APPConfig.currentToken.setAvailable_period(type);

                        String device = tokenInfo.getDevice_id();
                        if (device == null){
                            APPConfig.currentToken.setDevice_id(APPConfig.deviceId);
                            tokenInfo.setStart_time(new Date());
                        }else {
                            if (device.equals(APPConfig.deviceId)){
                                APPConfig.currentToken.setDevice_id(device);
                                updateToken(APPConfig.currentToken);
                            }else {
                                APPConfig.currentToken = new Token();
                            }
                        }


                    }

                }else {
                    APPConfig.globalFreeTime = 0;
                    APPConfig.isValible = false;
                    APPConfig.globalFreeTime = 0;
                    APPConfig.currentToken = new Token();
                    toast(EhApplication.getContext(),"激活码无效");
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
                Settings.putString(Settings.TOKEN,APPConfig.currentToken.getToken());
                checkInterFace.checkCallBack(APPConfig.currentToken);
            }
        });

    }

    public void updateToken(final Token tokenInfo){
        tokenInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    toast(EhApplication.getContext(),"更新数据成功");
                    Log.i("bmob","更新数据成功：" + s);
                }else{
                    toast(EhApplication.getContext(),"更新数据失败，请重新激活");
                    APPConfig.currentToken = new Token();
                    APPConfig.isValible = false;
                    APPConfig.globalFreeTime = 0;
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    public void toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
