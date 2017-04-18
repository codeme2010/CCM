package com.codeme.ccm;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class fragment0 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    View mMainView;
    ListView lv;
    SimpleCursorAdapter adapter;
    Cursor cursor;
    String selection = null;
    EditText E_卡代号, E_卡号, E_所属行, E_账户, E_固额, E_临额, E_账单日, E_还款日, E_有效期, E_CVV2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mMainView = inflater.inflate(R.layout.fragment0, (ViewGroup) getActivity().findViewById(R.id.container), false);

        E_卡代号 = (EditText) mMainView.findViewById(R.id.et_卡代号);
        E_卡号 = (EditText) mMainView.findViewById(R.id.et_卡号);
        E_所属行 = (EditText) mMainView.findViewById(R.id.et_所属行);
        E_账户 = (EditText) mMainView.findViewById(R.id.et_账户);
        E_固额 = (EditText) mMainView.findViewById(R.id.et_固额);
        E_临额 = (EditText) mMainView.findViewById(R.id.et_临额);
        E_账单日 = (EditText) mMainView.findViewById(R.id.et_账单日);
        E_还款日 = (EditText) mMainView.findViewById(R.id.et_还款日);
        E_有效期 = (EditText) mMainView.findViewById(R.id.et_有效期);
        E_CVV2 = (EditText) mMainView.findViewById(R.id.et_CVV2);

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
        Button bt = (Button) mMainView.findViewById(R.id.bt_录入);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("kadaihao", E_卡代号.getText().toString());
                values.put("kahao", E_卡号.getText().toString());
                values.put("suoshuhang", E_所属行.getText().toString());
                values.put("zhanghu", E_账户.getText().toString());
                values.put("gue", E_固额.getText().toString());
                values.put("line", E_临额.getText().toString());
                values.put("zhangdanri", E_账单日.getText().toString());
                values.put("huankuanri", E_还款日.getText().toString());
                values.put("youxiaoqi", E_有效期.getText().toString());
                values.put("cvv2", E_CVV2.getText().toString());
                getContext().getContentResolver().insert(App.Uri_CInfo, values);
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
/*        cursor = getContext().getContentResolver().query(App.CONTENT_URI,
                new String[]{"sum(round(benjin*piaoli*suodingqi/36500+benjin+hongbao+(case state " +
                        "when 0 then fanxian else 0 end),1)) as zongji"}, "state<>3" + (sch_Key == null ? "" : " and " + sch_Key), null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            tv.setText("待回合计：" + (cursor.getString(0) == null ? "0" : cursor.getString(0)));
            cursor.close();
        }*/
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
