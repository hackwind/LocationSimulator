
package com.tonyhu.location.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tonyhu.location.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/*Created by Tony on 2017/3/29.
 */
public class RewardActivity extends BaseActivity  {
    private TextView onlyOneText;
    private Button saveBtn;

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_reward);

        initView();
    }

    private void initView() {
        onlyOneText = (TextView)findViewById(R.id.only_one);
        onlyOneText.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        onlyOneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.help_container).setVisibility(View.VISIBLE);
            }
        });

        saveBtn = (Button)findViewById(R.id.btn_save_qrcode);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.qrcode_for_reward);
                boolean result = saveImageToGallery(RewardActivity.this,bitmap);
                if(result) {
                    Toast.makeText(RewardActivity.this,"保存二维码成功",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean saveImageToGallery(Context context, Bitmap bmp) {
        boolean result = true;
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "DCIM");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = getResources().getString(R.string.app_name) + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result = false;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result = false;
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        return result;
    }

}
