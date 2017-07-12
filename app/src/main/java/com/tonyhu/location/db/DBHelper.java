package com.tonyhu.location.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.tonyhu.location.R;
import com.tonyhu.location.TonyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/5.
 */

public class DBHelper  {
    private static  String DATABASE_NAME = "lovefood";
    private static int DATABASE_VERSION = 1;

    private static DBHelper mInstance;
    private HashMap<String,Dao> daos = new HashMap<>();
    private AndroidConnectionSource connectionSource;
    private String DB_PATH;

    public DBHelper(Context context) {
        List<String> favorites = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= 4.2) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        File dir = new File(DB_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, DATABASE_NAME);
        if (!file.exists()) {
            try {
                copyFile(context, file, R.raw.lovefood);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getPath(), null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

            if(db.getVersion() < DATABASE_VERSION) {//数据库升级
                try {
                    Cursor cursor = db.query("cuisine",new String[]{"name"},"is_favorite=?",new String[]{"1"},null,null,null);
                    String name = null;
                    while(cursor.moveToNext()) {
                        name = cursor.getString(0);
                        favorites.add(name);
                    }
                    cursor.close();
                } catch (Exception e) {e.printStackTrace();}
                try {
                    db.close();
                    file.deleteOnExit();
                    copyFile(context, file, R.raw.lovefood);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getPath(), null,
                SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        db.setVersion(DATABASE_VERSION);
        if(favorites.size() > 0) {
            try {
                ContentValues values = new ContentValues();
                values.put("is_favorite","1");
                db.beginTransaction();
                for(String name : favorites) {
                    db.update("cuisine", values, "name=?", new String[]{name});
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (Exception e){e.printStackTrace();}
        }
        connectionSource = new AndroidConnectionSource(db);
    }


    public static void copyFile(Context context, File file, int id)
            throws IOException {
        InputStream is = context.getResources().openRawResource(id);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = is.read(buffer)) > 0) {
            fos.write(buffer, 0, count);
        }
        fos.close();
        is.close();
    }

    public static synchronized DBHelper getHelper() {
        synchronized (DBHelper.class) {
            if(mInstance == null) {
                mInstance = new DBHelper(TonyApplication.getContext());
            }
            return mInstance;
        }
    }

    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        if (connectionSource != null) {
            return DaoManager.createDao(connectionSource, clazz);
        }
        return null;
    }




}
