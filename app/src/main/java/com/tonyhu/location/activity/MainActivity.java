package com.tonyhu.location.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerBottomHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerTopHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationMainContentHandler;
import com.tonyhu.location.R;
import com.tonyhu.location.fragment.MainFragment;
import com.tonyhu.location.fragment.SettingUpDialog;
import com.umeng.analytics.MobclickAgent;

import static com.tonyhu.location.TonyLocationApplication.getContext;


public class MainActivity extends com.blunderer.materialdesignlibrary.activities.NavigationDrawerActivity {
    private static final int REQUEST_PERMISSION_LOCATION = 255; // int should be between 0 and 255
    private long lastOnBackPressed = 0;
    private int backPressCount = 0;
    private FrameLayout popupMenu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar(R.color.color_primary);
        checkPermission();

        initView();
    }

    private void initView() {
        popupMenu = (FrameLayout)findViewById(R.id.popupwindow);
        popupMenu.findViewById(R.id.btn_favorite).setOnClickListener(mOnMenuClickListener);
        popupMenu.findViewById(R.id.btn_setup).setOnClickListener(mOnMenuClickListener);
        popupMenu.findViewById(R.id.btn_help).setOnClickListener(mOnMenuClickListener);
        popupMenu.setOnClickListener(mOnMenuClickListener);
    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            } else {
                // We have already permission to use the location
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We now have permission to use the location
            }
        }
    }

    @Override
    protected View.OnClickListener getMenuItemClickListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.search_place:
                        Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btn_more:
                        if(popupMenu.getVisibility() == View.VISIBLE) {
                            popupMenu.setVisibility(View.GONE);
                        } else {
                            popupMenu.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        };
    }

    @Override
    protected NavigationDrawerTopHandler getNavigationDrawerTopHandler() {
          return null;
    }

    @Override
    protected NavigationDrawerBottomHandler getNavigationDrawerBottomHandler() {
        return new NavigationDrawerBottomHandler()
                .addItem(R.string.drawer_favorite,0,mOnClickListener)
                .addItem(R.string.drawer_feedback,0,mOnClickListener)
                .addItem(R.string.drawer_reward,0,mOnClickListener)
                .addItem(R.string.drawer_about,0,mOnClickListener);
    }

    @Override
    protected NavigationMainContentHandler getNavigationMainContentHandler() {

        return new NavigationMainContentHandler().setContent(MainFragment.newInstance());
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView tvTitle = (TextView) view.findViewById(R.id.navigation_drawer_row_title);
            if(tvTitle == null) return;
            CharSequence title = tvTitle.getText();
            if(title.equals(getString(R.string.drawer_favorite))) {
                startActivity(MyFavoriteActivity.class);
            } else if(title.equals(getString(R.string.drawer_feedback))) {
                startActivity(FeedBackActivity.class);
            } else if(title.equals(getString(R.string.drawer_reward))) {
                startActivity(RewardActivity.class);
            } else if(title.equals(getString(R.string.drawer_about))) {
                startActivity(AboutActivity.class);
            }
        }
    };

    private View.OnClickListener mOnMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_favorite:
                    startActivity(MyFavoriteActivity.class);
                    popupMenu.setVisibility(View.GONE);
                    break;
                case R.id.btn_setup:
                    showSettingDialog();
                    popupMenu.setVisibility(View.GONE);
                    break;
                case R.id.btn_help:
                    popupMenu.setVisibility(View.GONE);
                    break;
                case R.id.popupwindow:
                    popupMenu.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private void showSettingDialog() {
        boolean canMock = canMockPosition();
        boolean isPosEnabled = isPositionServiceEnable();
        int type = getWrongType(canMock,isPosEnabled);
        SettingUpDialog dialog = new SettingUpDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(),"dialog");
    }

    private LocationManager locationManager;
    boolean hasAddTestProvider = false;
    private boolean canMockPosition() {
        //具体参考：http://blog.csdn.net/doris_d/article/details/51384285
        //Android 6.0 以下：使用Settings.Secure.ALLOW_MOCK_LOCATION判断。

        boolean canMockPosition = (Settings.Secure.getInt(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0)
                || Build.VERSION.SDK_INT > 22;
        if (canMockPosition && hasAddTestProvider == false) {
            try {
                String providerStr = LocationManager.GPS_PROVIDER;
                if(locationManager == null) {
                    locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
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

    private boolean isPositionServiceEnable() {
//        LocationManager locationManager =
//                ((LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE));
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return false;
    }

    private void startActivity(Class cls) {
        Intent intent = new Intent(this,cls);
        startActivity(intent);
    }

    public void openDrawer(){
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if(backPressCount == 0) {
                backPressCount ++;
                lastOnBackPressed = System.currentTimeMillis();
                Toast.makeText(this,R.string.press_back_again,Toast.LENGTH_SHORT).show();
                return;
            } else if(System.currentTimeMillis() - lastOnBackPressed > 3000) {
                lastOnBackPressed = System.currentTimeMillis();
                backPressCount = 0;
                Toast.makeText(this,R.string.press_back_again,Toast.LENGTH_LONG).show();
                return;
            }
            super.onBackPressed();
        }
    }

    /**
     * 状态栏处理：解决从欢迎页全屏切换到本页非全屏页面被压缩导致抖动一下问题
     */
    public void initStatusBar(int barColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            // 获取状态栏高度
            int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            View rectView = new View(this);
            // 绘制一个和状态栏一样高的矩形，并添加到视图中
            LinearLayout.LayoutParams params
                    = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            rectView.setLayoutParams(params);
            //设置状态栏颜色
            rectView.setBackgroundColor(getResources().getColor(barColor));
            // 添加矩形View到布局中
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            decorView.addView(rectView);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


}
