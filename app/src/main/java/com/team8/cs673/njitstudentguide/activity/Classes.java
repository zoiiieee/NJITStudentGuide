package com.team8.cs673.njitstudentguide.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.team8.cs673.njitstudentguide.Dashboard_Activity;
import com.team8.cs673.njitstudentguide.EventNotification.Alarm;
import com.team8.cs673.njitstudentguide.R;
import com.team8.cs673.njitstudentguide.Utilities.Courses;
import com.team8.cs673.njitstudentguide.Utilities.Event;
import com.team8.cs673.njitstudentguide.Utilities.ExpandableHeightListView;
import com.team8.cs673.njitstudentguide.Utilities.Meeting;
import com.team8.cs673.njitstudentguide.Utilities.MeetingAdapter;
import com.team8.cs673.njitstudentguide.helper.HttpCalls;
import com.team8.cs673.njitstudentguide.helper.SQLiteHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Classes extends ActionBarActivity {
    ImageButton back, c_AddClass;
    Spinner spinner;
    private SQLiteHandler db;
    private LinearLayout my_linear_layout;
    private ArrayList<Courses> cList;
    private MeetingAdapter meetingAdapter;
    private ArrayList<Meeting> course_meeting;
    private String email;
    String s;
    int pos;
    ArrayAdapter<String> dataAdapter;
    public  static HashMap<Integer, String> buildingHash = new HashMap<>();
    View view;
    HttpCalls hc;
    Alarm alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classes_layout);
        s="XXX";
        alarm=new Alarm(this);
        spinner=(Spinner)findViewById(R.id.spinner);
        Intent intent = getIntent();
        pos=0;
        email = intent.getStringExtra("email");
        hc=new HttpCalls(this,email);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                my_linear_layout.removeAllViews();
                pos=position;
                if(position!=0){
                    s=(String) parent.getItemAtPosition(position);
                prepareView(s);}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        db=new SQLiteHandler(getApplicationContext());
        gethashBuildingName();
        my_linear_layout = (LinearLayout) findViewById(R.id.my_linear_layout);




        customActionbar();
        addItemsOnSpinner();
        spinner.setSelection(1);
    }

    private void prepareView(String semester) {
         int i;
        cList=db.getAllCourses(semester);
        if(cList.size()>0){
        for ( i=0; i<cList.size();i++){
            final int final_i=i;
            view = LayoutInflater.from(this).inflate(R.layout.course_item,null);
            final View final_view=view;
            ImageButton course_delete=(ImageButton)view.findViewById(R.id.delete_course);
            TextView course_code=(TextView)view.findViewById(R.id.course_code);
            TextView course_name=(TextView)view.findViewById(R.id.course_name);
            ExpandableHeightListView course_lv2=(ExpandableHeightListView)view.findViewById(R.id.course_lv2);
            course_code.setText(cList.get(i).getCourse_No());
            course_name.setText(cList.get(i).getCourse_Name());
            course_meeting=cList.get(i).getMlist();
            meetingAdapter = new MeetingAdapter(Classes.this, course_meeting, this.buildingHash,false);
            course_lv2.setAdapter(meetingAdapter);
            course_lv2.setExpanded(true);
            my_linear_layout.addView(view);
            course_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCourse(cList.get(final_i), final_view);
                }
            });
        }}
    }

    private void deleteCourse(final Courses c,final View v) {
        Log.e("deletCourse", "deletingAlert");
        final String semester=s;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Classes.this);
        alertDialog.setTitle("Delete");
        alertDialog.setMessage("Are sure you want to delete?");
        alertDialog.setIcon(R.drawable.delete);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                my_linear_layout.removeView(v);

                hc.deleteSingleClass(c);
                deleteScheduledEvents(c);
               // db.deleteCoures(c.getCourse_ID());
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();


    }
    public void deleteScheduledEvents(Courses c) {
            ArrayList<Meeting> mList = c.getMlist();

            for (Meeting m : mList) {
                ArrayList<Event>eList=db.getEvent(c.getCourse_No() + " " + c.getCourse_Name(), buildingHash.get(m.getMeeting_Building()) + " " + m.getMeeting_Room(), m.getMeeting_time());
                Log.e(eList.size()+"", "size eList");
                for(Event e:eList){
                    alarm.deleteAlarm(e);
                }
                db.deleteClassEvents(c.getCourse_No() + " " + c.getCourse_Name(),buildingHash.get(m.getMeeting_Building()) + " " + m.getMeeting_Room());

            }
    }

    private void addItemsOnSpinner() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        int year = calendar.get(Calendar.YEAR);
        List<String> semesters;
        semesters = Arrays.asList(getResources().getStringArray(R.array.semester_arrays));
        Log.i("semesters", semesters.toString());
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 2; i++) {
            int y = year + i;
            for (int j = 0; j < semesters.size(); j++) {
                list.add(semesters.get(j) + " " + y);
            }
        }
        list.add(0, "Select Semester");
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar cal=Calendar.getInstance();
        Log.e("getDisplayName",cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));

    }

    private void customActionbar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        View view = getLayoutInflater().inflate(R.layout.action_bar,
                null);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layoutParams);
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(-10, -10);
        TextView title=(TextView)findViewById(R.id.txtitle);
        title.setText("Classes");
        Button btnLogin_logout = (Button) findViewById(R.id.btnlogin_out);
        btnLogin_logout.setVisibility(View.INVISIBLE);
        back = (ImageButton) findViewById(R.id.btn_backtomain);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Classes.this, Dashboard_Activity.class);
                startActivity(intent);
                finish();

            }
        });
        c_AddClass = (ImageButton) findViewById(R.id.btn_calAddEvent);
        c_AddClass.setVisibility(View.VISIBLE);
        c_AddClass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Classes.this, Addcourse.class);
                intent.putExtra("email", email);
                intent.putExtra("index", pos);
                startActivity(intent);
                finish();

            }
        });
    }


    private void gethashBuildingName() {
        db = new SQLiteHandler(getApplicationContext());
        buildingHash = db.getAllLoction("Building");
    }




}