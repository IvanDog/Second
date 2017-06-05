package com.example.driver;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LicensePlateManagementActivity extends Activity {
	private Button mAddLicensePlateBT;
	private TextView mLicensePlateFirstTV;
	private TextView mLicensePlateSecondTV;
	private ImageView mDeleteFirstLicensePlateIV;
	private ImageView mDeleteSecondLicensePlateIV;
	private View mLinearLicensePlateFirst;
	private View mLinearLicensePlateSecond;
	private String mTeleNumber;
    private String mLicensePlateFirst = null;
    private String mLicensePlateSecond = null;
    private int mType = -1;
	private UserDbAdapter mUserDbAdapter;
	private static final int EVENT_DISPLAY_DOUBLE_LICENSE=101;
	private static final int EVENT_DISPLAY_FIRST_LICENSE=102;
	private static final int EVENT_DISPLAY_SECOND_LICENSE=103;
	private static final int TYPE_DELETE_FIRST_LICENSE =201;
	private static final int TYPE_DELETE_SECOND_LICENSE =202;
	private static final int EVENT_DISMISS_FIRST_LICENSE=301;
	private static final int EVENT_DISMISS_SECOND_LICENSE=302;
	private static final int EVENT_DISMISS_BOTH_LICENSE=303;
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        mUserDbAdapter = new UserDbAdapter(this);
        Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mTeleNumber=bundle.getString("telenumber");
        setContentView(R.layout.activity_license_management);
        mAddLicensePlateBT=(Button)findViewById(R.id.bt_enter_bind_license_plate);
        mAddLicensePlateBT.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
            	Intent intent = new Intent(LicensePlateManagementActivity.this,InputLicenseActivity.class);
            	Bundle bundle = new Bundle();
            	bundle.putString("telenumber", mTeleNumber);
            	intent.putExtras(bundle);
            	startActivityForResult(intent,0);
            }
        });
        mLicensePlateFirstTV=(TextView)findViewById(R.id.tv_license_plate_first);
        mLicensePlateSecondTV=(TextView)findViewById(R.id.tv_license_plate_second);
        mDeleteFirstLicensePlateIV=(ImageView)findViewById(R.id.iv_delete_license_plate_first);
        mDeleteFirstLicensePlateIV.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
            	mType = TYPE_DELETE_FIRST_LICENSE;
            	new DeleteThread().start();
            }
        });
        mDeleteSecondLicensePlateIV=(ImageView)findViewById(R.id.iv_delete_license_plate_second);
        mDeleteSecondLicensePlateIV.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
            	mType = TYPE_DELETE_SECOND_LICENSE;
            	new DeleteThread().start();
            }
        });
        mLinearLicensePlateFirst=(View)findViewById(R.id.linear_license_plate_first);
        mLinearLicensePlateSecond=(View)findViewById(R.id.linear_license_plate_second);
        new DisplayThread().start();
        Log.e("yifan","DisplayThread is starting");
    	getActionBar().setDisplayHomeAsUpEnabled(true); 
	     IntentFilter filter = new IntentFilter();  
	     filter.addAction("ExitApp");  
	     filter.addAction("BackMain");  
	     registerReceiver(mReceiver, filter);
	}
	
	public void onResume(){
		super.onResume();
	}
	
	public void onPause(){
		super.onPause();
	}
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // TODO Auto-generated method stub  
        super.onActivityResult(requestCode, resultCode, data);  
        if(requestCode==0){  
        	finish();
        }  
    } 
    
	public class DeleteThread extends Thread {
        @Override
        public void run () {
        	mUserDbAdapter.open();
        	Cursor cursor = mUserDbAdapter.getUser(mTeleNumber);
        	try {
        		    if(mType==TYPE_DELETE_FIRST_LICENSE){
        		    	if((mLicensePlateFirstTV.getText().toString()).equals(mLicensePlateFirst)){
        		    		mUserDbAdapter.updateDriverLisence(mTeleNumber, null, 1);
        		    	}else if((mLicensePlateFirstTV.getText().toString()).equals(mLicensePlateSecond)){
        		    		mUserDbAdapter.updateDriverLisence(mTeleNumber, null, 2);
        		    	}
        		    	Message msg = new Message();
        		    	msg.what = EVENT_DISMISS_FIRST_LICENSE;
        		    	mHandler.sendMessage(msg);
        		    }else if(mType==TYPE_DELETE_SECOND_LICENSE){
            		    	if((mLicensePlateSecondTV.getText().toString()).equals(mLicensePlateFirst)){
            		    		mUserDbAdapter.updateDriverLisence(mTeleNumber, null, 1);
            		    	}else if((mLicensePlateSecondTV.getText().toString()).equals(mLicensePlateSecond)){
            		    		mUserDbAdapter.updateDriverLisence(mTeleNumber, null, 2);
            		    	}
            		    	Message msg = new Message();
            		    	msg.what = EVENT_DISMISS_SECOND_LICENSE;
            		    	mHandler.sendMessage(msg);
        		    }
        		}catch (Exception e) {
                    e.printStackTrace();
                } finally{
                	if(cursor!=null){
                		cursor.close();
                    }
                }
          }
    }
	
	public class DisplayThread extends Thread {
        @Override
        public void run () {
        	mUserDbAdapter.open();
        	Cursor cursor = mUserDbAdapter.getUser(mTeleNumber);
        		try {
        		    mLicensePlateFirst = cursor.getString(cursor.getColumnIndex("licenseplatefirst"));
        		    mLicensePlateSecond = cursor.getString(cursor.getColumnIndex("licenseplatesecond"));
        		    if(mLicensePlateFirst!=null && mLicensePlateSecond!=null){
        		    	Message msg = new Message();
        		    	msg.what = EVENT_DISPLAY_DOUBLE_LICENSE;
        		    	mHandler.sendMessage(msg);
        		    }else if(mLicensePlateFirst!=null && mLicensePlateSecond==null){
        		    	Message msg = new Message();
        		    	msg.what = EVENT_DISPLAY_FIRST_LICENSE;
        		    	mHandler.sendMessage(msg);
        		    }else if(mLicensePlateFirst==null && mLicensePlateSecond!=null){
        		    	Message msg = new Message();
        		    	msg.what = EVENT_DISPLAY_SECOND_LICENSE;
        		    	mHandler.sendMessage(msg);
        		    }else if(mLicensePlateFirst==null && mLicensePlateSecond==null){
        		    	Message msg = new Message();
        		    	msg.what = EVENT_DISMISS_BOTH_LICENSE;
        		    	mHandler.sendMessage(msg);
        		    }
        		}catch (Exception e) {
                    e.printStackTrace();
                } finally{
                	if(cursor!=null){
                		cursor.close();
                    }
                }
          }
    }
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what){
			    case EVENT_DISPLAY_DOUBLE_LICENSE:
                    Log.e("yifan","EVENT_DISPLAY_DOUBLE_LICENSE");
    		    	mLicensePlateFirstTV.setText(mLicensePlateFirst);
    		    	mLicensePlateSecondTV.setText(mLicensePlateSecond);
    		    	break;
			    case EVENT_DISPLAY_FIRST_LICENSE:
                    Log.e("yifan","EVENT_DISPLAY_FIRST_LICENSE");
    		    	mLicensePlateFirstTV.setText(mLicensePlateFirst);
    		    	mLinearLicensePlateSecond.setVisibility(View.GONE);
    		    	break;
			    case EVENT_DISPLAY_SECOND_LICENSE:
                    Log.e("yifan","EVENT_DISPLAY_SECOND_LICENSE");
    		    	mLicensePlateFirstTV.setText(mLicensePlateSecond);
    		    	mLinearLicensePlateSecond.setVisibility(View.GONE);
    		    	break;
			    case EVENT_DISMISS_FIRST_LICENSE:
			    	mLinearLicensePlateFirst.setVisibility(View.GONE);
			    	break;
			    case EVENT_DISMISS_SECOND_LICENSE:
			    	mLinearLicensePlateSecond.setVisibility(View.GONE);
			    	break;
			    case EVENT_DISMISS_BOTH_LICENSE:
			    	mLinearLicensePlateFirst.setVisibility(View.GONE);
		    	    mLinearLicensePlateSecond.setVisibility(View.GONE);
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
