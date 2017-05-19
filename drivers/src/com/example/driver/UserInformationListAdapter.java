package com.example.driver;

import java.util.List;
import java.util.Map;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserInformationListAdapter extends BaseAdapter {  
	  
    private List<Map<String, Object>> data;  
    private LayoutInflater layoutInflater;  
    private Context context;  
    public UserInformationListAdapter(Context context,List<Map<String, Object>> data){  
        this.context=context;  
        this.data=data;  
        this.layoutInflater=LayoutInflater.from(context);  
    }  
    /** 
     * 组件集合，对应list.xml中的控件 
     * @author Administrator 
     */  
    public final class Zujian{  
        public TextView userInformationTV; 
        public TextView userDeailInformationTV; 
        public ImageView userInformationSpreadFunctionIV; 
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
            convertView=layoutInflater.inflate(R.layout.list_user_information, null);  
            zujian.userInformationTV=(TextView)convertView.findViewById(R.id.tv_title_user_information);  
            zujian.userDeailInformationTV=(TextView)convertView.findViewById(R.id.tv_detail_user_information);  
            zujian.userInformationSpreadFunctionIV=(ImageView)convertView.findViewById(R.id.iv_enter_user_information);  
            convertView.setTag(zujian);  
        }else{  
            zujian=(Zujian)convertView.getTag();  
        }  
        //绑定数据  
        zujian.userInformationTV.setText((String)data.get(position).get("userInformation"));  
        if(((String)data.get(position).get("userInformation")).equals("头像: ")){
        	zujian.userDeailInformationTV.setText("");
        	zujian.userDeailInformationTV.setBackgroundDrawable((Drawable)data.get(position).get("userInformationDetail"));
        }else{
            zujian.userDeailInformationTV.setText((String)data.get(position).get("userInformationDetail"));  
        }
        if((data.get(position).get("userInformationSpreadImage"))!=null){
            zujian.userInformationSpreadFunctionIV.setImageResource((Integer)data.get(position).get("userInformationSpreadImage"));
        }
        return convertView;  
    }  
  
} 