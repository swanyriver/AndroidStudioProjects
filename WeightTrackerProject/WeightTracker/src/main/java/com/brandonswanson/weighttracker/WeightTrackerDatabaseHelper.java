package com.brandonswanson.weighttracker;

/**
 * Created by Brandon on 9/11/13.
 */


import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class WeightTrackerDatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME="weight.db";
    private static final int SCHEMA=1;
    static final String TITLE="title";
    static final String VALUE="value";



    //static final strings   !!!!!!!!
    // table names
    // collums



    private static Context mycontext;

    public WeightTrackerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
        mycontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            Log.w("sql", "begining of oncreate database helper");

            db.beginTransaction();
            //db.execSQL("CREATE TABLE weekdays (dow REAL, nameofday TEXT);");
            //db.execSQL("CREATE TABLE tablename (names of collums);");

            ContentValues cv=new ContentValues();



			/*
			cv.put(TITLE, "Gravity, Death Star I");
			cv.put(VALUE, SensorManager.GRAVITY_DEATH_STAR_I);
			db.insert("tablename", TITLE, cv);
			 */


            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        throw new RuntimeException("How did we get here?");
    }
}


