package com.team8.cs673.njitstudentguide.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.team8.cs673.njitstudentguide.Dashboard_Activity;
import com.team8.cs673.njitstudentguide.R;

public class Items_Grid extends BaseAdapter {
    Typeface iconsFont;
    private Context context;
    private final String[] names;
    private LayoutInflater inflater;
    private final int[] icons;

    public Items_Grid(Context c,String[] names,int[] icons) {
        iconsFont= Dashboard_Activity.FONT_ICONS;
        context = c;
        this.names = names;
        this.icons=icons;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View grid;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {

                grid = new View(context);
                grid = inflater.inflate(R.layout.grid_item, null);
                TextView title = (TextView) grid.findViewById(R.id.item_title);
                TextView icon = (TextView) grid.findViewById(R.id.item_icon);
                title.setText(names[position]);
                icon.setText(context.getString(icons[position]));
                icon.setTypeface(iconsFont);
               // icon.setTextSize(35);
                icon.setTextColor(Color.rgb(200,0,0));
            } else {
                grid = (View) convertView;
            }

            return grid;
        }}