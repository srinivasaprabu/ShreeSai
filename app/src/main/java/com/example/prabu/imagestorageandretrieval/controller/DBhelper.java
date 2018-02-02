package com.example.prabu.imagestorageandretrieval.controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 29/01/2018.
 */

public class DBhelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "sqliteimage.db";
    public static final int DB_VERSION = 1;

    public static final String COMMA_SEP = ",";
    public static final String TEXT_TYPE = " TEXT";
    public static final String NUMERIC_TYPE = " NUMERIC";

    public static final String TABLE_NAME = "image";
    public static final String COLUMN_ID = "cusId";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MOBILE = "mobile";
    public static final String COLUMN_MODEL = "model";
    public static final String COLUMN_COMPLAINT = "complaint";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_STATUS = "status";

    public static final String COLUMN_DATE = "date";
    // public static final String COLUMN_DESCRIPTION = "description";
   // public static final String PRIMARY_KEY = "PRIMARY KEY (" + COLUMN_NAME + "," + COLUMN_DATE + ")";

    private static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
            COLUMN_MOBILE + TEXT_TYPE + COMMA_SEP +
            COLUMN_MODEL + TEXT_TYPE + COMMA_SEP +
            COLUMN_COMPLAINT + TEXT_TYPE + COMMA_SEP +
            COLUMN_AMOUNT + TEXT_TYPE + COMMA_SEP +
            COLUMN_STATUS + TEXT_TYPE + COMMA_SEP +
            COLUMN_DATE + TEXT_TYPE + COMMA_SEP +
            COLUMN_PATH + TEXT_TYPE +")";

    public DBhelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }
}
