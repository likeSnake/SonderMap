package net.maps.navigation.gps.location.sondermap.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBaseData();

        setContentView(getRootView());

        initView();

        initData();

        initListener();

    }

    @Override
    public void onClick(View v) {

    }

    /**初始化基类数据*/
    private void initBaseData(){};
    /**初始化布局*/
    protected abstract int getRootView();
    /**初始化控件*/
    protected abstract void initView();
    /**初始化数据*/
    protected abstract void initData();
    /**初始化点击事件*/
    protected abstract void initListener();

    protected void startActivity(Class<? extends Activity> clazz){
        Intent in = new Intent(this,clazz);
        startActivity(in);
    }

}
