package com.example.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.driver.R.drawable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserInformationActivity extends Activity {
	private UserDbAdapter mUserDbAdapter;
	private UserInformationListAdapter mUserInformationListAdapter;
	private ListView mListView;
	private String mTeleNumber;
	private Context mContext;
	private String mNickName = "Yifan";
	private Drawable mHeadPortrait = null;
	private final static int EVENT_UPDATE_DISPLAY = 101;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_information);
		mUserDbAdapter = new UserDbAdapter(this);
		mUserDbAdapter.open();
		mContext = this;
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
        	mTeleNumber = bundle.getString("telenumber");
        }
        //new UpdateDisplayThread().start();
		mListView=(ListView)findViewById(R.id.list_user_information);  
        List<Map<String, Object>> list=getUserInformationData();  
        mUserInformationListAdapter = new UserInformationListAdapter(this, list);
        mListView.setAdapter(mUserInformationListAdapter); 
        mListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
             	Map<String,Object> map=(Map<String,Object>)mListView.getItemAtPosition(arg2);
                String userInformation=(String)map.get("userInformation");
                if(userInformation.equals("头像: ")){
                 	Intent intent = new Intent(UserInformationActivity.this,HeadPortraitActivity.class);
                 	Bundle bundle = new Bundle();
                 	bundle.putString("telenumber", mTeleNumber);
                 	intent.putExtras(bundle);
                 	startActivity(intent);
                 }else if(userInformation.equals("昵称: ")){
                 	Intent intent = new Intent(UserInformationActivity.this,SetNickNameActivity.class);
                 	Bundle bundle = new Bundle();
                 	bundle.putString("telenumber", mTeleNumber);
                 	intent.putExtras(bundle);
                 	startActivity(intent);
                 }else if(userInformation.equals("重置密码: ")){
                	Intent intent = new Intent(UserInformationActivity.this,ResetPasswdActivity.class);
                	Bundle bundle = new Bundle();
                	bundle.putString("telenumber", mTeleNumber);
                	intent.putExtras(bundle);
                	startActivity(intent);
                 }
            }
        });
        new UpdateDisplayThread().start();
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	}
	
    public List<Map<String, Object>> getUserInformationData(){  
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
        for (int i = 1; i <= 4; i++) {  
            Map<String, Object> map=new HashMap<String, Object>();  
            if(i==1){
                map.put("userInformation",  "头像: ");
                map.put("userInformationDetail",  getHeadPortrait());
                map.put("userInformationSpreadImage",  drawable.ic_chevron_right_black_24dp);
            }else if(i==2){
                map.put("userInformation",  "昵称: ");
                map.put("userInformationDetail",  /*getNickName()*/mNickName);
                map.put("userInformationSpreadImage",  drawable.ic_chevron_right_black_24dp);
            }else if(i==3){
            	map.put("userInformation",  "绑定手机: ");
            	map.put("userInformationDetail",  mTeleNumber);
            	map.put("userInformationSpreadImage",  null);
            }else if(i==4){
            	map.put("userInformation",  "重置密码: ");
            	map.put("userInformationDetail",  "**********");
            	map.put("userInformationSpreadImage",  drawable.ic_chevron_right_black_24dp);
            }
            list.add(map);  
        }  
        return list;  
      }
    
    public String getNickName(){
		mUserDbAdapter.open();
		Cursor cursor = mUserDbAdapter.getUser(mTeleNumber);
		try{
			mNickName = cursor.getString(cursor.getColumnIndex("nickname"));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
        	if(cursor!=null){
        		cursor.close();
            }
		}
		return mNickName;
    }
    
    public Drawable getHeadPortrait(){
		mUserDbAdapter.open();
		Cursor cursor = mUserDbAdapter.getUser(mTeleNumber);
		try{
			byte[] headPortraitByteArray = cursor.getBlob(cursor.getColumnIndex("headportrait"));
			if(headPortraitByteArray!=null){
				mHeadPortrait=bytes2Drawable(headPortraitByteArray);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
        	if(cursor!=null){
        		cursor.close();
            }
		}
		return mHeadPortrait;
    }
    
    // byte[]转换成Drawable  
    public Drawable bytes2Drawable(byte[] b) {  
        Bitmap bitmap = this.bytes2Bitmap(b);  
        return this.bitmap2Drawable(bitmap);  
    }
    
    // byte[]转换成Bitmap  
    public Bitmap bytes2Bitmap(byte[] b) {  
        if (b.length != 0) {  
            return BitmapFactory.decodeByteArray(b, 0, b.length);  
        }  
        return null;  
    }  
    
    // Bitmap转换成Drawable  
    public Drawable bitmap2Drawable(Bitmap bitmap) {  
        BitmapDrawable bd = new BitmapDrawable(bitmap);  
        Drawable d = (Drawable) bd;  
        return d;  
    } 
    public class UpdateDisplayThread extends Thread{
    	@Override
    	public void run(){
    		do{
    			Cursor cursor = null;
        		try{
            		cursor = mUserDbAdapter.getUser(mTeleNumber);
            		byte[] headPortraitByteArray = cursor.getBlob(cursor.getColumnIndex("headportrait"));
        			if(headPortraitByteArray!=null){
        				mHeadPortrait=bytes2Drawable(cursor.getBlob(cursor.getColumnIndex("headportrait")));
        			}
            		if((mNickName!=cursor.getString(cursor.getColumnIndex("nickname")))){
            			mNickName = cursor.getString(cursor.getColumnIndex("nickname"));
            		}
    			    Message msg = new Message();
    				msg.what=EVENT_UPDATE_DISPLAY;
    				mHandler.sendMessage(msg);
        			Thread.sleep(1000);
        		}catch(Exception e){
        			e.printStackTrace();
        		}finally{
                	if(cursor!=null){
                		cursor.close();
                    }
        		}
    		}while(true);
    	}
    }
    
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_UPDATE_DISPLAY:
        	        List<Map<String, Object>> list=getUserInformationData();  
        	        mUserInformationListAdapter = new UserInformationListAdapter(mContext, list);
        	        mListView.setAdapter(mUserInformationListAdapter); 
        			mUserInformationListAdapter.notifyDataSetChanged();
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
