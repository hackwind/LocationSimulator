package com.blunderer.materialdesignlibrary.handlers;


import android.support.v4.app.Fragment;
public class NavigationMainContentHandler {

    private Fragment mItem;


    public NavigationMainContentHandler setContent(Fragment fragment) {
        mItem = fragment;
        return this;
    }

    public Fragment getNavigationMainContent() {
        return mItem;
    }

}
