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
    static final String KEY_LICENSE_PLATE_FIRST = "licenseplatefirst";  
    static final String KEY_LICENSE_PLATE_SECOND = "licenseplatesecond";  
    
    static final String KEY_ROWID_PARKING_DETAIL = "_id";  
    static final String KEY_LICENSE_PLATE = "licenseplate";  
    static final String KEY_CAR_TYPE = "cartype";
    static final String KEY_PARKING_TYPE="parkingtype";
    static final String KEY_PARKING_NAME="parkingname";
    static final String KEY_PARKING_NUMBER="parkingnumber";
    static final String KEY_LOCATION_NUMBER="locationnumber";
    static final String KEY_START_TIME="starttime";
    static final String KEY_LEAVE_TIME="leavetime";
    static final String KEY_EXPENSE_STANDARD="expensestandard";
    static final String KEY_EXPENSE="expense";
    static final String KEY_PAYMENT_PATTERN="paymentpattern";
    
    static final String TAG = "UserDbAdapter";  
      
    static final String DATABASE_NAME = "driver";  
    static final String DATABASE_DRIVER_TABLE = "users";  
    static final String DATABASE_PARKING_DETAIL_TABLE = "parkings";  
    static final int DATABASE_VERSION = 1;  
      
    static final String DRIVER_TABLE_CREATE =   
            "create table users( _id integer primary key autoincrement, " +   
            "telenumber varchar(20) not null, passwd varchar(20), nickname varchar(20), " +
            "headportrait blob, accountbalance integer, parkingcoupon integer, " +
            "licenseplatefirst varchar(20), licenseplatesecond varchar(20));";  
    static final String PARKING_TABLE_CREATE =   
            "create table parkings( _id integer primary key autoincrement, " +   
            "licenseplate varchar(20), telenumber varchar(20), cartype varchar(20), parkingtype varchar(20), " +
            "parkingname varchar(50), parkingnumber varchar(20),locationnumber integer," +
            "starttime varchar(50),leavetime  varchar(50),expensestandard varchar(20)," +
            "expense integer, paymentpattern varchar(20));";  
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
                db.execSQL(DRIVER_TABLE_CREATE);  
                db.execSQL(PARKING_TABLE_CREATE);
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
            db.execSQL("DROP TABLE IF EXISTS users");  
            db.execSQL("DROP TABLE IF EXISTS parkings");  
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
    		int parkingcoupon,String licenseplatefirst,String licenseplatesecond)
    {  
        ContentValues initialValues = new ContentValues();  
        initialValues.put(KEY_TELE_NUMBER, telenumber);  
        initialValues.put(KEY_PASSWD, passwd);
        initialValues.put(KEY_NICK_NAME, nickname);
        initialValues.put(KEY_HEAD_PORTRAIT, headportrait);
        initialValues.put(KEY_ACCOUNT_BALANCE, accountbalance);
        initialValues.put(KEY_PARKING_COUPON, parkingcoupon);
        initialValues.put(KEY_LICENSE_PLATE_FIRST, licenseplatefirst);
        initialValues.put(KEY_LICENSE_PLATE_SECOND, licenseplatesecond);
        return db.insert(DATABASE_DRIVER_TABLE, null, initialValues);  
    }

    //delete a particular driver information
    public boolean deleteDriver(long rowId)  
    {  
        return db.delete(DATABASE_DRIVER_TABLE, KEY_ROWID + "=" +rowId, null) > 0;  
    }

    //get a particular driver  information
    public Cursor getUser(String teleNumber) throws SQLException  
    {  
        Cursor mCursor =   
                db.query(true, DATABASE_DRIVER_TABLE, new String[]{ KEY_ROWID,KEY_TELE_NUMBER,KEY_PASSWD,
                		KEY_NICK_NAME,KEY_HEAD_PORTRAIT,KEY_ACCOUNT_BALANCE,KEY_PARKING_COUPON,KEY_LICENSE_PLATE_FIRST,KEY_LICENSE_PLATE_SECOND},
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
                		KEY_NICK_NAME,KEY_HEAD_PORTRAIT,KEY_ACCOUNT_BALANCE,KEY_PARKING_COUPON,KEY_LICENSE_PLATE_FIRST,KEY_LICENSE_PLATE_SECOND},
                		null, null, null, null, null, null);  
        if (mCursor != null)  
            mCursor.moveToFirst();  
        return mCursor;  
    }

    //updates a driver information  
    public boolean updateDriver(long rowId, String telenumber, String passwd, String nickname, byte[] headportrait,int accountbalance,
    		int parkingcoupon,String licenseplatefirst,String licenseplatesecond)  
    {  
        ContentValues values = new ContentValues();  
        values.put(KEY_TELE_NUMBER, telenumber);  
        values.put(KEY_PASSWD, passwd);
        values.put(KEY_NICK_NAME, nickname);
        values.put(KEY_HEAD_PORTRAIT, headportrait);
        values.put(KEY_ACCOUNT_BALANCE, accountbalance);
        values.put(KEY_PARKING_COUPON, parkingcoupon);
        values.put(KEY_LICENSE_PLATE_FIRST, licenseplatefirst);
        values.put(KEY_LICENSE_PLATE_SECOND, licenseplatesecond);
        return db.update(DATABASE_DRIVER_TABLE, values, KEY_TELE_NUMBER + "=" +telenumber, null) > 0;  
    }  

    //updates a driver information  
    public boolean updateDriver(String telenumber, int accountbalance)  
    {  
        ContentValues values = new ContentValues();  
        values.put(KEY_TELE_NUMBER, telenumber);  
        values.put(KEY_ACCOUNT_BALANCE, accountbalance);
        return db.update(DATABASE_DRIVER_TABLE, values, KEY_TELE_NUMBER + "=" +telenumber, null) > 0;  
    }  
    
    //updates a driver information  
    public boolean updateDriverCoupon(String telenumber, int parkingcoupon)  
    {  
        ContentValues values = new ContentValues();  
        values.put(KEY_TELE_NUMBER, telenumber);  
        values.put(KEY_PARKING_COUPON, parkingcoupon);
        return db.update(DATABASE_DRIVER_TABLE, values, KEY_TELE_NUMBER + "=" +telenumber, null) > 0;  
    }  
    
    //updates a driver information  
    public boolean updateDriverLisence(String telenumber,String licenseplate,int type)  
    {  
        ContentValues values = new ContentValues();  
        if(type==1){
            values.put(KEY_LICENSE_PLATE_FIRST, licenseplate);  
        }else if(type==2){
            values.put(KEY_LICENSE_PLATE_SECOND, licenseplate);  
        }
        return db.update(DATABASE_DRIVER_TABLE, values, KEY_TELE_NUMBER + "=" +telenumber, null) > 0;  
    }  
     
    
    //updates a driver information  
    public boolean updateDriverPasswd(String telenumber, String passwd)  
    {  
        ContentValues values = new ContentValues();  
        values.put(KEY_PASSWD, passwd);
        return db.update(DATABASE_DRIVER_TABLE, values, KEY_TELE_NUMBER + "=" +telenumber, null) > 0;  
    }  
    
    //updates a driver information  
    public boolean updateDriverHeadPortrait(String telenumber, byte[] portrait)  
    {  
        ContentValues values = new ContentValues();  
        values.put(KEY_HEAD_PORTRAIT, portrait);
        return db.update(DATABASE_DRIVER_TABLE, values, KEY_TELE_NUMBER + "=" +telenumber, null) > 0;  
    }  
    
    //updates a driver information  
    public boolean updateDriverNickName(String telenumber, String nickname)  
    {  
        ContentValues values = new ContentValues();  
        values.put(KEY_NICK_NAME, nickname);
        return db.update(DATABASE_DRIVER_TABLE, values, KEY_TELE_NUMBER + "=" +telenumber, null) > 0;  
    }  
    
    //get all driver  information
    public Cursor getDriver() throws SQLException  
    {  
        Cursor mCursor =   
                db.query(true, DATABASE_DRIVER_TABLE, new String[]{ KEY_ROWID,KEY_TELE_NUMBER,KEY_PASSWD,
                		KEY_NICK_NAME,KEY_HEAD_PORTRAIT,KEY_ACCOUNT_BALANCE,KEY_PARKING_COUPON,KEY_LICENSE_PLATE_FIRST,KEY_LICENSE_PLATE_SECOND},
                		null, null, null, null, null, null);  
        if (mCursor != null)  
            mCursor.moveToFirst();  
        return mCursor;  
    }
    
    //delete all  parking detail  information
    public boolean deleteAllParkingDetail()  
    {  
        return db.delete(DATABASE_PARKING_DETAIL_TABLE, null, null) > 0;  
    }
    
    //get all  parking detail  information
    public Cursor getParkingDetail() throws SQLException  
    {  
        Cursor mCursor =   
                db.query(true, DATABASE_PARKING_DETAIL_TABLE, new String[]{ KEY_ROWID_PARKING_DETAIL,KEY_LICENSE_PLATE,KEY_TELE_NUMBER,KEY_CAR_TYPE,
                		KEY_PARKING_TYPE,KEY_PARKING_NAME,KEY_PARKING_NUMBER,KEY_LOCATION_NUMBER,KEY_START_TIME,KEY_LEAVE_TIME,
                		KEY_EXPENSE_STANDARD,KEY_EXPENSE,KEY_PAYMENT_PATTERN}, null, null, null, null, null, null);  
        if (mCursor != null)  
            mCursor.moveToFirst();  
        return mCursor;  
    }
    
    //get particular parking detail  information
    public Cursor getParkingDetail(String telenumber) throws SQLException  
    {  
        Cursor mCursor =   
                db.query(true, DATABASE_PARKING_DETAIL_TABLE, new String[]{ KEY_ROWID_PARKING_DETAIL,KEY_LICENSE_PLATE,KEY_TELE_NUMBER,KEY_CAR_TYPE,
                		KEY_PARKING_TYPE,KEY_PARKING_NAME,KEY_PARKING_NUMBER,KEY_LOCATION_NUMBER,KEY_START_TIME,KEY_LEAVE_TIME,
                		KEY_EXPENSE_STANDARD,KEY_EXPENSE,KEY_PAYMENT_PATTERN}, KEY_TELE_NUMBER + " = " + "\"" + telenumber + "\"", null, null, null, null, null);  
        if (mCursor != null)  
            mCursor.moveToFirst();  
        return mCursor;  
    }
    
    //get particular parking detail  information
    public Cursor getParkingDetail(long rowid) throws SQLException  
    {  
        Cursor mCursor =   
                db.query(true, DATABASE_PARKING_DETAIL_TABLE, new String[]{ KEY_ROWID_PARKING_DETAIL,KEY_LICENSE_PLATE,KEY_TELE_NUMBER,KEY_CAR_TYPE,
                		KEY_PARKING_TYPE,KEY_PARKING_NAME,KEY_PARKING_NUMBER,KEY_LOCATION_NUMBER,KEY_START_TIME,KEY_LEAVE_TIME,
                		KEY_EXPENSE_STANDARD,KEY_EXPENSE,KEY_PAYMENT_PATTERN}, KEY_ROWID_PARKING_DETAIL + "=" + rowid, null, null, null, null, null);  
        if (mCursor != null)  
            mCursor.moveToFirst();  
        return mCursor;  
    }
    
    //get particular parking detail  information
    public Cursor getParkingDetailByPaymentPattern(String paymentPattern,boolean equal) throws SQLException  
    {  
    	Cursor mCursor;
    	if(equal){
            mCursor =   
                    db.query(true, DATABASE_PARKING_DETAIL_TABLE, new String[]{ KEY_ROWID_PARKING_DETAIL,KEY_LICENSE_PLATE,KEY_TELE_NUMBER,KEY_CAR_TYPE,
                    		KEY_PARKING_TYPE,KEY_PARKING_NAME,KEY_PARKING_NUMBER,KEY_LOCATION_NUMBER,KEY_START_TIME,KEY_LEAVE_TIME,
                    		KEY_EXPENSE_STANDARD,KEY_EXPENSE,KEY_PAYMENT_PATTERN}, KEY_PAYMENT_PATTERN + " = " + "\"" +paymentPattern + "\"", null, null, null, null, null);
    	}else{
            mCursor =   
                    db.query(true, DATABASE_PARKING_DETAIL_TABLE, new String[]{ KEY_ROWID_PARKING_DETAIL,KEY_LICENSE_PLATE,KEY_TELE_NUMBER,KEY_CAR_TYPE,
                    		KEY_PARKING_TYPE,KEY_PARKING_NAME,KEY_PARKING_NUMBER,KEY_LOCATION_NUMBER,KEY_START_TIME,KEY_LEAVE_TIME,
                    		KEY_EXPENSE_STANDARD,KEY_EXPENSE,KEY_PAYMENT_PATTERN}, KEY_PAYMENT_PATTERN + " != " + "\"" +paymentPattern + "\"", null, null, null, null, null);
    	}
        if (mCursor != null)  
            mCursor.moveToFirst();  
        return mCursor;  
    }
    
    //get particular parking detail  information
    public Cursor getParkingDetailByLisenceNumber(String licenseNumber) throws SQLException  
    {  
        Cursor mCursor =   
                db.query(true, DATABASE_PARKING_DETAIL_TABLE, new String[]{ KEY_ROWID_PARKING_DETAIL,KEY_LICENSE_PLATE,KEY_TELE_NUMBER,KEY_CAR_TYPE,
                		KEY_PARKING_TYPE,KEY_PARKING_NAME,KEY_PARKING_NUMBER,KEY_LOCATION_NUMBER,KEY_START_TIME,KEY_LEAVE_TIME,
                		KEY_EXPENSE_STANDARD,KEY_EXPENSE,KEY_PAYMENT_PATTERN}, KEY_LICENSE_PLATE + " = " + "\"" +licenseNumber + "\"", null, null, null, null, null);  
        if (mCursor != null)  
            mCursor.moveToFirst();  
        return mCursor;  
    }
    
    //insert parking information  into the database  
    public long insertParkingDeatail(String licensePlate, String telenumber, String carType, String parkingType, String parkingName, String parkingNumber, 
    		int locationNumber,String startTime, String leaveTime, String expenseStandard, int expense, String paymentPattern)  
    {  
        ContentValues initialValues = new ContentValues();  
        initialValues.put(KEY_LICENSE_PLATE, licensePlate);  
        initialValues.put(KEY_TELE_NUMBER, telenumber);  
        initialValues.put(KEY_CAR_TYPE, carType);
        initialValues.put(KEY_PARKING_TYPE, parkingType);
        initialValues.put(KEY_PARKING_NAME, parkingName);
        initialValues.put(KEY_PARKING_NUMBER, parkingNumber);
        initialValues.put(KEY_LOCATION_NUMBER, locationNumber);
        initialValues.put(KEY_START_TIME, startTime);
        initialValues.put(KEY_LEAVE_TIME, leaveTime);
        initialValues.put(KEY_EXPENSE_STANDARD, expenseStandard);
        initialValues.put(KEY_EXPENSE, expense);
        initialValues.put(KEY_PAYMENT_PATTERN, paymentPattern);
        return db.insert(DATABASE_PARKING_DETAIL_TABLE, null, initialValues);  
    }
    
    //updates a parking information  
    public boolean updateParkingDetail(long rowId, String licensePlate, String telenumber, String carType, String parkingType, String parkingName, String parkingNumber, int locationNumber,String startTime,
    		String leaveTime, String expenseStandard, int expense, String paymentPattern)  
    {  
        ContentValues values = new ContentValues();  
        values.put(KEY_LICENSE_PLATE, licensePlate);  
        values.put(KEY_TELE_NUMBER, telenumber);  
        values.put(KEY_CAR_TYPE, carType);
        values.put(KEY_PARKING_TYPE, parkingType);
        values.put(KEY_PARKING_NAME, parkingName);
        values.put(KEY_PARKING_NUMBER, parkingNumber);
        values.put(KEY_LOCATION_NUMBER, locationNumber);
        values.put(KEY_START_TIME, startTime);
        values.put(KEY_LEAVE_TIME, leaveTime);
        values.put(KEY_EXPENSE, expenseStandard);
        values.put(KEY_EXPENSE, expense);
        values.put(KEY_PAYMENT_PATTERN, paymentPattern);
        return db.update(DATABASE_PARKING_DETAIL_TABLE, values, KEY_ROWID + "=" +rowId, null) > 0;  
    }  
    
    //updates a parking information  
    public boolean updateParkingDetail(long rowId, String leaveTime,  int expense, String paymentPattern)  
    {  
        ContentValues values = new ContentValues();  
        values.put(KEY_LEAVE_TIME, leaveTime);
        values.put(KEY_EXPENSE, expense);
        values.put(KEY_PAYMENT_PATTERN, paymentPattern);
        return db.update(DATABASE_PARKING_DETAIL_TABLE, values, KEY_ROWID + "=" +rowId, null) > 0;  
    }  
} 