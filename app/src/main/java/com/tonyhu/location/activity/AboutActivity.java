
package com.tonyhu.location.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonyhu.location.R;
import com.tonyhu.location.util.ScreenUtil;
import com.tonyhu.location.widget.ScrollTextView;


/*Created by Tony on 2017/3/29.
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {
    private ScrollTextView scrollTextView;
    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_about);
        getVersion();
        findViewById(R.id.reward).setOnClickListener(this);

        scrollTextView = (ScrollTextView)findViewById(R.id.thanks);
        scrollTextView.setScrollText((String)getResources().getText(R.string.thanks_content));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(60));
        params.leftMargin = ScreenUtil.dip2px(15);
        params.rightMargin = ScreenUtil.dip2px(15);
        scrollTextView.setLayoutParams(params);
        scrollTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                scrollTextView.updateScrollStatus();
            }
        });
    }

    private void getVersion() {
        try {
            PackageInfo pkg = getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);

            String appName = pkg.applicationInfo.loadLabel(getPackageManager()).toString();
            String versionName = pkg.versionName;
            ((TextView)findViewById(R.id.about_version)).setText(appName + " v" + versionName);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this,RewardActivity.class));
    }
}
