package com.team8.cs673.njitstudentguide.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.team8.cs673.njitstudentguide.Dashboard_Activity;
import com.team8.cs673.njitstudentguide.EventNotification.Alarm;
import com.team8.cs673.njitstudentguide.R;
import com.team8.cs673.njitstudentguide.Utilities.CalendarAdapter;
import com.team8.cs673.njitstudentguide.Utilities.Event;
import com.team8.cs673.njitstudentguide.Utilities.Events_Aapter;
import com.team8.cs673.njitstudentguide.helper.HttpCalls;
import com.team8.cs673.njitstudentguide.helper.SQLiteHandler;
import com.team8.cs673.njitstudentguide.helper.SessionManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalendarActivity extends ActionBarActivity {
    private String email;
    private static final String tag = "MyCalendarActivity";
    public GregorianCalendar month, itemmonth;
    ImageButton calAddEvent;
    public CalendarAdapter adapter;
    public Events_Aapter eventAdapter;
    public Handler handler;
    public ArrayList<String> items;
    ArrayList<Event> events;
    SessionManager session;
    HttpCalls hc;
    SQLiteHandler db;
    ImageButton back;
    ImageButton btn_previous, btn_next;
    TextView title;
    GridView gridview;
    String selectedGridDate;
    ListView e_lv;
    View view;
    int pos;
    Alarm alarm;
    ViewGroup vg;
    private ProgressDialog pDialog;
    TextView noEvent;
    final Context context = this;
    private static final String TAG = CalendarActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);
        Intent intent = getIntent();
        pos=-1;
        alarm=new Alarm(getApplicationContext());
        email = intent.getStringExtra("email");
        hc=new HttpCalls(this,email);
        Locale.setDefault(Locale.US);
        customActionbar();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        month = (GregorianCalendar) GregorianCalendar.getInstance();
        itemmonth = (GregorianCalendar) month.clone();
        items = new ArrayList<String>();
        TextView noEventMsg=(TextView)findViewById(R.id.noEventMsg);
        noEventMsg.setVisibility(View.VISIBLE);
        e_lv = (ListView) findViewById(R.id.events_lv);
        e_lv.setEmptyView(noEventMsg);
        adapter = new CalendarAdapter(this, month);
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(adapter);
        handler = new Handler();
        refreshCalendar();
        if (!session.isEventLoaded()) {

            if (email != "") {
                hc.downloadEvent();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Please Login to get events", Toast.LENGTH_SHORT)
                        .show();

            }
        }

        title = (TextView) findViewById(R.id.title);
        btn_previous = (ImageButton) findViewById(R.id.btn_previous);
        btn_next = (ImageButton) findViewById(R.id.btn_next);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String curentDate = df.format(Calendar.getInstance().getTime());
        selectedGridDate = curentDate;
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar();
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar();
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                view=v;
                vg=parent;
                pos=position;
               ((CalendarAdapter) parent.getAdapter()).setSelected(v);
                selectedGridDate = CalendarAdapter.dayString
                        .get(position);

                String[] separatedTime = selectedGridDate.split("-");
                String gridvalueString = separatedTime[2].replaceFirst("^0*",
                        "");// taking last part of date. ie; 2 from 2012-12-02.
                int gridvalue = Integer.parseInt(gridvalueString);
                // navigate to next or previous month on clicking offdays.
                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar();
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar();
                } else {
                    ((CalendarAdapter) parent.getAdapter()).setSelected(v);
                    events = new ArrayList<Event>();
                    events = db.getAllDayEvent(selectedGridDate);
                    if (events != null) {
                        prepareEventList();

                    }


                }


            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareEventList();

    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private void customActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        final View view = getLayoutInflater().inflate(R.layout.action_bar,
                null);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layoutParams);
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(-10, -10);
        TextView title = (TextView) findViewById(R.id.txtitle);
        calAddEvent = (ImageButton) findViewById(R.id.btn_calAddEvent);
        calAddEvent.setVisibility(View.VISIBLE);
        title.setText("Calendar");
        Button btnLogin_logout = (Button) findViewById(R.id.btnlogin_out);
        btnLogin_logout.setVisibility(View.INVISIBLE);
        calAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
                Date dateObj = null;
                String newDateStr = "";
                try {
                    dateObj = curFormater.parse(selectedGridDate);
                    SimpleDateFormat postFormater = new SimpleDateFormat("EEEE, MMM dd");
                    newDateStr = postFormater.format(dateObj);
                } catch (ParseException e) {
                    e.printStackTrace();
                    dateObj = null;
                }
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date date = cal.getTime();
                Log.e("Email", email + "");
                if (dateObj != null&&email!=null) {
                    if (dateObj.after(date)) {
                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.addevent_dialog);
                        final Button saveEvent = (Button) dialog.findViewById(R.id.btnSave);
                        final Button cancelAdd = (Button) dialog.findViewById(R.id.btnDismiss);
                        final TextView dialogTitle = (TextView) dialog.findViewById(R.id.lblTitle);
                        final EditText eventTitle = (EditText) dialog.findViewById(R.id.edit_title);
                        final EditText eventDes = (EditText) dialog.findViewById(R.id.edit_desc);
                        final TimePicker fromTime = (TimePicker) dialog.findViewById(R.id.timePicker_From);
                        final TimePicker toTime = (TimePicker) dialog.findViewById(R.id.timePicker_To);
                        fromTime.setIs24HourView(true);
                        toTime.setIs24HourView(true);
                        dialogTitle.setText("Add an Event on " + newDateStr);
                        cancelAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });


                        saveEvent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean titleOK, timeOK, emailOK;
                                if (!isEmpty(eventTitle)) {

                                    if (fromTime.getCurrentHour() <= toTime.getCurrentHour()) {
                                        if ((fromTime.getCurrentHour() == toTime.getCurrentHour()&&fromTime.getCurrentMinute() <= toTime.getCurrentMinute())
                                                || (fromTime.getCurrentHour() < toTime.getCurrentHour())) {
                                            String time = fromTime.getCurrentHour() + ":" + fromTime.getCurrentMinute() + " - "
                                                    + toTime.getCurrentHour() + ":" + toTime.getCurrentMinute();
                                            String desc = isEmpty(eventDes) ? "" : eventDes.getText().toString();
                                            Log.d("add", selectedGridDate + ", " + time + "," + eventTitle.getText().toString() + ", " + desc);
                                            final Event e = new Event(selectedGridDate, time, eventTitle.getText().toString(), desc);
                                            if (email != "") {
                                                items.add(e.getEventDate());
                                                long eID =db.addEvent(e);
                                                hc.uploadSingleEvent(e);
                                                adapter.notifyDataSetChanged();
                                                prepareEventList();
                                                eventAdapter.notifyDataSetChanged();
                                                alarm.addAlarm(e, (int)eID);
                                            } else {
                                                Toast.makeText(getApplicationContext(),
                                                        "Please Login to add event", Toast.LENGTH_SHORT)
                                                        .show();
                                            }


                                            dialog.dismiss();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "The Strting time should be less that Ending time", Toast.LENGTH_LONG)
                                                .show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Please enter a title for your event", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }

                        });
                        dialog.show();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Cannot add event in the past", Toast.LENGTH_SHORT)
                                .show();
                    }
                }else if(dateObj==null) { Toast.makeText(getApplicationContext(),
                        "Please Select A Day", Toast.LENGTH_SHORT)
                        .show();}
                else{Toast.makeText(getApplicationContext(),
                        "Please Login to add events", Toast.LENGTH_SHORT)
                        .show();}

            }
        });
        back = (ImageButton) findViewById(R.id.btn_backtomain);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventAdapter.notifyDataSetChanged();
                Intent intent = new Intent(CalendarActivity.this, Dashboard_Activity.class);
                startActivity(intent);
                finish();

            }
        });
    }


    protected void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1),
                    month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) + 1);
        }
        selectedGridDate = "";
        prepareEventList();
    }

    private void prepareEventList() {
        Log.e("events_size", db.getAllDayEvent(selectedGridDate).size()+"");
        events = db.getAllDayEvent(selectedGridDate);

        eventAdapter = new Events_Aapter(CalendarActivity.this, events);
        e_lv.setAdapter(eventAdapter);
        handler.post(selectView);
    }

    protected void setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
        selectedGridDate = "";
        prepareEventList();
    }


    public void refreshCalendar() {
        TextView title = (TextView) findViewById(R.id.title);
        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        handler.post(calendarUpdater); // generate some calendar items
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

    }

    public Runnable calendarUpdater = new Runnable() {
        @Override
        public void run() {
          items.clear();
          items=db.getAllEvent();
          adapter.setItems(items);
          adapter.notifyDataSetChanged();
        }
    };

    public void deleteEvent(Event e) {
        if (email != "") {
            items.remove(e.getEventDate());
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
            //prepareEventList();
            alarm.deleteAlarm(e);
            handler.post(selectView);
            db.deleteEvent(e.getEventID());
            hc.deleteSingleEvent(e);





            Log.d("Delete", e.getEventID() + "");
        } else {
            Toast.makeText(getApplicationContext(),
                    "Please Login to delete event", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    public Runnable selectView = new Runnable() {
        @Override
        public void run() {  if(vg!= null && pos >= 0) {
            ((CalendarAdapter) adapter).setSelected(vg.getChildAt(pos));

        }
        }};

}
