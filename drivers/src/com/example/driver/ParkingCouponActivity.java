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

	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_parking_coupon);
        mListView=(ListView)findViewById(R.id.list_parking_coupon);  
        List<Map<String, Object>> list=getData();  
        mListView.setAdapter(new ParkingCouponAdapter(this, list)); 
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	}

    public List<Map<String, Object>> getData(){  
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
        for (int i = 1; i <= 1; i++) {  
            Map<String, Object> map=new HashMap<String, Object>();  
            if(i==1){
                map.put("title",  "停车券");
                map.put("starttime", "2017.5.17" + " " + "15:32:26");
                map.put("endtime", "2017.6.17" + " " + "15:32:25");
                map.put("notify", "限京津地区使用");
                map.put("denomination", "5元");
            }
            list.add(map);  
        }  
        return list;  
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
