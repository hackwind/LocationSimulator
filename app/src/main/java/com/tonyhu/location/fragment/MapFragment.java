package com.tonyhu.location.fragment;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.tonyhu.location.R;
import com.tonyhu.location.TonyLocationApplication;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MapFragment extends Fragment implements View.OnClickListener{
    private View rootView;
    private TextureMapView mapView;
    private Button btnAddFavorite;
    private Button btnPassThrough;
    private TextView txtPosition;
    private LocationManager locationManager;

    public MapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_map, null);
            initView(rootView);
        }
        return rootView;
    }

    private void initView(View view) {
        mapView = (TextureMapView) view.findViewById(R.id.baidumap);
        btnAddFavorite = (Button)view.findViewById(R.id.btn_add_favorite);
        btnPassThrough = (Button)view.findViewById(R.id.btn_pass);
        txtPosition = (TextView)view.findViewById(R.id.text_position);
        btnAddFavorite.setOnClickListener(this);
        btnPassThrough.setOnClickListener(this);
    }


    @Override
    public void onPause(){
        mapView.setVisibility(View.INVISIBLE);
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume(){
        mapView.setVisibility(View.VISIBLE);
        mapView.onResume();
        super.onResume();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_favorite:

                break;
            case R.id.btn_pass:
                new Thread(new RunnableMockLocation()).start();
                break;
        }
    }

    private class RunnableMockLocation implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(2000);

                    if (hasAddTestProvider() == false) {
                        continue;
                    }

                    try {
                        // 模拟位置（addTestProvider成功的前提下）
                        String providerStr = LocationManager.GPS_PROVIDER;
                        Location mockLocation = new Location(providerStr);
                        LatLng latLng = mapView.getMap().getMapStatus().target;
                        mockLocation.setLatitude(latLng.latitude);   // 维度（度）
                        mockLocation.setLongitude(latLng.longitude);  // 经度（度）
                        mockLocation.setAltitude(30);    // 高程（米）
                        mockLocation.setBearing(180);   // 方向（度）
                        mockLocation.setSpeed(10);    //速度（米/秒）
                        mockLocation.setAccuracy(0.1f);   // 精度（米）
                        mockLocation.setTime(new Date().getTime());   // 本地时间
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                        }
                        locationManager.setTestProviderLocation(providerStr, mockLocation);
                    } catch (Exception e) {
                        // 防止用户在软件运行过程中关闭模拟位置或选择其他应用
                        stopMockLocation();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 停止模拟位置，以免启用模拟数据后无法还原使用系统位置
     * 若模拟位置未开启，则removeTestProvider将会抛出异常；
     * 若已addTestProvider后，关闭模拟位置，未removeTestProvider将导致系统GPS无数据更新；
     */
    public void stopMockLocation() {
        if (hasAddTestProvider) {
            try {
                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                // 若未成功addTestProvider，或者系统模拟位置已关闭则必然会出错
            }
            hasAddTestProvider = false;
        }
    }
    boolean hasAddTestProvider = false;
    private boolean hasAddTestProvider() {
        //具体参考：http://blog.csdn.net/doris_d/article/details/51384285
        //Android 6.0 以下：使用Settings.Secure.ALLOW_MOCK_LOCATION判断。
        boolean canMockPosition = (Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0)
                || Build.VERSION.SDK_INT > 22;
        if (canMockPosition && hasAddTestProvider == false) {
            try {
                String providerStr = LocationManager.GPS_PROVIDER;
                if(locationManager == null) {
                    locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
                }
                LocationProvider provider = locationManager.getProvider(providerStr);
                if (provider != null) {
                    locationManager.addTestProvider(
                            provider.getName()
                            , provider.requiresNetwork()
                            , provider.requiresSatellite()
                            , provider.requiresCell()
                            , provider.hasMonetaryCost()
                            , provider.supportsAltitude()
                            , provider.supportsSpeed()
                            , provider.supportsBearing()
                            , provider.getPowerRequirement()
                            , provider.getAccuracy());
                } else {
                    locationManager.addTestProvider(
                            providerStr
                            , true, true, false, false, true, true, true
                            , Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                }
                locationManager.setTestProviderEnabled(providerStr, true);
                locationManager.setTestProviderStatus(providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis());

                // 模拟位置可用
                hasAddTestProvider = true;
                canMockPosition = true;
            } catch (SecurityException e) {
                canMockPosition = false;
            }
        }
        return canMockPosition;
    }

    /**
     * 通过经纬度获取地址
     *
     * @param point
     * @return
     */
    private String getLocationAddress(LatLng point) {
        String add = "";
        Geocoder geoCoder = new Geocoder(TonyLocationApplication.getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                    point.latitudeE6 / 1E6, point.longitudeE6 / 1E6,
                    1);
            Address address = addresses.get(0);
            int maxLine = address.getMaxAddressLineIndex();
            if (maxLine >= 2) {
                add = address.getAddressLine(1) + address.getAddressLine(2);
            } else {
                add = address.getAddressLine(1);
            }
        } catch (IOException e) {
            add = "";
            e.printStackTrace();
        }
        return add;
    }

    private void convertCoord(LatLng sourceLatLng) {
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
    }
}
