
package com.tonyhu.location.activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.tonyhu.location.R;
import com.tonyhu.location.util.Constants;


/*Created by Tony on 2017/3/29.
 */
public class FeedBackActivity extends BaseActivity  {
    private LinearLayout adView;
    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_feedback);
        adView = (LinearLayout)findViewById(R.id.bannerview);
        loadAd();
    }


    private void loadAd() {
        BannerView bv = new BannerView(this, ADSize.BANNER,
                Constants.GDT_APPID, Constants.GDT_PAGE_FEEDBACK);
        bv.setRefresh(30);// 广告轮播时间 按钮默认关闭
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(int arg0) {
                // 广告加载失败
            }

            @Override
            public void onADReceiv() {
                // 加载广告成功时
            }

            @Override
            public void onADClicked() {
                // 广告点击时
                super.onADClicked();
            }
        });
        adView.addView(bv);
        bv.loadAD();

    }


}
