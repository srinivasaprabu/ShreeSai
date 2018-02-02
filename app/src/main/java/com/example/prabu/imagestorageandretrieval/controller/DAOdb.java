package com.example.prabu.imagestorageandretrieval.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prabu.imagestorageandretrieval.model.MyImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 29/01/2018.
 */

public class DAOdb {
    private SQLiteDatabase database;
    private DBhelper dbHelper;

    public DAOdb(Context context) {
        dbHelper = new DBhelper(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * close any database object
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * insert a text report item to the location database table
     *
     * @param image
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long addImage(MyImage image) {
        ContentValues cv = new ContentValues();
        // cv.put(DBhelper.COLUMN_ID, image.getCusId());
        cv.put(DBhelper.COLUMN_NAME, image.getName());
        cv.put(DBhelper.COLUMN_MOBILE, image.getMobile());
        cv.put(DBhelper.COLUMN_MODEL, image.getModel());
        cv.put(DBhelper.COLUMN_COMPLAINT, image.getComplaint());
        cv.put(DBhelper.COLUMN_AMOUNT, image.getAmount());
        cv.put(DBhelper.COLUMN_STATUS, image.getStatus());
        cv.put(DBhelper.COLUMN_DATE, image.getDate());
        cv.put(DBhelper.COLUMN_PATH, image.getPath());
        long insertid = database.insert(DBhelper.TABLE_NAME, null, cv);
        image.setCusId(insertid);
        return insertid;
    }

    private static final String[] allColumns = {
            DBhelper.COLUMN_ID,
            DBhelper.COLUMN_NAME,
            DBhelper.COLUMN_MOBILE,
            DBhelper.COLUMN_MODEL,
            DBhelper.COLUMN_COMPLAINT,
            DBhelper.COLUMN_AMOUNT,
            DBhelper.COLUMN_STATUS,
            DBhelper.COLUMN_DATE,
            DBhelper.COLUMN_PATH


    };

    /**
     * delete the given image from database
     *
     * @param image
     */
    public void deleteImage(MyImage image) {
        String whereClause =
                DBhelper.COLUMN_NAME + "=? AND " + DBhelper.COLUMN_DATE +
                        "=?";
        String[] whereArgs = new String[]{image.getName(),
                String.valueOf(image.getDate())};
        database.delete(DBhelper.TABLE_NAME, whereClause, whereArgs);
    }

    /**
     * @return all image as a List
     */
    public List<MyImage> getImages() {
        List<MyImage> MyImages = new ArrayList<>();
        Cursor cursor =
                database.query(DBhelper.TABLE_NAME, null, null, null, null,
                        null, DBhelper.COLUMN_DATE + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MyImage MyImage = cursorToMyImage(cursor);
            MyImages.add(MyImage);
            cursor.moveToNext();
        }
        cursor.close();
        return MyImages;
    }

    public MyImage getCustomer(long id) {

        Cursor cursor = database.query(DBhelper.TABLE_NAME, allColumns, DBhelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MyImage e = new MyImage(Long.parseLong(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
        // return Employee
        return e;
    }

    public int updateCustomer(MyImage customer) {
        ContentValues values = new ContentValues();
        values.put(DBhelper.COLUMN_NAME, customer.getName());
        values.put(DBhelper.COLUMN_MOBILE, customer.getMobile());
        values.put(DBhelper.COLUMN_MODEL, customer.getModel());
        values.put(DBhelper.COLUMN_COMPLAINT, customer.getComplaint());
        values.put(DBhelper.COLUMN_AMOUNT, customer.getAmount());
        values.put(DBhelper.COLUMN_STATUS, customer.getStatus());
        values.put(DBhelper.COLUMN_DATE, customer.getDate());
        values.put(DBhelper.COLUMN_PATH, customer.getPath());
        System.err.print("id=" + customer.getCusId() + "  " + values.toString());
        // updating row
        return database.update(DBhelper.TABLE_NAME, values, DBhelper.COLUMN_ID + "=" + customer.getCusId(), null);
    }

    // Deleting Employee
    public void removeCustomer(MyImage customer) {

        database.delete(dbHelper.TABLE_NAME, dbHelper.COLUMN_ID + "=" + customer.getCusId(), null);
    }

    /**
     * read the cursor row and convert the row to a MyImage object
     *
     * @param cursor
     * @return MyImage object
     */
    private MyImage cursorToMyImage(Cursor cursor) {
        MyImage image = new MyImage();
        image.setCusId(
                cursor.getLong(cursor.getColumnIndex(DBhelper.COLUMN_ID)));
        image.setName(
                cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_NAME)));
        image.setMobile(cursor.getString(
                cursor.getColumnIndex(DBhelper.COLUMN_MOBILE)));

        image.setModel(cursor.getString(
                cursor.getColumnIndex(DBhelper.COLUMN_MODEL)));

        image.setComplaint(cursor.getString(
                cursor.getColumnIndex(DBhelper.COLUMN_COMPLAINT)));

        image.setAmount(cursor.getString(
                cursor.getColumnIndex(DBhelper.COLUMN_AMOUNT)));

        image.setStatus(cursor.getString(
                cursor.getColumnIndex(DBhelper.COLUMN_STATUS)));

        image.setDate(cursor.getString(
                cursor.getColumnIndex(DBhelper.COLUMN_DATE)));
        image.setPath(
                cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_PATH)));

        return image;
    }
    public Cursor getuser() {
        //SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = database.rawQuery("select * from " + DBhelper.TABLE_NAME + " ",
                null);
        return res;
    }
}
