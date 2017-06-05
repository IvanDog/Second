package com.example.driver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ParkingInformationActivity extends Activity {
	private final static int EVENT_DISPLAY_TIME = 101;
	private TextView mLisenseNumberTV;
	private TextView mParkNameTV;
	private TextView mParkNumberTV;
	private TextView mLocationNumberTV;
	private TextView mLicenseNumberTV;
	private TextView mCarTypeTV;
	private TextView mParkTypeTV;
	private TextView mExpenseStandardTV;
	private TextView mStartTimeTV;
	private TextView mLeaveTimeTV;
	private Button mConfirmLeavingBT;
	private Button mCancelLeavingBT;
	private UserDbAdapter mUserDbAdapter;
	
	private String mTeleNumber;
	private long mID;
	private String mLicensePlateNumber;
	private String mParkName;
	private String mParkNumber;
	private int mLocationNumber;
	private String mCarType;
	private String mParkType;
	private String mExpenseStandard;
	private String mStartTime;
	private String mLeaveTime;;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mUserDbAdapter = new UserDbAdapter(this);
        setContentView(R.layout.activity_parking_detail);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTeleNumber= bundle.getString("telenumber");
        mID = bundle.getLong("id");
		mLicenseNumberTV=(TextView)findViewById(R.id.tv_license_plate_number_parking_detail);
		mParkNameTV = (TextView)findViewById(R.id.tv_parking_name_parking_detail);
		mParkNumberTV = (TextView) findViewById(R.id.tv_parking_number_parking_detail);
		mCarTypeTV=(TextView)findViewById(R.id.tv_car_type_parking_detail);
		mParkTypeTV=(TextView)findViewById(R.id.tv_parking_type_parking_detail);
        mLocationNumberTV=(TextView)findViewById(R.id.tv_location_number_parking_detail);
        mExpenseStandardTV=(TextView)findViewById(R.id.tv_expense_standard_parking_detail);
        mStartTimeTV=(TextView) findViewById(R.id.tv_start_time_parking_detail);
        mLeaveTimeTV=(TextView) findViewById(R.id.tv_leave_time_parking_detail);
        mConfirmLeavingBT=(Button)findViewById(R.id.bt_confirm_leaving);
        mConfirmLeavingBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		Intent intent = new Intent(ParkingInformationActivity.this,PaymentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong("id", mID);
				bundle.putString("telenumber", mTeleNumber);
				bundle.putString("licensenumber", mLicensePlateNumber);
				bundle.putString("starttime", mStartTime);
				bundle.putString("leavetime", mLeaveTime);
				bundle.putString("expensestandard", mExpenseStandard);
				bundle.putInt("expense", 5);
				intent.putExtras(bundle);
				startActivity(intent);
        	}
        });
        mCancelLeavingBT=(Button)findViewById(R.id.bt_cancel_leaving);
        mCancelLeavingBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		finish();
        	}
        });
        new TimeThread().start();
        setParkingInformation();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        IntentFilter filter = new IntentFilter();  
        filter.addAction("ExitApp");  
        filter.addAction("BackMain");  
        registerReceiver(mReceiver, filter);
	}
	
	public class TimeThread extends Thread {
        @Override
        public void run () {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = EVENT_DISPLAY_TIME;
                    mHandler.sendMessage(msg);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(true);
        }
    }
	
	public void setParkingInformation(){
		mUserDbAdapter.open();
    	Cursor cursor = mUserDbAdapter.getParkingDetail(mID);
        try {
        	      mLicensePlateNumber =  cursor.getString(cursor.getColumnIndex("licenseplate"));
        	      mParkName =  cursor.getString(cursor.getColumnIndex("parkingname"));
        	      mParkNumber =  cursor.getString(cursor.getColumnIndex("parkingnumber"));
	              mCarType = cursor.getString(cursor.getColumnIndex("cartype"));
	              mParkType = cursor.getString(cursor.getColumnIndex("parkingtype"));
  		          mLocationNumber =  cursor.getInt(cursor.getColumnIndex("locationnumber"));
  		          mExpenseStandard =  cursor.getString(cursor.getColumnIndex("expensestandard"));
  		          mStartTime = cursor.getString(cursor.getColumnIndex("starttime"));
                  CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
                  mLeaveTime=sysTimeStr.toString();
  				  mLicenseNumberTV.setText("车牌号: " + mLicensePlateNumber);
  				  mParkNameTV.setText("车场名称: " + mParkName);
  				  mParkNumberTV.setText("车场编号: " + mParkNumber);
  				  mLocationNumberTV.setText("泊位号: " + mLocationNumber);
  		    	  mCarTypeTV.setText("车辆类型: " + mCarType);
  		          mParkTypeTV.setText("泊车类型: " + mParkType);
  		          mExpenseStandardTV.setText("收费标准: " + mExpenseStandard);
  		          mStartTimeTV.setText("入场时间: " + mStartTime);
  		          mLeaveTimeTV.setText("离场时间：" + mLeaveTime);
        }
        catch (Exception e) {
                e.printStackTrace();
        } finally{
            	if(cursor!=null){
            		cursor.close();
                }
        }
        mUserDbAdapter.close();
	}
	
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_DISPLAY_TIME:
                    CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
                    mLeaveTime=sysTimeStr.toString();
                    mLeaveTimeTV.setText("离场时间：" + mLeaveTime);
                    break;
                default:
                    break;
            }
        }
    };
    
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	         case android.R.id.home:  
	             finish();  
	             break;    
	        default:  
	             break;  
	    }  
	    return super.onOptionsItemSelected(item);  
	  }  
	
    private BroadcastReceiver mReceiver = new BroadcastReceiver(){  
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction()!=null && intent.getAction().equals("ExitApp")){
				finish();
			}else if(intent.getAction()!=null && intent.getAction().equals("BackMain")){
				finish();
			}
		}            
    };
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
