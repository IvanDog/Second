package com.example.driver;

import com.example.driver.R.drawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RechargeActivity extends Activity {
	private static final int EVENT_PAYMENT_FINISHED=101;
	private static final int EVENT_UPDATE_ACCOUNT_RB_STATE=102;
	private static final int EVENT_MOBILE_PAYMENT_SUCCESS=103;
	private static final int EVENT_MOBILE_PAYMENT_FAIL = 104;
	private final static int EVENT_DISPLAY_WALLET_DETAIL_MONEY=105;
	
	private static final int PAYMENT_TYPE_ACCOUNT=201;
	private static final int PAYMENT_TYPE_ALIPAY=202;
	private static final int PAYMENT_TYPE_WECHATPAY=203;
	private static final int PAYMENT_TYPE_MOBILE=204;
	
	private TextView mWalletDetailMoneyTV;
	private Button mTwentyBT;
	private Button mFiftyBT;
	private Button mHundredBT;
	private Button mConfirmPaymentBT;
	private Button mCancelLeavingBT;
	private RadioGroup mPaymentTypeRG;
	private RadioButton  mAccountPaymentTypeRB;
	private RadioButton mAlipayPaymentTypeRB;
	private RadioButton mWechatpayPaymentRB;;
	private int mPaymentType;
	private UserDbAdapter mUserDbAdapter;
	private String mTeleNumber;
	private int mAccountbalance;
	private int mCharge;
	private Context mContext;
	private AlertDialog mDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);
		mContext=this;
		mUserDbAdapter = new UserDbAdapter(this);
		mUserDbAdapter.open();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mTeleNumber=bundle.getString("telenumber");
		mWalletDetailMoneyTV=(TextView)findViewById(R.id.tv_wallet_detail_money_recharge);
		mTwentyBT=(Button)findViewById(R.id.bt_payment_twenty_recharge);
		mTwentyBT.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v){
		    	if(mFiftyBT.isSelected()){
		    		mFiftyBT.setSelected(false);
		    	}
		    	if(mHundredBT.isSelected()){
		    		mHundredBT.setSelected(false);
		    	}
		    	mTwentyBT.setSelected(!mTwentyBT.isSelected());
		    	mCharge=20;
		    }
		});
		mFiftyBT=(Button)findViewById(R.id.bt_payment_fifty_recharge);
		mFiftyBT.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v){
		    	if(mTwentyBT.isSelected()){
		    		mTwentyBT.setSelected(false);
		    	}
		    	if(mHundredBT.isSelected()){
		    		mHundredBT.setSelected(false);
		    	}
		    	mFiftyBT.setSelected(!mFiftyBT.isSelected());
		    	mCharge=50;
		    }
		});
		mHundredBT=(Button)findViewById(R.id.bt_payment_hundred_recharge);
		mHundredBT.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v){
		    	if(mTwentyBT.isSelected()){
		    		mTwentyBT.setSelected(false);
		    	}
		    	if(mFiftyBT.isSelected()){
		    		mFiftyBT.setSelected(false);
		    	}
				mHundredBT.setSelected(!mHundredBT.isSelected());
				mCharge=100;
		    }
		});
		mPaymentTypeRG=(RadioGroup)findViewById(R.id.rg_payment_type_recharge);
		mAlipayPaymentTypeRB=(RadioButton)findViewById(R.id.rb_alipay_payment_recharge);
		mWechatpayPaymentRB=(RadioButton)findViewById(R.id.rb_wechatpay_payment_recharge);
		mPaymentTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
			@Override 
			public void onCheckedChanged(RadioGroup group, int checkedId){
                if (mAlipayPaymentTypeRB.getId() == checkedId){
		        	mPaymentType = PAYMENT_TYPE_ALIPAY; 
			    }else if (mWechatpayPaymentRB.getId() == checkedId){
		        	mPaymentType = PAYMENT_TYPE_WECHATPAY; 
			    }
                mConfirmPaymentBT.setEnabled(true);
			  } 
			});
		mConfirmPaymentBT=(Button)findViewById(R.id.bt_confirm_payment_leaving_recharge);
		mConfirmPaymentBT.setEnabled(false);
		mConfirmPaymentBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
                if(mPaymentType==PAYMENT_TYPE_ALIPAY){
					new ChargeThread().start();
				}else if(mPaymentType==PAYMENT_TYPE_WECHATPAY){
					new ChargeThread().start();
				}
			}
		});
/*        mCancelLeavingBT=(Button)findViewById(R.id.bt_cancel_payment_recharge);
        mCancelLeavingBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		finish();
        	}
        });*/
		new WalletThread().start();
		 getActionBar().setDisplayHomeAsUpEnabled(true);
	     IntentFilter filter = new IntentFilter();  
	     filter.addAction("ExitApp");  
	     filter.addAction("BackMain");  
	     registerReceiver(mReceiver, filter);
	}

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_MOBILE_PAYMENT_SUCCESS:
                	showPaymentSuccessDialog();
                	break;
                case EVENT_MOBILE_PAYMENT_FAIL:
                	Toast.makeText(getApplicationContext(), "移动支付失败", Toast.LENGTH_SHORT).show();
                	break;
                case 	EVENT_DISPLAY_WALLET_DETAIL_MONEY:
                	mWalletDetailMoneyTV.setText((double)mAccountbalance + "");
                	break;
                default:
                    break;
            }
        }
    };

    public class ChargeThread extends Thread{
    	@Override
    	public void run(){
        	try{
            	mAccountbalance=mAccountbalance + mCharge;
            	if(mUserDbAdapter.updateDriver(mTeleNumber,mAccountbalance)){
    		    	Message msg = new Message();
    		    	msg.what = EVENT_MOBILE_PAYMENT_SUCCESS;
    		    	mHandler.sendMessage(msg);
            	}else{
    		    	Message msg = new Message();
    		    	msg.what = EVENT_MOBILE_PAYMENT_FAIL;
    		    	mHandler.sendMessage(msg);
            	}
        	}catch(Exception e){
        		e.printStackTrace();
        	}
    	}
    }

    public class WalletThread extends Thread{
    	@Override
    	public void run(){
    		mUserDbAdapter.open();
    		Cursor cursor = mUserDbAdapter.getUser(mTeleNumber);
    		try{
    			mAccountbalance = cursor.getInt(cursor.getColumnIndex("accountbalance"));
		    	Message msg = new Message();
		    	msg.what = EVENT_DISPLAY_WALLET_DETAIL_MONEY;
		    	mHandler.sendMessage(msg);
    		}catch(Exception e){
    			e.printStackTrace();
    		}finally{
            	if(cursor!=null){
            		cursor.close();
                }
    		}
    	}
    }
    
    private void showPaymentSuccessDialog(){
    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.dialog_charge_success, null); // 加载自定义的布局文件
        TextView chargeSuccessNotifyTV=(TextView)view.findViewById(R.id.tv_charge_success_notify);
        chargeSuccessNotifyTV.setText("成功充值" + mCharge + "元");
		Button confirmChargeSuccessBT=(Button)view.findViewById(R.id.bt_confirm_success_charge);
		confirmChargeSuccessBT.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v){
		    	finish();
		    }
		});
		AlertDialog.Builder VCdialogBuilder = new AlertDialog.Builder(RechargeActivity.this);
		VCdialogBuilder.setView(view); // 自定义dialog
		mDialog = VCdialogBuilder.create();
		mDialog.show();
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
