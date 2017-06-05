package com.example.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.driver.R.drawable;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MessageCenterActivity extends Activity {
	private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_center);
		mListView=(ListView)findViewById(R.id.list_message_center);  
        List<Map<String, Object>> list=getData();  
        mListView.setAdapter(new MessageCenterListAdapter(this, list)); 
        mListView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
            	//TODO
            }
        });
		getActionBar().setDisplayHomeAsUpEnabled(true); 
	     IntentFilter filter = new IntentFilter();  
	     filter.addAction("ExitApp");  
	     filter.addAction("BackMain");  
	     registerReceiver(mReceiver, filter);
	}

    public List<Map<String, Object>> getData(){  
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
        for (int i = 1; i <= 1; i++) {  
            Map<String, Object> map=new HashMap<String, Object>();  
            if(i==1){
                map.put("messageCenterImage",  drawable.ic_plus_one_black_18dp);
                map.put("messageCenterTitle", "赠您一张停车券");
                map.put("messageCenterDetail", "5元停车券");
                map.put("messageCenterTime", "2017.05.17" + " " + "15:32:26");
                map.put("messageCenterDetailHide", "感谢您的支持，现送您一张5元停车券，请到'我的'-'停车券'中查看详情。");
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
	/**
	 * Add for request message
	public void requestMessage()throws ParseException, IOException, JSONException{
		  HttpClient httpClient = new DefaultHttpClient();
		  String strurl = "//此处url待定";
		  HttpPost request = new HttpPost(strurl);
		  request.addHeader("Accept","application/json");
		  request.addHeader("Content-Type","application/json");//还可以自定义增加header
		  JSONObject param = new JSONObject();//定义json对象
		  param.put("type", "requestmessage");
		  Log.e("yifan", param.toString());
		  StringEntity se = new StringEntity(param.toString());
		  request.setEntity(se);//发送数据
		  HttpResponse httpResponse = httpClient.execute(request);//获得响应
		  int code = httpResponse.getStatusLine().getStatusCode();
		  if(code==HttpStatus.SC_OK){
			  String strResult = EntityUtils.toString(httpResponse.getEntity());
			  JSONObject jsonData1 = new JSONObject(strResult);
		      if(jsonData1.get("list")!=null){  
                 JSONArray array = jsonData1.getJSONArray("list");  
                 for (int i = 0; i < array.length(); i++) {                                
                  JSONObject jsonData2 = (JSONObject) array.get(i);
                  if(jsonDataDetail.get("list")!=null){   
                      JSONArray array2 = jsonData2.getJSONArray("list");  
                     for (int i = 0; i < array2.length(); i++) {
                         JSONObject jsonData3 = (JSONObject) array.get(i);
                         String messageCenterTitle = (String) jsonData3.get("messagecentertitle");
			             String messageCenterDetail = (String) jsonData3.get("messagecenterdetail");
			             String messageCenterTime = (String) jsonData3.get("messagecentertime");
			             String messageCenterDetailHide = (String) jsonData3.get("messagecenterdetailhide");
                     }                              
            }  
		      JSONObject jsonData = new JSONObject(data);
		  }else{
			  Log.e("yifan", Integer.toString(code));
		  }
		 }
	//Client's json:{ "type":"requestmessage"}
	//Server's json:{"list":{"list":{"messagecentertitle":"考勤通知", "messagecenterdetail":"您5月4日出现一次考勤异常", "messagecentertime":“2017-05-04 15:49:20”, "messagecenterdetailhide":“您5月4日上班打卡时间08:40:36(上班时间9:00)，
                		                                  下班打卡时间15:30:23(下班时间17:30)，存在异常，请联系考勤员确认。”},
	 //                                        "list":{"messagecentertitle":"停车通知", "messagecenterdetail":"您5月4日出现一次停车逃费现象", "messagecentertime":“2017-05-04 16:50:25”, "messagecenterdetailhide":“4月25日出现一次停车逃费现象，入场时间11:15:36，
                		                                  牌照号津A00001，泊位号6，请联系稽查员确认。”}}}
	*/
}
