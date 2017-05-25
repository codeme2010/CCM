package com.codeme.ccm

import android.app.DatePickerDialog
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.CursorAdapter
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment1.*
import kotlinx.android.synthetic.main.fragment1.view.*
import kotlinx.android.synthetic.main.item_zhangdan.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class fragment1 : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var mMainView: View? = null
    private var adapter: SimpleCursorAdapter? = null
    private var cursor: Cursor? = null
    private var date: Date? = null
    private var kadaihao: Array<String?>? = null
    private val uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainView = activity.layoutInflater.inflate(R.layout.fragment1, activity.container, false)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        /*val a =activity.layoutInflater.inflate(R.layout.fragment1, activity.findViewById(R.id.container)!! as ViewGroup, false).parent as ViewGroup
//        p = mMainView?.parent as ViewGroup
        a.removeAllViewsInLayout()
        return mMainView*/
        return mMainView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uiBindFrom = arrayOf("_id", "shuakae", "kadaihao", "shijian", "feilv", "beizhu")
        val uiBindTo = intArrayOf(R.id.ID1, R.id.tv_shuakae1, R.id.tv_kadaihao1, R.id.tv_shijian1, R.id.tv_feilv1, R.id.tv_beizhu1)
        loaderManager.initLoader(1, null, this)
        adapter = SimpleCursorAdapter(
                context, R.layout.item_zhangdan, null, uiBindFrom, uiBindTo,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        lv1.adapter = adapter
        update()

        val f = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = f.format(java.util.Date())

        et_shijian1.setText(today)
        //et_时间点击显示日期
        et_shijian1.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                try {
                    date = f.parse(year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                et_shijian1.setText(f.format(date))
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
        //应该加一个判断是否有Text，来确定弹出的选项默认值，懒得写了。
        et_kadaihao1.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b)
                AlertDialog.Builder(activity)
                        .setTitle("请选择信用卡代号")
                        .setSingleChoiceItems(kadaihao, 0) { dialog, which ->
                            et_kadaihao1.setText(kadaihao!![which])
                            dialog.dismiss()
                        }
                        .show()
        }

        et_kadaihao1.setOnClickListener {
            AlertDialog.Builder(activity)
                    .setTitle("请选择信用卡代号")
                    .setSingleChoiceItems(kadaihao, 0) { dialog, which ->
                        et_kadaihao1.setText(kadaihao!![which])
                        dialog.dismiss()
                    }
                    .show()
        }

        bt_jiru.setOnClickListener {
            val values = ContentValues()
            values.put("shuakae", et_shuakae1.text.toString())
            values.put("kadaihao", et_kadaihao1.text.toString())
            values.put("shijian", et_shijian1.text.toString())
            values.put("feilv", et_feilv1.text.toString())
            values.put("yihuan", "0")
            values.put("beizhu", et_beizhu1.text.toString())
            val s: String
            if (bt_jiru.text.toString() == "记入") {
                context.contentResolver.insert(App.Uri_ZhangDan, values)
                s = "添加成功"
            } else {
                context.contentResolver.update(uri!!, values, null, null)
                s = "修改成功"
                bt_jiru.text = "记入"
            }
            App.spa.update(0)
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
        }
        lv1.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            val _id = view.ID1.text.toString()
            val p = PopupMenu(context, view.tv_shijian1)
            p.menuInflater.inflate(R.menu.menu1, p.menu)
            p.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.shanchu1 -> context.contentResolver.delete(uri!!, null, null)
                    R.id.xiugai1 -> {
                        view.bt_jiru.text = "修改"
                        val projection = arrayOf("shuakae", "kadaihao", "shijian", "feilv", "beizhu")
                        cursor = context.contentResolver.query(App.Uri_ZhangDan, projection,
                                "_id =" + _id, null, null)
                        cursor!!.moveToFirst()
                        view.et_shuakae1.setText(cursor!!.getString(0))
                        view.et_kadaihao1.setText(cursor!!.getString(1))
                        view.et_shijian1.setText(cursor!!.getString(2))
                        view.et_feilv1.setText(cursor!!.getString(3))
                        view.et_beizhu1.setText(cursor!!.getString(4))
                    }
                }
                update()
                App.spa.update(0)
                true
            }
            p.show()
        }
    }

    fun update() {
        cursor = context.contentResolver.query(App.Uri_CInfo, arrayOf("kadaihao"), null, null, null)
        if (cursor != null) {
            val count = cursor!!.count
            if (count > 0) {
                kadaihao = arrayOfNulls<String>(count)
                cursor!!.moveToFirst()
                kadaihao!![0] = cursor!!.getString(0)
                for (i in 1..cursor!!.count - 1) {
                    if (!cursor!!.isLast) cursor!!.moveToNext()
                    kadaihao!![i] = cursor!!.getString(0)
                }
                if (App.DEBUG) {
                    Toast.makeText(activity, "DEBUG:更新成功", Toast.LENGTH_SHORT).show()
                }
            }
            cursor!!.close()
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf("_id", "shuakae", "kadaihao", "shijian", "feilv", "beizhu")
        val selection = "yihuan=0"
        return CursorLoader(context, App.Uri_ZhangDan, projection, selection, null, "kadaihao asc,shijian desc")
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        adapter!!.swapCursor(data!!)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter!!.swapCursor(null)
    }
}
