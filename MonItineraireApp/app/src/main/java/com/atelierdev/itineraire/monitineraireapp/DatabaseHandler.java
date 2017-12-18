package com.atelierdev.itineraire.monitineraireapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Guillaume on 18/12/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String MONUMENT_KEY = "id";
    public static final String MONUMENT_NAME = "name";
    public static final String MONUMENT_CATEGORY = "category";
    public static final String MONUMENT_TYPE= "type";
    public static final String MONUMENT_ADDRESS = "address";
    public static final String MONUMENT_LAT = "lat";
    public static final String MONUMENT_LONG = "lon";
    public static final String MONUMENT_RATING = "rating";




    public static final String MONUMENT_TABLE_NAME = "Monument";
    public static final String METIER_TABLE_CREATE =
            "CREATE TABLE " + MONUMENT_TABLE_NAME + " (" +
                    MONUMENT_KEY + " INTEGER PRIMARY KEY, " +
                    MONUMENT_NAME + " TEXT, " +
                    MONUMENT_CATEGORY + " INTEGER, " +
                    MONUMENT_TYPE + " TEXT, " +
                    MONUMENT_ADDRESS + " TEXT, " +
                    MONUMENT_LAT + " INTEGER, " +
                    MONUMENT_LONG + " INTEGER, " +
                    MONUMENT_RATING + " INTEGER);";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(METIER_TABLE_CREATE);
    }

    //Code utile en cas de mise à jour de l'application

    public static final String METIER_TABLE_DROP = "DROP TABLE IF EXISTS " + MONUMENT_TABLE_NAME + ";";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(METIER_TABLE_DROP);
        onCreate(db);
    }

}
