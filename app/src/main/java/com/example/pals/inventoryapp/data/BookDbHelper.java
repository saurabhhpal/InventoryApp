package com.example.pals.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.pals.inventoryapp.data.BookContract;

public class BookDbHelper extends SQLiteOpenHelper {

    public final static String LOG_TAG = BookDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private final static String DATABASE_NAME = "book.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;


    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " ("
                + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookContract.BookEntry.COLUMN_PRODUCT_NAME + " TEXT, "
                + BookContract.BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookContract.BookEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + BookContract.BookEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                + BookContract.BookEntry.COLUMN_SUPPLIER_MNO + " INTEGER NOT NULL DEFAULT 0 ); ";

        Log.i("database query" , SQL_CREATE_BOOKS_TABLE);
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
