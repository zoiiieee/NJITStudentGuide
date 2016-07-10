package com.team8.cs673.njitstudentguide.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.team8.cs673.njitstudentguide.R;
import com.team8.cs673.njitstudentguide.activity.Addcourse;
import com.team8.cs673.njitstudentguide.activity.CalendarActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aledo-PC on 11/6/2015.
 */
public class MeetingAdapter extends BaseAdapter {
    Context context;
    ArrayList<Meeting> listMeeting;
    HashMap<Integer,String> buildingHash;
    boolean deletable;

    public MeetingAdapter(Context context, ArrayList<Meeting> listEvent, HashMap<Integer,String> buildingHash,boolean deletable) {
        this.context = context;
        this.listMeeting = listEvent;
        this.buildingHash=buildingHash;
        this.deletable=deletable;
    }

    @Override
    public int getCount() {
        return listMeeting.size();
    }

    @Override
    public Object getItem(int position) {
        return listMeeting.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder {
        private TextView day, time, loc;
        private ImageButton delete;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.class_meeting_item, null);
            viewHolder = new ViewHolder();
            viewHolder.day = (TextView) view.findViewById(R.id.c_day);
            viewHolder.time = (TextView) view.findViewById(R.id.c_time);
            viewHolder.loc = (TextView) view.findViewById(R.id.c_loc);
            viewHolder.delete = (ImageButton) view.findViewById(R.id.delete_meeting);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.delete.setTag(position);
        final Meeting m = listMeeting.get(position);
        String mDay = m.getMeeeting_Day();
        String mTime = m.getMeeting_time();
        String  b=buildingHash.get(m.getMeeting_Building());
        String mLoc = b+" "+ m.getMeeting_Room();
        viewHolder.day.setText(mDay);
        viewHolder.time.setText(mTime);
        viewHolder.loc.setText(mLoc);
        if(deletable){
            viewHolder.delete.setVisibility(View.VISIBLE);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int pos = (int) arg0.getTag();
                listMeeting.remove(pos);
                MeetingAdapter.this.notifyDataSetChanged();
                if (context instanceof CalendarActivity) {
                    ((Addcourse) context).deleteEvent(m);
                }
            }
        });}
        else {viewHolder.delete.setVisibility(View.INVISIBLE);}
        return view;
    }
}
