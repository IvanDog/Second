package com.example.driver;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ParkingDetailAdapter extends BaseAdapter {  
	  
    private List<Map<String, Object>> data;  
    private LayoutInflater layoutInflater;  
    private Context context;  
    public int clickPosition = -1;
    public ParkingDetailAdapter(Context context,List<Map<String, Object>> data){  
        this.context=context;  
        this.data=data;  
        this.layoutInflater=LayoutInflater.from(context);  
    }
    
    /** 
     * 组件集合，对应list.xml中的控件 
     * @author Administrator 
     */  
    public final class Zujian{  
    	private TextView licensenumberTV; 
        private TextView startTimeTV;  
        private TextView locationTV;  
    }  
    @Override  
    public int getCount() {  
        return data.size();  
    }  
    /** 
     * 获得某一位置的数据 
     */  
    @Override  
    public Object getItem(int position) {  
        return data.get(position);  
    }  
    /** 
     * 获得唯一标识 
     */  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(final int position, View convertView, ViewGroup parent) {  
        Zujian zujian=null;  
        if(convertView==null){  
            zujian=new Zujian();  
            //获得组件，实例化组件  
            convertView=layoutInflater.inflate(R.layout.list_parking_detail, null);  
            zujian.licensenumberTV=(TextView)convertView.findViewById(R.id.tv_license_number_parking_detail);  
            zujian.startTimeTV=(TextView)convertView.findViewById(R.id.tv_start_time_parking_detail);  
            zujian.locationTV=(TextView)convertView.findViewById(R.id.tv_location_parking_detail);  
            convertView.setTag(zujian);  
        }else{  
            zujian=(Zujian)convertView.getTag();  
        }  
        //绑定数据  
        zujian.licensenumberTV.setText((String)data.get(position).get("licenseNumber"));
        zujian.startTimeTV.setText("入场时间：" +  ((String)data.get(position).get("startTime")));
        zujian.locationTV.setText((String)data.get(position).get("parkingname"));  
        return convertView;
    }  
  
} 