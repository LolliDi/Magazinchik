package com.example.magazinchik;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper  extends SQLiteOpenHelper{

    public static final int db_ver = 13;
    public static final String db_name = "contactDb";

    public static final String tb_contacts = "contacts";
    public static final String KEY_ID = "_id";
    public static final String nazv = "nazv";
    public static final String sprice = "cena";

    public static  final String tbUsers = "Users";
    public static  final String idUser = "_id";
    public static  final String login = "Login";
    public static  final String password = "Password";
    public static  final String role = "Role";

    public DBHelper(Context context) {
        super(context, db_name, null, db_ver);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ tbUsers+"("+idUser+" integer primary key,"+login + " text,"+password+" text,"+role+" REAL);");
        db.execSQL("INSERT INTO "+tbUsers+ " ("+login + ", "+password+", "+role+") VALUES ('admin','admin',1);");
        db.execSQL("create table " + tb_contacts + "("+ KEY_ID
                + " integer primary key," + nazv + " text," + sprice + " INTEGER" + ")");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + tb_contacts + ";");
        db.execSQL("drop table if exists " + tbUsers);
        onCreate(db);
    }
}