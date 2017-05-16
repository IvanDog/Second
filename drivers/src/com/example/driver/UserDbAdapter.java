package com.example.driver;

import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.SQLException;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  
import android.util.Log;  
  
public class UserDbAdapter {  
  
    static final String KEY_ROWID = "_id";  
    static final String KEY_TELE_NUMBER = "telenumber";  
    static final String KEY_PASSWD = "passwd";  
    static final String KEY_NICK_NAME = "nickname";  
    static final String KEY_HEAD_PORTRAIT = "headportrait";  
    static final String KEY_ACCOUNT_BALANCE = "accountbalance";  
    static final String KEY_PARKING_COUPON = "parkingcoupon";  
    static final String TAG = "UserDbAdapter";  
      
    static final String DATABASE_NAME = "driverDB";  
    static final String DATABASE_DRIVER_TABLE = "users";  
    static final int DATABASE_VERSION = 1;  
      
    static final String DATABASE_CREATE =   
            "create table users( _id integer primary key autoincrement, " +   
            "telenumber varchar(20) not null, passwd varchar(20), nickname varchar(20), " +
            "headportrait blob, accountbalance integer, parkingcoupon integer);";  
    final Context context;  
      
    DatabaseHelper DBHelper;  
    SQLiteDatabase db;  
      
    public UserDbAdapter(Context cxt)  
    {  
        this.context = cxt;  
        DBHelper = new DatabaseHelper(context);  
    }  
      
    private static class DatabaseHelper extends SQLiteOpenHelper  
    {  
  
        DatabaseHelper(Context context)  
        {  
            super(context, DATABASE_NAME, null, DATABASE_VERSION);  
        }  
        @Override  
        public void onCreate(SQLiteDatabase db) {   
            try  
            {  
                db.execSQL(DATABASE_CREATE);  
                android.util.Log.d("yifan","DATABASE_CREATE" );
            }  
            catch(SQLException e)  
            {  
                e.printStackTrace();  
            }  
        }  
  
        @Override  
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
            Log.wtf(TAG, "Upgrading database from version "+ oldVersion + "to "+  
             newVersion + ", which will destroy all old data");  
            db.execSQL("DROP TABLE IF EXISTS driver");  
            onCreate(db);  
        }  
    }  
      
    //open the database  
    public UserDbAdapter open() throws SQLException  
    {  
        db = DBHelper.getWritableDatabase();  
        return this;  
    }

    //close the database  
    public void close()  
    {  
        DBHelper.close();  
    }  

    //insert parking information  into the database  
    public long insertDriver(String telenumber, String passwd, String nickname, byte[] headportrait,int accountbalance,
    		int parkingcoupon)  
    {  
        ContentValues initialValues = new ContentValues();  
        initialValues.put(KEY_TELE_NUMBER, telenumber);  
        initialValues.put(KEY_PASSWD, passwd);
        initialValues.put(KEY_NICK_NAME, nickname);
        initialValues.put(KEY_HEAD_PORTRAIT, headportrait);
        initialValues.put(KEY_ACCOUNT_BALANCE, accountbalance);
        initialValues.put(KEY_PARKING_COUPON, parkingcoupon);
        return db.insert(DATABASE_DRIVER_TABLE, null, initialValues);  
    }

    //delete a particular driver information
    public boolean deleteParking(long rowId)  
    {  
        return db.delete(DATABASE_DRIVER_TABLE, KEY_ROWID + "=" +rowId, null) > 0;  
    }

    //get a particular driver  information
    public Cursor getUser(String teleNumber) throws SQLException  
    {  
        Cursor mCursor =   
                db.query(true, DATABASE_DRIVER_TABLE, new String[]{ KEY_ROWID,KEY_TELE_NUMBER,KEY_PASSWD,
                		KEY_NICK_NAME,KEY_HEAD_PORTRAIT,KEY_ACCOUNT_BALANCE,KEY_PARKING_COUPON},
                		KEY_TELE_NUMBER + "=" + teleNumber, null, null, null, null, null);  
        if (mCursor != null)  
            mCursor.moveToFirst();  
        return mCursor;  
    }

    //get all driver  information
    public Cursor getUser() throws SQLException  
    {  
        Cursor mCursor =   
                db.query(true, DATABASE_DRIVER_TABLE, new String[]{ KEY_ROWID,KEY_TELE_NUMBER,KEY_PASSWD,
                		KEY_NICK_NAME,KEY_HEAD_PORTRAIT,KEY_ACCOUNT_BALANCE,KEY_PARKING_COUPON},
                		null, null, null, null, null, null);  
        if (mCursor != null)  
            mCursor.moveToFirst();  
        return mCursor;  
    }
    
    //updates a driver information  
    public boolean updateParking(long rowId, String telenumber, String passwd, String nickname, byte[] headportrait,int accountbalance,
    		int parkingcoupon)  
    {  
        ContentValues values = new ContentValues();  
        values.put(KEY_TELE_NUMBER, telenumber);  
        values.put(KEY_PASSWD, passwd);
        values.put(KEY_NICK_NAME, nickname);
        values.put(KEY_HEAD_PORTRAIT, headportrait);
        values.put(KEY_ACCOUNT_BALANCE, accountbalance);
        values.put(KEY_PARKING_COUPON, parkingcoupon);
        return db.update(DATABASE_DRIVER_TABLE, values, KEY_TELE_NUMBER + "=" +telenumber, null) > 0;  
    }  

    //updates a driver information  
    public boolean updatePasswd(String telenumber, String passwd)  
    {  
        ContentValues values = new ContentValues();  
        values.put(KEY_PASSWD, passwd);
        return db.update(DATABASE_DRIVER_TABLE, values, KEY_TELE_NUMBER + "=" +telenumber, null) > 0;  
    }  
} 