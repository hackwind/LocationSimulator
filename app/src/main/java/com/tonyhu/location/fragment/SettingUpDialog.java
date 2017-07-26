package com.tonyhu.location.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonyhu.location.R;

/**
 * Created by Administrator on 2017/7/19.
 */

public class SettingUpDialog extends DialogFragment implements View.OnClickListener {
    private LinearLayout settingup1;
    private LinearLayout settingup2;
    private TextView result1;
    private TextView result2;
    private TextView btnIKnown;

    private int type = 0;//0：都设置正确，1：开启虚拟位置设置不正确，关闭位置服务正确；2：关闭位置服务不正确，开启虚拟位置不正确；3:都不正确

    public SettingUpDialog() {
        super();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        type = getArguments().getInt("type");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_setting_dialog, null);
        settingup1 = (LinearLayout) view.findViewById(R.id.layout_setting1);
        settingup2 = (LinearLayout) view.findViewById(R.id.layout_setting2);
        result1 = (TextView)view.findViewById(R.id.result_setting1);
        result2 = (TextView)view.findViewById(R.id.result_setting2);
        btnIKnown = (TextView)view.findViewById(R.id.btn_i_known);
        final String setRight = getResources().getString(R.string.setting_right);
        final String setWrong = getResources().getString(R.string.fast_setting);
        final String setting1 = Build.VERSION.SDK_INT >= 23 ? getResources().getString(R.string.open_mock_app_high) : getResources().getString(R.string.open_mock_app_low);
        final String setting2 = getResources().getString(R.string.close_postion_service);
        switch (type) {
            case 0:
                result1.setText(Html.fromHtml(setting1 + "<font color='#4169E1'>" + setRight + "</font>"));

                result2.setText(Html.fromHtml(setting2 + "<font color='#4169E1'>" + setRight + "</font>"));
                break;
            case 1:
                result1.setText(Html.fromHtml(setting1 + "<font color='#FF0000'>" + setWrong + "</font>"));

                result2.setText(Html.fromHtml(setting2 + "<font color='#4169E1'>" + setRight + "</font>"));
                break;
            case 2:
                result1.setText(Html.fromHtml(setting1 + "<font color='#4169E1'>" + setRight + "</font>"));

                result2.setText(Html.fromHtml(setting2 + "<font color='#FF0000'>" + setWrong + "</font>"));
                break;
            case 3:
                result1.setText(Html.fromHtml(setting1 + "<font color='#FF0000'>" + setWrong + "</font>"));

                result2.setText(Html.fromHtml(setting2 + "<font color='#FF0000'>" + setWrong + "</font>"));
                break;
        }
        result1.setOnClickListener(this);
        result2.setOnClickListener(this);
        btnIKnown.setOnClickListener(this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.result_setting1:
                Intent intent =  new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                startActivity(intent);
                dismiss();
                break;
            case R.id.result_setting2:
                intent =  new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dismiss();
                break;
            case R.id.btn_i_known:
                dismiss();
                break;
        }
    }
}
