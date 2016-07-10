package com.team8.cs673.njitstudentguide;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.team8.cs673.njitstudentguide.EventNotification.Alarm;
import com.team8.cs673.njitstudentguide.Utilities.Courses;
import com.team8.cs673.njitstudentguide.Utilities.Event;
import com.team8.cs673.njitstudentguide.Utilities.Meeting;
import com.team8.cs673.njitstudentguide.Utilities.Semester;
import com.team8.cs673.njitstudentguide.WhatsThat.ScanActivity;
import com.team8.cs673.njitstudentguide.activity.CalendarActivity;
import com.team8.cs673.njitstudentguide.activity.CampusMap;
import com.team8.cs673.njitstudentguide.activity.Classes;
import com.team8.cs673.njitstudentguide.activity.Contacts;
import com.team8.cs673.njitstudentguide.activity.LoginActivity;
import com.team8.cs673.njitstudentguide.app.AppConfig;
import com.team8.cs673.njitstudentguide.app.AppController;
import com.team8.cs673.njitstudentguide.helper.SQLiteHandler;
import com.team8.cs673.njitstudentguide.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Dashboard_Activity extends Activity {
    public static Typeface FONT_ICONS;

    private static String [] itemNameList={"Contacts","Calendar","Campus Map","Classes", "What's That"};
    private static int [] itemImages={R.string.contacts,R.string.calendar,R.string.campusMap,R.string.classes,R.string.whats};
    private SQLiteHandler db;
    private SessionManager session;

    private  TextView welcome;
    public  static String userEmail;
    private  TextView txt_temp, login_out_icon;
    private static String TAG = Dashboard_Activity.class.getSimpleName();
    private String getTemp;
    private TextView cal_notify;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);
        alarm=new Alarm(this);
        login_out_icon=(TextView)findViewById(R.id.login_out_icon);
        welcome=(TextView) findViewById(R.id.txt_welcome);
        txt_temp=(TextView)findViewById(R.id.txt_temp);
        FONT_ICONS = Typeface.createFromAsset(getAssets(), "fonts/icons.ttf");
        prepareView();
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            login_out_icon.setText(getString(R.string.login_icon));
            login_out_icon.setTypeface(FONT_ICONS);
            logoutUser();
        }
        else{
            login_out_icon.setText(getString(R.string.logout_icon));
            login_out_icon.setTypeface(FONT_ICONS);
        }
        login_out_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!session.isLoggedIn()) {


                    Intent intent = new Intent(Dashboard_Activity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    logoutUser();
                    login_out_icon.setText(getString(R.string.login_icon));
                    login_out_icon.setTypeface(FONT_ICONS);
                    welcome.setText("Welcome");
                }
            }
        });
        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");
        userEmail=email;
        welcome.setVisibility(View.VISIBLE);
        String username= name==null ?"":name;
        welcome.setText("Welcome: \n"+username);

    }

    private void prepareView() {
        View includedContact = findViewById(R.id.contact_icon);
        TextView contact_icon = (TextView)includedContact.findViewById(R.id.item_icon);
        TextView contact_title = (TextView)includedContact.findViewById(R.id.item_title);
        contact_title.setText(itemNameList[0]);
        contact_icon.setText(getString(itemImages[0]));
        contact_icon.setTypeface(FONT_ICONS);
        includedContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent first_Intent = new Intent(getApplicationContext(),
                                Contacts.class);
                        first_Intent.putExtra("email", userEmail);
                        startActivity(first_Intent);
                       // finish();
            }
        });

        View includedCal = findViewById(R.id.calendar_icon);
        TextView cal_icon = (TextView)includedCal.findViewById(R.id.item_icon);
        TextView cal_title = (TextView)includedCal.findViewById(R.id.item_title);
       cal_notify=(TextView)includedCal.findViewById(R.id.notify_icon);
        cal_notify.setTypeface(FONT_ICONS);


        cal_title.setText(itemNameList[1]);
        cal_icon.setText(getString(itemImages[1]));
        cal_icon.setTypeface(FONT_ICONS);
        includedCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.isLoggedIn()){
                Intent second_Intent = new Intent(getApplicationContext(),
                              CalendarActivity.class);
                      second_Intent.putExtra("email", userEmail);
                      startActivity(second_Intent);

                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "Please Log in", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        View includedMap = findViewById(R.id.map_icon);
        TextView map_icon = (TextView)includedMap.findViewById(R.id.item_icon);
        TextView map_title = (TextView)includedMap.findViewById(R.id.item_title);
        map_title.setText(itemNameList[2]);
        map_icon.setText(getString(itemImages[2]));
        map_icon.setTypeface(FONT_ICONS);
        includedMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent third_Intent = new Intent(getApplicationContext(),
                                CampusMap.class);
                       startActivity(third_Intent);

            }
        });

        View includedClasses = findViewById(R.id.class_icon);
        TextView class_icon = (TextView)includedClasses.findViewById(R.id.item_icon);
        TextView class_title = (TextView)includedClasses.findViewById(R.id.item_title);
        class_title.setText(itemNameList[3]);
        class_icon.setText(getString(itemImages[3]));
        class_icon.setTypeface(FONT_ICONS);
        includedClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.isLoggedIn()){
                Intent fourth_Intent = new Intent(getApplicationContext(),
                              Classes.class);
                fourth_Intent.putExtra("email", userEmail);
                      startActivity(fourth_Intent);

                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "Please Log in", Toast.LENGTH_LONG)
                            .show();}
            }
        });

        View includedWhats = findViewById(R.id.what_icon);
        TextView whats_icon = (TextView)includedWhats.findViewById(R.id.item_icon);
        TextView whats_title = (TextView)includedWhats.findViewById(R.id.item_title);
        whats_title.setText(itemNameList[4]);
        whats_icon.setText(getString(itemImages[4]));
        whats_icon.setTypeface(FONT_ICONS);
        final Context context = this.getApplicationContext();
        includedWhats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fourth_Intent = new Intent(getApplicationContext(),
                        ScanActivity.class);
                startActivity(fourth_Intent);


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        update_Temperature();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        ArrayList<Courses> cList=db.getCourses();
        addMeetingtoEvents(cList);
    }



    private  void update_Temperature(){
        if (session.updateWeather())
        { getWeatherTemperature();
        }
        else{
            String savedTemp=session.getTemp();
            if (!savedTemp.equals("")){
                session.setTemp(savedTemp);}
            txt_temp.setText(savedTemp+""+getString(R.string.degree));
        }

    }
    private void getWeatherTemperature () {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                AppConfig.URL_WEATHER_API, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response != null) {
                        JSONObject data = null;
                        data = response.getJSONObject("data");
                        JSONObject current_condition = data.getJSONArray("current_condition").getJSONObject(0);
                        getTemp=current_condition.getString("temp_F");
                        Log.d(TAG,"Temp="+getTemp);
                        }
                    else {getTemp="";}
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                if (!getTemp.equals("")){
                session.setTemp(getTemp);}
                txt_temp.setText(getTemp + " " + getString(R.string.degree));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                getTemp="";
            }
        }
        );

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void logoutUser() {
        ArrayList<Event>eventeList;
        eventeList=db.getEvents();
        session.setLogin(false);
        session.eventLoaded(false);
        session.coursesLoaded(false);
        session.semesterLoaded(false);
        session.meetingStoredToEvent(false);
        alarm.deleteAllAlarm(eventeList);
        db.deleteUsers();
        db.deleteEvents();
        db.deleteClasses();
        db.deleteSemester();
        cal_notify.setVisibility(View.INVISIBLE);

    }

    private boolean setEventsNotifyor(){
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date today = Calendar.getInstance().getTime();
        String reportDate = curFormater.format(today);
        ArrayList<Event>eList;
        eList=db.getAllDayEvent(reportDate);
        if(eList.size()>0){
        cal_notify.setVisibility(View.VISIBLE);}else{
       cal_notify.setVisibility(View.INVISIBLE);}
        return true;
    }



    private void addMeetingtoEvents(ArrayList<Courses> cList ) {
        if(!session.isMeetingToEvents()){
            HashMap<Integer, String> buildingHash;
            buildingHash= db.getAllLoction("Building");
            for(Courses c:cList) {
                Semester s = db.getSemester(c.getSemester());
                String start = s.getStart();
                String end = s.getEnd();
                SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate;
                Date endDate;
                Calendar c_Start = Calendar.getInstance();
                Calendar c_End = Calendar.getInstance();
                try {
                    startDate = curFormater.parse(start);
                    endDate = curFormater.parse(end);
                    c_Start.setTime(startDate);
                    c_End.setTime(endDate);
                    ArrayList<Meeting> mList = c.getMlist();
                    String day;
                    for (Meeting m : mList) {
                        c_Start.setTime(startDate);
                        boolean dayReached = false;
                        day = m.getMeeeting_Day();
                        while (c_Start.before(c_End)) {
                            if (c_Start.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).equals(day)) {
                                dayReached = true;
                                String formatted = curFormater.format(c_Start.getTime());
                                String title = c.getCourse_No() + " " + c.getCourse_Name();
                                String desc = buildingHash.get(m.getMeeting_Building()) + " " + m.getMeeting_Room();
                                db.addEvent(new Event(formatted, m.getMeeting_time(), title, desc));
                            }
                            if (dayReached) {
                                c_Start.add(Calendar.DATE, 7);
                            } else {
                                c_Start.add(Calendar.DATE, 1);
                            }
                        }
                    }

                } catch (ParseException e) {
                }
            }
            setEventsNotifyor();
            session.meetingStoredToEvent(true);
            Handler handler = new Handler();
            handler.post(setAlarm);
        }}

    public Runnable setAlarm = new Runnable() {
        @Override
        public void run() {
            alarm.addAllAlarm();
        }};
}
