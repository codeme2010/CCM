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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class fragment1 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    View mMainView;
    ListView lv;
    SimpleCursorAdapter adapter;
    Cursor cursor;
    String selection = null;
    EditText E_刷卡额, E_刷卡时间, E_费率, E_备注;
    AutoCompleteTextView E_卡代号;
    Date date;

    final String[] kadaihao = {""};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mMainView = inflater.inflate(R.layout.fragment1, (ViewGroup) getActivity().findViewById(R.id.container), false);

        E_刷卡额 = (EditText) mMainView.findViewById(R.id.et_刷卡额);
        E_卡代号 = (AutoCompleteTextView) mMainView.findViewById(R.id.et_卡代号);
        E_刷卡时间 = (EditText) mMainView.findViewById(R.id.et_时间);
        E_费率 = (EditText) mMainView.findViewById(R.id.et_费率);
        E_备注 = (EditText) mMainView.findViewById(R.id.et_备注);

        lv = (ListView) mMainView.findViewById(R.id.lv);
        /*String[] uiBindFrom = { "_id","pingtai","zhanghu","huikuan","huikuanriqi","state","nianhua",
                "benjin","shijian","beizhu"};
        int[] uiBindTo = { R.id.id,R.id.pingtai,R.id.zhanghu,R.id.huikuan,R.id.huikuanriqi,R.id.state,R.id.nianhua,
                R.id.benjin,R.id.shijian,R.id.beizhu};
        getLoaderManager().initLoader(2, null, this);
        adapter = new SimpleCursorAdapter(
                getContext(), R.layout.item_huankuan,
                null, uiBindFrom, uiBindTo,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lv.setAdapter(adapter);*/
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

        Button bt = (Button) mMainView.findViewById(R.id.bt_记入);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("shuakae", E_刷卡额.getText().toString());
                values.put("kadaihao", E_卡代号.getText().toString());
                values.put("shijian", E_刷卡时间.getText().toString());
                values.put("feilv", E_费率.getText().toString());
                values.put("beizhu", E_备注.getText().toString());
                getContext().getContentResolver().insert(App.Uri_ZhangDan, values);
                Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
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
            cursor.moveToFirst();
            kadaihao[0] = cursor.getString(0);
            for (int i = 1; i < cursor.getCount();i++){
                if (!cursor.isLast())cursor.moveToNext();
                Arrays.asList(kadaihao).add(cursor.getString(0));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, kadaihao);
            E_卡代号.setAdapter(adapter);
            cursor.close();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
/*        String[] projection = {"_id", "pingtai", "zhanghu", "date(shijian,'+'||suodingqi||' day') as huikuanriqi", "state","cast(round(nianhua,0)as int)||'%' as nianhua",
                "'￥'||round(benjin*piaoli*suodingqi/36500+benjin+hongbao+(case state when 0 then fanxian else 0 end),1) as huikuan",
                "'￥'||cast(round(benjin,0)as int)||' + '||cast(round(hongbao,0)as int)||' + '||cast(round(fanxian,0)as int)||' + '||piaoli||'%' as benjin", "shijian||' + '||suodingqi||'天' as shijian", "beizhu"};
        selection = sch_Key==null ? (isC ? "" : "state <> 3") : sch_Key + (isC ? "" : "and state <> 3");
        //selection = isC ? null : "state <> 3";
        return new CursorLoader(getContext(), App.CONTENT_URI, projection, selection, null, " huikuanriqi");*/
        return null;
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
