package com.example.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ParkingCouponAdapter extends BaseAdapter {

	private List<Map<String, Object>> data;  
    private LayoutInflater layoutInflater;
    private Context context;  

    public ParkingCouponAdapter(Context context,List<Map<String, Object>> data) {
        this.context=context;  
        this.data=data;  
        this.layoutInflater=LayoutInflater.from(context);  
    }
    
    public class ViewHolder{
    	TextView title;
    	TextView startTime;
    	TextView endTime;
    	TextView notify;
    	TextView denomination;
    }

    @Override
    public int getCount() {
        return data.size();
    }
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null){
            vh = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_parking_coupon, null);
            vh.title = (TextView) convertView.findViewById(R.id.tv_coupon_title);
            vh.startTime = (TextView) convertView.findViewById(R.id.tv_start_time_coupon);
            vh.endTime = (TextView) convertView.findViewById(R.id.tv_end_time_coupon);
            vh.notify = (TextView) convertView.findViewById(R.id.tv_notify_coupon);
            vh.denomination = (TextView) convertView.findViewById(R.id.tv_denomination_coupon);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.title.setText((String)(data.get(position).get("title")));
        vh.startTime.setText((String)(data.get(position).get("starttime")));
        vh.endTime.setText((String)(data.get(position).get("endtime")));
        vh.notify.setText((String)(data.get(position).get("notify")));
        vh.denomination.setText((String)(data.get(position).get("denomination")));
        Log.e("yifan","getview");
        return convertView;
    }
}
