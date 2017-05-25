package com.codeme.ccm

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.CursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.PopupMenu
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment0.*
import kotlinx.android.synthetic.main.item_huankuan.view.*

class fragment0 : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var mMainView: View? = null
    private var adapter: SimpleCursorAdapter? = null
    private var cursor: Cursor? = null
    private var uri: Uri? = null
    private var id: String? = null
    private var kadaihao: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainView = activity.layoutInflater.inflate(R.layout.fragment0, activity.container, false)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = mMainView

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uiBindFrom = arrayOf("_id", "huankuanriqi", "kadaihao", "huankuane", "yue", "mianxiqi")
        val uiBindTo = intArrayOf(R.id.tv_id, R.id.tv_huankuanri, R.id.tv_kadaihao, R.id.tv_huankuane, R.id.tv_yue, R.id.tv_mianxiqi)
        loaderManager.initLoader(0, null, this)
        adapter = SimpleCursorAdapter(
                context, R.layout.item_huankuan, null, uiBindFrom, uiBindTo,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        lv.adapter = adapter
        bt_luru.setOnClickListener {
            val values = ContentValues()
            values.put("kadaihao", (et_kadaihao.text?:"").toString())
            values.put("kahao", (et_kahao.text?:"").toString())
            values.put("suoshuhang", (et_suoshuhang.text?:"").toString())
            values.put("zhanghu", (et_zhanghu.text?:"").toString())
            values.put("gue", (et_gue.text?:"").toString())
            values.put("line", (et_line.text?:"").toString())
            values.put("zhangdanri", (et_zhangdanri.text?:"").toString())
            values.put("huankuanri", (et_huankuanri.text?:"").toString())
            values.put("youxiaoqi", (et_youxiaoqi.text?:"").toString())
            values.put("cvv2", (et_CVV2.text?:"").toString())
            val s: String
            if (bt_luru.text.toString() == "录入") {
                context.contentResolver.insert(App.Uri_CInfo, values)
                s = "录入成功"
            } else {
                uri = Uri.withAppendedPath(App.Uri_CInfo, id)
                context.contentResolver.update(uri!!, values, null, null)
                uri = Uri.withAppendedPath(App.Uri_ZhangDan, "group/" + kadaihao!!)
                values.clear()
                values.put("kadaihao", et_kadaihao.text.toString())
                context.contentResolver.update(uri!!, values, null, null)
                s = "修改成功"
                bt_luru.text = "录入"
            }
            update()
            App.spa.update(1)
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
        }
        lv.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            kadaihao = view.tv_kadaihao.text.toString()
            id = view.tv_id.text.toString()
            val p = PopupMenu(context, view.tv_kadaihao)
            p.menuInflater.inflate(R.menu.menu0, p.menu)
            p.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.huanqing//账单还清
                    -> {
                        val values = ContentValues()
                        values.put("yihuan", "1")//kadaihao简单传递下拉倒
                        if (context.contentResolver.update(App.Uri_ZhangDan, values, kadaihao, null) != -1) {
                            Toast.makeText(activity, kadaihao!! + " 的账单已还清", Toast.LENGTH_SHORT).show()
                        }
                    }
                    R.id.xiugai0//修改卡信息
                    -> {
                        bt_luru.text = "修改"
                        val projection = arrayOf("kadaihao", "kahao", "suoshuhang", "zhanghu", "gue", "line", "zhangdanri", "huankuanri", "youxiaoqi", "cvv2")
                        cursor = context.contentResolver.query(App.Uri_CInfo, projection,
                                "_id =" + id!!, null, null)
                        cursor!!.moveToFirst()
                        et_kadaihao.setText(cursor!!.getString(0))
                        et_kahao.setText(cursor!!.getString(1))
                        et_suoshuhang.setText(cursor!!.getString(2))
                        et_zhanghu.setText(cursor!!.getString(3))
                        et_gue.setText(cursor!!.getString(4))
                        et_line.setText(cursor!!.getString(5))
                        et_zhangdanri.setText(cursor!!.getString(6))
                        et_huankuanri.setText(cursor!!.getString(7))
                        et_youxiaoqi.setText(cursor!!.getString(8))
                        et_CVV2.setText(cursor!!.getString(9))
                    }
                    R.id.shanchu0//删除此卡片
                    -> {
                        uri = Uri.withAppendedPath(App.Uri_CInfo, id)
                        context.contentResolver.delete(uri!!, null, null)
                        //同时删除ZhangDan里的相关信息
                        uri = Uri.withAppendedPath(App.Uri_ZhangDan, "group/" + kadaihao!!)
                        if (context.contentResolver.delete(uri!!, null, null) != -1) {
                            Toast.makeText(activity, "卡片 $kadaihao 删除成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                App.spa.update(1)
                update()
                true
            }
            p.show()
        }
    }

    fun update() {
        loaderManager.restartLoader(0, null, this@fragment0)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf("_id", "huankuanriqi", "kadaihao", "huankuane", "yue", "mianxiqi")
        return CursorLoader(context, App.Uri_huankuan, projection, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        adapter!!.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter!!.swapCursor(null)
    }
}
