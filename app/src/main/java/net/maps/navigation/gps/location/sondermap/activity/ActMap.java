package net.maps.navigation.gps.location.sondermap.activity;

import static net.maps.navigation.gps.location.sondermap.util.MyUtil.MyLog;
import static net.maps.navigation.gps.location.sondermap.util.MyUtil.decodeBitmapFromResource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tencent.mmkv.MMKV;

import net.maps.navigation.gps.location.sondermap.R;
import net.maps.navigation.gps.location.sondermap.adapter.TopicAdapter;
import net.maps.navigation.gps.location.sondermap.base.BaseActivity;
import net.maps.navigation.gps.location.sondermap.util.MapUtil;
import net.maps.navigation.gps.location.sondermap.util.MyUtil;

import java.util.ArrayList;

public class ActMap extends BaseActivity implements LocationListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private GoogleMapOptions options;
    private Geocoder geocoder;
    private TopicAdapter topicAdapter;
    private ImageView menu_map;
    private PopupWindow popupWindow;
    private View contentView;
    private RelativeLayout bar;
    private LocationManager locationManager;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private RecyclerView recyclerView1;
    private TextView Done;
    private TextView cancel;
    private ArrayList<String> list = new ArrayList<>();
    private LatLng latLng;
    private Marker marker;

    @Override
    protected int getRootView() {
        return R.layout.act_map;
    }

    @Override
    protected void initView() {

        menu_map = findViewById(R.id.menu_map);
        bar = findViewById(R.id.bar);
        menu_map = findViewById(R.id.menu_map);

        // 加载底部弹窗的布局
        bottomSheetView = getLayoutInflater().inflate(R.layout.item_bottom, null);
        recyclerView1 = bottomSheetView.findViewById(R.id.recyclerView1);
        Done = bottomSheetView.findViewById(R.id.Done);
        cancel = bottomSheetView.findViewById(R.id.cancel);


    }

    @Override
    protected void initData() {

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        //初始化底部弹窗
        initBottomSheet();

        mapFragment.getMapAsync(this);
        MapUtil.getLocation(this);
    }

    @Override
    protected void initListener() {

        menu_map.setOnClickListener(this);
        Done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        bar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.menu_map:
                bottomSheetDialog.show();
                break;
            case R.id.Done:
                int selectedPosition = topicAdapter.getSelectedPosition();
                switchMapType(selectedPosition);

                bottomSheetDialog.cancel();
                break;
            case R.id.cancel:
                bottomSheetDialog.cancel();
                break;
            case R.id.bar:
                startActivity(new Intent(ActMap.this,ActSearch.class));

        }

    }

    public void switchMapType(int selectedPosition) {
        switch (list.get(selectedPosition)) {
            case "Regular":
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "Satellite":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "Terrain":
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MMKV.defaultMMKV().decodeString("longitude")!=null&&MMKV.defaultMMKV().decodeString("latitude")!=null){
            if (marker!=null){
                marker.remove();
            }

            String longitude = MMKV.defaultMMKV().decodeString("longitude");
            String latitude = MMKV.defaultMMKV().decodeString("latitude");
            LatLng myLatLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 20));
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(myLatLng);
            marker = mMap.addMarker(markerOptions);

            MMKV.defaultMMKV().removeValueForKey("longitude");
            MMKV.defaultMMKV().removeValueForKey("latitude");

        }else {

        }


    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        MyUtil.MyLog(location);
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.setPadding(20, 250, 20, 20);
    }


    @Override
    public boolean onMyLocationButtonClick() {
        System.out.println("定位");
        if (latLng != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
        }
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        MyUtil.MyLog(location.toString());
    }

    private void initBottomSheet() {
        start(false);

        // 创建底部弹窗
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogStyle);
        } else {

        }

        bottomSheetDialog.setContentView(bottomSheetView);
    }

    public void start(Boolean b) {
        list.clear();
        list.add("Regular");
        list.add("Satellite");
        list.add("Terrain");
        LinearLayoutManager manager = new LinearLayoutManager(ActMap.this, LinearLayoutManager.VERTICAL, b);
        topicAdapter = new TopicAdapter(this, list);
        recyclerView1.setLayoutManager(manager);
        recyclerView1.setAdapter(topicAdapter);
    }



    public void drawLocationMarker(){

    }
}