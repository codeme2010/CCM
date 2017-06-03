package com.codeme.ccm

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (openDatabase())
            App.db = SQLiteDatabase.openOrCreateDatabase(App.databaseFilename, null)

        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(fragment0())
        fragmentList.add(fragment1())

        val titleList = ArrayList<String>()
        titleList.add("还款明细")
        titleList.add("记账")

        App.spa = SectionsPagerAdapter(supportFragmentManager, fragmentList, titleList)
        container.adapter = App.spa
        tabs.setupWithViewPager(container)
    }

    private fun openDatabase(): Boolean {
        //app.DATABASE_PATH=MainActivity.this.getFilesDir().toString();
        try {
            val dir = File(App.DATABASE_PATH)
            if (!dir.exists())
                dir.mkdir()
            if (!File(App.databaseFilename).exists()) {
                // 获得封装dictionary.db文件的InputStream对象
                val `is` = resources.openRawResource(R.raw.ccm)
                val fos = FileOutputStream(App.databaseFilename)
                val buffer = ByteArray(7168)
                var count: Int = `is`.read(buffer)
                // 开始复制dictionary.db文件
                while (count > 0) {
                    fos.write(buffer, 0, count)
                    count = `is`.read(buffer)
                }
                Toast.makeText(this, "数据库创建成功", Toast.LENGTH_LONG).show()
                fos.close()
                `is`.close()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "数据库创建错误：" + e.message,
                    Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}
