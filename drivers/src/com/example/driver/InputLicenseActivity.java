package com.example.driver;

import com.example.driver.R.color;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InputLicenseActivity extends FragmentActivity {
	private static final int LICENSE_PLATE_NUMBER_SIZE=7;
	private static final int EVENT_BIND_SUCCESS=201;
	private static final int EVENT_BIND_FAIL=202;
	private static final int EVENT_EXIST_LICENSE_PLATE=203;
	private static final int EVENT_INVALID_LICENSE_PLATE=204;
	private static final int EVENT_BIND_FULL=205;
	private String mTeleNumber;
	private Fragment mNumberFragment;
	private Fragment mLetterFragment;
	private Fragment mLocationFragment;
	private EditText mLicensePlateET;
	private TextView mNumberTV;
	private TextView mLetterTV;
	private TextView mLocationTV;
	private Button mConfirmBT;
	private int mCurrentId;
	private int mType;
	private UserDbAdapter mUserDbAdapter;
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
        mUserDbAdapter = new UserDbAdapter(this);
        Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mTeleNumber=bundle.getString("telenumber");
        setContentView(R.layout.activity_input_license);
        mLicensePlateET = (EditText) findViewById(R.id.et_input_license_plate);
        mLetterTV = (TextView) findViewById(R.id.tv_letter_input_license_title);
        mNumberTV = (TextView) findViewById(R.id.tv_number_input_license_title);
        mLocationTV = (TextView) findViewById(R.id.tv_location_input_license_title);
        mConfirmBT = (Button) findViewById(R.id.bt_confirm__input_license_title);
    	mLocationTV.setOnClickListener(mTabClickListener);
        mLetterTV.setOnClickListener(mTabClickListener); 
    	mNumberTV.setOnClickListener(mTabClickListener);
    	changeSelect(R.id.tv_location);
    	changeFragment(R.id.tv_location);
    	mLicensePlateET.setOnTouchListener(new OnTouchListener() {	 
    	      @Override
    	      public boolean onTouch(View v, MotionEvent event) {
    	        // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
    	        Drawable drawable = mLicensePlateET.getCompoundDrawables()[2];
    	        //如果右边没有图片，不再处理
    	        if (drawable == null)
    	            return false;
    	        //如果不是按下事件，不再处理
    	        if (event.getAction() != MotionEvent.ACTION_UP)
    	            return false;
    	        if (event.getX() > mLicensePlateET.getWidth()
                   -mLicensePlateET.getPaddingRight()
    	           - drawable.getIntrinsicWidth()){
    	        	mLicensePlateET.setText("");
    	        }
    	          return false;
    	      }
    	    });
    	mLicensePlateET.setInputType(InputType.TYPE_NULL);  
    	mConfirmBT.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				if(mLicensePlateET.getText().length() !=LICENSE_PLATE_NUMBER_SIZE){
            		Message msg = new Message();
            		msg.what=EVENT_INVALID_LICENSE_PLATE;
            		mHandler.sendMessage(msg);
            		return;
				}
				new SQLThread().start();
			}
		});
    	getActionBar().setDisplayHomeAsUpEnabled(true); 
	}

	private void changeFragment(int resId) {  
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();//开启一个Fragment事务  
        hideFragments(transaction);//隐藏所有fragment  
        if(resId==R.id.tv_location_input_license_title){
            if(mLocationFragment==null){//如果为空先添加进来.不为空直接显示  
            	mLocationFragment = new LocationFragment();  
                transaction.add(R.id.main_container,mLocationFragment);  
            }else {  
                transaction.show(mLocationFragment);  
            }
        }else if(resId==R.id.tv_letter_input_license_title){
            if(mLetterFragment==null){//如果为空先添加进来.不为空直接显示  
            	mLetterFragment = new LetterFragment();  
                transaction.add(R.id.main_container,mLetterFragment);  
            }else {  
                transaction.show(mLetterFragment);  
            }
        }else if(resId==R.id.tv_number_input_license_title){
            if(mNumberFragment==null){//如果为空先添加进来.不为空直接显示  
            	mNumberFragment = new NumberFragment();  
                transaction.add(R.id.main_container,mNumberFragment);  
            }else {  
                transaction.show(mNumberFragment);  
            }
        }
        transaction.commit();//一定要记得提交事务  
    }

	private void hideFragments(FragmentTransaction transaction){  
        if (mLetterFragment != null)  
            transaction.hide(mLetterFragment);
        if (mNumberFragment != null)
            transaction.hide(mNumberFragment);
        if (mLocationFragment != null)
            transaction.hide(mLocationFragment);
    }

	private void changeSelect(int resId) {  
		mLetterTV.setSelected(false);
		mLetterTV.setBackgroundResource(R.color.gray);
		mNumberTV.setSelected(false);
		mNumberTV.setBackgroundResource(R.color.gray);
		mLocationTV.setSelected(false);
		mLocationTV.setBackgroundResource(R.color.gray);
        switch (resId) {  
            case R.id.tv_location_input_license_title:  
        	    mLocationTV.setSelected(true);  
        	    mLocationTV.setBackgroundResource(R.color.orange);
                break;  
            case R.id.tv_letter_input_license_title:  
        	    mLetterTV.setSelected(true);  
        	    mLetterTV.setBackgroundResource(R.color.orange);
                break;  
            case R.id.tv_number_input_license_title:  
        	    mNumberTV.setSelected(true);  
        	    mNumberTV.setBackgroundResource(R.color.orange);
                break;
         }  
    }

    public class SQLThread extends Thread {
        @Override
        public void run () {
        	mUserDbAdapter.open();
        	Cursor cursor = mUserDbAdapter.getUser(mTeleNumber);
        	try {
        		    String licensePlateFirst = cursor.getString(cursor.getColumnIndex("licenseplatefirst"));
        		    String licensePlateSecond = cursor.getString(cursor.getColumnIndex("licenseplatesecond"));
        		    if(((mLicensePlateET.getText().toString()).equals(licensePlateFirst)) || ((mLicensePlateET.getText().toString()).equals(licensePlateSecond)) ){
                		Message msg = new Message();
                		msg.what=	EVENT_EXIST_LICENSE_PLATE;
                		mHandler.sendMessage(msg);
        		    }else if(licensePlateFirst!=null && licensePlateSecond!=null){
                		Message msg = new Message();
                		msg.what=	EVENT_BIND_FULL;
                		mHandler.sendMessage(msg);
        		    }else if(licensePlateFirst==null){
        		    	if(mUserDbAdapter.updateDriverLisence(mTeleNumber, mLicensePlateET.getText().toString(), 1)){
                    		Message msg = new Message();
                    		msg.what=	EVENT_BIND_SUCCESS;
                    		mHandler.sendMessage(msg);
        		    	}else{
                    		Message msg = new Message();
                    		msg.what=	EVENT_BIND_FAIL;
                    		mHandler.sendMessage(msg);
        		    	}
        		    }else if(licensePlateSecond==null){
        		    	if(mUserDbAdapter.updateDriverLisence(mTeleNumber, mLicensePlateET.getText().toString(), 2)){
                    		Message msg = new Message();
                    		msg.what=	EVENT_BIND_SUCCESS;
                    		mHandler.sendMessage(msg);
        		    	}else{
                    		Message msg = new Message();
                    		msg.what=	EVENT_BIND_FAIL;
                    		mHandler.sendMessage(msg);
        		    	}
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
        	

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_BIND_SUCCESS:
                	Toast.makeText(getApplicationContext(), "绑定成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(InputLicenseActivity.this,LicensePlateManagementActivity.class);
    				Bundle bundle = new Bundle();
    				bundle.putString("telenumber", mTeleNumber);
    				intent.putExtras(bundle);
       	        	startActivity(intent); 
       	        	finish();
                	break;
                case EVENT_BIND_FAIL:
                	Toast.makeText(getApplicationContext(), "绑定失败", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_EXIST_LICENSE_PLATE:
                	Toast.makeText(getApplicationContext(), "该牌照已被绑定", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_INVALID_LICENSE_PLATE:
                    Toast.makeText(getApplicationContext(), "请输入正确牌照", Toast.LENGTH_SHORT).show();
            	    break;
                 case EVENT_BIND_FULL:
                     Toast.makeText(getApplicationContext(), "无法绑定新的牌照", Toast.LENGTH_SHORT).show();
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
	
}
