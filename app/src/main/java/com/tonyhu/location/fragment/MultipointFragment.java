package com.tonyhu.location.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyhu.location.R;


public class MultipointFragment extends Fragment {
    private View rootView;

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

    }


}
