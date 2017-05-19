package com.example.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RecordFragment extends Fragment {
	public static final int TYPE_CURRENT_RECORD = 101;
	public static final int TYPE_HISTORY_RECORD = 102;
	private View mView;
	private ListView mListView;
	private int mType;
	private boolean mEqual;
	private UserDbAdapter mUserDbAdapter;
	public RecordFragment(int type,boolean equal){
		mType = type;
		mEqual = equal;
	}
	 @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	    }

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	    }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	mView = inflater.inflate(R.layout.fragment_record, container, false);
	        mListView=(ListView)mView.findViewById(R.id.list_record);  
	        mUserDbAdapter = new UserDbAdapter(getActivity());
	        List<Map<String, Object>> list=getData(mType);  
	        mListView.setAdapter(new RecordListAdapter(getActivity(), list)); 
	        return mView;
	    }

	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	    }

	    @Override
	    public void onStart() {
	        super.onStart();
	    }

	    @Override
	    public void onResume() {
	        super.onResume();
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	    }

	    @Override
	    public void onStop() {
	        super.onStop();
	    }

	    @Override
	    public void onDestroyView() {
	        super.onDestroyView();
	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	    }

	    @Override
	    public void onDetach() {
	        super.onDetach();
	    }

	    public List<Map<String, Object>> getData(int type){  
	        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
	        setHistoryRecord(mType,list);
	        return list;  
	    }
	    
	    public void setHistoryRecord(int type,List<Map<String, Object>> list){
	    	Map<String, Object> titleMap=new HashMap<String, Object>();
	    	titleMap.put("licensePlateNumber","牌照");
	    	titleMap.put("startTime","入场时间");
	    	titleMap.put("leaveTime", "离场时间");
	    	titleMap.put("parkingName", "车场名称");
	    	titleMap.put("paymentPattern","支付方式");
	    	titleMap.put("expense", "支付");
            list.add(titleMap); 
	    	mUserDbAdapter.open();
	    	Cursor cursor = mUserDbAdapter.getParkingDetailByPaymentPattern("未付",mEqual);
	    	Log.e("yifan","count : " + cursor.getCount());
	        try {
	        	int count = 0;
	        	do{
	        	    		  Map<String, Object> map=new HashMap<String, Object>();
	        	    		  map.put("licensePlateNumber", cursor.getString(cursor.getColumnIndex("licenseplate")));
	        	    		  map.put("startTime", "入场: " + cursor.getString(cursor.getColumnIndex("starttime")));
	        	    		  if(cursor.getString(cursor.getColumnIndex("leavetime"))==null){
	        	    			  map.put("leaveTime", null);
	        	    		  }else{
		        	    		  map.put("leaveTime", "离场: " + cursor.getString(cursor.getColumnIndex("leavetime")));
	        	    		  }
	        	    		  map.put("parkingName", cursor.getString(cursor.getColumnIndex("parkingname")));
	        	    		  map.put("paymentPattern", cursor.getString(cursor.getColumnIndex("paymentpattern")));
	        	    		  if(cursor.getString(cursor.getColumnIndex("expense"))==null){
		        	    		  map.put("expense", null);
	        	    		  }else{
		        	    		  map.put("expense", cursor.getInt(cursor.getColumnIndex("expense")) + "元");
	        	    		  }
	      		              list.add(map); 
	      		              count++;
	        	 }while(cursor.moveToNext());
	        	 if(count==0){
	        	     list.remove(titleMap); 
	        	  }
	        }
	        catch (Exception e) {
	                e.printStackTrace();
	        } finally{
	            	if(cursor!=null){
	            		cursor.close();
	                }
	        }
	    }

}
