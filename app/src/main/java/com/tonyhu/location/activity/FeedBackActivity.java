
package com.tonyhu.location.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        initView();
        loadAd();
    }
    private void initView(){
        final TextView tvSearch = (TextView)findViewById(R.id.about_version);
        tvSearch.setText(Html.fromHtml(getResources().getString(R.string.search_gongzhonghao)));
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(getString(R.string.qq_qun));
                Toast.makeText(FeedBackActivity.this, "QQ群号已成功复制", Toast.LENGTH_SHORT).show();
            }
        });
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
