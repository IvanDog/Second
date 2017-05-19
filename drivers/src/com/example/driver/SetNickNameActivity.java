package com.example.driver;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetNickNameActivity extends Activity {
	private static final int EVENT_SET_NICKNAME_SUCCESS = 101;
	private static final int EVENT_SET_NICKNAME_FAIL = 102;
	private static final int EVENT_NICKNAME_EMPTY = 103;
	private UserDbAdapter mUserDbAdapter;
	private EditText mNickNameET;
	private Button mConfirmBT;
	private String mTeleNumber;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserDbAdapter = new UserDbAdapter(this);
		mUserDbAdapter.open();
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
        	mTeleNumber = bundle.getString("telenumber");
        }
		setContentView(R.layout.activity_set_nickname);
		mNickNameET=(EditText)findViewById(R.id.et_set_nickname);
		mConfirmBT=(Button)findViewById(R.id.bt_confirm_nickname);
		mConfirmBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(mNickNameET.getText().toString()==null){
					Message msg = new Message();
					msg.what=EVENT_NICKNAME_EMPTY;
					mHandler.sendMessage(msg);
					return;
				}
				if(mUserDbAdapter.updateDriverNickName(mTeleNumber,mNickNameET.getText().toString())){
					Message msg = new Message();
					msg.what=EVENT_SET_NICKNAME_SUCCESS;
					mHandler.sendMessage(msg);
				}else{
					Message msg = new Message();
					msg.what=EVENT_SET_NICKNAME_FAIL;
					mHandler.sendMessage(msg);
				}
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	}
	
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_SET_NICKNAME_SUCCESS:
            	    Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
/*                	Intent intent = new Intent(SetNickNameActivity.this,UserInformationActivity.class);
                	Bundle bundle = new Bundle();
                	bundle.putString("telenumber", mTeleNumber);
                	intent.putExtras(bundle);
                	startActivity(intent);*/
            	    finish();
            	    break;
                case EVENT_SET_NICKNAME_FAIL:
            	    Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
            	    break;
                case EVENT_NICKNAME_EMPTY:
            	    Toast.makeText(getApplicationContext(), "设置不可为空", Toast.LENGTH_SHORT).show();
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
