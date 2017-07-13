package com.tonyhu.location.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/4/10.
 */

public class TonyScrollView extends ScrollView {
    private OnScrollListener mListener;
    private int mLastScrollY;

    public TonyScrollView(Context context) {
        super(context);
    }

    public TonyScrollView(Context context, AttributeSet attr){
        super(context,attr);
    }

    public TonyScrollView(Context context, AttributeSet attr,int defStyleAttr){
        super(context,attr,defStyleAttr);
    }

    private TonyHander handler = new TonyHander(TonyScrollView.this);

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(mListener != null) {
            mListener.onScroll(mLastScrollY = this.getScrollY());
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:{
                handler.sendMessageDelayed(Message.obtain(),15);//手指松口时，scrollview可能还在惯性滑动
            }
        }
        return super.onTouchEvent(e);
    }

    public void setOnScrollListener(OnScrollListener listener) {
        this.mListener = listener;
    }

    public  interface OnScrollListener {
        public void onScroll(int y);
    }

    class TonyHander extends Handler {
        private WeakReference<TonyScrollView> mHandlerRefer;

        public TonyHander(TonyScrollView scrollView) {
            this.mHandlerRefer = new WeakReference<TonyScrollView>(scrollView);
        }

        @Override
        public void handleMessage(Message msg) {
            if(mHandlerRefer == null || mHandlerRefer.get() == null){
                return;
            }
            int scrollY = TonyScrollView.this.getScrollY();

            //此时的距离和记录下的距离不相等，再隔15毫秒给handler发送消息，直到scrollview静止下来
            if(mLastScrollY != scrollY){
                mLastScrollY = scrollY;
                handler.sendMessageDelayed(handler.obtainMessage(), 15);
            }
            if(mListener != null){
                mListener.onScroll(scrollY);
            }
        }
    }
}
