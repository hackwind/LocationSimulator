package com.tonyhu.location.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonyhu.location.R;

/**
 * Created by Administrator on 2017/7/19.
 */

public class SettingUpDialog extends DialogFragment {
    private LinearLayout settingup1;
    private LinearLayout settingup2;
    private TextView result1;
    private TextView result2;

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
        switch (type) {
            case 0:
                result1.setText(R.string.setting_right);
                result1.setTextColor(Color.rgb(153,217,234));

                result2.setText(R.string.setting_right);
                result2.setTextColor(Color.rgb(153,217,234));
                break;
            case 1:
                result1.setText(R.string.fast_setting);
                result1.setTextColor(Color.RED);

                result2.setText(R.string.setting_right);
                result2.setTextColor(Color.rgb(153,217,234));
                break;
            case 2:
                result1.setText(R.string.setting_right);
                result1.setTextColor(Color.rgb(153,217,234));

                result2.setText(R.string.fast_setting);
                result2.setTextColor(Color.RED);
                break;
            case 3:
                result1.setText(R.string.fast_setting);
                result1.setTextColor(Color.RED);

                result2.setText(R.string.fast_setting);
                result2.setTextColor(Color.RED);
                break;
        }

        builder.setView(view);
        return builder.create();
    }
}
