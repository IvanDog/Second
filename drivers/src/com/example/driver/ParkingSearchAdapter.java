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

public class ParkingSearchAdapter extends BaseAdapter {

	private List<Map<String, Object>> data;  
    private LayoutInflater layoutInflater;
    private Context context;  

    public ParkingSearchAdapter(Context context,List<Map<String, Object>> data) {
        this.context=context;  
        this.data=data;  
        this.layoutInflater=LayoutInflater.from(context);  
    }
    
    public class ViewHolder{
    	TextView name;
    	TextView location;
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
            convertView = layoutInflater.inflate(R.layout.list_search, null);
            vh.name = (TextView) convertView.findViewById(R.id.tv_item_name_search);
            vh.location = (TextView) convertView.findViewById(R.id.tv_item_address_search);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.name.setText((String)(data.get(position).get("name")));
        vh.location.setText((String)(data.get(position).get("address")));
        Log.e("yifan","getview");
        return convertView;
    }
}
