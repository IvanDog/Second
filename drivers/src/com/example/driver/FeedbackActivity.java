package com.example.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class FeedbackActivity extends Activity {
	private String mTeleNumber;
	private Button mConfirmBT;
	private Button mCancelBT;
	private static final int EVENT_CONFIRM_SUCCESS = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
        	mTeleNumber = bundle.getString("telenumber");
        }
        mConfirmBT=(Button)findViewById(R.id.bt_confirm_feedback);
        mConfirmBT.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
            	Message msg = new Message();
            	msg.what=EVENT_CONFIRM_SUCCESS;
            	mHandler.sendMessage(msg);
            }
        });
        mCancelBT=(Button)findViewById(R.id.bt_cancel_feedback);
        mCancelBT.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
            	finish();
            }
        });
		 getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_CONFIRM_SUCCESS:
                	Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_SHORT).show();
                	finish();
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
