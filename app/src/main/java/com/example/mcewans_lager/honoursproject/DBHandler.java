package com.example.mcewans_lager.honoursproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class DBHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Signature.db";
    public static final String TABLE_PRODUCTS = "Signatures";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LOCATIONNAME = "locationName";
    public static final String COLUMN_LATLNG = "latLng";
    public static final String COLUMN_WIFI = "WIFI";


    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LOCATIONNAME + " TEXT " +
//                COLUMN_LATLNG + "TEXT" +
//                COLUMN_WIFI + "TEXT" +
                ");";
        db.execSQL(query);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);

        onCreate(db);
    }

    public void addLocation(Signatures signatures) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOCATIONNAME, signatures.getLocationName());
//        values.put(COLUMN_LATLNG, signatures.getGPS());
//        values.put(COLUMN_WIFI, signatures.getWifi());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    public void deleteLocation(String LocationName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_LOCATIONNAME + "=\"" + LocationName + "\";");
    }


    public ArrayList returnLocation() {

        ArrayList <Signatures> holder = new ArrayList<>();


        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();


        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("locationName")) != null) {
                Signatures siggy = cursorToSignitures(c);
                holder.add(siggy);


            }
            c.moveToNext();
        }
        c.close();
        db.close();

        return holder;

    }




    private Signatures cursorToSignitures(Cursor cursor) {
        Signatures sig = new Signatures();
        sig.setID(cursor.getInt(0));
        sig.setLocationName(cursor.getString(2));
        sig.setGPS(cursor.getString(3));
        sig.setWIFI(cursor.getString(4));
        return sig;
    }





    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public String databaseToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";

        //Cursor points to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        //Position after the last row means the end of the results
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("locationName")) != null) {
                dbString += c.getString(c.getColumnIndex("locationName"));
                dbString += "\n";
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }


    }