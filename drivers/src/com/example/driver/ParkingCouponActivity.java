package com.example.driver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.driver.R.drawable;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ParkingCouponActivity extends FragmentActivity {
    private ListView mListView = null;
    private TextView mEmptyCouponNotifyTV;
	private UserDbAdapter mUserDbAdapter;
	private String mTeleNumber;
	private int mParkingCoupon;

	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        mUserDbAdapter = new UserDbAdapter(this);
        setContentView(R.layout.activity_parking_coupon);
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
        	mTeleNumber = bundle.getString("telenumber");
        }
        mListView=(ListView)findViewById(R.id.list_parking_coupon);  
        mEmptyCouponNotifyTV=(TextView)findViewById(R.id.tv_empty_coupon_notify_coupon);  
        List<Map<String, Object>> list=getData();  
        mListView.setAdapter(new ParkingCouponAdapter(this, list)); 
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	}

    public List<Map<String, Object>> getData(){  
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
        if(getParkingCouponNumber()!=0){
            for (int i = 1; i <= getParkingCouponNumber(); i++) {  
                Map<String, Object> map=new HashMap<String, Object>();  
                //if(i==1){
                    map.put("title",  "停车券");
                    map.put("starttime", "2017.5.22" + " " + "15:32:26");
                    map.put("endtime", "~2017.6.22" + " " + "15:32:25");
                    map.put("notify", "限天津、北京地区使用");
                    map.put("denomination", "5元");
                    map.put("coupondetail", "1.本停车券不找零、不兑现。" + "\n" + "2.每次停车仅限使用一张停车券。" + "\n" + "3.本停车券的解释权归易华录所有。");
                //}
                list.add(map);  
            }  
        	mListView.setVisibility(View.VISIBLE);
        	mEmptyCouponNotifyTV.setVisibility(View.GONE);
        }else{
        	mListView.setVisibility(View.GONE);
        	mEmptyCouponNotifyTV.setVisibility(View.VISIBLE);
        }
        return list;  
      }
    
    public int getParkingCouponNumber(){
		mUserDbAdapter.open();
		Cursor cursor = null;
		try{
    		cursor = mUserDbAdapter.getUser(mTeleNumber);
			mParkingCoupon = cursor.getInt(cursor.getColumnIndex("parkingcoupon"));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
        	if(cursor!=null){
        		cursor.close();
            }
		}
		mUserDbAdapter.close();
		return mParkingCoupon;
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
	
}
