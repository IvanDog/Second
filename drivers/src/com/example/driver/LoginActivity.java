package com.example.driver;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"gouyf@ehualu.com:123456"};

	/**
	 * The default email to populate the email field with.
	 */
	//public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mTeleNumber = null;
	private String mPassword;

	// UI references.
	private EditText mTeleNumberET;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
    private CheckBox mKeepUserinfo;
    private CheckBox mKeepPassword;
    private TextView mForgetPasswdTV;
    private Button mRegisterBT;
	private UserDbAdapter mUserDbAdapter;
	private Button mApplyVerificationCodeBT;
	private AlertDialog mDialog;
	private Object mLock = new Object();
	private Thread mTimeThread;
	private  boolean mUpdateTime =true;
	private int mThreadTime = 60;
    private static final String SAVE_FILE_NAME = "save_spref";
    private static final int EVENT_EXIST_NUMBER=101;
    private static final int EVENT_EMPTY_TELE_NUMBER=102;
    private static final int EVENT_EMPTY_VERIFICATION_CODE=103;
    private static final int EVENT_REGISTER_SUCCESS=104;
    private static final int EVENT_EMPTY_PASSWD=105;
    private static final int EVENT_EMPTY_RE_PASSWD=106;
    private static final int EVENT_INCONSISTENT_PASSWD=107;
    private static final int EVENT_NO_EXIST_USER=108;
    
	private static final int ATTENDANCE_TYPE_START=301;
	private static final int ATTENDANCE_TYPE_END=302;
	private static final int ERROR_TYPE_TELE=401;
	private static final int ERROR_TYPE_PASSWD=402;
	private static final int ERROR_TYPE_NO_ERROR=403;
	private static final int ERROR_TYPE_EMPTY_TELE=404;
	
	private static final int EVENT_UPDATE_TIME=501;
	private int mErrorType = ERROR_TYPE_NO_ERROR;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserDbAdapter = new UserDbAdapter(this);
		setContentView(R.layout.activity_login);
		mTeleNumberET = (EditText) findViewById(R.id.et_tele_login);
		mTeleNumberET.setText(mTeleNumber);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});
		mRegisterBT=(Button)findViewById(R.id.register_button);
		mRegisterBT.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	if(mTimeThread!=null){
                	mTimeThread.interrupt();
            	}
		    	mUpdateTime = false;
		    	mThreadTime = 60;
		    	showRegisterDialog(false);
            }
		});
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		mForgetPasswdTV=(TextView)findViewById(R.id.tv_forget_passwd);
		mForgetPasswdTV.setOnClickListener(new OnClickListener(){
		    @Override
		    public void onClick(View v){
            	if(mTimeThread!=null){
                	mTimeThread.interrupt();
            	}
		    	mUpdateTime = false;
		    	mThreadTime = 60;
		    	showRegisterDialog(true);
		    }
		});
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
        mKeepUserinfo = (CheckBox) findViewById(R.id.ck_userinfo);
        mKeepPassword = (CheckBox) findViewById(R.id.ck_password);
        mKeepUserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeBoolean(mKeepUserinfo.isChecked(), mKeepPassword.isChecked());
            }
        });
        mKeepPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeBoolean(mKeepUserinfo.isChecked(), mKeepPassword.isChecked());
            }
        });
        initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

    private void initView() {
        if (readBoolean("isuserinfo")) {
        	mKeepUserinfo.setChecked(true);
        	mTeleNumberET.setText(readData("userinfo").toString());
        }
        if (readBoolean("ispassword")) {
        	mKeepPassword.setChecked(true);
            mPasswordView.setText(readData("password").toString());
        }
    }

    private String readData(String data) {
        SharedPreferences pref = getSharedPreferences(SAVE_FILE_NAME, MODE_MULTI_PROCESS);
        String str = pref.getString(data, "");
        return str;
    }

    private boolean writeData(String userinfo, String password, boolean isUserinfo, boolean isPassword) {
        SharedPreferences.Editor share_edit = getSharedPreferences(SAVE_FILE_NAME,
                MODE_MULTI_PROCESS).edit();
        share_edit.putString("userinfo", userinfo);
        share_edit.putString("password", password);
        share_edit.putBoolean("isuserinfo", isUserinfo);
        share_edit.putBoolean("ispassword", isPassword);
        share_edit.commit();
        return true;
    }

    private boolean readBoolean(String data) {
        SharedPreferences pref = getSharedPreferences(SAVE_FILE_NAME, MODE_MULTI_PROCESS);
        return pref.getBoolean(data, false);
    }

    private void writeBoolean(boolean isUserinfo, boolean isPassword) {
        SharedPreferences.Editor share_edit = getSharedPreferences(SAVE_FILE_NAME,
                MODE_MULTI_PROCESS).edit();
        share_edit.putBoolean("isuserinfo", isUserinfo);
        share_edit.putBoolean("ispassword", isPassword);
        share_edit.commit();
    }

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_EXIST_NUMBER:
                	Toast.makeText(getApplicationContext(), "该号码已注册", Toast.LENGTH_SHORT).show();
                	mDialog.dismiss();
                    break;
                case EVENT_EMPTY_TELE_NUMBER:
                	Toast.makeText(getApplicationContext(), "手机号不可为空", Toast.LENGTH_SHORT).show();
                    break;
                case EVENT_EMPTY_VERIFICATION_CODE:
                	Toast.makeText(getApplicationContext(), "验证码不可为空", Toast.LENGTH_SHORT).show();
                    break;
                case EVENT_REGISTER_SUCCESS:
                    showSetPasswdDialog(false);
                    mUpdateTime=false;
                    mApplyVerificationCodeBT.setText("验证码");
                    mApplyVerificationCodeBT.setEnabled(true);
                	break;
                case EVENT_EMPTY_PASSWD:
                	Toast.makeText(getApplicationContext(), "密码不可为空", Toast.LENGTH_SHORT).show();
                    break;
                case EVENT_EMPTY_RE_PASSWD:
                	Toast.makeText(getApplicationContext(), "确认密码不可为空", Toast.LENGTH_SHORT).show();
                    break;
                case EVENT_INCONSISTENT_PASSWD:
                	Toast.makeText(getApplicationContext(), "请确保输入一致", Toast.LENGTH_SHORT).show();
                    break;
                case EVENT_NO_EXIST_USER:
                	Toast.makeText(getApplicationContext(), "该用户不存在", Toast.LENGTH_SHORT).show();
                	mDialog.dismiss();
                    break;
                case EVENT_UPDATE_TIME:
                	mApplyVerificationCodeBT.setText(msg.obj + "s");
                    break;
                default:
                    break;
            }
        }
    };
    
    public void showRegisterDialog(final boolean forget){
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.dialog_verification_code, null); // 加载自定义的布局文件
		final EditText teleNumberET = (EditText)view.findViewById(R.id.et_input_tele_number);
		final EditText verificationCodeET = (EditText)view.findViewById(R.id.et_input_verification_code);
		mApplyVerificationCodeBT = (Button)view.findViewById(R.id.bt_apply_verification_code);
		mApplyVerificationCodeBT.setText("验证码");
		final Button nextRegisterStepBT=(Button)view.findViewById(R.id.bt_next_register_step);
		nextRegisterStepBT.setEnabled(false);
		final CheckBox agreeCB = (CheckBox)view.findViewById(R.id.cb_agree);
		agreeCB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            	nextRegisterStepBT.setEnabled(agreeCB.isChecked());
            }
        });
		if(forget){
			agreeCB.setVisibility(View.GONE);
			nextRegisterStepBT.setEnabled(true);
		}else{
			agreeCB.setVisibility(View.VISIBLE);
		}
		final AlertDialog.Builder VCdialogBuilder = new AlertDialog.Builder(LoginActivity.this);
		VCdialogBuilder.setView(view); // 自定义dialog
		//VCdialogBuilder.setCancelable(false);//点击对话框外面的区域无效
		nextRegisterStepBT.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mUserDbAdapter.open();
            	if(teleNumberET.getText().toString().equals("")){
            		Message msg = new Message();
            		msg.what = EVENT_EMPTY_TELE_NUMBER;
            		mHandler.sendMessage(msg);
            	}else if(verificationCodeET.getText().toString().equals("")){
            		Message msg = new Message();
            		msg.what = EVENT_EMPTY_VERIFICATION_CODE;
            		mHandler.sendMessage(msg);
            	}else if((mUserDbAdapter.getUser(teleNumberET.getText().toString())).getCount()!=0){
            		if(forget){
            			mDialog.dismiss();
            			mTeleNumber = teleNumberET.getText().toString();
            			showSetPasswdDialog(true);
            		}else{
                		Message msg = new Message();
                		msg.what = EVENT_EXIST_NUMBER;
                		mHandler.sendMessage(msg);
            		}
            	}else{
            		if(forget){
                		Message msg = new Message();
                		msg.what = EVENT_NO_EXIST_USER;
                		mHandler.sendMessage(msg);
            		}else{
                		mTeleNumber = teleNumberET.getText().toString();
                    	long  result = mUserDbAdapter.insertDriver(teleNumberET.getText().toString(), null, null, null, 0, 10, null, null);
                    	if(result != -1){
                    		Log.e("yifan","insert ok");
                    		Message msg = new Message();
                    		msg.what = EVENT_REGISTER_SUCCESS;
                    		mHandler.sendMessage(msg);
                    		mDialog.dismiss();
                    	}else{
                    		Log.e("yifan","insert fail");
                    	}
            		}
            	}
            	mUserDbAdapter.close(); 
			}
		});
		class TimeThread extends Thread {
	        @Override
	        public void run () {
	        	synchronized(mLock) {
		                try {
		                	while(mThreadTime>=0 && mUpdateTime){
			                    Thread.sleep(1000);
			                    Message msg = new Message();
			                    msg.what = EVENT_UPDATE_TIME;
			                    msg.obj = mThreadTime--;
								mHandler.sendMessage(msg);
								Log.e("gouyifan","update time");
		                	}
		                }
		                catch (InterruptedException e) {
		                    e.printStackTrace();
		                }
	        	}
	        }
	    }
		mApplyVerificationCodeBT.setOnClickListener(new OnClickListener() {
			@Override
		    public void onClick(View v) {
				mApplyVerificationCodeBT.setEnabled(false);
				mUpdateTime=true;
				mTimeThread=new TimeThread();
				mTimeThread.start();
		    }
	     });
		mDialog = VCdialogBuilder.create();
		mDialog.show();
    }
    
    public void showSetPasswdDialog(final boolean forget){
    	LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.dialog_passwd_set, null); // 加载自定义的布局文件
		final EditText setPasswdET = (EditText)view.findViewById(R.id.et_set_passwd);
		final EditText reInputPasswdET = (EditText)view.findViewById(R.id.et_re_input_passwd);
		final Button finishRegisterButton=(Button)view.findViewById(R.id.bt_finish_register);
		final AlertDialog.Builder VCdialogBuilder = new AlertDialog.Builder(LoginActivity.this);
		VCdialogBuilder.setView(view); // 自定义dialog
		finishRegisterButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mUserDbAdapter.open();
            	if(setPasswdET.getText().toString().equals("")){
            		Message msg = new Message();
            		msg.what = EVENT_EMPTY_PASSWD;
            		mHandler.sendMessage(msg);
            	}else if(reInputPasswdET.getText().toString().equals("")){
            		Message msg = new Message();
            		msg.what = EVENT_EMPTY_RE_PASSWD;
            		mHandler.sendMessage(msg);
            	}else if(!(setPasswdET.getText().toString()).equals(reInputPasswdET.getText().toString())){
            		Message msg = new Message();
            		msg.what = EVENT_INCONSISTENT_PASSWD;
            		mHandler.sendMessage(msg);
            	}else{           		
            		boolean result = mUserDbAdapter.updateDriverPasswd(mTeleNumber, setPasswdET.getText().toString());
            		if(result){
            			if(forget){
            				Toast.makeText(getApplicationContext(), "更改密码成功", Toast.LENGTH_SHORT).show();
                        	mDialog.dismiss();
            			}else{
                        	Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        	mDialog.dismiss();
            			}
            		}else{
            			if(forget){
            				Toast.makeText(getApplicationContext(), "更改密码失败", Toast.LENGTH_SHORT).show();
            			}else{
                        	Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
            			}
            		}
            	}
            	mUserDbAdapter.close(); 
			}
		});
		mDialog = VCdialogBuilder.create();
		mDialog.show();
    }
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mTeleNumberET.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mTeleNumber = mTeleNumberET.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
/*		if (TextUtils.isEmpty(mEmail)) {
			mTeleNumberET.setError(getString(R.string.error_field_required));
			focusView = mTeleNumberET;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mTeleNumberET.setError(getString(R.string.error_invalid_email));
			focusView = mTeleNumberET;
			cancel = true;
		}*/

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mTeleNumber)) {
					// Account exists, return true if the password matches.
					if(pieces[1].equals(mPassword)){
						return true;
					}else{
						mErrorType = ERROR_TYPE_PASSWD;
					}
				}else{
					mErrorType = ERROR_TYPE_TELE;
				}
			}

			mUserDbAdapter.open();
			Cursor cursor = mUserDbAdapter.getUser();
			do{
				String teleNumber = cursor.getString(cursor.getColumnIndex("telenumber"));
				String passwd = cursor.getString(cursor.getColumnIndex("passwd"));
				if(teleNumber.equals(mTeleNumber)){
					if(passwd.equals(mPassword)){
						return true;
					}else{
						mErrorType = ERROR_TYPE_PASSWD;
						return false;
					}
				}else{
					if(("").equals(mTeleNumber)){
						mErrorType = ERROR_TYPE_EMPTY_TELE;
					}else{
						mErrorType = ERROR_TYPE_TELE;
					}
				}
			}while(cursor.moveToNext());
			mUserDbAdapter.close();
			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				new SQLThread().start();
				String userinfo = mTeleNumberET.getText().toString();
		        String password = mPasswordView.getText().toString();
		        writeData(userinfo, password, mKeepUserinfo.isChecked(), mKeepPassword.isChecked());
				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("telenumber", userinfo);
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
			  if(mErrorType == ERROR_TYPE_EMPTY_TELE){
					mTeleNumberET.setError(getString(R.string.error_empty_tele));
					mTeleNumberET.requestFocus();
			  }else if(mErrorType == ERROR_TYPE_TELE){
					mTeleNumberET.setError(getString(R.string.error_incorrect_tele));
					mTeleNumberET.requestFocus();
			   }else if(mErrorType == ERROR_TYPE_PASSWD){
					mPasswordView
					.setError(getString(R.string.error_incorrect_password));
					mPasswordView.requestFocus();
				}
			}
			mErrorType=ERROR_TYPE_NO_ERROR;
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
    public class SQLThread extends Thread {
        @Override
        public void run () {
        	    mUserDbAdapter.open();
        	    mUserDbAdapter.deleteAllParkingDetail();
            	String licensePlate = "津HG9025";
            	String teleNumber = "13512494993";
            	String carType = "小客车";
            	String parkingType = "普通停车";
            	String parkingName = "天津市-津南区-易华录停车场";
            	String parkingNumber = "P1234";
            	int locationNumber = 0;
            	String startTime = null;
            	String leaveTime = null;
            	String expenseStandard="5元/次";
            	int expense=5;
            	String paymentPattern = null;
            	CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
            	String time = sysTimeStr.toString();
            	for(int i=1;i<=3;i++){
            		if(i==1){
            			paymentPattern="未付";
            			locationNumber = 5;
            			startTime = time;
            			expense = 0;
            		}else if(i==2){
            			paymentPattern="移动支付";
            			locationNumber = 8;
            			startTime = "2017-05-16 14:26:15";
            			leaveTime = "2017-05-16 15:17:22";
               			expense = 5;
            		}else if(i==3){
            			paymentPattern="现金支付";
            			locationNumber = 12;
            			startTime = "2017-05-18 17:33:19";
            			leaveTime = "2017-05-18 18:31:27";
               			expense = 5;
            		}
                    try {
            		        mUserDbAdapter.insertParkingDeatail(licensePlate, teleNumber, carType, parkingType, 
            				    parkingName, parkingNumber, locationNumber, startTime, leaveTime, expenseStandard, 
            				    expense, paymentPattern);
            		        Log.e("yifan","insert");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            	}
            	mUserDbAdapter.close();
            }
        }
    
}
