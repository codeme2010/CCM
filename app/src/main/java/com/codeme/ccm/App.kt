package com.codeme.ccm

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

class App : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        val DATABASE_PATH = System.getenv("EXTERNAL_STORAGE") + "/ccm/"
        val TABLE_CInfo = "CInfo"
        val TABLE_ZhangDan = "ZhangDan"
        val databaseFilename = DATABASE_PATH + "ccm.db"
        val AUTHORITY = "com.codeme.ccm.Provider"
        val Uri_CInfo = Uri.parse("content://$AUTHORITY/CInfo")!!
        val Uri_ZhangDan = Uri.parse("content://$AUTHORITY/ZhangDan")!!
        val Uri_huankuan = Uri.parse("content://$AUTHORITY/ZhangDan+CInfo")!!
        var db: SQLiteDatabase? = null
        val DEBUG = false
        lateinit var spa: SectionsPagerAdapter
    }

}
