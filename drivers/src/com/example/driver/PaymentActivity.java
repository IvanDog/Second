package com.example.driver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
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

public class PaymentActivity extends Activity {
	private static final int EVENT_PASSWD_EMPTY = 101;
	private static final int EVENT_PASSWD_ERROR = 102;
	private static final int EVENT_ACCOUNT_PAYMENT_SUCCESS = 103;
	private static final int EVENT_ACCOUNT_PAYMENT_FAIL = 104;
	private static final int EVENT_PAYMENT_FINISHED=105;
	private static final int EVENT_UPDATE_ACCOUNT_RB_STATE=106;
	private static final int EVENT_MOBILE_PAYMENT_SUCCESS=107;
	private static final int EVENT_MOBILE_PAYMENT_FAIL = 108;
	private static final int PAYMENT_TYPE_ACCOUNT=201;
	private static final int PAYMENT_TYPE_ALIPAY=202;
	private static final int PAYMENT_TYPE_WECHATPAY=203;
	private static final int PAYMENT_TYPE_MOBILE=204;

	private TextView mLicensePlateNumberTV;
	private TextView mStartTimeTV;
	private TextView mLeaveTimeTV;
	private TextView mExpenseStandardTV;
	private TextView mExpenseTV;
	private TextView mExpenseDiscountTV;
	private TextView mExpenseFinalTV;
	private Button mConfirmPaymentBT;
	private Button mCancelLeavingBT;
	private RadioGroup mPaymentTypeRG;
	private RadioButton  mAccountPaymentTypeRB;
	private RadioButton mAlipayPaymentTypeRB;
	private RadioButton mWechatpayPaymentRB;;
	private int mPaymentType;
	private UserDbAdapter mUserDbAdapter;
	private String mTeleNumber;
	private Long mID;
	private String mLicensePlateNumber;
	private int mLocationNumber;
	private String mCarType;
	private String mParkType;
	private String mStartTime;
	private String mLeaveTime;
	private String mExpenseStandard;
	private int mExpense;
	private int mAccountbalance;
	private int mParkingCoupon;
	private Context mContext;
	private AlertDialog mDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		mContext=this;
		mUserDbAdapter = new UserDbAdapter(this);
		mUserDbAdapter.open();
		mLicensePlateNumberTV=(TextView)findViewById(R.id.tv_license_number_leaving);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mTeleNumber=bundle.getString("telenumber");
		mID=bundle.getLong("id");
		mLicensePlateNumber = bundle.getString("licensenumber");
		mStartTime = bundle.getString("starttime");
		mLeaveTime = bundle.getString("leavetime");
		mExpenseStandard = bundle.getString("expensestandard");
		mExpense = bundle.getInt("expense");
		mLicensePlateNumberTV.setText("牌照: " + mLicensePlateNumber);
		mStartTimeTV=(TextView)findViewById(R.id.tv_start_time_leaving);
		mStartTimeTV.setText("入场时间: " + mStartTime);
		mLeaveTimeTV=(TextView)findViewById(R.id.tv_leave_time_leaving);
		mLeaveTimeTV.setText("离场时间: " + mLeaveTime);
		mExpenseStandardTV=(TextView)findViewById(R.id.tv_fee_Scale_leaving);
		mExpenseStandardTV.setText("收费标准: " + mExpenseStandard);
		mExpenseTV=(TextView)findViewById(R.id.tv_expense_leaving);
		mExpenseTV.setText("停车费用: " +mExpense + "元");
		mExpenseDiscountTV=(TextView)findViewById(R.id.tv_expense_discount_leaving);
		mExpenseFinalTV=(TextView)findViewById(R.id.tv_expense_final_leaving);
		mPaymentTypeRG=(RadioGroup)findViewById(R.id.rg_payment_type_leaving);
		mAccountPaymentTypeRB=(RadioButton)findViewById(R.id.rb_account_payment_leaving);
		mAlipayPaymentTypeRB=(RadioButton)findViewById(R.id.rb_alipay_payment_leaving);
		mWechatpayPaymentRB=(RadioButton)findViewById(R.id.rb_wechatpay_payment_leaving);
		mPaymentTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
			@Override 
			public void onCheckedChanged(RadioGroup group, int checkedId){
			    if (mAccountPaymentTypeRB.getId() == checkedId) {
			    	mPaymentType = PAYMENT_TYPE_ACCOUNT; 
		        }else if (mAlipayPaymentTypeRB.getId() == checkedId){
		        	mPaymentType = PAYMENT_TYPE_ALIPAY; 
			    }else if (mWechatpayPaymentRB.getId() == checkedId){
		        	mPaymentType = PAYMENT_TYPE_WECHATPAY; 
			    }
			    mConfirmPaymentBT.setEnabled(true);
			  } 
			});
		mConfirmPaymentBT=(Button)findViewById(R.id.bt_confirm_payment_leaving);
		mConfirmPaymentBT.setEnabled(false);
		mConfirmPaymentBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(CheckPaymentState()){
    				Message msg = new Message();
                    msg.what = EVENT_PAYMENT_FINISHED;
                    mHandler.sendMessage(msg);
					Intent intent = new Intent(PaymentActivity.this,MainActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("telenumber", mTeleNumber);
					intent.putExtras(bundle);
					startActivity(intent);
					return;
				}
				if(mPaymentType==PAYMENT_TYPE_ACCOUNT){
					showAccountPaymentDialog();
				}else if(mPaymentType==PAYMENT_TYPE_ALIPAY){
					makeMobilePay();
				}else if(mPaymentType==PAYMENT_TYPE_WECHATPAY){
					makeMobilePay();
				}
			}
		});
