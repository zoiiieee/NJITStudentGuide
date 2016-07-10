package com.team8.cs673.njitstudentguide.EventNotification;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.team8.cs673.njitstudentguide.Utilities.Event;
import com.team8.cs673.njitstudentguide.helper.SQLiteHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Aledo-PC on 11/15/2015.
 */
public class Alarm {
    private Context context;
    SQLiteHandler db;
    private PendingIntent mAlarmSender;
    public Alarm(Context context){

        this.context = context;
        db=new SQLiteHandler(context);
    }

    public void addAlarm(Event e, int eventID){
            String strDate=e.getEventDate();
            Date date = null;
            String[] time=e.getEventTime().split(" - ");
            Log.e("Alarm=Event:"+e.getEventTitle(), time[0]);
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String datetime=strDate+" "+time[0];
            Log.e("Alarm=Event:"+e.getEventTitle(), "datetime: "+datetime);
            try {
                date=curFormater.parse(datetime);
                GregorianCalendar gc=new GregorianCalendar();
                GregorianCalendar currentDateTime= new GregorianCalendar();
                currentDateTime.getInstance();
                gc.setTime(date);

                Log.e("Alarm=Event:"+currentDateTime.getTime(), "datetime: "+gc.getTime());
                if (currentDateTime.getTime().before(gc.getTime())){
                    gc.add(Calendar.HOUR, -1);
                Intent intent = new Intent(context,AlertReceiver.class);
                intent.setData(Uri.parse("timer:" + eventID));
                Bundle bundle = new Bundle();
                bundle.putString("msg", e.getEventTitle());
                bundle.putString("msgText", e.getEventTime());
                bundle.putString("msgAlert", "You have an event coming up");
                intent.putExtras(bundle);
                Log.e("Alarm=Event:" + e.getEventTitle(), "Id:"+Integer.parseInt(intent.getData().getSchemeSpecificPart()));
                mAlarmSender = PendingIntent.getBroadcast(context, 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, gc.getTimeInMillis(),
                        mAlarmSender);}
            } catch (ParseException e1) {
                e1.printStackTrace();

            }
        Log.d("Add Alarm id:", eventID+"");


    }
    public void deleteAlarm(Event e ){
        int id= e.getEventID();
        Log.d("Delete Alarm id:", e.getEventID()+"");
            Log.e("Alarm=Event:" + e.getEventTitle(), "Id:" + e.getEventID());
        Intent intent = new Intent(context,AlertReceiver.class);
        intent.setData(Uri.parse("timer:" + e.getEventID()));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intent, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            pendingIntent.cancel();
        am.cancel(pendingIntent);

            NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(e.getEventID());

        }



    public void deleteAllAlarm( ArrayList<Event> eList){
       for(Event e:eList){
           Intent intent = new Intent(context,AlertReceiver.class);
           intent.setData(Uri.parse("timer:" + e.getEventID()));

           PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intent, 0);
           AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
           pendingIntent.cancel();
           am.cancel(pendingIntent);
           NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
           notificationManager.cancel(e.getEventID());
       }

    }
    public void addAllAlarm() {
        ArrayList<Event> eList;
        eList=db.getEvents();
        if(eList!=null){
            for(Event e:eList){
                addAlarm(e,e.getEventID());
            }

        }
    }
}
