package com.codeme.ccm;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class Provider extends ContentProvider {
    static final int DIR_CInfo = 0;
    static final int ITEM_CInfo = 1;
    static final int group_CInfo = 2;
    static final int DIR_ZhangDan = 3;
    static final int ITEM_ZhangDan = 4;
    static final int group_ZhangDan = 5;
    static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(App.AUTHORITY, "CInfo", DIR_CInfo);
        uriMatcher.addURI(App.AUTHORITY, "CInfo/#", ITEM_CInfo);
        uriMatcher.addURI(App.AUTHORITY, "CInfo/group/*", group_CInfo);
        uriMatcher.addURI(App.AUTHORITY, "ZhangDan", DIR_ZhangDan);
        uriMatcher.addURI(App.AUTHORITY, "ZhangDan/#", ITEM_ZhangDan);
        uriMatcher.addURI(App.AUTHORITY, "ZhangDan/group/*", group_ZhangDan);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Cursor cursor;
        String groupBy = null;
        switch (uriMatcher.match(uri)) {
            case DIR_CInfo:
                queryBuilder.setTables(App.TABLE_CInfo);
                break;
            case DIR_ZhangDan:
                queryBuilder.setTables(App.TABLE_ZhangDan);
                break;
            case ITEM_CInfo:
                queryBuilder.setTables(App.TABLE_CInfo);
                queryBuilder.appendWhere("_id=" + uri.getLastPathSegment());
                break;
            case group_CInfo:
                queryBuilder.setTables(App.TABLE_CInfo);
                groupBy = uri.getPathSegments().get(2);
                break;
            case ITEM_ZhangDan:
                queryBuilder.setTables(App.TABLE_ZhangDan);
                queryBuilder.appendWhere("_id=" + uri.getLastPathSegment());
                break;
            case group_ZhangDan:
                queryBuilder.setTables(App.TABLE_ZhangDan);
                groupBy = uri.getPathSegments().get(2);
                break;
            default:
                queryBuilder.setTables(
                        "(select t1._id,case(strftime('%d','now')-t1.huankuanri>0)\n" +
                                "when 1 then STRFTIME ('%m-%d',date('now','start of month','+1 month','+'|| t1.huankuanri ||' day','-1 day')) \n" +
                                "else STRFTIME ('%m-%d',date('now','start of month','+'|| t1.huankuanri ||' day','-1 day')) end as huankuanriqi,t1.kadaihao, case when t2.huankuane isnull then '无需还款' else t2.huankuane end as huankuane, case when t3.yue is null then t3.zonge else t3.yue end as yue,t4.mianxiqi\n" +
                                "from CInfo t1 left join\n" +
                                "(SELECT kadaihao,SUM ([shuakae]) as huankuane FROM   [ZhangDan]\n" +
                                "WHERE  [yihuan] = 0 AND [shijian] < (\n" +
                                "SELECT CASE (CAST (([zhangdanri] < [huankuanri]) AS [char]) \n" +
                                "|| CAST ((STRFTIME ('%d', 'now') - [zhangdanri] >= 0) AS [char]) \n" +
                                "|| CAST ((STRFTIME ('%d', 'now') - [huankuanri] >= 0) AS [char])) \n" +
                                "WHEN '110' THEN DATE ('now', 'start of month', '+' || [zhangdanri] || ' day', '-1 day') \n" +
                                "WHEN '000' THEN DATE ('now', 'start of month', '+' || [zhangdanri] || ' day', '-1 day', '-1 month') \n" +
                                "WHEN '011' THEN DATE ('now', 'start of month', '+' || [zhangdanri] || ' day', '-1 day') ELSE DATE ('now') END\n" +
                                "FROM   [CInfo] where CInfo.kadaihao=ZhangDan.kadaihao)\n" +
                                "GROUP  BY [kadaihao]) t2 on t1.kadaihao=t2.kadaihao left join\n" +
                                "(select c.kadaihao k,gue+line as zonge, t.yue from CInfo c left join\n" +
                                "(select c.kadaihao as k, c.gue+c.line-SUM (z.shuakae) AS yue from CInfo c,Zhangdan z WHERE  z.yihuan = 0 AND k = z.kadaihao GROUP  BY k) t on c.kadaihao=t.k) t3 on t1.kadaihao=t3.k left join\n" +
                                "(select kadaihao, case(\n" +
                                "cast((huankuanri-STRFTIME ('%d', 'now')>=0) as char)\n" +
                                "|| cast((zhangdanri-STRFTIME ('%d', 'now')>=0) as char))\n" +
                                "when '11' then huankuanri-STRFTIME ('%d', 'now')\n" +
                                "when '00' then julianday(date('now', 'start of month', '+' || [huankuanri] || ' day', '-1 day', '+2 month'))-julianday(date('now'))\n" +
                                "else julianday(date('now', 'start of month', '+' || [huankuanri] || ' day', '-1 day', '+1 month'))-julianday(date('now')) end as mianxiqi\n" +
                                "from CInfo) t4 on t1.kadaihao=t4.kadaihao\n" +
                                "order by huankuanriqi)"
                );
        }
        cursor = queryBuilder.query(App.db, projection, selection, selectionArgs, groupBy, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        switch (uriMatcher.match(uri)) {
            case DIR_CInfo:
                id = App.db.insertOrThrow(App.TABLE_CInfo, null, values);
                break;
            case DIR_ZhangDan:
                id = App.db.insert(App.TABLE_ZhangDan, null, values);
                break;
            default:
                return null;
        }
        Uri newUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String id = uri.getLastPathSegment();
        int count;
        switch (uriMatcher.match(uri)) {
            case ITEM_CInfo:
                count = App.db.delete(App.TABLE_CInfo, "_id=" + id, null);
                break;
            case ITEM_ZhangDan:
                count = App.db.delete(App.TABLE_ZhangDan, "_id=" + id, null);
                break;
            case group_ZhangDan:
                count = App.db.delete(App.TABLE_ZhangDan, "kadaihao='" + id + "'", null);
                break;
            default:
                return -1;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        String id;
        switch (uriMatcher.match(uri)) {
            case ITEM_CInfo:
                id = uri.getLastPathSegment();
                count = App.db.update(App.TABLE_CInfo, values, "_id=" + id, null);
                break;
            case ITEM_ZhangDan:
                id = uri.getLastPathSegment();
                count = App.db.update(App.TABLE_ZhangDan, values, "_id=" + id, null);
                break;
            case group_ZhangDan:
                id = uri.getLastPathSegment();
                count = App.db.update(App.TABLE_ZhangDan, values, "kadaihao='" + id + "'", null);
                break;
            case DIR_ZhangDan:
                id = selection;//kadaihao简单传递下拉倒
                count = App.db.update(App.TABLE_ZhangDan, values, "kadaihao = '" + id + "' AND [yihuan] = 0 AND [shijian] < (\n" +
                        "SELECT CASE (CAST (([zhangdanri] < [huankuanri]) AS [char]) \n" +
                        "|| CAST ((STRFTIME ('%d', 'now') - [zhangdanri] >= 0) AS [char]) \n" +
                        "|| CAST ((STRFTIME ('%d', 'now') - [huankuanri] >= 0) AS [char])) \n" +
                        "WHEN '110' THEN DATE ('now', 'start of month', '+' || [zhangdanri] || ' day', '-1 day') \n" +
                        "WHEN '000' THEN DATE ('now', 'start of month', '+' || [zhangdanri] || ' day', '-1 day', '-1 month') \n" +
                        "WHEN '011' THEN DATE ('now', 'start of month', '+' || [zhangdanri] || ' day', '-1 day') ELSE DATE ('now') END\n" +
                        "FROM   [CInfo] where CInfo.kadaihao=ZhangDan.kadaihao)", null);
                break;
            default:
                return -1;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
