package com.example.mcewans_lager.honoursproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


/**
 * Created by FraserMcEwan on 20/02/16.
 *
 * This class is used to create a database/table and store signatures so the main service can assess
 * what level of scans it has to run and determine if locations have been determined for the users Work
 * and Home
 *
 * A tutorial was followed online for the creation of this class, the code provided by this tutorial is linked here
 * https://thenewboston.com/forum/topic.php?id=3767, it was only used as a rough template, all work is my own
 */

public class DBhandler extends SQLiteOpenHelper {
    private static final String TAG = "Database";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Signature.db";
    public static final String TABLE_PRODUCTS = "Signatures";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LOCATIONNAME = "_locationName";
    public static final String COLUMN_LAT = "_lat";
    public static final String COLUMN_LNG = "_lng";
    public static final String COLUMN_WIFI = "_WIFI";


    public DBhandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        /**
         * This method creates the table should it be required, this will happen when the application
         * is loaded on a new device or when the database updates to a newer version
         */

        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LOCATIONNAME + " TEXT, " +
                COLUMN_LAT + " TEXT, " +
                COLUMN_LNG + " TEXT, " +
                COLUMN_WIFI + " TEXT" +
                ");";
        db.execSQL(query);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**
         * This method checks to see if the database has been upgraded and if it has it then drops and produces a new
         * table
         */
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public void addLocation(Signatures signatures) {
        /**
         * This method adds location objects to the database. It takes in a signature object and then gets
         * the values associated with each field and creates a new element within the database
         */
        Log.i(TAG, "addLocation: ");
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOCATIONNAME, signatures.getLocationName());
        values.put(COLUMN_LAT, signatures.getLat());
        values.put(COLUMN_LNG, signatures.get_lon());
        values.put(COLUMN_WIFI, signatures.getWifi());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    public void methodAccess() {
        /**
         * Allows the main service to drop the table on demand
         */
        SQLiteDatabase db = getWritableDatabase();
        dropTable(db, TABLE_PRODUCTS);

    }

    public void deleteLocation(String LocationName) {
        /**
         * Provides functionality so the main service may remove database elements if required
         */
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_LOCATIONNAME + "=\"" + LocationName + "\";");
    }


    public ArrayList returnAllLocation() {

        /**
         * Returns a list of all locations so the Main Service can examine potential scenarios and start
         * scans if needed
         */

        ArrayList<Signatures> holder = new ArrayList<>();


        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS;

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        while (!c.isAfterLast()) {
            Signatures siggy = cursorToSignitures(c);
            holder.add(siggy);
            Log.i(TAG, "returnAllLocation: " + holder.size());
            c.moveToNext();
        }
        c.close();
        db.close();

        return holder;

    }


//    public Signatures returnHome () {
//        ArrayList <Signatures> holder = new ArrayList<>();
//
//
//        SQLiteDatabase db = getWritableDatabase();
//        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_LOCATIONNAME + "LIKE %Home%'";
//
//        Cursor c = db.rawQuery(query, null);
//
//        c.moveToFirst();
//
//
//        while (!c.isAfterLast()) {
//            if (c.getString(c.getColumnIndex("locationName")) != null) {
//                Signatures siggy = cursorToSignitures(c);
//                holder.add(siggy);
//
//
//            }
//            c.moveToNext();
//        }
//        c.close();
//        db.close();
//
//        return null;
//    }


    private Signatures cursorToSignitures(Cursor cursor) {
        /**
         * Converts database objects back to Signature objects so they can be passed back to Main
         */
        Signatures sig = new Signatures();
        sig.setID(cursor.getInt(0));
        sig.setLocationName(cursor.getString(1));
        sig.setLat(cursor.getString(2));
        sig.setLon(cursor.getString(3));
        sig.setWIFI(cursor.getString(4));
        return sig;
    }


    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void dropTable(SQLiteDatabase db, String tableName) {
        Log.i(TAG, "dropTable:");
        String query = ("DROP TABLE IF EXISTS " + tableName);
        db.execSQL(query);
        onCreate(db);

    }


}