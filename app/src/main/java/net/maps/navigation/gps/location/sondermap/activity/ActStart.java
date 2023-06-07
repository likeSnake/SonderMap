package net.maps.navigation.gps.location.sondermap.activity;

import static net.maps.navigation.gps.location.sondermap.constant.Constant.AD_BEAN;
import static net.maps.navigation.gps.location.sondermap.constant.Constant.APP_Agent_URL;
import static net.maps.navigation.gps.location.sondermap.constant.Constant.APP_GET_URL;
import static net.maps.navigation.gps.location.sondermap.constant.Constant.Agent;
import static net.maps.navigation.gps.location.sondermap.constant.Constant.DFToken;
import static net.maps.navigation.gps.location.sondermap.constant.Constant.DFiD;
import static net.maps.navigation.gps.location.sondermap.util.HttpUtils.getStringRequest;
import static net.maps.navigation.gps.location.sondermap.util.HttpUtils.postStringRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;
import com.wang.avi.AVLoadingIndicatorView;

import net.maps.navigation.gps.location.sondermap.R;
import net.maps.navigation.gps.location.sondermap.base.BaseActivity;
import net.maps.navigation.gps.location.sondermap.bean.AdBean;
import net.maps.navigation.gps.location.sondermap.util.AesUtil;
import net.maps.navigation.gps.location.sondermap.util.MyUtil;

import java.util.ArrayList;
import java.util.List;

public class ActStart extends BaseActivity {

    private AdBean adBean;
    private ImageView enter;
    private AVLoadingIndicatorView progress;
    @Override
    protected int getRootView() {
        return R.layout.act_start;
    }

    @Override
    protected void initView() {
        enter = findViewById(R.id.enter);
        progress = findViewById(R.id.avi);
        progress.show();
    }

    @Override
    protected void initData() {
        getPermission();
        getAgent();

        getStringRequest(APP_GET_URL, this,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyUtil.MyLog(response);
                        // 处理响应成功时的逻辑
                        runOnUiThread(()-> {
                            if (response != null&&!response.equals("")) {
                                try {

                                    Gson gson = new Gson();

                                    adBean = gson.fromJson(AesUtil.decrypt(response), new TypeToken<AdBean>() {
                                    }.getType());
                                    System.out.println("成功" + adBean.toString());


                                    setFacebook(adBean.getFid(),adBean.getFtoken());


                                    System.out.println(adBean.getMy());
                                    if (adBean.getAdIcon() != null && !adBean.getAdIcon().isEmpty()) {
                                        enter.setImageResource(R.drawable.ic_enter2);
                                    }
                                }catch (Exception e){
                                    Log.e("response结果异常",e.toString());
                                    startActivity(new Intent(ActStart.this, ActMap.class));
                                    finish();
                                }
                                enter.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.GONE);
                                enter.setOnClickListener(ActStart.this);

                            } else {
                                startActivity(new Intent(ActStart.this, ActMap.class));
                                finish();
                            }
                        });
                        
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 处理响应失败时的逻辑
                    }
                });
    }

    @Override
    protected void initListener() {

    }

    public void setFacebook(String fid, String fToken ){
        if (adBean.getFid()==null&&adBean.getFid().equals("")&&adBean.getFtoken()==null&adBean.getFtoken().equals("")){
            Log.e("setFacebook","值为null，使用默认值");
            fid = DFiD;
            fToken = DFToken;
        }

        Log.i("setFacebook","setFacebook"+fid+"--"+fToken);
        FacebookSdk.setApplicationId(fid);
        FacebookSdk.setClientToken(fToken);
        FacebookSdk.sdkInitialize(this);

        postStringRequest("https://graph.facebook.com/" + fid + "/activities",ActStart.this,"",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 处理响应成功时的逻辑
                        MyUtil.MyLog("response");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 处理响应失败时的逻辑
                        MyUtil.MyLog(error);
                    }
                });

    }

    public void getPermission(){

        // 创建一个权限列表，把需要使用而没用授权的的权限存放在这里
        List<String> permissionList = new ArrayList<>();

        // 判断权限是否已经授予，没有就把该权限添加到列表中
        //精确定位
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        //粗略定位
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        // 如果列表为空，就是全部权限都获取了，不用再次获取了。不为空就去申请权限
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1002);
        } else {
            //已获取权限
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantresponses) {
        super.onRequestPermissionsResult(requestCode, permissions, grantresponses);
        switch (requestCode) {
            case 1002:
                // 1002请求码对应的是申请多个权限
                if (grantresponses.length > 0) {
                    List<String> list = new ArrayList<>();
                    // 因为是多个权限，所以需要一个循环获取每个权限的获取情况
                    for (int i = 0; i < grantresponses.length; i++) {
                        // PERMISSION_DENIED 这个值代表是没有授权，我们可以把被拒绝授权的权限显示出来
                        if (grantresponses[i] == PackageManager.PERMISSION_DENIED){
                            String permission = permissions[i];

                            list.add(permission);
                            // Toast.makeText(Start.this, permissions[i] + "权限被拒绝了,请手动打开权限", Toast.LENGTH_SHORT).show();
                            // getAppDetailSettingIntent(Start.this);
                        }
                    }
                    if (list.isEmpty()){

                    }else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                        System.out.println("用户选择始终拒绝不再弹出");
                        Toast.makeText(ActStart.this,"Be sure to grant location permissions, otherwise it won't work!",Toast.LENGTH_LONG).show();
                        getAppDetailSettingIntent();
                    }else{
                        //Toast.makeText(StartActivity.this,"Please give permission to locate",Toast.LENGTH_SHORT).show();

                        AlertDialog alertDialog = new AlertDialog.Builder(this)
                                .setTitle("Permission settings")
                                .setMessage("Be sure to grant location permissions, otherwise it won't work!")
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getPermission();
                                    }
                                }).create();
                        alertDialog.show();
                    }
                }
        }
    }
    /**
     *
     * 跳转到权限设置界面
     *
     */
    private void getAppDetailSettingIntent(){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT >= 9){
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if(Build.VERSION.SDK_INT <= 8){
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(intent);
    }

    public void startMain(){
        startActivity(new Intent(this,ActMap.class));
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.enter:
                if (adBean!=null) {
                    MMKV.defaultMMKV().encode(AD_BEAN,new Gson().toJson(adBean));
                }else {
                    startActivity(new Intent(ActStart.this,ActMap.class));
                }
                Intent intent = new Intent(this, ActLogin.class);
                intent.putExtra(AD_BEAN,adBean);
                startActivity(intent);
                finish();
                break;
        }
    }
    public void getAgent(){
        getStringRequest(APP_Agent_URL, ActStart.this,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("获取成功"+response);
                        MMKV.defaultMMKV().encode(Agent,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("请求头失败",error.toString());
                        MMKV.defaultMMKV().removeValueForKey(Agent);
                    }
                }
        );
    }
}