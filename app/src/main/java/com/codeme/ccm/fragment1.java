package com.codeme.ccm;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class fragment1 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private View mMainView;
    private SimpleCursorAdapter adapter;
    private Cursor cursor;
    private EditText E_刷卡额;
    private EditText E_卡代号;
    private EditText E_刷卡时间;
    private EditText E_费率;
    private EditText E_备注;
    private Date date;
    private String[] kadaihao;
    private final Uri uri = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mMainView = inflater.inflate(R.layout.fragment1, (ViewGroup) getActivity().findViewById(R.id.container), false);

        E_刷卡额 = (EditText) mMainView.findViewById(R.id.et_刷卡额);
        E_卡代号 = (EditText) mMainView.findViewById(R.id.et_卡代号);
        E_刷卡时间 = (EditText) mMainView.findViewById(R.id.et_时间);
        E_费率 = (EditText) mMainView.findViewById(R.id.et_费率);
        E_备注 = (EditText) mMainView.findViewById(R.id.et_备注);

        final MainActivity m = (MainActivity) getActivity();
        ListView lv = (ListView) mMainView.findViewById(R.id.lv);
        String[] uiBindFrom = {"_id", "shuakae", "kadaihao", "shijian", "feilv", "beizhu"};
        int[] uiBindTo = {R.id.ID, R.id.刷卡额, R.id.卡代号, R.id.时间, R.id.费率, R.id.备注};
        getLoaderManager().initLoader(1, null, this);
        adapter = new SimpleCursorAdapter(
                getContext(), R.layout.item_zhangdan,
                null, uiBindFrom, uiBindTo,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lv.setAdapter(adapter);
        update();

        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = f.format(new java.util.Date());

        E_刷卡时间.setText(today);
        //et_时间点击显示日期
        E_刷卡时间.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        try {
                            date = f.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        E_刷卡时间.setText(f.format(date));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //应该加一个判断是否有Text，来确定弹出的选项默认值，懒得写了。
        E_卡代号.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    new AlertDialog.Builder(getActivity())
                            .setTitle("请选择信用卡代号")
                            .setSingleChoiceItems(kadaihao, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    E_卡代号.setText(kadaihao[which]);
                                    dialog.dismiss();
                                }
                            })
                            .show();
            }
        });

        E_卡代号.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("请选择信用卡代号")
                        .setSingleChoiceItems(kadaihao, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                E_卡代号.setText(kadaihao[which]);
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        final Button bt = (Button) mMainView.findViewById(R.id.bt_记入);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("shuakae", E_刷卡额.getText().toString());
                values.put("kadaihao", E_卡代号.getText().toString());
                values.put("shijian", E_刷卡时间.getText().toString());
                values.put("feilv", E_费率.getText().toString());
                values.put("yihuan", "0");
                values.put("beizhu", E_备注.getText().toString());
                String s;
                if (bt.getText().toString().equals("记入")) {
                    getContext().getContentResolver().insert(App.Uri_ZhangDan, values);
                    s = "添加成功";
                }
                else {
                    getContext().getContentResolver().update(uri,values,null,null);
                    s = "修改成功";
                    bt.setText("记入");
                }
                m.spa.update(0);
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String[] item = {"修改", "删除"};
                final String _id = ((TextView)view.findViewById(R.id.ID)).getText().toString();
                PopupMenu p = new PopupMenu(getContext(),view.findViewById(R.id.时间));
                p.getMenuInflater().inflate(R.menu.menu1,p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.shanchu1:
                                getContext().getContentResolver().delete(uri,null,null);
                                break;
                            case R.id.xiugai1:
                                bt.setText("修改");
                                String[] projection = {"shuakae","kadaihao","shijian","feilv","beizhu"};
                                cursor = getContext().getContentResolver().query(App.Uri_ZhangDan, projection,
                                        "_id =" + _id, null, null);
                                cursor.moveToFirst();
                                E_刷卡额.setText(cursor.getString(0));
                                E_卡代号.setText(cursor.getString(1));
                                E_刷卡时间.setText(cursor.getString(2));
                                E_费率.setText(cursor.getString(3));
                                E_备注.setText(cursor.getString(4));
                                break;
                        }
                        update();
                        m.spa.update(0);
                        return true;
                    }
                });
                p.show();
                /*new AlertDialog.Builder(getActivity())
                        .setTitle("请选择对ID" + _id + "的操作")
                        .setSingleChoiceItems(item, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uri = Uri.withAppendedPath(App.Uri_ZhangDan,_id);
                                if (which == 1){
                                    getContext().getContentResolver().delete(uri,null,null);
                                }
                                else{
                                    bt.setText("修改");
                                    String[] projection = {"shuakae","kadaihao","shijian","feilv","beizhu"};
                                    cursor = getContext().getContentResolver().query(App.Uri_ZhangDan, projection,
                                            "_id =" + _id, null, null);
                                    cursor.moveToFirst();
                                    E_刷卡额.setText(cursor.getString(0));
                                    E_卡代号.setText(cursor.getString(1));
                                    E_刷卡时间.setText(cursor.getString(2));
                                    E_费率.setText(cursor.getString(3));
                                    E_备注.setText(cursor.getString(4));
                                }
                                update();
                                m.spa.update(0);
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();*/
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        ViewGroup p = (ViewGroup) mMainView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mMainView;
    }

    public void update() {
        cursor = getContext().getContentResolver().query(App.Uri_CInfo, new String[]{"kadaihao"}, null, null, null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                kadaihao = new String[count];
                cursor.moveToFirst();
                kadaihao[0] = cursor.getString(0);
                for (int i = 1; i < cursor.getCount(); i++) {
                    if (!cursor.isLast()) cursor.moveToNext();
                    kadaihao[i] = cursor.getString(0);
                }
                if (App.DEBUG){
                    Toast.makeText(getActivity(), "DEBUG:更新成功", Toast.LENGTH_SHORT).show();
                }
            }
            cursor.close();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {"_id", "shuakae", "kadaihao", "shijian", "feilv", "beizhu"};
        String selection = "yihuan=0";
        return new CursorLoader(getContext(), App.Uri_ZhangDan, projection, selection, null, "kadaihao asc,shijian desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
