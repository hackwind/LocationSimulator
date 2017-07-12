package com.blunderer.materialdesignlibrary.handlers;

import android.support.v4.app.Fragment;

import com.blunderer.materialdesignlibrary.models.NavigationDrawerItem;
import com.blunderer.materialdesignlibrary.models.NavigationDrawerItemHeader;
import com.blunderer.materialdesignlibrary.models.NavigationDrawerItemNormal;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerTopHandler {
    private String tip;
    private String motto;

    public NavigationDrawerTopHandler() {

    }

    public NavigationDrawerTopHandler addSection(String tip,String motto) {
        this.tip = tip;
        this.motto = motto;
        return this;
    }

    public String getNavigationDrawerTopTip() {
        return tip;
    }

    public String getNavigationDrawerTopMotto() {
        return motto;
    }
}
