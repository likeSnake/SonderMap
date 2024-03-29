package net.maps.navigation.gps.location.sondermap;




import static net.maps.navigation.gps.location.sondermap.constant.Constant.FIRST;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.tencent.mmkv.MMKV;

import net.maps.navigation.gps.location.sondermap.util.MyUtil;


/** Application class that initializes, loads and show ads when activities change states. */
public class App extends Application
        implements ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private int count=0;
    public  static Boolean isForground = true;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        MMKV.initialize(this);
        initDate();


    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public void initDate(){
        if (MMKV.defaultMMKV().decodeString(FIRST)==null){
            MMKV.defaultMMKV().encode(FIRST,String.valueOf(System.currentTimeMillis()));
        }


    }
    //初始化google ads
    public void initGoogleAds(){



    }




    /**
     * DefaultLifecycleObserver method that shows the app open ad when the app moves to foreground.
     */
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);

    }

    /** ActivityLifecycleCallback methods. */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

        count++;
        if (!isForground){
            //后台回到前台
            isForground = true;


        }

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        MyUtil.MyLog("Resumed--"+activity.getClass().getName());

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        MyUtil.MyLog("Paused");

    }



    @Override
    public void onActivityStopped(@NonNull Activity activity) {

        count--;
        if (count==0){
            isForground = false;
            //切后台

        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        MyUtil.MyLog("onActivitySaveInstanceState");


    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }


    /**
     * 广告加载完成以及关闭回调
     */
    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
        void TurnoffAds();

    }

    @Override
    public String getPackageName() {

        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement item : stackTrace) {

                if ("org.chromium.base.BuildInfo".equals(item.getClassName())) {
                    if ("getAll".equals(item.getMethodName())) {
                        return null;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.getPackageName();
    }
}