package com.tonyhu.location.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.tonyhu.location.R;
import com.tonyhu.location.activity.MainActivity;
import com.tonyhu.location.activity.SearchActivity;
import com.tonyhu.location.db.CountDown;
import com.tonyhu.location.db.CountDownDao;
import com.tonyhu.location.db.Favorite;
import com.tonyhu.location.db.FavoriteDao;
import com.tonyhu.location.util.Constants;
import com.tonyhu.location.util.JZLocationConverter;

import java.util.Date;
import java.util.List;


public class MapFragment extends Fragment implements View.OnClickListener,BaiduMap.OnMapStatusChangeListener,
        OnGetGeoCoderResultListener,BaiduMap.OnMapLoadedCallback {
    private static final int NOTIFICATION_ID = 1;
    private View rootView;
    private TextureMapView mapView;
    private TextView btnAddFavorite;
    private TextView btnPassThrough;
    private TextView btnCancelPass;
    private TextView txtPosition;
    private LocationManager locationManager;
    private GeoCoder geoCoder;
    private Vibrator vibrator;
    private boolean isLoopVibrate = false;
    private boolean stop = false;
    private String currentAddress;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    btnPassThrough.setVisibility(View.GONE);
                    btnAddFavorite.setVisibility(View.GONE);
                    btnCancelPass.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    btnPassThrough.setVisibility(View.VISIBLE);
                    btnAddFavorite.setVisibility(View.VISIBLE);
                    btnCancelPass.setVisibility(View.GONE);
                    break;
            }

        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null && SearchActivity.ACTION_SEARCH_COMPLETE.equals(intent.getAction())) {
                double lat = intent.getDoubleExtra("lat",0);
                double lng = intent.getDoubleExtra("lng",0);
                String address = intent.getStringExtra("address");
                String name = intent.getStringExtra("name");
                if(lat != 0d && lng != 0d && mapView.getMap() != null) {
                    LatLng latLng = new LatLng(lat,lng);
                    MapStatus status =  new MapStatus.Builder().target(latLng).zoom(18).build();
                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                            .newMapStatus(status);
                    mapView.getMap().setMapStatus(mMapStatusUpdate);
                    currentAddress = address;
                    txtPosition.setText(address);
                    handler.sendEmptyMessage(1);
                }


            }
        }
    };

    //腾讯广点通插屏广告
    private InterstitialAD iad;

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

        btnAddFavorite = (TextView)view.findViewById(R.id.btn_add_favorite);
        btnPassThrough = (TextView)view.findViewById(R.id.btn_pass);
        btnCancelPass = (TextView)view.findViewById(R.id.btn_cancel_pass);
        txtPosition = (TextView)view.findViewById(R.id.text_position);
        btnAddFavorite.setOnClickListener(this);
        btnPassThrough.setOnClickListener(this);
        btnCancelPass.setOnClickListener(this);
        mapView.getMap().setOnMapStatusChangeListener(this);
        mapView.getMap().setOnMapLoadedCallback(this);
    }

    private InterstitialAD getIAD() {
        if (iad == null) {
            iad = new InterstitialAD(getActivity(), Constants.GDT_APPID, Constants.GDT_PAGE_MAIN);
        }
        return iad;
    }

    private void showAD() {
        getIAD().setADListener(new AbstractInterstitialADListener() {

            @Override
            public void onNoAD(int arg0) {
                //没有广告的情况，避免无法点击穿越，赠送一次
                int count = getChuanyueTimes();
                updateTimes(count - 1);
            }

            @Override
            public void onADReceive() {
//                iad.show();
            }

            @Override
            public void onADClicked(){
                int count = getChuanyueTimes();
                updateTimes(count - 1);
            }

            @Override
            public void onADClosed() {
                iad = null;
                showAD();
            }
        });
        iad.loadAD();
    }

    @Override
    public void onPause(){
        mapView.setVisibility(View.INVISIBLE);
        mapView.onPause();

        super.onPause();
    }
    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SearchActivity.ACTION_SEARCH_COMPLETE);
        getContext().registerReceiver(receiver,filter);
        super.onStart();
        showAD();
    }
    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(receiver);
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
            case R.id.btn_add_favorite:
                addFavorite();
                break;
            case R.id.btn_pass:
                boolean canMock = canMockPosition();
                boolean isPosEnabled = isPositionServiceEnable();
                int type = getWrongType(canMock,isPosEnabled);
                if(type != 0) {
                    showAlertDialog(type);
                    return;
                }
                int count = getChuanyueTimes();
                if(count >= Constants.MAX_CHUANYUE_TIMES){
                    showClickAdDialog();
                    return;
                }
                updateTimes(++count);
                stop = false;
                isLoopVibrate = false;
                new Thread(new RunnableMockLocation()).start();
                break;
            case R.id.btn_cancel_pass:
                btnPassThrough.setVisibility(View.VISIBLE);
                btnAddFavorite.setVisibility(View.VISIBLE);
                btnCancelPass.setVisibility(View.GONE);
                cancelMockPosition();
                break;
        }
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

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        handler.sendEmptyMessage(1);
        getLocationAddress(mapView.getMap().getMapStatus().target);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        currentAddress = reverseGeoCodeResult.getAddress();
        txtPosition.setText(currentAddress);
    }

    @Override
    public void onMapLoaded() {
        Log.d("hjs","onMapLoaded");
        getLocationAddress(mapView.getMap().getMapStatus().target);
    }

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
                        LatLng latLng = mapView.getMap().getMapStatus().target;
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
                        if(!isLoopVibrate) {
                            isLoopVibrate = true;
                            vibrate();
                            showNotification();
                            handler.sendEmptyMessage(0);
                            addOverlay(latLng);
                        }
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
        cancelNotification();
        hasAddTestProvider = false;
        isLoopVibrate = false;
    }

    /**
     * 通过经纬度获取地址
     *
     * @param point
     * @return
     */
    private void getLocationAddress(LatLng point) {
        Log.d("hjs","lat,lng:" + point.latitude + "," + point.longitude);

        if(geoCoder == null) {
            geoCoder = GeoCoder.newInstance();
            geoCoder.setOnGetGeoCodeResultListener(this);
        }
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption()
                .location(point));
    }

    private void vibrate() {
        if(vibrator == null){
            vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        }
        vibrator.vibrate(1000);
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getContext())
                .setTicker("穿越成功")
                .setContentTitle("恭喜，大神啊")
                .setContentText("您已成功穿越到：" + (currentAddress != null ? currentAddress : ""))
                .setSmallIcon(R.mipmap.ic_launcher);

        builder.setWhen(System.currentTimeMillis());  //设置通知显示的时间

        //获得一个PendingIntent对象，用来设置用户点击通知时发生的跳转
        Intent newintent = new Intent(getActivity(),MainActivity.class);
        PendingIntent pintent = PendingIntent.getActivity(getContext(), 0, newintent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pintent);
        notificationManager.notify(NOTIFICATION_ID,builder.build());
    }

    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void showAlertDialog(int type) {
        SettingUpDialog dialog = new SettingUpDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(),"dialog");
    }

    private void showClickAdDialog(){
        ClickAdDialog dialog = new ClickAdDialog();
        dialog.setBtnClickListener(new ClickAdDialog.OnDialogButtonClickListener() {
            @Override
            public void onLeftButtonClick() {
                iad.show();
            }

            @Override
            public void onRightButtonClick() {
            }
        });
        dialog.show(getChildFragmentManager(),"addialog");
    }

    private boolean isPositionServiceEnable() {
//        LocationManager locationManager =
//                ((LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE));
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return false;
    }

    private void addOverlay(LatLng latLng) {
        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.iamhere);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(latLng).icon(
                bitmap);
        // 在地图上添加Marker，并显示
        mapView.getMap().clear();
        mapView.getMap().addOverlay(option);
    }

    private void addFavorite() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_add_favoriate,null);
        final  TextView tvAddress = (TextView)view.findViewById(R.id.favorite_address);
        final EditText etName = (EditText)view.findViewById(R.id.favorite_name);
        tvAddress.setText(currentAddress);
        new AlertDialog.Builder(getActivity()).setTitle("添加收藏")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        LatLng latLng = mapView.getMap().getMapStatus().target;
                        Favorite favorite = new Favorite();
                        favorite.setAddress(currentAddress);
                        favorite.setLatitude(latLng.latitude);
                        favorite.setLongitude(latLng.longitude);
                        favorite.setName(etName.getText().toString().trim());

                        FavoriteDao dao = new FavoriteDao();
                        int row = dao.add(favorite);
                        if(row == 1) {
                            Toast.makeText(getContext(), "添加收藏成功",
                                    Toast.LENGTH_LONG).show();
                        }


                    }
                }).setNegativeButton("取消", null).show();
    }

    private int getChuanyueTimes() {
        CountDownDao dao = new CountDownDao();
        List<CountDown> list = dao.listAll();
        if(list == null || list.size() == 0) {
            return 0;
        }
        for(CountDown countDown : list) {
            if(countDown.getType() == Constants.TYPE_CHUANYUE) {
                return countDown.getCount();
            }
        }
        return 0;
    }

    private void updateTimes(int times) {
        CountDownDao dao = new CountDownDao();
        List<CountDown> list = dao.listAll();
        if(list == null || list.size() == 0) {
            CountDown countDown = new CountDown();
            countDown.setCount(times);
            countDown.setType(Constants.TYPE_CHUANYUE);
            dao.insert(countDown);
        } else {
            for (CountDown countDown : list) {
                if (countDown.getType() == Constants.TYPE_CHUANYUE) {
                    countDown.setCount(times);
                    dao.update(countDown);
                    return;
                }
            }
        }

    }
}