/*        mCancelLeavingBT=(Button)findViewById(R.id.bt_cancel_payment);
        mCancelLeavingBT.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
				if(CheckPaymentState()){
    				Message msg = new Message();
                    msg.what = EVENT_PAYMENT_FINISHED;
                    mHandler.sendMessage(msg);
				}else{
					finish();
				}
        	}
        });*/
		new UpdateAccountStateThread().start();
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
                case EVENT_PASSWD_EMPTY:
                	Toast.makeText(getApplicationContext(), "密码为空", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_PASSWD_ERROR:
                	Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_ACCOUNT_PAYMENT_SUCCESS:
                	mDialog.dismiss();
                	//Toast.makeText(getApplicationContext(), "余额支付成功", Toast.LENGTH_SHORT).show();
                	updateUserAccount();
                	updateUserCoupon();
            		Intent accountIntent = new Intent(PaymentActivity.this,PaymentSuccessActivity.class);
            		Bundle accountBundle = new Bundle();
            		accountBundle.putInt("expense", mExpense);
            		accountBundle.putString("telenumber", mTeleNumber);
            		accountIntent.putExtras(accountBundle);
            		startActivity(accountIntent);
                	break;
                case EVENT_ACCOUNT_PAYMENT_FAIL:
                	mDialog.dismiss();
                	Toast.makeText(getApplicationContext(), "余额支付失败", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_MOBILE_PAYMENT_SUCCESS:
                	updateUserCoupon();
            		Intent mobileIntent = new Intent(PaymentActivity.this,PaymentSuccessActivity.class);
            		Bundle mobileBundle = new Bundle();
            		mobileBundle.putInt("expense", mExpense);
            		mobileBundle.putString("telenumber", mTeleNumber);
            		mobileIntent.putExtras(mobileBundle);
            		startActivity(mobileIntent);
                	break;
                case EVENT_MOBILE_PAYMENT_FAIL:
                	mDialog.dismiss();
                	Toast.makeText(getApplicationContext(), "移动支付失败", Toast.LENGTH_SHORT).show();
                	break;
                case EVENT_PAYMENT_FINISHED:
                	Toast.makeText(getApplicationContext(), "该订单已支付", Toast.LENGTH_SHORT).show();
    				Intent intent = new Intent(PaymentActivity.this,MainActivity.class);
    				Bundle bundle = new Bundle();
    				bundle.putString("telenumber", mTeleNumber);
    				intent.putExtras(bundle);
    				startActivity(intent);
                	break;
                case EVENT_UPDATE_ACCOUNT_RB_STATE:
        			mAccountPaymentTypeRB.setText("余额不足");
        			mAccountPaymentTypeRB.setEnabled(false);
                	break;
                default:
                    break;
            }
        }
    };

    public class UpdateAccountStateThread extends Thread{
    	@Override
    	public void run(){
    		Cursor cursor = mUserDbAdapter.getUser(mTeleNumber);
			mAccountbalance =  cursor.getInt(cursor.getColumnIndex("accountbalance"));
			mParkingCoupon =  cursor.getInt(cursor.getColumnIndex("parkingcoupon"));
			if(mParkingCoupon>=1){
				mExpenseDiscountTV.setText("停车券抵扣: 5元" );
				mExpense = mExpense -5;
				mParkingCoupon = mParkingCoupon-1;
			}else{
				mExpenseDiscountTV.setText("无停车券可用");
				mExpenseDiscountTV.setEnabled(false);
			}
			mExpenseFinalTV.setText("支付金额: " + mExpense + "元");
			if(mAccountbalance<mExpense){
			    Message msg = new Message();
			    msg.what = EVENT_UPDATE_ACCOUNT_RB_STATE;
			    mHandler.sendMessage(msg);
			}
    	}
    }
    
    
    private void showAccountPaymentDialog(){
    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.dialog_account_payment, null); // 加载自定义的布局文件
		final EditText passwdET = (EditText)view.findViewById(R.id.et_input_passwd);
		final Button finishPaymentBT=(Button)view.findViewById(R.id.bt_finish_account_payment);
		final AlertDialog.Builder VCdialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
		VCdialogBuilder.setView(view); // 自定义dialog
		finishPaymentBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Cursor cursor = mUserDbAdapter.getUser(mTeleNumber);
				String passwd = cursor.getString(cursor.getColumnIndex("passwd"));
            	if(passwdET.getText().toString().equals("")){
            		Message msg = new Message();
            		msg.what = EVENT_PASSWD_EMPTY;
            		mHandler.sendMessage(msg);
            	}else if(!(passwdET.getText().toString()).equals(passwd)){
                    Message msg = new Message();
                	msg.what = EVENT_PASSWD_ERROR;
                	mHandler.sendMessage(msg);
            	}else{
            		if(mUserDbAdapter.updateParkingDetail(mID,  mLeaveTime, mExpense, "余额支付")){
        				Message msg = new Message();
                        msg.what = EVENT_ACCOUNT_PAYMENT_SUCCESS;
                        mHandler.sendMessage(msg);
            		}else{
        				Message msg = new Message();
                        msg.what = EVENT_ACCOUNT_PAYMENT_FAIL;
                        mHandler.sendMessage(msg);
            		}
            	}
			}
		});
		mDialog = VCdialogBuilder.create();
		mDialog.show();
    }

    public void makeMobilePay(){
		if(mUserDbAdapter.updateParkingDetail(mID,  mLeaveTime, mExpense, "移动支付")){
			Message msg = new Message();
            msg.what = EVENT_MOBILE_PAYMENT_SUCCESS;
            mHandler.sendMessage(msg);
		}else{
			Message msg = new Message();
            msg.what = EVENT_MOBILE_PAYMENT_FAIL;
            mHandler.sendMessage(msg);
		}
    }
    
    public boolean CheckPaymentState(){
    	boolean paymentState = false;
    	Cursor cursor = mUserDbAdapter.getParkingDetailByLisenceNumber(mLicensePlateNumber);
        try {
        	      if(cursor.getString(cursor.getColumnIndex("paymentpattern")).equals("未付")){
        	    	  paymentState= false;
        	      }else{
        	    	  paymentState= true;
        	      }
        }
        catch (Exception e) {
                e.printStackTrace();
        } finally{
            	if(cursor!=null){
            		cursor.close();
                }
        }
        return paymentState;
    }
    
    public void updateUserAccount(){
    	mAccountbalance=mAccountbalance-mExpense;
    	mUserDbAdapter.updateDriver(mTeleNumber,mAccountbalance);
    }
    
    public void updateUserCoupon(){
        mUserDbAdapter.updateDriverCoupon(mTeleNumber, mParkingCoupon);
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
