package com.example.jjh.http_proxy_app.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jjh.http_proxy_app.entity.Suidao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SuidaoRepo {
    private DBHelper dbHelper;

    public SuidaoRepo(Context context){
        dbHelper=new DBHelper(context);
    }

    public int insert(Suidao suidao){
        //打开连接，写入数据
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Suidao.KEY_cloud_ip,suidao.cloudIp);
        values.put(Suidao.KEY_cloud_port,suidao.cloudPort);
        values.put(Suidao.KEY_token,suidao.token);
        values.put(Suidao.KEY_remote_port,suidao.remotePort);
        values.put(Suidao.KEY_user_nm,suidao.userNm);
        //
        long student_Id=db.insert(Suidao.TABLE,null,values);
        db.close();
        return (int)student_Id;
    }

    public void delete(int student_Id){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete(Suidao.TABLE,Suidao.KEY_ID+"=?", new String[]{String.valueOf(student_Id)});
        db.close();
    }
    public void update(Suidao suidao){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(Suidao.KEY_cloud_ip,suidao.cloudIp);
        values.put(Suidao.KEY_remote_port,suidao.cloudPort);
        values.put(Suidao.KEY_token,suidao.token);
        values.put(Suidao.KEY_remote_port,suidao.remotePort);
        values.put(Suidao.KEY_user_nm,suidao.userNm);

        db.update(Suidao.TABLE,values,Suidao.KEY_ID+"=?",new String[] { String.valueOf(suidao.suidao_ID) });
        db.close();
    }

    public List<Suidao> getStudentList(){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String selectQuery="SELECT "+
                Suidao.KEY_ID+","+
                Suidao.KEY_cloud_ip+","+
                Suidao.KEY_cloud_port+","+
                Suidao.KEY_token+","+
                Suidao.KEY_remote_port+","+
                Suidao.KEY_user_nm+" FROM "+Suidao.TABLE;
        List<Suidao> studentList=new ArrayList<>();
        Cursor cursor=db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                Suidao suidao = new Suidao();
                suidao.suidao_ID =cursor.getInt(cursor.getColumnIndex(Suidao.KEY_ID));
                suidao.cloudIp =cursor.getString(cursor.getColumnIndex(Suidao.KEY_cloud_ip));
                suidao.cloudPort  =cursor.getInt(cursor.getColumnIndex(Suidao.KEY_cloud_port));
                suidao.token  =cursor.getString(cursor.getColumnIndex(Suidao.KEY_token));
                suidao.remotePort  =cursor.getInt(cursor.getColumnIndex(Suidao.KEY_remote_port));
                suidao.userNm = cursor.getString(cursor.getColumnIndex(Suidao.KEY_user_nm));

//                suidaoMap.put("id",cursor.getString(cursor.getColumnIndex(Suidao.KEY_ID)));
//                suidaoMap.put("cloudIp",cursor.getString(cursor.getColumnIndex(Suidao.KEY_cloud_ip)));
//                suidaoMap.put("cloudPort",cursor.getString(cursor.getColumnIndex(Suidao.KEY_cloud_port)));
//                suidaoMap.put("token",cursor.getString(cursor.getColumnIndex(Suidao.KEY_token)));
//                suidaoMap.put("remotePort",cursor.getString(cursor.getColumnIndex(Suidao.KEY_remote_port)));
                studentList.add(suidao);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return studentList;
    }

    public Suidao getSuidaoById(int Id){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String selectQuery="SELECT "+
                Suidao.KEY_ID + "," +
                Suidao.KEY_cloud_ip + "," +
                Suidao.KEY_cloud_port + "," +
                Suidao.KEY_token + "," +
                Suidao.KEY_remote_port + "," +
                Suidao.KEY_user_nm +
                " FROM " + Suidao.TABLE
                + " WHERE " +
                Suidao.KEY_ID + "=?";
        int iCount=0;
        Suidao student=new Suidao();
        Cursor cursor=db.rawQuery(selectQuery,new String[]{String.valueOf(Id)});
        if(cursor.moveToFirst()){
            do{
                student.suidao_ID =cursor.getInt(cursor.getColumnIndex(Suidao.KEY_ID));
                student.cloudIp =cursor.getString(cursor.getColumnIndex(Suidao.KEY_cloud_ip));
                student.cloudPort  =cursor.getInt(cursor.getColumnIndex(Suidao.KEY_cloud_port));
                student.token  =cursor.getString(cursor.getColumnIndex(Suidao.KEY_token));
                student.remotePort  =cursor.getInt(cursor.getColumnIndex(Suidao.KEY_remote_port));
                student.userNm = cursor.getString(cursor.getColumnIndex(Suidao.KEY_user_nm));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return student;
    }
}
