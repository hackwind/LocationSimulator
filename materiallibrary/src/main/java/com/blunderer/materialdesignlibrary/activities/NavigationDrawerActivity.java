package com.blunderer.materialdesignlibrary.activities;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.blunderer.materialdesignlibrary.R;
import com.blunderer.materialdesignlibrary.adapters.NavigationDrawerAdapter;
import com.blunderer.materialdesignlibrary.adapters.NavigationDrawerBottomAdapter;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerBottomHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerTopHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationMainContentHandler;
import com.blunderer.materialdesignlibrary.models.NavigationDrawerItem;
import com.blunderer.materialdesignlibrary.models.NavigationDrawerItemBottom;
import com.blunderer.materialdesignlibrary.models.NavigationDrawerItemNormal;

import java.util.ArrayList;
import java.util.List;

public abstract class NavigationDrawerActivity extends AppCompatActivity {

    protected DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    protected View mDrawerLeft;
//    private TextView mTip;
    private TextView mMotto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        if(getMenuItemClickListener() != null) {
            toolbar.findViewById(R.id.expanded_menu).setOnClickListener(getMenuItemClickListener());
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        mTip = (TextView) findViewById(R.id.drawer_tip_text);
//        mMotto = (TextView) findViewById(R.id.life_motto);

        mDrawerLeft = findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                NavigationDrawerTopHandler navigationDrawerTopHandler = getNavigationDrawerTopHandler();
//                if (navigationDrawerTopHandler != null && navigationDrawerTopHandler.getNavigationDrawerTopTip() != null
//                        && navigationDrawerTopHandler.getNavigationDrawerTopMotto() != null) {
//                    mTip.setText(navigationDrawerTopHandler.getNavigationDrawerTopTip());
//                    mMotto.setText(navigationDrawerTopHandler.getNavigationDrawerTopMotto());
//                }
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        List<NavigationDrawerItemBottom> navigationDrawerItemsBottom;
        NavigationDrawerBottomHandler navigationDrawerBottomHandler = getNavigationDrawerBottomHandler();
        if (navigationDrawerBottomHandler == null || navigationDrawerBottomHandler.getNavigationDrawerBottomItems() == null)
            navigationDrawerItemsBottom = new ArrayList<>();
        else
            navigationDrawerItemsBottom = navigationDrawerBottomHandler.getNavigationDrawerBottomItems();

        NavigationDrawerBottomAdapter bottomAdapter = new NavigationDrawerBottomAdapter(this, R.layout.navigation_drawer_row, navigationDrawerItemsBottom);
        final ListView mDrawerBottomListView = (ListView) findViewById(R.id.left_drawer_bottom_listview);
        mDrawerBottomListView.setAdapter(bottomAdapter);
        mDrawerBottomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDrawerLayout.closeDrawer(findViewById(R.id.left_drawer));
                View.OnClickListener onClickListener = ((NavigationDrawerItemBottom) adapterView.getAdapter().getItem(i)).getOnClickListener();
                if (onClickListener != null)
                    onClickListener.onClick(view);
            }

        });
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            mDrawerBottomListView.setBackgroundResource(R.drawable.navigation_drawer_bottom_shadow);

        initFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void initFragment() {
        if(getNavigationMainContentHandler() == null || getNavigationMainContentHandler().getNavigationMainContent() == null) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, getNavigationMainContentHandler().getNavigationMainContent()).commit();
    }


    protected abstract NavigationDrawerTopHandler getNavigationDrawerTopHandler();

    protected abstract NavigationDrawerBottomHandler getNavigationDrawerBottomHandler();

    protected abstract NavigationMainContentHandler getNavigationMainContentHandler();

    protected abstract View.OnClickListener getMenuItemClickListener();
}
