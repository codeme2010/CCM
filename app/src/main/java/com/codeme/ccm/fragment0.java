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
    EditText E_卡代号, E_卡号, E_所属行, E_账户, E_固额, E_临额, E_账单日, E_还款日, E_有效期, E_CVV2;
    Cursor cursor;
    Uri uri;
    String id,kadaihao;

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

        final MainActivity m = (MainActivity) getActivity();
        lv = (ListView) mMainView.findViewById(R.id.lv);
        String[] uiBindFrom = {"_id","huankuanriqi","kadaihao","huankuane","yue","mianxiqi"};
        int[] uiBindTo = {R.id.id,R.id.还款日,R.id.卡代号,R.id.还款额,R.id.余额,R.id.免息期};
        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(
                getContext(), R.layout.item_huankuan,
                null, uiBindFrom, uiBindTo,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lv.setAdapter(adapter);
        final Button bt = (Button) mMainView.findViewById(R.id.bt_录入);
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
                String s;
                if (bt.getText().toString().equals("录入")){
                    getContext().getContentResolver().insert(App.Uri_CInfo, values);
                    s = "录入成功";
                }
                else {
                    uri = Uri.withAppendedPath(App.Uri_CInfo,id);
                    getContext().getContentResolver().update(uri,values,null,null);
                    uri = Uri.withAppendedPath(App.Uri_ZhangDan,"group/" + kadaihao);
                    values.clear();
                    values.put("kadaihao", E_卡代号.getText().toString());
                    getContext().getContentResolver().update(uri,values,null,null);
                    s = "修改成功";
                    bt.setText("录入");
                }
                update();
                m.spa.update(1);
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            final String[] item = {"本期账单已还清", "修改卡信息", "删除此卡片"};
            int b;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                kadaihao = ((TextView)view.findViewById(R.id.卡代号)).getText().toString();
                id = ((TextView)view.findViewById(R.id.id)).getText().toString();
                new AlertDialog.Builder(getActivity())
                        .setTitle("请选择对 " + kadaihao + " 的操作")
                        .setSingleChoiceItems(item, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b = which;
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (b){
                                    case 0://账单还清
                                        ContentValues values = new ContentValues();
                                        values.put("yihuan", "1");//kadaihao简单传递下拉倒
                                        if (getContext().getContentResolver().update(App.Uri_ZhangDan,values,kadaihao,null)!=-1) {Toast.makeText(getActivity(), kadaihao + " 的账单已还清", Toast.LENGTH_SHORT).show();}
                                        break;
                                    case 1://修改卡信息
                                        bt.setText("修改");
                                        String[] projection = {"kadaihao","kahao","suoshuhang","zhanghu","gue","line",
                                                "zhangdanri","huankuanri","youxiaoqi","cvv2"};
                                        cursor = getContext().getContentResolver().query(App.Uri_CInfo, projection,
                                                "_id =" + id, null, null);
                                        cursor.moveToFirst();
                                        E_卡代号.setText(cursor.getString(0));
                                        E_卡号.setText(cursor.getString(1));
                                        E_所属行.setText(cursor.getString(2));
                                        E_账户.setText(cursor.getString(3));
                                        E_固额.setText(cursor.getString(4));
                                        E_临额.setText(cursor.getString(5));
                                        E_账单日.setText(cursor.getString(6));
                                        E_还款日.setText(cursor.getString(7));
                                        E_有效期.setText(cursor.getString(8));
                                        E_CVV2.setText(cursor.getString(9));
                                        break;
                                    case 2://删除此卡片
                                        uri = Uri.withAppendedPath(App.Uri_CInfo,id);
                                        getContext().getContentResolver().delete(uri,null,null);
                                        //同时删除ZhangDan里的相关信息
                                        uri = Uri.withAppendedPath(App.Uri_ZhangDan,"group/" + kadaihao);
                                        if (getContext().getContentResolver().delete(uri,null,null)!=-1) {Toast.makeText(getActivity(), "卡片 " + kadaihao + " 删除成功", Toast.LENGTH_SHORT).show();}
                                        break;
                                }
                                m.spa.update(1);
                                update();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
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
        getLoaderManager().restartLoader(0,null,fragment0.this);
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
        String[] projection = {"_id","huankuanriqi","kadaihao","huankuane","yue","mianxiqi"};
        return new CursorLoader(getContext(), App.Uri_huankuan, projection, null, null, null);
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
