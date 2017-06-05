package com.example.driver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class HeadPortraitActivity extends Activity {
	    /* 头像文件 */
	    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";

	    /* 请求识别码 */
	    private static final int CODE_GALLERY_REQUEST = 0xa0;//本地
	    private static final int CODE_CAMERA_REQUEST = 0xa1;//拍照
	    private static final int CODE_RESULT_REQUEST = 0xa2;//最终裁剪后的结果

		private static final int EVENT_SET_PORTRAIT_SUCCESS = 101;
		private static final int EVENT_SET_PORTRAIT_FAIL = 102;
		
	    // 裁剪后图片的宽(X)和高(Y),300 X 300的正方形。
	    private static int output_X = 300;
	    private static int output_Y = 300;

		private UserDbAdapter mUserDbAdapter;
	    private ImageView headImage = null;
	    private Button localBT;
	    private Button cameraBT;
	    private String mTeleNumber;
	    private Bitmap mPhoto = null;


	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
			mUserDbAdapter = new UserDbAdapter(this);
			mUserDbAdapter.open();
	        setContentView(R.layout.activity_set_head_portrait);
	        Intent intent = getIntent();
	        Bundle bundle=intent.getExtras();
	        if(bundle!=null){
	        	mTeleNumber = bundle.getString("telenumber");
	        }
	        headImage = (ImageView) findViewById(R.id.iv_user_head_portrait);
	        if(getHeadPortrait()!=null){
	        	headImage.setImageBitmap(getHeadPortrait());
	        }
	        localBT = (Button) findViewById(R.id.bt_local_photo);
	        localBT.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                choseHeadImageFromGallery();
	            }
	        });

	        cameraBT = (Button) findViewById(R.id.bt_camera);
	        cameraBT.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                choseHeadImageFromCameraCapture();
	            }
	        });
			getActionBar().setDisplayHomeAsUpEnabled(true); 
		     IntentFilter filter = new IntentFilter();  
		     filter.addAction("ExitApp");  
		     filter.addAction("BackMain");  
		     registerReceiver(mReceiver, filter);
	    }

	    // 从本地相册选取图片作为头像
	    private void choseHeadImageFromGallery() {
	        Intent intentFromGallery = new Intent();
	        // 设置文件类型
	        intentFromGallery.setType("image/*");//选择图片
	        intentFromGallery.setAction(/*Intent.ACTION_GET_CONTENT*/Intent.ACTION_PICK);
	        //如果你想在Activity中得到新打开Activity关闭后返回的数据，
	        //你需要使用系统提供的startActivityForResult(Intent intent,int requestCode)方法打开新的Activity
	        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
	    }

	    // 启动手机相机拍摄照片作为头像
	    private void choseHeadImageFromCameraCapture() {
	        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        // 判断存储卡是否可用，存储照片文件
	        if (hasSdcard()) {
	            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
	                    .fromFile(new File(Environment
	                            .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
	        }
	        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
	    }

	    @Override
	    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
	        // 用户没有进行有效的设置操作，返回
	        if (resultCode == RESULT_CANCELED) {//取消
	            Toast.makeText(getApplication(), "设置取消", Toast.LENGTH_SHORT).show();
	            return;
	        }
	        switch (requestCode) {
	            case CODE_GALLERY_REQUEST://如果是来自本地的
	                cropRawPhoto(intent.getData());//直接裁剪图片
	                break;
	            case CODE_CAMERA_REQUEST:
	                if (hasSdcard()) {
	                    File tempFile = new File(
	                            Environment.getExternalStorageDirectory(),
	                            IMAGE_FILE_NAME);
	                    cropRawPhoto(Uri.fromFile(tempFile));
	                } else {
	                    Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG)
	                            .show();
	                }
	                break;
	            case CODE_RESULT_REQUEST:
	                if (intent != null) {
	                    setImageToHeadView(intent);//设置图片框
	                }
	                break;
	        }
	        super.onActivityResult(requestCode, resultCode, intent);
	    }

	    /**
	     * 裁剪原始的图片
	     */
	    public void cropRawPhoto(Uri uri) {

	        Intent intent = new Intent("com.android.camera.action.CROP");
	        intent.setDataAndType(uri, "image/*");

	        //把裁剪的数据填入里面

	        // 设置裁剪
	        intent.putExtra("crop", "true");

	        // aspectX , aspectY :宽高的比例
	        intent.putExtra("aspectX", 1);
	        intent.putExtra("aspectY", 1);

	        // outputX , outputY : 裁剪图片宽高
	        intent.putExtra("outputX", output_X);
	        intent.putExtra("outputY", output_Y);
	        intent.putExtra("return-data", true);

	        startActivityForResult(intent, CODE_RESULT_REQUEST);
	    }

	    /**
	     * 提取保存裁剪之后的图片数据，并设置头像部分的View
	     */
	    private void setImageToHeadView(Intent intent) {
	        Bundle extras = intent.getExtras();
	        if (extras != null) {
	        	mPhoto = extras.getParcelable("data");
	            headImage.setImageBitmap(mPhoto);
	            if(mUserDbAdapter.updateDriverHeadPortrait(mTeleNumber, converImageToByte(mPhoto))){
					Message msg = new Message();
					msg.what=EVENT_SET_PORTRAIT_SUCCESS;
					mHandler.sendMessage(msg);
				}else{
					Message msg = new Message();
					msg.what=EVENT_SET_PORTRAIT_FAIL;
					mHandler.sendMessage(msg);
				}
/*                //新建文件夹 先选好路径 再调用mkdir函数 现在是根目录下面的Ask文件夹
	            File nf = new File(Environment.getExternalStorageDirectory()+"/Ask");
	            nf.mkdir();
	            //在根目录下面的ASk文件夹下 创建okkk.jpg文件
	            File f = new File(Environment.getExternalStorageDirectory()+"/Ask", "okkk.jpg");
	            FileOutputStream out = null;
	            try {
	               //打开输出流 将图片数据填入文件中
	                out = new FileOutputStream(f);
	                photo.compress(Bitmap.CompressFormat.PNG, 90, out);
	                try {
	                    out.flush();
	                    out.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            } catch (FileNotFoundException e) {
	                e.printStackTrace();
	            }*/
	        }
	    }

		private Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage (Message msg) {
	            super.handleMessage(msg);
	            switch (msg.what) {
	                case EVENT_SET_PORTRAIT_SUCCESS:
	            	    Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
	            	    break;
	                case EVENT_SET_PORTRAIT_FAIL:
	            	    Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
	            	    break;
	                default:
	                    break;
	            }
	        }
	    };
	    
	    public Bitmap getHeadPortrait(){
	    	Bitmap bitmap = null;
	    	Cursor cursor = mUserDbAdapter.getUser(mTeleNumber);
	    	try{
	    		byte[] headPortraitByteArray = cursor.getBlob(cursor.getColumnIndex("headportrait"));
	    		bitmap = bytes2Bitmap(headPortraitByteArray);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		if(cursor!=null){
	    			cursor.close();
	    		}
	    	}
	    	return bitmap;
	    }
	    public byte[] converImageToByte(Bitmap bitmap) {
		    if(bitmap!=null){
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
	            return baos.toByteArray();
		    }else{
		    	return null;
		    }
	    }
	    
	    // byte[]转换成Bitmap  
	    public Bitmap bytes2Bitmap(byte[] b) {  
	        if (b.length != 0) {  
	            return BitmapFactory.decodeByteArray(b, 0, b.length);  
	        }  
	        return null;  
	    }  
	    
	    /**
	     * 检查设备是否存在SDCard的工具方法
	     */
	    public static boolean hasSdcard() {
	        String state = Environment.getExternalStorageState();
	        if (state.equals(Environment.MEDIA_MOUNTED)) {
	            // 有存储的SDCard
	            return true;
	        } else {
	            return false;
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
