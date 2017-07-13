package com.tonyhu.location.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.SupportMapFragment;
import com.tonyhu.location.R;
import com.tonyhu.location.activity.MainActivity;

public class MainFragment extends Fragment implements View.OnClickListener{
    private View rootView;
    private FragmentTabHost mTabHost;
    private String[] tabs ;
    private Class[] cls ;
    private TextView button1;
    private TextView button2;
    private TextView button3;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;

    public MainFragment() {

    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main_tabhost, null);
            initView(rootView);
        }
        return rootView;
    }

    private void initView(View view) {

        tabs = new String[]{
                getResources().getString(R.string.tab_title_first),
                getResources().getString(R.string.tab_title_second)
        };
        cls = new Class[]{
                MapFragment.class,
                MultipointFragment.class
        };


        mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup(getContext(), getActivity().getSupportFragmentManager(), R.id.tabcontent);
        mTabHost.getTabWidget().setVisibility(View.GONE);//隐藏顶部切换菜单
        for (int i = 0; i < tabs.length; i++) {
            mTabHost.addTab(mTabHost.newTabSpec(tabs[i]).setIndicator(tabs[i]),cls[i], null);
        }
        button1 = (TextView) view.findViewById(R.id.text1);
        button2 = (TextView) view.findViewById(R.id.text2);
        button3 = (TextView) view.findViewById(R.id.text3);
        image1 = (ImageView)view.findViewById(R.id.image1);
        image2 = (ImageView)view.findViewById(R.id.image2);
        image3 = (ImageView)view.findViewById(R.id.image3);
        view.findViewById(R.id.button1).setOnClickListener(this);
        view.findViewById(R.id.button2).setOnClickListener(this);
        view.findViewById(R.id.button3).setOnClickListener(this);
        mTabHost.setCurrentTabByTag(tabs[0]);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                onButton1Click();
                mTabHost.setCurrentTabByTag(tabs[0]);
                break;
            case R.id.button2:
                onButton2Click();
                mTabHost.setCurrentTabByTag(tabs[1]);
                break;
            case R.id.button3:
                onButton3Click();
//                mTabHost.setCurrentTabByTag(tabs[2]);
                MainActivity main = (MainActivity)getActivity();
                main.openDrawer();
                break;
        }
    }

    private void onButton1Click() {
        button1.setTextColor(getResources().getColor(R.color.main_title_selected));
        image1.setBackgroundResource(R.drawable.main_tab_cuisine_pressed);
        button2.setTextColor(getResources().getColor(R.color.main_title_normal));
        image2.setBackgroundResource(R.drawable.main_tab_type_normal);
        button3.setTextColor(getResources().getColor(R.color.main_title_normal));
        image3.setBackgroundResource(R.drawable.main_tab_nutrition_normal);
    }

    private void onButton2Click() {
        button1.setTextColor(getResources().getColor(R.color.main_title_normal));
        image1.setBackgroundResource(R.drawable.main_tab_cuisine_normal);
        button2.setTextColor(getResources().getColor(R.color.main_title_selected));
        image2.setBackgroundResource(R.drawable.main_tab_type_pressed);
        button3.setTextColor(getResources().getColor(R.color.main_title_normal));
        image3.setBackgroundResource(R.drawable.main_tab_nutrition_normal);
    }

    private void onButton3Click() {
        button1.setTextColor(getResources().getColor(R.color.main_title_normal));
        image1.setBackgroundResource(R.drawable.main_tab_cuisine_normal);
        button2.setTextColor(getResources().getColor(R.color.main_title_normal));
        image2.setBackgroundResource(R.drawable.main_tab_type_normal);
        button3.setTextColor(getResources().getColor(R.color.main_title_selected));
        image3.setBackgroundResource(R.drawable.main_tab_nutrition_pressed);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
