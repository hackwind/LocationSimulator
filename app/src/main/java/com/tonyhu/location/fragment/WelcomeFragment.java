package com.tonyhu.location.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tonyhu.location.R;
import com.tonyhu.location.activity.MainActivity;

public class WelcomeFragment extends Fragment {

    private static final String ARG_RES = "res";
    private static final String ARG_LAST = "last";

    public WelcomeFragment() {
    }

    public static WelcomeFragment newInstance(int resId,boolean isLast) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_RES, resId);
        bundle.putBoolean(ARG_LAST, isLast);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_fragment,null);
        if (getArguments() == null || getArguments().getInt(ARG_RES) == 0)
            throw new RuntimeException("PD !!!!!!!!!");
        ImageView imgView = (ImageView)view.findViewById(R.id.welcome_image);
        imgView.setImageResource(getArguments().getInt(ARG_RES));
        if(getArguments().getBoolean(ARG_LAST)) {
            view.findViewById(R.id.welcome_enter).setVisibility(View.VISIBLE);
            view.findViewById(R.id.welcome_enter).setOnClickListener(mClickListener);
        } else {
            view.findViewById(R.id.welcome_enter).setVisibility(View.GONE);
        }
        return view;
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(),MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    };
}
