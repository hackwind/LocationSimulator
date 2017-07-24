package com.tonyhu.location.fragment;

import android.content.Context;
import android.location.Criteria;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.tonyhu.location.R;
import com.tonyhu.location.db.Favorite;
import com.tonyhu.location.db.FavoriteDao;
import com.tonyhu.location.util.JZLocationConverter;

import java.util.Date;
import java.util.List;


public class MultipointFragment extends Fragment implements View.OnClickListener{
    private View rootView;
    private TextureMapView mapView;
    private Button btnStart;
    private Button btnStop;
    private CheckBox checkBox;
    private EditText etInterval;
    private LocationManager locationManager;
    private boolean stop = false;
    private List<Favorite> favorites;
    private boolean repeat = false;//是否循环
    private int interval = 10*60*1000;//轮训间隔,默认10分钟

    public MultipointFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_multipoint_location, null);
            initView(rootView);
        }
        return rootView;
    }

    private void initView(View view) {
        mapView = (TextureMapView) view.findViewById(R.id.baidumap);

        btnStart = (Button)view.findViewById(R.id.btn_start);
        btnStop = (Button)view.findViewById(R.id.btn_stop);
        checkBox = (CheckBox)view.findViewById(R.id.checkbox) ;
        etInterval = (EditText)view.findViewById(R.id.edit_interval) ;
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

    }
    @Override
    public void onPause(){
        mapView.setVisibility(View.INVISIBLE);
        mapView.onPause();

        super.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
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
            case R.id.btn_start:
                startLocate();
                break;
            case R.id.btn_stop:
                stopLocate();
                break;
        }
    }
    private void startLocate() {
        boolean canMock = canMockPosition();
        boolean isPosEnabled = isPositionServiceEnable();
        int type = getWrongType(canMock,isPosEnabled);
        if(type != 0) {
            showAlertDialog(type);
            return;
        }
        if(getFavorite() == 0) {
            Toast.makeText(getContext(),"收藏夹为空哦，先去收藏位置吧",Toast.LENGTH_LONG).show();
            return;
        }
        stop = false;
        checkBox.setEnabled(false);
        etInterval.setEnabled(false);
        new Thread(new MultipointFragment.RunnableMockLocation()).start();
    }

    private int getFavorite() {
        FavoriteDao dao = new FavoriteDao();
        favorites = dao.listAll();
        return favorites == null ? 0 : favorites.size();
    }

    private boolean isPositionServiceEnable() {
//        LocationManager locationManager =
//                ((LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE));
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return false;
    }
    private void showAlertDialog(int type) {
        SettingUpDialog dialog = new SettingUpDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(),"dialog");
    }

    private int getWrongType(boolean canMock, boolean isPosEnabled) {
        if(canMock && !isPosEnabled) {
            return 0;
        } else if(!canMock && !isPosEnabled) {
            return 1;
        } else if(canMock && isPosEnabled) {
            return 2;
        } else {
            return 3;
        }
    }

    private void stopLocate(){
        stop = true;
        checkBox.setEnabled(true);
        etInterval.setEnabled(true);
    }

    private int index = 0;
    private class RunnableMockLocation implements Runnable {

        @Override
        public void run() {
            while (!stop) {
                try {

                    if (canMockPosition() == false) {
                        continue;
                    }

                    try {
                        // 模拟位置（addTestProvider成功的前提下）
                        String providerStr = LocationManager.GPS_PROVIDER;
                        Location mockLocation = new Location(providerStr);
                        Favorite favorite = favorites.get(index);
                        LatLng latLng = new LatLng(favorite.getLatitude(),favorite.getLongitude());
                        JZLocationConverter.LatLng srcLatLng = new JZLocationConverter.LatLng(latLng.latitude,latLng.longitude);
                        JZLocationConverter.LatLng destLatLng = JZLocationConverter.bd09ToWgs84(srcLatLng);
                        mockLocation.setLatitude(destLatLng.latitude);   // 维度（度）
                        mockLocation.setLongitude(destLatLng.longitude);  // 经度（度）
                        mockLocation.setAltitude(30);    // 高程（米）
                        mockLocation.setBearing(180);   // 方向（度）
                        mockLocation.setSpeed(10);    //速度（米/秒）
                        mockLocation.setAccuracy(0.1f);   // 精度（米）
                        mockLocation.setTime(new Date().getTime());   // 本地时间
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                        }
                        locationManager.setTestProviderLocation(providerStr, mockLocation);
                        addOverlay(latLng);

                    } catch (Exception e) {
                        e.printStackTrace();
                        // 防止用户在软件运行过程中关闭模拟位置或选择其他应用
                        cancelMockPosition();
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    boolean hasAddTestProvider = false;
    private boolean canMockPosition() {
        //具体参考：http://blog.csdn.net/doris_d/article/details/51384285
        //Android 6.0 以下：使用Settings.Secure.ALLOW_MOCK_LOCATION判断。
        if(getActivity() == null) {
            return false;
        }
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

    private void cancelMockPosition(){
        stop = true;
        try {
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch(Exception e) {
            e.printStackTrace();
        }
        hasAddTestProvider = false;
//        isLoopVibrate = false;
    }

    private void addOverlay(LatLng latLng) {
        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.location);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(latLng).icon(
                bitmap);
        // 在地图上添加Marker，并显示
        mapView.getMap().clear();
        mapView.getMap().addOverlay(option);
    }
}
