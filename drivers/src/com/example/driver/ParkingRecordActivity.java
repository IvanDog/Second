package com.example.driver;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ParkingRecordActivity extends FragmentActivity {
	public static final int TYPE_CURRENT_RECORD = 101;
	public static final int TYPE_HISTORY_RECORD = 102;
	private Fragment mCurrentRecordFragment;
	private Fragment mHistoryRecordFragment;
	private TextView mCurrentRecordTV;
	private TextView mHistoryRecordTV;
	private int mCurrentId;
	private OnClickListener mTabClickListener = new OnClickListener() {
        @Override  
        public void onClick(View v) {  
            if (v.getId() != mCurrentId) {//如果当前选中跟上次选中的一样,不需要处理  
                changeSelect(v.getId());//改变图标跟文字颜色的选中   
                changeFragment(v.getId());//fragment的切换  
                mCurrentId = v.getId();//设置选中id  
            }  
        }  
    };  

	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_record_search);
        mCurrentRecordTV = (TextView) findViewById(R.id.tv_record_current);
        mHistoryRecordTV = (TextView) findViewById(R.id.tv_record_history);
        mCurrentRecordTV.setOnClickListener(mTabClickListener); 
        mHistoryRecordTV.setOnClickListener(mTabClickListener);
        changeSelect(R.id.tv_record_current);
        changeFragment(R.id.tv_record_current);
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	     IntentFilter filter = new IntentFilter();  
	     filter.addAction("ExitApp");  
	     filter.addAction("BackMain");  
	     registerReceiver(mReceiver, filter);
	}

	private void changeFragment(int resId) {  
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();//开启一个Fragment事务  
        hideFragments(transaction);//隐藏所有fragment  
        if(resId==R.id.tv_record_current){
        	mCurrentRecordFragment = new RecordFragment(TYPE_CURRENT_RECORD,true);
        	 transaction.replace(R.id.record_container, mCurrentRecordFragment);
        }else if(resId==R.id.tv_record_history){
        	mHistoryRecordFragment = new RecordFragment(TYPE_HISTORY_RECORD,false);  
        	transaction.replace(R.id.record_container, mHistoryRecordFragment);
        }
        transaction.commit();//提交事务  
    }

	private void hideFragments(FragmentTransaction transaction){  
        if (mCurrentRecordFragment != null) {
        	transaction.hide(mCurrentRecordFragment);
        }else if(mHistoryRecordFragment!=null){
        	transaction.hide(mHistoryRecordFragment);
        }
    }

	private void changeSelect(int resId) {  
		mCurrentRecordTV.setSelected(false);
		mCurrentRecordTV.setBackgroundResource(R.color.gray);
		mHistoryRecordTV.setSelected(false);  
		mHistoryRecordTV.setBackgroundResource(R.color.gray);
        switch (resId) {  
        case R.id.tv_record_current:  
        	mCurrentRecordTV.setSelected(true);  
        	mCurrentRecordTV.setBackgroundResource(R.color.orange);
            break;  
        case R.id.tv_record_history:  
        	mHistoryRecordTV.setSelected(true);  
        	mHistoryRecordTV.setBackgroundResource(R.color.orange);
            break;
        }  
    }
	
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
	
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                default:
                    break;
            }
        }
    };
    
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
