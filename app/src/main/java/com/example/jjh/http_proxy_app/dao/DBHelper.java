package com.example.jjh.http_proxy_app.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jjh.http_proxy_app.entity.Suidao;

public class DBHelper extends SQLiteOpenHelper {

    //数据库版本
    private static final int DATABASE_VERSION=3;

    //数据库名称
    private static final String DATABASE_NAME="sqlitesuidao.db";

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        String CREATE_TABLE_STUDENT="CREATE TABLE "+ Suidao.TABLE+"("
                +Suidao.KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                +Suidao.KEY_cloud_ip+" TEXT, "
                +Suidao.KEY_cloud_port+" INTEGER, "
                +Suidao.KEY_token+" TEXT, "
                +Suidao.KEY_remote_port+" INTEGER, "
                +Suidao.KEY_user_nm + " TEXT)";
        db.execSQL(CREATE_TABLE_STUDENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //如果旧表存在，删除，所以数据将会消失
        db.execSQL("DROP TABLE IF EXISTS "+ Suidao.TABLE);

        //再次创建表
        onCreate(db);
    }
}
