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

public class ClickAdDialog extends DialogFragment implements View.OnClickListener {
    private TextView btnLeft;
    private TextView btnRight;

    public void setBtnClickListener(OnDialogButtonClickListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    private OnDialogButtonClickListener btnClickListener;


    public ClickAdDialog() {
        super();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_click_ad_dialog, null);

        btnLeft = (TextView)view.findViewById(R.id.btn_left);
        btnRight = (TextView)view.findViewById(R.id.btn_right);

        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_left:
                if(btnClickListener != null) {
                    btnClickListener.onLeftButtonClick();
                }
                dismiss();
                break;
            case R.id.btn_right:
                if(btnClickListener != null) {
                    btnClickListener.onRightButtonClick();
                }
                dismiss();
                break;
        }
    }

    public interface OnDialogButtonClickListener{
        public void onLeftButtonClick();
        public void onRightButtonClick();
    }
}
