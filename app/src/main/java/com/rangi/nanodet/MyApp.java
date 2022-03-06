package com.rangi.nanodet;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.multidex.MultiDex;

import com.rangi.nanodet.utils.sdkinit.ANRWatchDogInit;
import com.rangi.nanodet.utils.sdkinit.UMengInit;
import com.rangi.nanodet.utils.sdkinit.XBasicLibInit;
import com.rangi.nanodet.utils.sdkinit.XUpdateInit;
import com.xuexiang.xhttp2.XHttpSDK;
import com.xuexiang.xui.XUI;
import com.zxy.recovery.callback.RecoveryCallback;
import com.zxy.recovery.core.Recovery;


public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XUI.init(this);
        //崩溃界面
        initLibs();
//        initRecovery();
        XHttpSDK.init(this);   //初始化网络请求框架，必须首先执

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

//    private void initRecovery() {
//        Recovery.getInstance()
//                .debug(BuildConfig.DEBUG)
//                .recoverInBackground(true)
//                .recoverStack(true)
//                .mainPage(MainActivity.class)
//                .recoverEnabled(true)
//                .callback(new MyCrashCallback())
//                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
////                .skip(TestActivity.class)
//                .init(this);
//        AppCrashHandler.register();
//    }
//
//    static final class MyCrashCallback implements RecoveryCallback {
//        @Override
//        public void stackTrace(String exceptionMessage) {
//            Log.e("wzt", "exceptionMessage:" + exceptionMessage);
//        }
//
//        @Override
//        public void cause(String cause) {
//            Log.e("wzt", "cause:" + cause);
//        }
//
//        @Override
//        public void exception(String exceptionType, String throwClassName, String throwMethodName, int throwLineNumber) {
//            Log.e("wzt", "exceptionClassName:" + exceptionType);
//            Log.e("wzt", "throwClassName:" + throwClassName);
//            Log.e("wzt", "throwMethodName:" + throwMethodName);
//            Log.e("wzt", "throwLineNumber:" + throwLineNumber);
//        }
//
//        @Override
//        public void throwable(Throwable throwable) {
//
//        }
//    }
    private void initLibs() {
        // X系列基础库初始化
        XBasicLibInit.init(this);
        // 版本更新初始化
        XUpdateInit.init(this);
        // 运营统计数据
        UMengInit.init(this);
        // ANR监控
        ANRWatchDogInit.init();
    }


    /**
     * @return 当前app是否是调试开发模式
     */
    public static boolean isDebug() {
        return androidx.multidex.BuildConfig.DEBUG;
    }

    private String name;
    private String pwd;
    private String number;
    private int index;
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
}
