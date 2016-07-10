package com.team8.cs673.njitstudentguide.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.team8.cs673.njitstudentguide.R;
import com.team8.cs673.njitstudentguide.activity.CalendarActivity;

import java.util.ArrayList;


public class Events_Aapter extends BaseAdapter {

    Context context;
    ArrayList<Event> listEvent;

    public Events_Aapter(Context context, ArrayList<Event> listEvent) {
        this.context = context;
        this.listEvent = listEvent;
    }

    @Override
    public int getCount() {
        return listEvent.size();
    }

    @Override
    public Object getItem(int position) {
        return listEvent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder {
        private TextView calTitle, calTime, calDesc;
        private ImageButton delete;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cal_event_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.calTitle = (TextView) view.findViewById(R.id.calTile);
            viewHolder.calTime = (TextView) view.findViewById(R.id.calTime);
            viewHolder.calDesc = (TextView) view.findViewById(R.id.calDesc);
            viewHolder.delete = (ImageButton) view.findViewById(R.id.event_delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.delete.setTag(position);
        final Event e = listEvent.get(position);
        String eTitle = e.getEventTitle();
        String eTime = e.getEventTime();
        String eEventDesc = e.getEventDesc();
        viewHolder.calTitle.setText(eTitle);
        viewHolder.calTime.setText(eTime);
        viewHolder.calDesc.setText(eEventDesc);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int pos = (int) arg0.getTag();
                listEvent.remove(pos);
                Events_Aapter.this.notifyDataSetChanged();
                if (context instanceof CalendarActivity) {
                    ((CalendarActivity) context).deleteEvent(e);
                }
            }
        });
        return view;
    }
}
