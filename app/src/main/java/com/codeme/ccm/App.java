package com.codeme.ccm;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.EditText;

import com.shamanland.fonticon.FontIconTypefaceHolder;


public class App extends Application {
    public static final String DATABASE_PATH = System.getenv("EXTERNAL_STORAGE") + "/ccm/";
    public static final String TABLE_CInfo = "CInfo";
    public static final String TABLE_ZhangDan = "ZhangDan";
    public static final String databaseFilename = DATABASE_PATH + "ccm.db";
    public static final String AUTHORITY = "com.codeme.ccm.Provider";
    public static final Uri Uri_CInfo = Uri.parse("content://" + AUTHORITY + "/CInfo");
    public static final Uri Uri_ZhangDan = Uri.parse("content://" + AUTHORITY + "/ZhangDan");
    public static final Uri Uri_huankuan = Uri.parse("content://" + AUTHORITY + "/ZhangDan+CInfo");
    public static SQLiteDatabase db;
    public static final boolean DEBUG = false;
    @Override
    public void onCreate() {
        super.onCreate();
        FontIconTypefaceHolder.init(getAssets(), "fontawesome-webfont.ttf");
    }

    static float str2int(EditText et){
        return et.getText().toString().equals("")?0:Float.parseFloat(et.getText().toString());
    }

}
