package com.example.androidlab2;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "MessagesDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "MESSAGES";
    public final static String COL_TEXT = "TEXT";
    public final static String COL_OUTGOING = "OUTGOING";
    public final static String COL_ID = "_id";

    public DBOpener(Context ctx){
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TEXT + " text,"
                + COL_OUTGOING  + " INTEGER);");

        System.out.println("in on create");


        int version = db.getVersion();

        Cursor cursor = db.query("MESSAGES", null, null, null, null, null, null);
        printCursor(cursor,version);
        //Log.e("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public static void printCursor(Cursor c, int version){
        Log.e("Print Cursor", "Entering print cursor");
        Log.e("Database version", "Database version is " + version);
        Log.e("Column count", "Column count is " + c.getColumnCount());
        Log.e("Column names", "Column names are: ");
        for(int i = 0; i < c.getColumnCount(); i++){
            Log.e("Column " + i + " name", c.getColumnName(i));
        }
        Log.e("Number of results", "Number of results is " + c.getCount());

        Log.e("Messages", "Here are the messages in this cursor:");
        int counter = 1;
        while(c.moveToNext()){
            long id = c.getLong(c.getColumnIndex(COL_ID));

            //check that column exists
            if (c.getColumnIndex(COL_TEXT) != -1) {
                String text = c.getString(c.getColumnIndex(COL_TEXT));
                int outgoing = c.getInt(c.getColumnIndex(COL_OUTGOING));
                String destination = (outgoing == 1) ? "Outgoing" : "Incoming";

                Log.e("Row " + counter,
                        "ID: " + id + "; Text: " + text + "; Destination: " + destination);
            }
            counter++;
        }
        c.moveToFirst();
        c.moveToPrevious();
    }
}
