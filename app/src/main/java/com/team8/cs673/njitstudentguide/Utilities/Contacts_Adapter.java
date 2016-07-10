package com.team8.cs673.njitstudentguide.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.team8.cs673.njitstudentguide.R;

import java.util.ArrayList;


public class Contacts_Adapter extends BaseAdapter {

    Context context;
    ArrayList<Contact> listData;

    public Contacts_Adapter(Context context, ArrayList<Contact> listData){
        this.context = context;
        this.listData = listData;
    }
    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        private TextView txt_Name;
        private TextView txt_Number;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contact_item,null);
            viewHolder = new ViewHolder();
            viewHolder.txt_Name = (TextView) view.findViewById(R.id.contact_name);
            viewHolder.txt_Number = (TextView) view.findViewById(R.id.contact_number);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        Contact c = listData.get(position);
        String cName = c.getName();
        String cNumber=c.getNumber();
        viewHolder.txt_Name.setText(cName);
        viewHolder.txt_Number.setText(cNumber);
        return view;
    }
}
