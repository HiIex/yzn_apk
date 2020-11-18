package com.example.yzn.activity.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_DB_FRIEND = "create table friend("
            + "id text primary key ,"
            + "nickname text,"
            + "remark text,"
            + "time text)";

    //1表示是收到的，0表示是发出去的
    public static final String CREATE_DB_MESSAGE = "create table message("
            + "id text primary key,"
            + "content text,"
            + "time text,"
            + "isRecieve interger)";

    public static final String CREATE_DB_BILL="create table bill("
            + "billID text primary key,"
            + "issureID text,"
            + "productName text,"
            + "price text,"
            + "type integer,"
            + "middleName text,"
            + "currency integer,"
            + "detail text)";

    public static final int TABLE_FRIEND=1;
    public static final int TABLE_MESSAGE=2;
    public static final int TABLE_BILL=3;

    private Context mContext;
    private int type;//1为phonebook，2为message,3为bill

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,int type) {
        super(context, name, factory, version);
        mContext = context;
        this.type=type;
    }

    //当执行getWritableDatabase或getReadableDatabase时，若数据库不存在，则只执行一次
    @Override
    public void onCreate(SQLiteDatabase db) {
        if(type==TABLE_FRIEND){
            db.execSQL(CREATE_DB_FRIEND);
            //Toast.makeText(mContext, "打开联系人数据库成功！", Toast.LENGTH_SHORT).show();
        }else if(type== TABLE_MESSAGE){
            db.execSQL(CREATE_DB_MESSAGE);
            //Toast.makeText(mContext, "打开短信数据库成功！", Toast.LENGTH_SHORT).show();
        }else if(type==TABLE_BILL){
            db.execSQL(CREATE_DB_BILL);
        }else{
            Toast.makeText(mContext, "发生未知错误！", Toast.LENGTH_SHORT).show();
        }

    }

    //当newVersion大于oldVersion时执行
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists friend");
        db.execSQL("drop table if exists message");
        db.execSQL("drop table if exists bill");
        //db.execSQL("drop table if exists Category");
        type=0;
        onCreate(db);
    }
}