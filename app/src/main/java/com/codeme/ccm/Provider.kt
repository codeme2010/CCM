package com.codeme.ccm

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri

class Provider : ContentProvider() {

    override fun onCreate() = true

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val queryBuilder = SQLiteQueryBuilder()
        val cursor: Cursor
        var groupBy: String? = null
        when (uriMatcher.match(uri)) {
            DIR_CInfo -> queryBuilder.tables = App.TABLE_CInfo
            DIR_ZhangDan -> queryBuilder.tables = App.TABLE_ZhangDan
            ITEM_CInfo -> {
                queryBuilder.tables = App.TABLE_CInfo
                queryBuilder.appendWhere("_id=" + uri.lastPathSegment)
            }
            group_CInfo -> {
                queryBuilder.tables = App.TABLE_CInfo
                groupBy = uri.pathSegments[2]
            }
            ITEM_ZhangDan -> {
                queryBuilder.tables = App.TABLE_ZhangDan
                queryBuilder.appendWhere("_id=" + uri.lastPathSegment)
            }
            group_ZhangDan -> {
                queryBuilder.tables = App.TABLE_ZhangDan
                groupBy = uri.pathSegments[2]
            }
            else -> queryBuilder.tables = "(select t1._id,case(strftime('%d','now','localtime')-t1.huankuanri>0)\n" +
                    "when 1 then STRFTIME ('%m-%d',date('now','localtime','start of month','+1 month','+'|| t1.huankuanri ||' day','-1 day')) \n" +
                    "else STRFTIME ('%m-%d',date('now','localtime','start of month','+'|| t1.huankuanri ||' day','-1 day')) end as huankuanriqi,t1.kadaihao, case when t2.huankuane isnull then '无需还款' else round(t2.huankuane,2) end as huankuane, case when t3.yue is null then t3.zonge else round(t3.yue,2) end as yue,t4.mianxiqi\n" +
                    "from CInfo t1 left join\n" +
                    "(SELECT kadaihao,SUM ([shuakae]) as huankuane FROM   [ZhangDan]\n" +
                    "WHERE  [yihuan] = 0 AND [shijian] < (\n" +
                    "SELECT CASE (CAST (([zhangdanri] < [huankuanri]) AS [char]) \n" +
                    "|| CAST ((STRFTIME ('%d', 'now','localtime') - [zhangdanri] > 0) AS [char]) \n" +
                    "|| CAST ((STRFTIME ('%d', 'now','localtime') - [huankuanri] > 0) AS [char])) \n" +
                    "WHEN '110' THEN DATE ('now','localtime', 'start of month', '+' || [zhangdanri] || ' day', '-1 day') \n" +
                    "WHEN '000' THEN DATE ('now','localtime', 'start of month', '+' || [zhangdanri] || ' day', '-1 day', '-1 month') \n" +
                    "WHEN '011' THEN DATE ('now','localtime', 'start of month', '+' || [zhangdanri] || ' day', '-1 day') ELSE DATE ('now','localtime') END\n" +
                    "FROM   [CInfo] where CInfo.kadaihao=ZhangDan.kadaihao)\n" +
                    "GROUP  BY [kadaihao]) t2 on t1.kadaihao=t2.kadaihao left join\n" +
                    "(select c.kadaihao k,gue+line as zonge, t.yue from CInfo c left join\n" +
                    "(select c.kadaihao as k, c.gue+c.line-SUM (z.shuakae) AS yue from CInfo c,Zhangdan z WHERE  z.yihuan = 0 AND k = z.kadaihao GROUP  BY k) t on c.kadaihao=t.k) t3 on t1.kadaihao=t3.k left join\n" +
                    "(select kadaihao, case(CAST (([zhangdanri] < [huankuanri]) AS [char]) \n" +
                    "|| CAST ((STRFTIME ('%d', 'now','localtime') - [zhangdanri] > 0) AS [char]) \n" +
                    "|| CAST ((STRFTIME ('%d', 'now','localtime') - [huankuanri] > 0) AS [char]))\n" +
                    "when '100' then huankuanri-STRFTIME ('%d', 'now','localtime')\n" +
                    "when '011' then julianday(date('now','localtime', 'start of month', '+' || [huankuanri] || ' day', '-1 day', '+2 month'))-julianday(date('now','localtime'))\n" +
                    "else julianday(date('now','localtime', 'start of month', '+' || [huankuanri] || ' day', '-1 day', '+1 month'))-julianday(date('now','localtime')) end as mianxiqi\n" +
                    "from CInfo) t4 on t1.kadaihao=t4.kadaihao\n" +
                    "order by huankuanriqi)"
        }
        cursor = queryBuilder.query(App.db, projection, selection, selectionArgs, groupBy, null, sortOrder)
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri) = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id =
                when (uriMatcher.match(uri)) {
                    DIR_CInfo -> App.db!!.insertOrThrow(App.TABLE_CInfo, null, values)
                    DIR_ZhangDan -> App.db!!.insert(App.TABLE_ZhangDan, null, values)
                    else -> return null
                }
        val newUri = ContentUris.withAppendedId(uri, id)
        context!!.contentResolver.notifyChange(newUri, null)
        return newUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val id = uri.lastPathSegment
        val count =
                when (uriMatcher.match(uri)) {
                    ITEM_CInfo -> App.db!!.delete(App.TABLE_CInfo, "_id=" + id, null)
                    ITEM_ZhangDan -> App.db!!.delete(App.TABLE_ZhangDan, "_id=" + id, null)
                    group_ZhangDan -> App.db!!.delete(App.TABLE_ZhangDan, "kadaihao='$id'", null)
                    else -> return -1
                }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val count: Int
        val id: String
        when (uriMatcher.match(uri)) {
            ITEM_CInfo -> {
                id = uri.lastPathSegment
                count = App.db!!.update(App.TABLE_CInfo, values, "_id=" + id, null)
            }
            ITEM_ZhangDan -> {
                id = uri.lastPathSegment
                count = App.db!!.update(App.TABLE_ZhangDan, values, "_id=" + id, null)
            }
            group_ZhangDan -> {
                id = uri.lastPathSegment
                count = App.db!!.update(App.TABLE_ZhangDan, values, "kadaihao='$id'", null)
            }
            DIR_ZhangDan -> {
                id = selection!!//kadaihao简单传递下拉倒
                count = App.db!!.update(App.TABLE_ZhangDan, values, "kadaihao = '" + id + "' AND [yihuan] = 0 AND [shijian] < (\n" +
                        "SELECT CASE (CAST (([zhangdanri] < [huankuanri]) AS [char]) \n" +
                        "|| CAST ((STRFTIME ('%d', 'now','localtime') - [zhangdanri] > 0) AS [char]) \n" +
                        "|| CAST ((STRFTIME ('%d', 'now','localtime') - [huankuanri] > 0) AS [char])) \n" +
                        "WHEN '110' THEN DATE ('now','localtime', 'start of month', '+' || [zhangdanri] || ' day', '-1 day') \n" +
                        "WHEN '000' THEN DATE ('now','localtime', 'start of month', '+' || [zhangdanri] || ' day', '-1 day', '-1 month') \n" +
                        "WHEN '011' THEN DATE ('now','localtime', 'start of month', '+' || [zhangdanri] || ' day', '-1 day') ELSE DATE ('now','localtime') END\n" +
                        "FROM   [CInfo] where CInfo.kadaihao=ZhangDan.kadaihao)", null)
            }
            else -> return -1
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    companion object {
        private val DIR_CInfo = 0
        private val ITEM_CInfo = 1
        private val group_CInfo = 2
        private val DIR_ZhangDan = 3
        private val ITEM_ZhangDan = 4
        private val group_ZhangDan = 5
        private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(App.AUTHORITY, "CInfo", DIR_CInfo)
            uriMatcher.addURI(App.AUTHORITY, "CInfo/#", ITEM_CInfo)
            uriMatcher.addURI(App.AUTHORITY, "CInfo/group/*", group_CInfo)
            uriMatcher.addURI(App.AUTHORITY, "ZhangDan", DIR_ZhangDan)
            uriMatcher.addURI(App.AUTHORITY, "ZhangDan/#", ITEM_ZhangDan)
            uriMatcher.addURI(App.AUTHORITY, "ZhangDan/group/*", group_ZhangDan)
        }
    }
}
