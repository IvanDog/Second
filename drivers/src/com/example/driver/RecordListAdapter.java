package com.example.driver;

import java.util.List;
import java.util.Map;

import com.example.driver.RecordListAdapter.Zujian;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecordListAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;  
    private LayoutInflater layoutInflater;  
    private Context context;  
    public RecordListAdapter(Context context,List<Map<String, Object>> data){  
        this.context=context;  
        this.data=data;  
        this.layoutInflater=LayoutInflater.from(context);  
    }  
    /** 
     * 组件集合，对应list.xml中的控件 
     * @author Administrator 
     */  
    public final class Zujian{  
        public TextView licensePlateNumberTV; 
        public TextView startTimeTV; 
        public TextView leaveTimeTV; 
        public TextView parkingNameTV; 
        public TextView paymentPatternTV; 
        public TextView paymentBillTV; 
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
    public View getView(int position, View convertView, ViewGroup parent) {  
        Zujian zujian=null;  
        if(convertView==null){  
            zujian=new Zujian();  
            //获得组件，实例化组件  
            convertView=layoutInflater.inflate(R.layout.list_record, null);  
            zujian.licensePlateNumberTV=(TextView)convertView.findViewById(R.id.tv_licensePlateNumber_record);  
            zujian.startTimeTV=(TextView)convertView.findViewById(R.id.tv_startTime_record);  
            zujian.leaveTimeTV=(TextView)convertView.findViewById(R.id.tv_leaveTime_record);  
            zujian.parkingNameTV=(TextView)convertView.findViewById(R.id.tv_parking_name_record);  
            zujian.paymentPatternTV=(TextView)convertView.findViewById(R.id.tv_payment_pattern_record);  
            zujian.paymentBillTV=(TextView)convertView.findViewById(R.id.tv_payment_bill_record);  
            convertView.setTag(zujian);  
        }else{  
            zujian=(Zujian)convertView.getTag();  
        }  
        //绑定数据  
        zujian.licensePlateNumberTV.setText((String)data.get(position).get("licensePlateNumber"));  
        zujian.startTimeTV.setText((String)data.get(position).get("startTime"));  
        zujian.leaveTimeTV.setText((String)data.get(position).get("leaveTime"));  
        zujian.parkingNameTV.setText((String)data.get(position).get("parkingName"));  
        zujian.paymentPatternTV.setText((String)data.get(position).get("paymentPattern"));  
        zujian.paymentBillTV.setText((String)data.get(position).get("expense")); 
        return convertView;  
    }

}
