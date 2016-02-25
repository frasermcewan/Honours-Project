package com.example.mcewans_lager.honoursproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class DBHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Location.db";
    public static final String TABLE_PRODUCTS = "Locations";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LOCATIONNAME = "locationName";
    public static final String COLUMN_LATLNG = "latLng";
    public static final String COLUMN_WIFI = "WIFI";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LOCATIONNAME + " TEXT " +
                COLUMN_LATLNG + "TEXT" +
                COLUMN_WIFI + "TEXT" +
                ");";
        db.execSQL(query);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);

        onCreate(db);
    }

     public void addLocation(Locations locations) {
         ContentValues values = new ContentValues();
         values.put(COLUMN_LOCATIONNAME, locations.getLocationName());
         values.put(COLUMN_LATLNG,locations.getGPS());
         values.put(COLUMN_WIFI,locations.getWifi());
         SQLiteDatabase db = getWritableDatabase();
         db.insert(TABLE_PRODUCTS, null, values);
         db.close();
     }

    public void deleteLocation(String LocationName) {
         SQLiteDatabase db = getWritableDatabase();
         db.execSQL("DELETE FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_LOCATIONNAME + "=\"" + LocationName + "\";");
         }


    public Locations returnLocation() {

        ArrayList <Locations> holder = new ArrayList<>();
        Locations holderLocation = null;
        String locationHolder = "";
        String WifiHolder="";
        String GPSHolder ="";

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();


        while (!c.isAfterLast()) {
             if (c.getString(c.getColumnIndex("locationName")) != null) {
                 locationHolder += c.getString(c.getColumnIndex("locationName"));
                 WifiHolder += c.getString(c.getColumnIndex("WIFI"));
                 GPSHolder += c.getString(c.getColumnIndex("latLng"));
                 locationHolder += "\n";
                }
            c.moveToNext();
         }
        db.close();

        return holderLocation;

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}