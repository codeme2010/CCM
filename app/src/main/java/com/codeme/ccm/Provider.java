package com.codeme.ccm;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

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
                break;
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
            case DIR_CInfo:
                count = App.db.delete(App.TABLE_CInfo, "_id=" + id, null);
                break;
            case DIR_ZhangDan:
                count = App.db.delete(App.TABLE_ZhangDan, "_id=" + id, null);
                break;
            default:
                return -1;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String id = uri.getLastPathSegment();
        int count;
        switch (uriMatcher.match(uri)) {
            case DIR_CInfo:
                count = App.db.update(App.TABLE_CInfo, values, "_id=" + id, null);
                break;
            case DIR_ZhangDan:
                count = App.db.update(App.TABLE_ZhangDan, values, "_id=" + id, null);
                break;
            default:
                return -1;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
