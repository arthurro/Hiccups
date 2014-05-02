package com.frostbittmedia.Hiccups.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.frostbittmedia.Hiccups.Objects.LogEvent;

import java.util.ArrayList;
import java.util.List;

public class DBClient extends SQLiteOpenHelper{

    // ===========================================================
    // Fields
    // ===========================================================
    private static final String TAG = "DBClient";

    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "hiccups";
    private static final String TABLE_NAME = "log";
    private static final String COLUMN_EVENT = "event";
    private static final String COLUMN_DETAIL = "detail";
    private static final String COLUMN_DATETIME = "datetime";

    // ===========================================================
    // Constructor
    // ===========================================================

    public DBClient(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ===========================================================
    // Methods from superclass/interface
    // ===========================================================

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v(TAG, "Creating table");

        final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_EVENT + " VARCHAR(30) NOT NULL,"
                + COLUMN_DETAIL + " VARCHAR(255),"
                + COLUMN_DATETIME + " DATETIME NOT NULL"
                + ")";

        try{
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }catch (SQLException e){
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        Log.v(TAG, "New version, drop / create");

        try{
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }catch (SQLException e){
            Log.e(TAG, e.toString());
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public void addLogEvent(String event, String details, String dateTime){
        Log.i(TAG, "Inserting values");

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EVENT, event);
        contentValues.put(COLUMN_DETAIL, details);
        contentValues.put(COLUMN_DATETIME, dateTime);

        try{
            db.insert(TABLE_NAME, null, contentValues);
            db.close();
        }catch (SQLException e){
            Log.e(TAG, e.toString());
        }
    }

    public List<LogEvent> getLogEvents(){
        Log.i(TAG, "Fetching data");

        List<LogEvent> logEventList = new ArrayList<LogEvent>();
        String query = "SELECT * FROM " + TABLE_NAME + " order by datetime DESC";
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    LogEvent logEvent = new LogEvent();
                    logEvent.setEvent(cursor.getString(0));
                    logEvent.setDetail(cursor.getString(1));
                    logEvent.setDateTime(cursor.getString(2));

                    logEventList.add(logEvent);
                } while (cursor.moveToNext());
            }
        }
        catch (SQLException e){
            Log.e(TAG, e.toString());
        }
        return logEventList;
    }

    public String getLastSleepEvent(){
        Log.i(TAG, "Fetching last sleep event");

        String query = "select * from log where datetime IN(\n" +
                "  select MAX(datetime)\n" +
                "  from log\n" +
                "  where event IN(\"Woke up\", \"Went to sleep\")\n" +
                ")";
        String result = "";
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    result = cursor.getString(0);
                } while (cursor.moveToNext());
            }
        }
        catch (SQLException e){
            Log.e(TAG, e.toString());
        }
        return result;
    }

    public LogEvent getLastEvent(){
        Log.i(TAG, "Fetching last event");

        String query = "select * from log where datetime in(select max(datetime) from log)";
        SQLiteDatabase db = this.getWritableDatabase();
        LogEvent logEvent = null;

        try{
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    logEvent = new LogEvent();
                    logEvent.setEvent(cursor.getString(0));
                    logEvent.setDetail(cursor.getString(1));
                    logEvent.setDateTime(cursor.getString(2));
                } while (cursor.moveToNext());
            }
        }
        catch (SQLException e){
            Log.e(TAG, e.toString());
        }
        return logEvent;
    }

    public void deleteTableRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    // To delete single row, not in use
    public void deleteTableRow(LogEvent event){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_NAME,COLUMN_EVENT + "=\"" + event.getEvent() + "\" and " + COLUMN_DATETIME + "=\"" + event.getDateTime() + "\"", null);
    }
}
